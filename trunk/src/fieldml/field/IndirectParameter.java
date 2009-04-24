package fieldml.field;

import java.util.Arrays;

import fieldml.FieldML;
import fieldml.domain.Domain;
import fieldml.exception.FieldmlException;
import fieldml.util.FieldmlObjectManager;

public class IndirectParameter
    extends Parameter
{
    private final int fieldParameterIndex;

    private final int fieldParameterComponentIndex;

    private final int[] parameterIndexes;
    
    private final int destinationIndex;
    
    private final FieldmlObjectManager<Field> manager;


    public IndirectParameter( FieldmlObjectManager<Field> manager, String name, Domain domain, int fieldParameterIndex, int fieldParameterComponentIndex,
        int[] parameterIndexes, int destinationIndex )
    {
        super( name, domain );
        
        this.fieldParameterIndex = fieldParameterIndex;
        this.fieldParameterComponentIndex = fieldParameterComponentIndex;
        this.parameterIndexes = Arrays.copyOf( parameterIndexes, parameterIndexes.length );
        this.destinationIndex = destinationIndex;
        this.manager = manager;
    }


    @Override
    public void evaluate( FieldParameters inputParameters, int[] argumentIndexes, FieldParameters localParameters )
        throws FieldmlException
    {
        int fieldId = localParameters.values.get( fieldParameterIndex ).fieldIdValues[fieldParameterComponentIndex];

        Field field = manager.get( fieldId );

        field.evaluate( localParameters, parameterIndexes, localParameters.values.get( destinationIndex ) );
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
