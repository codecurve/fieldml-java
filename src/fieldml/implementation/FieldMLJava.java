package fieldml.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fieldml.FieldML;
import fieldml.domain.ContinuousDomain;
import fieldml.domain.DiscreteIndexDomain;
import fieldml.domain.Domain;
import fieldml.exception.FieldmlException;
import fieldml.field.ComputedField;
import fieldml.field.ComputedIndexField;
import fieldml.field.ComputedRealField;
import fieldml.field.Field;
import fieldml.field.FieldParameters;
import fieldml.field.MappedField;
import fieldml.field.MappedIndexField;
import fieldml.field.MappedRealField;
import fieldml.field.library.BilinearInterpolation;
import fieldml.field.library.BilinearLagrange;
import fieldml.util.FieldmlObjectManager;
import fieldml.util.general.StringUtils;
import fieldml.value.Value;

public class FieldMLJava
    implements FieldML
{
    private final FieldmlObjectManager<Domain> domainManager;

    private final FieldmlObjectManager<Field> fieldManager;

    private final FieldmlObjectManager<FieldParameters> cacheManager;

    /**
     * The domain currently being defined. Only one 'partially-defined' object
     * can exist at a time.
     */
    private Domain currentDomain;

    private Field currentField;

    /**
     * Temporary values that fields can evaluate into for the
     * FieldML_Evaluate*Field calls.
     */
    private final Map<Field, Value> outputValues;

    private final int[] initialParameterIndexes;


    public FieldMLJava()
    {
        domainManager = new FieldmlObjectManager<Domain>();
        fieldManager = new FieldmlObjectManager<Field>();
        cacheManager = new FieldmlObjectManager<FieldParameters>();
        outputValues = new HashMap<Field, Value>();

        initialParameterIndexes = new int[32];

        for( int i = 0; i < initialParameterIndexes.length; i++ )
        {
            initialParameterIndexes[i] = i;
        }
    }


    @Override
    public int FieldML_BeginContinuousDomain( String name )
    {
        try
        {
            if( ( currentField != null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            currentDomain = new ContinuousDomain( domainManager, name );

            return FieldML.NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_BeginDiscreteDomain( String name )
    {
        try
        {
            if( ( currentField != null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            currentDomain = new DiscreteIndexDomain( domainManager, name );

            return FieldML.NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_EndDomain()
    {
        if( ( currentField != null ) || ( currentDomain == null ) )
        {
            return FieldML.ERR_INVALID_CALL;
        }

        int id = currentDomain.getId();

        currentDomain = null;

        return id;
    }


    @Override
    public int FieldML_GetDomainId( String originalDomainName )
    {
        try
        {
            return domainManager.getId( originalDomainName );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_AddContinuousDomainComponent( String componentName, double min, double max )
    {
        try
        {
            if( ( currentField != null ) || ( currentDomain == null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }
            
            ContinuousDomain domain = domainManager.getByClass( currentDomain.getId(), ContinuousDomain.class );

            return domain.addComponent( componentName, min, max );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_AddDiscreteDomainComponent( String componentName, int[] values, int valueCount )
    {
        try
        {
            if( ( currentField != null ) || ( currentDomain == null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            DiscreteIndexDomain domain = domainManager.getByClass( currentDomain.getId(), DiscreteIndexDomain.class );

            return domain.addComponent( componentName, values, valueCount );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_BeginField( String name, int valueDomainId )
    {
        // We could use domains to generate correctly-typed fields here without
        // having to use instaceofs.
        try
        {
            if( ( currentField != null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            Domain domain = domainManager.get( valueDomainId );

            if( domain instanceof DiscreteIndexDomain )
            {
                DiscreteIndexDomain discreteDomain = (DiscreteIndexDomain)domain;

                currentField = new ComputedIndexField( fieldManager, discreteDomain, name );

                outputValues.put( currentField, new Value( discreteDomain ) );

                return FieldML.NO_ERROR;
            }
            else if( domain instanceof ContinuousDomain )
            {
                ContinuousDomain continuousDomain = (ContinuousDomain)domain;

                if( name.equals( "library::bilinear_lagrange" ) )
                {
                    currentField = new BilinearLagrange( fieldManager, domainManager );
                }
                else if( name.equals( "library::bilinear_interpolation" ) )
                {
                    currentField = new BilinearInterpolation( fieldManager, domainManager );
                }
                else
                {
                    currentField = new ComputedRealField( fieldManager, continuousDomain, name );
                }

                outputValues.put( currentField, new Value( continuousDomain ) );

                return FieldML.NO_ERROR;
            }
            else
            {
                return ERR_WRONG_OBJECT_TYPE;
            }
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }
    
    
    @Override
    public int FieldML_EndField()
    {
        if( ( currentField == null ) || ( currentDomain != null ) )
        {
            return FieldML.ERR_INVALID_CALL;
        }

        int id = currentField.getId();

        currentField = null;

        return id;
    }


    @Override
    public int FieldML_GetFieldId( String originalFieldName )
    {
        try
        {
            return fieldManager.getId( originalFieldName );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_BeginMappedField( String name, int valueDomainId )
    {
        try
        {
            if( ( currentField != null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            Domain domain = domainManager.get( valueDomainId );

            if( domain instanceof DiscreteIndexDomain )
            {
                DiscreteIndexDomain discreteDomain = (DiscreteIndexDomain)domain;

                currentField = new MappedIndexField( fieldManager, name, discreteDomain );
                outputValues.put( currentField, new Value( discreteDomain ) );

                return FieldML.NO_ERROR;
            }
            else if( domain instanceof ContinuousDomain )
            {
                ContinuousDomain continuousDomain = (ContinuousDomain)domain;

                currentField = new MappedRealField( fieldManager, name, continuousDomain );
                outputValues.put( currentField, new Value( continuousDomain ) );

                return FieldML.NO_ERROR;
            }
            else
            {
                return ERR_WRONG_OBJECT_TYPE;
            }
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_AssignDiscreteComponentValues( int parameterValue, int[] componentValues )
    {
        try
        {
            if( ( currentField == null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            MappedIndexField field = fieldManager.getByClass( currentField.getId(), MappedIndexField.class );

            field.setComponentValues( parameterValue, componentValues );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_AssignContinuousComponentValues( int parameterValue, double[] componentValues )
    {
        try
        {
            if( ( currentField == null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            MappedRealField field = fieldManager.getByClass( currentField.getId(), MappedRealField.class );

            field.setComponentValues( parameterValue, componentValues );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_SetMappingParameter( int domainId, int componentIndex )
    {
        try
        {
            if( ( currentField == null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            MappedField field = fieldManager.getByClass( currentField.getId(), MappedField.class );

            DiscreteIndexDomain domain = domainManager.getByClass( domainId, DiscreteIndexDomain.class );

            field.setMappingParameterDomain( domain, componentIndex );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_AddInputParameter( String parameterName, int domainId )
    {
        try
        {
            if( ( currentField == null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            ComputedField field = fieldManager.getByClass( currentField.getId(), ComputedField.class );

            Domain domain = domainManager.get( domainId );

            return field.addInputParameter( parameterName, domain );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_AddDerivedParameter( String parameterName, int parameterFieldId, int[] argumentIndexes )
    {
        try
        {
            if( ( currentField == null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            ComputedField field = fieldManager.getByClass( currentField.getId(), ComputedField.class );

            Field parameterField = fieldManager.get( parameterFieldId );

            return field.addDerivedParameter( parameterName, parameterField, argumentIndexes );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_AddDerivedParameter( String parameterName, int fieldParameterIndex,
        int fieldParameterComponentIndex, int[] argumentIndexes )
    {
        try
        {
            if( ( currentField == null ) || ( currentDomain != null ) )
            {
                return FieldML.ERR_INVALID_CALL;
            }

            ComputedField field = fieldManager.getByClass( currentField.getId(), ComputedField.class );

            return field
                .addIndirectParameter( parameterName, fieldParameterIndex, fieldParameterComponentIndex, argumentIndexes );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetInputParameterCount( int fieldId )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            return field.getInputParameterCount();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetInputParameterDomains( int fieldId, int[] domainIds )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            field.getInputParameterDomains( domainIds );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_CreateCache( int[] domainIds, int parameterCount )
    {
        try
        {
            ArrayList<Domain> domains = new ArrayList<Domain>();

            for( int i = 0; i < parameterCount; i++ )
            {
                domains.add( domainManager.get( domainIds[i] ) );
            }

            FieldParameters cache = new FieldParameters( cacheManager, domains );

            return cache.getId();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_DestroyCache( int cacheId )
    {
        cacheManager.remove( cacheId );

        return NO_ERROR;
    }


    @Override
    public int FieldML_EvaluateContinuousField( int fieldId, int cacheId, double[] values )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            FieldParameters cache = cacheManager.get( cacheId );

            Value output = outputValues.get( field );

            field.evaluate( cache, initialParameterIndexes, output );

            System.arraycopy( output.realValues, 0, values, 0, output.realValues.length );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_EvaluateDiscreteField( int fieldId, int cacheId, int[] values )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            FieldParameters cache = cacheManager.get( cacheId );

            Value output = outputValues.get( field );

            field.evaluate( cache, initialParameterIndexes, output );

            System.arraycopy( output.indexValues, 0, values, 0, output.indexValues.length );

            return NO_ERROR;
        }

        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_SetContinousCacheValues( int cacheId, int parameterNumber, double[] values )
    {
        try
        {
            FieldParameters cache = cacheManager.get( cacheId );

            double[] destination = cache.values.get( parameterNumber ).realValues;
            if( destination == null )
            {
                return ERR_WRONG_OBJECT_TYPE;
            }
            if( destination.length > values.length )
            {
                return ERR_BAD_PARAMETER;
            }

            System.arraycopy( values, 0, destination, 0, destination.length );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_SetDiscreteCacheValues( int cacheId, int parameterNumber, int[] values )
    {
        try
        {
            FieldParameters cache = cacheManager.get( cacheId );

            int[] destination = cache.values.get( parameterNumber ).indexValues;
            if( destination == null )
            {
                return ERR_WRONG_OBJECT_TYPE;
            }
            if( destination.length > values.length )
            {
                return ERR_BAD_PARAMETER;
            }

            System.arraycopy( values, 0, destination, 0, destination.length );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetContinuousComponentValues( int fieldId, int parameterValue, double[] componentValues )
    {
        try
        {
            MappedRealField field = fieldManager.getByClass( fieldId, MappedRealField.class );

            field.getComponentValues( parameterValue, componentValues );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetContinuousDomainComponentExtrema( int domainId, int componentIndex, double[] values )
    {
        try
        {
            ContinuousDomain domain = domainManager.getByClass( domainId, ContinuousDomain.class );

            domain.getComponentExtrema( componentIndex, values );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetDiscreteComponentValues( int fieldId, int parameterValue, int[] componentValues )
    {
        try
        {
            MappedIndexField field = fieldManager.getByClass( fieldId, MappedIndexField.class );

            field.getComponentValues( parameterValue, componentValues );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetDiscreteDomainComponentValueCount( int domainId, int componentIndex )
    {
        try
        {
            DiscreteIndexDomain domain = domainManager.getByClass( domainId, DiscreteIndexDomain.class );

            return domain.getComponentValueCount( componentIndex );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetDiscreteDomainComponentValues( int domainId, int componentIndex, int[] values )
    {
        try
        {
            DiscreteIndexDomain domain = domainManager.getByClass( domainId, DiscreteIndexDomain.class );

            return domain.getComponentValues( componentIndex, values );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetDomainComponentCount( int domainId )
    {
        try
        {
            Domain domain = domainManager.get( domainId );

            return domain.getComponentCount();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetDomainComponentName( int domainId, int componentIndex, char[] name )
    {
        try
        {
            // TODO We need a better way to return strings.
            Domain domain = domainManager.get( domainId );

            String componentName = domain.getComponentName( componentIndex );

            StringUtils.stringToChars( name, componentName );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }

    }


    @Override
    public int FieldML_GetParameterCount( int fieldId )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            return field.getParameterCount();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }
    
    
    @Override
    public int FieldML_GetParameterType( int fieldId, int parameterIndex )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            return field.getParameterType( parameterIndex );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetDerivedParameterField( int fieldId, int derivedParameterIndex )
    {
        try
        {
            ComputedField field = fieldManager.getByClass( fieldId, ComputedField.class );

            return field.getDerivedParameterField( derivedParameterIndex );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetParameterDomain( int fieldId, int parameterIndex )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            Domain domain = field.getParameterDomain( parameterIndex );

            return domain.getId();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetParameterName( int fieldId, int parameterIndex, char[] name )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            String parameterName = field.getParameterName( parameterIndex );

            StringUtils.stringToChars( name, parameterName );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetMappingParameterComponentIndex( int fieldId )
    {
        try
        {
            MappedField field = fieldManager.getByClass( fieldId, MappedField.class );

            return field.getMappingParameterComponentIndex();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetMappingParameterDomain( int fieldId )
    {
        try
        {
            MappedField field = fieldManager.getByClass( fieldId, MappedField.class );

            Domain domain = field.getMappingParameterDomain();

            return domain.getId();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetDerivedParameterArguments( int fieldId, int derivedParameterIndex, int[] argumentIndexes )
    {
        try
        {
            ComputedField field = fieldManager.getByClass( fieldId, ComputedField.class );

            return field.getDerivedParameterArguments( derivedParameterIndex, argumentIndexes );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetDomainName( int domainId, char[] name )
    {
        try
        {
            Domain domain = domainManager.get( domainId );

            String domainName = domain.getName();

            StringUtils.stringToChars( name, domainName );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetFieldName( int fieldId, char[] name )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            String fieldName = field.getName();

            StringUtils.stringToChars( name, fieldName );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    @Override
    public int FieldML_GetValueDomain( int fieldId )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            Domain domain = field.getValueDomain();
            return domain.getId();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }
}
