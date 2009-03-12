package fieldml.field;

import java.util.ArrayList;
import java.util.Arrays;

import fieldml.domain.Domain;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

public abstract class ComputedField
    extends Field
{
    /**
     * Fields used to evaluate derived parameters.
     */
    private final ArrayList<Field> parameterEvaluationFields;

    /**
     * parameter indexes required for evaluating the fields used for derived
     * parameters.
     * 
     * Using a 'third party' to determine parameter indexes allows us to re-use
     * the fieldParameters object to evaluate all derived parameters belonging
     * to this field.
     */
    private final ArrayList<int[]> parameterEvaluationIndexes;

    /**
     * The cache used to store the values of all this fields parameters, and
     * thence to evaluate its components.
     */
    private final FieldParameters fieldParameters;


    public ComputedField( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
    {
        super( manager, name, valueDomain );

        parameterEvaluationFields = new ArrayList<Field>();
        parameterEvaluationIndexes = new ArrayList<int[]>();

        fieldParameters = new FieldParameters();
    }


    public int addInputParameter( Domain domain )
    {
        addParameterDomain( domain );

        fieldParameters.addDomain( domain );
        parameterEvaluationFields.add( null );
        parameterEvaluationIndexes.add( null );

        return 0;
    }


    public int addDerivedParameter( Field parameterField, int[] parameterIndexes )
    {
        for( int i = 0; i < parameterIndexes.length; i++ )
        {
            if( parameterIndexes[i] >= fieldParameters.count )
            {
                // ERROR derived parameter references an unknown parameter.
                // NOTE although a derived parameter could forward-reference a
                // parameter, the only
                // way to prevent circular dependancies is to insist that
                // derived parameters only
                // refer to already defined ones.
                return -1;
            }
        }

        fieldParameters.addDomain( parameterField.valueDomain );
        parameterEvaluationFields.add( parameterField );
        parameterEvaluationIndexes.add( Arrays.copyOf( parameterIndexes, parameterIndexes.length ) );

        return 0;
    }


    abstract int evaluateComponents( FieldParameters parameters, Value value );


    public int evaluate( FieldParameters parameters, int[] parameterIndexes, Value value )
    {
        for( int i = 0; i < fieldParameters.count; i++ )
        {
            Field parameterField = parameterEvaluationFields.get( i );
            Value parameterValue = fieldParameters.values.get( i );

            if( parameterField == null )
            {
                fieldParameters.values.set( i, value );
            }
            else
            {
                parameterField.evaluate( parameters, parameterEvaluationIndexes.get( i ), parameterValue );
            }
        }

        return evaluateComponents( fieldParameters, value );
    }
}
