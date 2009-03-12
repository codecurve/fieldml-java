package fieldml.field.component;

import java.util.HashMap;
import java.util.Map;

import fieldml.field.FieldParameters;

public class IndexMappedRealComponent
    extends RealComponent
{
    private final int indexParameter;

    private final int indexComponent;

    private final Map<Integer, Double> values;


    public IndexMappedRealComponent( int indexParameter, int indexComponent )
    {
        this.indexParameter = indexParameter;
        this.indexComponent = indexComponent;

        values = new HashMap<Integer, Double>();
    }


    public void setValue( int parameterValue, double value )
    {
        values.put( parameterValue, value );
    }


    @Override
    public double evaluate( FieldParameters parameters )
    {
        int value = parameters.values.get( indexParameter ).indexValues[indexComponent];

        return values.get( value );
    }
}
