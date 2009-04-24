package fieldml.field;

import fieldml.domain.ContinuousDomain;
import fieldml.domain.Domain;
import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

public class MultiplexedField
    extends ComputedField
{
    private MappedField sourceField;

    private Value sourceValue;

    private int sourceParameterIndex;

    private int sourceComponentIndex;

    private final int sourceFieldParameterIndexes[];

    private FieldParameters sourceParameters;

    // HACK!
    private boolean isReal;


    public MultiplexedField( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
        throws FieldmlException
    {
        super( manager, name, valueDomain );

        sourceFieldParameterIndexes = new int[1];
        sourceFieldParameterIndexes[0] = 0;
    }


    void evaluateComponents( FieldParameters parameters, Value value )
        throws FieldmlException
    {
        // TODO Parameter checking
        Value sourceParameterValue = sourceParameters.values.get( 0 );
        Value inputParameterValues = parameters.values.get( sourceParameterIndex );

        for( int i = 0; i < getComponentCount(); i++ )
        {
            sourceParameterValue.indexValues[0] = inputParameterValues.indexValues[i];

            sourceField.evaluate( sourceParameters, sourceFieldParameterIndexes, sourceValue );

            if( isReal )
            {
                value.realValues[i] = sourceValue.realValues[sourceComponentIndex];
            }
            else
            {
                value.indexValues[i] = sourceValue.indexValues[sourceComponentIndex];
            }
        }
    }


    public void setMultiplexComponent( MappedField sourceField, int sourceComponentIndex, int parameterIndex )
        throws FieldmlException
    {
        // TODO Parameter checking
        if( ( sourceField.valueDomain instanceof ContinuousDomain ) != ( valueDomain instanceof ContinuousDomain ) )
        {
            throw new BadFieldmlParameterException();
        }

        if( sourceComponentIndex >= sourceField.getComponentCount() )
        {
            throw new BadFieldmlParameterException();
        }

        if( sourceField.getParameterDomain( 0 ).getComponentCount() != valueDomain.getComponentCount() )
        {
            throw new BadFieldmlParameterException();
        }

        isReal = sourceField instanceof RealField;

        this.sourceField = sourceField;
        this.sourceComponentIndex = sourceComponentIndex;
        this.sourceParameterIndex = parameterIndex;

        sourceValue = new Value( sourceField.valueDomain );

        sourceParameters = new FieldParameters();
        sourceParameters.addDomain( sourceField.getParameterDomain( 0 ) );
    }

}
