package fieldml.field.component.evaluated;

import fieldml.field.FieldParameters;

public class EvaluateComponent
    implements Evaluator
{
    private final int parameterIndex;

    private final int componentIndex;


    public EvaluateComponent( int parameterIndex, int componentIndex )
    {
        this.parameterIndex = parameterIndex;
        this.componentIndex = componentIndex;
    }


    @Override
    public double evaluate( FieldParameters values )
    {
        //Evaluators are only used with real-valued parameters
        return values.values.get( parameterIndex ).realValues[componentIndex];
    }
}
