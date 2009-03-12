package fieldml.field.component;

import java.util.HashMap;
import java.util.Map;

import fieldml.field.FieldParameters;

public class IndexMappedIndexComponent
    extends IndexComponent
{
    private final int indexParameter;

    private final int indexComponent;

    private final Map<Integer, Integer> values;


    public IndexMappedIndexComponent( int indexParameter, int indexComponent )
    {
        this.indexParameter = indexParameter;
        this.indexComponent = indexComponent;

        values = new HashMap<Integer, Integer>();
    }


    public void setValue( int parameterValue, int value )
    {
        values.put( parameterValue, value );
    }


    @Override
    public int evaluate( FieldParameters parameters )
    {
        int value = parameters.values.get( indexParameter ).indexValues[indexComponent];

        return values.get( value );
    }
}
