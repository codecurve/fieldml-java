package fieldml.field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fieldml.domain.ContinuousDomain;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

public class MappedRealField
    extends MappedField
    implements RealField
{
    private final Map<Integer, double[]> valueMap;


    public MappedRealField( FieldmlObjectManager<Field> manager, String name, ContinuousDomain valueDomain )
    {
        super( manager, name, valueDomain );

        valueMap = new HashMap<Integer, double[]>();
    }


    public int setComponentValues( int parameterValue, double[] componentValues )
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
        int keyValue = parameters.values.get( parameterIndexes[0] ).indexValues[keyComponentIndex];
        double[] values = valueMap.get( keyValue );
        int count = getComponentCount();

        for( int i = 0; i < count; i++ )
        {
            value.realValues[i] = values[i];
        }

        return 0;
    }
}
