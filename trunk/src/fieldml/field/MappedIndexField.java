package fieldml.field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fieldml.domain.DiscreteDomain;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

// TODO: Needs Javadoc
public class MappedIndexField
    extends MappedField
    implements IndexField
{
    private final Map<Integer, int[]> valueMap;


    public MappedIndexField( FieldmlObjectManager<Field> manager, String name, DiscreteDomain valueDomain )
    {
        super( manager, name, valueDomain );

        valueMap = new HashMap<Integer, int[]>();
    }


    public int setComponentValues( int parameterValue, int[] componentValues )
    {
        int componentCount = getComponentCount();

        if( componentValues.length < componentCount )
        {
            // ERROR not enough values
            return -1;
        }

        valueMap.put( parameterValue, Arrays.copyOf( componentValues, componentCount ) );

        return 0;
    }


    @Override
    public int evaluate( FieldParameters parameters, int[] parameterIndexes, Value value )
    {
    	// TODO: This code could be more self-documenting, value2 was extracted, but a more meaningful name for it is required.
        Value value2 = parameters.values.get( parameterIndexes[0] );
		int keyValue = value2.indexValues[keyComponentIndex];
		
        int[] values = valueMap.get( keyValue );
        int count = getComponentCount();

        for( int i = 0; i < count; i++ )
        {
            value.indexValues[i] = values[i];
        }

        return 0;
    }

}
