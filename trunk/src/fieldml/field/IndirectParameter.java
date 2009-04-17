package fieldml.field;

import java.util.Arrays;

import fieldml.FieldML;
import fieldml.domain.Domain;
import fieldml.exception.FieldmlException;
import fieldml.implementation.FieldMLJava;
import fieldml.value.Value;

public class IndirectParameter
    extends Parameter
{
    private final int fieldParameterIndex;

    private final int fieldParameterComponentIndex;

    private final int[] parameterIndexes;


    public IndirectParameter( String name, Domain domain, int fieldParameterIndex, int fieldParameterComponentIndex,
        int[] parameterIndexes )
    {
        super( name, domain );

        this.fieldParameterIndex = fieldParameterIndex;
        this.fieldParameterComponentIndex = fieldParameterComponentIndex;
        this.parameterIndexes = Arrays.copyOf( parameterIndexes, parameterIndexes.length );
    }


    @Override
    public void evaluate( FieldParameters inputParameters, int[] argumentIndexes, FieldParameters localParameters )
        throws FieldmlException
    {
        int fieldId = localParameters.values.get( fieldParameterIndex ).fieldIdValues[fieldParameterComponentIndex];

        Field field = FieldMLJava.

        field.evaluate( localParameters, parameterIndexes, value );
    }


    public int[] getIndexes()
    {
        return parameterIndexes;
    }


    @Override
    public int getType()
    {
        return FieldML.PT_INDIRECT_PARAMETER;
    }
}
