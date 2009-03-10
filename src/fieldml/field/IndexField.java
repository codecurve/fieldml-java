package fieldml.field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fieldml.domain.DiscreteDomain;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.RealValue;

/**
 * IndexField defines a non-composite index-valued field. Index-valued fields are separate from integer/real valued fields, as
 * few (if any) mathematical operations on them make sense. It would also be incorrent to interpret such fields as representing
 * even dimensionless values such as radians or temperature. Typically, an IndexField's domain is a single-component instance of
 * DiscreteDomain, and serves as an index into another field.
 * 
 * In fact, until a good use-case can be found for a multiple-index discrete-domain field, the API will infer
 * "parameter 1, component 1" when assigning and evaluating.
 */
public class IndexField
    extends Field
{
    private final DiscreteDomain valueDomain;

    private final Map<Integer, int[]> values;


    public IndexField( FieldmlObjectManager<Field> manager, DiscreteDomain valueDomain, String name )
    {
        super( manager, name );

        this.valueDomain = valueDomain;

        values = new HashMap<Integer, int[]>();
    }


    public int assignValues( int indexValue, int[] assignedValues )
    {
        if( indexDomain == null )
        {
            //ERROR field must have an index domain
            return -1;
        }
        
        if( assignedValues.length != getComponentCount() )
        {
            //ERROR
            return -1;
        }
        
        if( values.get( indexValue ) != null )
        {
            // ERROR already assigned
            return -1;
        }

        values.put( indexValue, Arrays.copyOf( assignedValues, assignedValues.length ) );

        return 0;
    }


    public int evaluate( FieldParameters parameters, RealValue value )
    {
        int[] localValues = values.get( parameters.indexValues.get( 0 ).values[0] );

        for( int i = 0; i < valueDomain.getComponentCount(); i++ )
        {
            value.values[i] = localValues[i];
        }

        return 0;
    }


    public int getComponentCount()
    {
        return valueDomain.getComponentCount();
    }
}
