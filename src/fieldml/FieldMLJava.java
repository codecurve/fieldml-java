package fieldml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fieldml.domain.ContinuousDomain;
import fieldml.domain.DiscreteDomain;
import fieldml.domain.Domain;
import fieldml.field.Field;
import fieldml.field.FieldParameters;
import fieldml.field.IndexField;
import fieldml.field.RealField;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

/**
 * In some far-off future, these could all be JNI calls to a FieldML library
 * written in C/C++. Because the API is to be called from Fortran as well, only
 * primitive types can be used as parameters.
 * 
 */
public class FieldMLJava
    implements FieldML
{
    private final FieldmlObjectManager<Domain> domainManager;
    private final FieldmlObjectManager<Field> fieldManager;

    private final FieldmlObjectManager<FieldParameters> cacheManager;
    
    /**
     * Temporary values that fields can evaluate into for the FieldML_Evaluate*Field calls.
     */
    private final Map<Field, Value> outputValues;

    private final int[] initialParameterIndexes;


    FieldMLJava()
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
        Domain domain = new ContinuousDomain( domainManager, name );

        return domain.getId();
    }


    public int FieldML_CreateDiscreteDomain( String name )
    {
        Domain domain = new DiscreteDomain( domainManager, name );

        return domain.getId();
    }


    public int FieldML_GetDomainId( String originalDomainName )
    {
        return domainManager.getId( originalDomainName );
    }


    public int FieldML_AddContinuousDomainComponent( int domainId, String componentName, double min, double max )
    {
        Domain domain = domainManager.get( domainId );

        if( !( domain instanceof ContinuousDomain ) )
        {
            // ERROR
            return -1;
        }
        else
        {
            ContinuousDomain continuousDomain = (ContinuousDomain)domain;

            return continuousDomain.addComponent( componentName, min, max );
        }
    }


    public int FieldML_AddDiscreteDomainComponent( int domainId, String componentName, int start, int count, int[] values )
    {
        Domain domain = domainManager.get( domainId );

        if( !( domain instanceof DiscreteDomain ) )
        {
            // ERROR
            return -1;
        }
        else
        {
            DiscreteDomain discreteDomain = (DiscreteDomain)domain;

            return discreteDomain.addComponent( componentName, start, count, values );
        }
    }


    public int FieldML_CreateField( String name, int valueDomainId )
    {
        Domain domain = domainManager.get( valueDomainId );

        if( domain instanceof DiscreteDomain )
        {
            DiscreteDomain discreteDomain = (DiscreteDomain)domain;

            Field field = new IndexField( fieldManager, discreteDomain, name );
            outputValues.put( field, new Value( discreteDomain ) );

            return field.getId();

        }
        else if( domain instanceof ContinuousDomain )
        {
            ContinuousDomain continuousDomain = (ContinuousDomain)domain;

            Field field = new RealField( fieldManager, continuousDomain, name );
            outputValues.put( field, new Value( continuousDomain ) );

            return field.getId();
        }

        // ERROR
        return -1;
    }


    public int FieldML_AssignDiscreteComponentValues( int fieldId, int parameterValue, int[] componentValues )
    {
        Field field = fieldManager.get( fieldId );

        if( !( field instanceof IndexField ) )
        {
            // ERROR
            return -1;
        }

        IndexField indexField = (IndexField)field;

        return indexField.setComponentValues( parameterValue, componentValues );
    }


    public int FieldML_AssignContinuousComponentValues( int fieldId, int parameterValue, double[] componentValues )
    {
        Field field = fieldManager.get( fieldId );

        if( !( field instanceof RealField ) )
        {
            // ERROR
            return -1;
        }

        RealField indexField = (RealField)field;

        return indexField.setComponentValues( parameterValue, componentValues );
    }


    public int FieldML_AddInputParameter( int fieldId, int domainId, boolean isIndexParameter )
    {
        Field field = fieldManager.get( fieldId );

        Domain domain = domainManager.get( domainId );

        return field.addInputParameter( domain, isIndexParameter );
    }


    public int FieldML_AddDerivedParameter( int fieldId, int mappingFieldId, int[] parameterIndexes, boolean isIndexParameter )
    {
        Field field = fieldManager.get( fieldId );

        Field parameterField = fieldManager.get( mappingFieldId );

        return field.addDerivedParameter( parameterField, parameterIndexes, isIndexParameter );
    }


    public int FieldML_GetFieldParameterCount( int fieldId )
    {
        Field field = fieldManager.get( fieldId );

        return field.getParameterCount();
    }


    public int FieldML_GetFieldParameterDomainIds( int fieldId, int[] domainIds )
    {
        Field field = fieldManager.get( fieldId );

        return field.getParameterDomainIds( domainIds );
    }


    public int FieldML_CreateCache( int[] domainIds, int parameterCount )
    {
        ArrayList<Domain> domains = new ArrayList<Domain>();

        for( int i = 0; i < parameterCount; i++ )
        {
            domains.add( domainManager.get( domainIds[i] ) );
        }

        FieldParameters cache = new FieldParameters( cacheManager, domains );

        return cache.getId();
    }


    public int FieldML_DestroyCache( int cacheId )
    {
        return cacheManager.remove( cacheId );
    }


    public int FieldML_EvaluateContinuousField( int fieldId, int cacheId, double[] values )
    {
        Field field = fieldManager.get( fieldId );

        FieldParameters cache = cacheManager.get( cacheId );

        if( !( field instanceof RealField ) )
        {
            // ERROR
            return -1;
        }

        Value output = outputValues.get( field );

        int err = field.evaluate( cache, initialParameterIndexes, output );

        if( err == 0 )
        {
            System.arraycopy( output.realValues, 0, values, 0, output.realValues.length );
        }

        return err;
    }


    public int FieldML_EvaluateDiscreteField( int fieldId, int cacheId, int[] values )
    {
        Field field = fieldManager.get( fieldId );

        FieldParameters cache = cacheManager.get( cacheId );

        if( !( field instanceof IndexField ) )
        {
            // ERROR
            return -1;
        }

        Value output = outputValues.get( field );

        int err = field.evaluate( cache, initialParameterIndexes, output );

        if( err == 0 )
        {
            System.arraycopy( output.indexValues, 0, values, 0, output.indexValues.length );
        }

        return err;
    }


    public int FieldML_SetContinousCacheValues( int cacheId, int parameterNumber, double[] values )
    {
        FieldParameters cache = cacheManager.get( cacheId );
        
        double[] destination = cache.values.get( parameterNumber ).realValues;
        if( ( destination == null ) || ( destination.length > values.length ) )
        {
            //ERROR
            return -1;
        }

        System.arraycopy( values, 0, destination, 0, destination.length );
        
        return 0;
    }


    public int FieldML_SetDiscreteCacheValues( int cacheId, int parameterNumber, int[] values )
    {
        FieldParameters cache = cacheManager.get( cacheId );
        
        int[] destination = cache.values.get( parameterNumber ).indexValues;
        if( ( destination == null ) || ( destination.length > values.length ) )
        {
            //ERROR
            return -1;
        }

        System.arraycopy( values, 0, destination, 0, destination.length );
        
        return 0;
    }
}
