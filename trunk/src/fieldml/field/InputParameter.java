package fieldml.field;

import fieldml.FieldML;
import fieldml.domain.Domain;
import fieldml.exception.FieldmlException;

public class InputParameter
    extends Parameter
{
    private final int inputParameterIndex;


    public InputParameter( String name, Domain domain, int inputParameterIndex )
    {
        super( name, domain );

        this.inputParameterIndex = inputParameterIndex;
    }


    @Override
    public void evaluate( FieldParameters inputParameters, int[] argumentIndexes, FieldParameters localParameters )
        throws FieldmlException
    {
        localParameters.values.get( inputParameterIndex ).assign(
            inputParameters.values.get( argumentIndexes[inputParameterIndex] ) );
    }


    @Override
    public int getType()
    {
        return FieldML.PT_INPUT_PARAMETER;
    }
}
