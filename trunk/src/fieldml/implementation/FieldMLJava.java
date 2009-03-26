package fieldml.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fieldml.FieldML;
import fieldml.domain.ContinuousDomain;
import fieldml.domain.DiscreteDomain;
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


    public int FieldML_CreateContinuousDomain( String name )
    {
        try
        {
            Domain domain = new ContinuousDomain( domainManager, name );

            return domain.getId();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_CreateDiscreteDomain( String name )
    {
        try
        {
            Domain domain = new DiscreteDomain( domainManager, name );

            return domain.getId();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


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


    public int FieldML_AddContinuousDomainComponent( int domainId, String componentName, double min, double max )
    {
        try
        {
            ContinuousDomain domain = domainManager.getByClass( domainId, ContinuousDomain.class );

            return domain.addComponent( componentName, min, max );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_AddDiscreteDomainComponent( int domainId, String componentName, int[] values, int valueCount )
    {
        try
        {
            DiscreteDomain domain = domainManager.getByClass( domainId, DiscreteDomain.class );

            return domain.addComponent( componentName, values, valueCount );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_CreateField( String name, int valueDomainId )
    {
        // We could use domains to generate correctly-typed fields here without
        // having to use instaceofs.
        try
        {
            Domain domain = domainManager.get( valueDomainId );

            if( domain instanceof DiscreteDomain )
            {
                DiscreteDomain discreteDomain = (DiscreteDomain)domain;

                Field field = new ComputedIndexField( fieldManager, discreteDomain, name );

                outputValues.put( field, new Value( discreteDomain ) );

                return field.getId();

            }
            else if( domain instanceof ContinuousDomain )
            {
                ContinuousDomain continuousDomain = (ContinuousDomain)domain;

                Field field;

                if( name.equals( "library::bilinear_lagrange" ) )
                {
                    field = new BilinearLagrange( fieldManager, domainManager );
                }
                else if( name.equals( "library::bilinear_interpolation" ) )
                {
                    field = new BilinearInterpolation( fieldManager, domainManager );
                }
                else
                {
                    field = new ComputedRealField( fieldManager, continuousDomain, name );
                }

                outputValues.put( field, new Value( continuousDomain ) );

                return field.getId();
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


    public int FieldML_CreateMappedField( String name, int valueDomainId )
    {
        try
        {
            Domain domain = domainManager.get( valueDomainId );

            if( domain instanceof DiscreteDomain )
            {
                DiscreteDomain discreteDomain = (DiscreteDomain)domain;

                Field field = new MappedIndexField( fieldManager, name, discreteDomain );
                outputValues.put( field, new Value( discreteDomain ) );

                return field.getId();

            }
            else if( domain instanceof ContinuousDomain )
            {
                ContinuousDomain continuousDomain = (ContinuousDomain)domain;

                Field field = new MappedRealField( fieldManager, name, continuousDomain );
                outputValues.put( field, new Value( continuousDomain ) );

                return field.getId();
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


    public int FieldML_AssignDiscreteComponentValues( int fieldId, int parameterValue, int[] componentValues )
    {
        try
        {
            MappedIndexField field = fieldManager.getByClass( fieldId, MappedIndexField.class );

            field.setComponentValues( parameterValue, componentValues );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_AssignContinuousComponentValues( int fieldId, int parameterValue, double[] componentValues )
    {
        try
        {
            MappedRealField field = fieldManager.getByClass( fieldId, MappedRealField.class );

            field.setComponentValues( parameterValue, componentValues );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_SetMappingParameter( int fieldId, int domainId, int componentIndex )
    {
        try
        {
            MappedField field = fieldManager.getByClass( fieldId, MappedField.class );

            DiscreteDomain domain = domainManager.getByClass( domainId, DiscreteDomain.class );

            field.setMappingParameterDomain( domain, componentIndex );

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_AddInputParameter( int fieldId, String parameterName, int domainId )
    {
        try
        {
            ComputedField field = fieldManager.getByClass( fieldId, ComputedField.class );

            Domain domain = domainManager.get( domainId );

            return field.addInputParameter( parameterName, domain );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_AddDerivedParameter( int fieldId, String parameterName, int parameterFieldId, int[] argumentIndexes )
    {
        try
        {
            ComputedField field = fieldManager.getByClass( fieldId, ComputedField.class );

            Field parameterField = fieldManager.get( parameterFieldId );

            return field.addDerivedParameter( parameterName, parameterField, argumentIndexes );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


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


    public int FieldML_DestroyCache( int cacheId )
    {
        cacheManager.remove( cacheId );

        return NO_ERROR;
    }


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


    public int FieldML_GetDiscreteDomainComponentValueCount( int domainId, int componentIndex )
    {
        try
        {
            DiscreteDomain domain = domainManager.getByClass( domainId, DiscreteDomain.class );

            return domain.getComponentValueCount( componentIndex );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_GetDiscreteDomainComponentValues( int domainId, int componentIndex, int[] values )
    {
        try
        {
            DiscreteDomain domain = domainManager.getByClass( domainId, DiscreteDomain.class );

            return domain.getComponentValues( componentIndex, values );
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


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


    public int FieldML_GetDomainComponentName( int domainId, int componentIndex, char[] name )
    {
        try
        {
            // TODO We need a better way to return strings.
            Domain domain = domainManager.get( domainId );

            String componentName = domain.getComponentName( componentIndex );

            StringUtils.stringToChars(name, componentName);


            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }

    }


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


    public int FieldML_GetInputParameterDomain( int fieldId, int parameterIndex )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            Domain domain = field.getInputParameterDomain( parameterIndex );

            return domain.getId();
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_GetParameterName( int fieldId, int parameterIndex, char[] name )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            String parameterName = field.getParameterName( parameterIndex );

            StringUtils.stringToChars(name, parameterName);

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


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


    public int FieldML_GetDomainName( int domainId, char[] name )
    {
        try
        {
            Domain domain = domainManager.get( domainId );

            String domainName = domain.getName();

            StringUtils.stringToChars(name, domainName);

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


    public int FieldML_GetFieldName( int fieldId, char[] name )
    {
        try
        {
            Field field = fieldManager.get( fieldId );

            String fieldName = field.getName();

            StringUtils.stringToChars(name, fieldName);

            return NO_ERROR;
        }
        catch( FieldmlException e )
        {
            return e.errorCode;
        }
    }


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
