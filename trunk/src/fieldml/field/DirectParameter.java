package fieldml.field;

import java.util.Arrays;

import fieldml.FieldML;
import fieldml.exception.FieldmlException;

public class DirectParameter
    extends Parameter
{
    private final Field field;

    private final int[] fieldParameterIndexes;

    private final int destinationIndex;


    public DirectParameter( String name, Field field, int[] fieldParameterIndexes, int destinationIndex )
    {
        super( name, field.valueDomain );

        this.field = field;
        this.fieldParameterIndexes = Arrays.copyOf( fieldParameterIndexes, field.getInputParameterCount() );
        this.destinationIndex = destinationIndex;
    }


    @Override
    public void evaluate( FieldParameters inputParameters, int[] argumentIndexes, FieldParameters localParameters )
        throws FieldmlException
    {
        field.evaluate( localParameters, fieldParameterIndexes, localParameters.values.get( destinationIndex ) );
    }


    public Field getField()
    {
        return field;
    }


    public int[] getIndexes()
    {
        return fieldParameterIndexes;
    }


    @Override
    public int getType()
    {
        return FieldML.PT_DIRECT_PARAMETER;
    }
}
