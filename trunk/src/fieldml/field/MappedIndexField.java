package fieldml.field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fieldml.domain.DiscreteDomain;
import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

// TODO: Needs Javadoc
public class MappedIndexField
    extends MappedField
    implements IndexField
{
    private final Map<Integer, int[]> valueMap;


    public MappedIndexField( FieldmlObjectManager<Field> manager, String name, DiscreteDomain valueDomain )
        throws FieldmlException
    {
        super( manager, name, valueDomain );

        valueMap = new HashMap<Integer, int[]>();
    }


    public void setComponentValues( int parameterValue, int[] componentValues )
        throws FieldmlException
    {
        int componentCount = getComponentCount();

        if( componentValues.length < componentCount )
        {
            throw new BadFieldmlParameterException();
        }

        valueMap.put( parameterValue, Arrays.copyOf( componentValues, componentCount ) );
    }


    @Override
    public void evaluate( FieldParameters parameters, int[] parameterIndexes, Value value )
        throws FieldmlException
    {
        if( parameterIndexes.length < 1 )
        {
            throw new BadFieldmlParameterException();
        }
        Value parameter = parameters.values.get( parameterIndexes[0] );

        if( ( parameter.indexValues == null ) || ( parameter.indexValues.length <= keyComponentIndex ) )
        {
            throw new BadFieldmlParameterException();
        }
        if( ( value.indexValues == null ) || ( value.indexValues.length < getComponentCount() ) )
        {
            throw new BadFieldmlParameterException();
        }

        int keyValue = parameter.indexValues[keyComponentIndex];
        int[] values = valueMap.get( keyValue );
        int count = getComponentCount();

        for( int i = 0; i < count; i++ )
        {
            value.indexValues[i] = values[i];
        }
    }


    public void getComponentValues( int parameterValue, int[] componentValues )
        throws FieldmlException
    {
        int[] values = valueMap.get( parameterValue );

        if( ( values == null ) || ( componentValues.length < values.length ) )
        {
            throw new BadFieldmlParameterException();
        }

        System.arraycopy( values, 0, componentValues, 0, getComponentCount() );
    }
}