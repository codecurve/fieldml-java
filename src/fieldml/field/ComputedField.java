package fieldml.field;

import java.util.ArrayList;
import java.util.Arrays;

import fieldml.domain.DiscreteFieldDomain;
import fieldml.domain.Domain;
import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.util.FieldmlObjectManager;
import fieldml.util.general.ImmutableList;
import fieldml.value.Value;

public abstract class ComputedField
    extends Field
{
    /**
     * This does indeed shadow Field.parameterNames, but ComputedField
     * parameters are indexed differently. This
     */
    private final ArrayList<String> derivedParameterNames;

    /**
     * Fields used to evaluate derived parameters.
     */
    private final ArrayList<Field> localParameterEvaluationFields;

    /**
     * parameter indexes required for evaluating the fields used for derived
     * parameters.
     * 
     * Using a 'third party' to determine parameter indexes allows us to re-use
     * the fieldParameters object to evaluate all derived parameters belonging
     * to this field.
     */
    private final ArrayList<int[]> localParameterEvaluationIndexes;

    /**
     * The cache used to store the values of all this fields parameters, and
     * thence to evaluate its components.
     */
    private final FieldParameters localFieldParameters;

    private boolean hasDerivedParameters;


    public ComputedField( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
        throws FieldmlException
    {
        super( manager, name, valueDomain );

        derivedParameterNames = new ArrayList<String>();
        localParameterEvaluationFields = new ArrayList<Field>();
        localParameterEvaluationIndexes = new ArrayList<int[]>();

        localFieldParameters = new FieldParameters();
    }


    public int addInputParameter( String parameterName, Domain domain )
        throws FieldmlException
    {
        // To keep parameter indexes consistent between Field.Foo calls and
        // ComputedField.Foo calls, we need to ensure that input parameters
        // always occur before derived parameters.
        if( hasDerivedParameters )
        {
            throw new BadFieldmlParameterException();
        }

        addParameter( parameterName, domain );

        // Keep all indexes consistent.
        derivedParameterNames.add( null );
        localFieldParameters.addDomain( domain );
        localParameterEvaluationFields.add( null );
        localParameterEvaluationIndexes.add( null );

        return localParameterEvaluationFields.size() - 1;
    }


    private boolean checkSignature( ImmutableList<Domain> signature, int[] argumentIndexes )
    {
        for( int i = 1; i < signature.size(); i++ )
        {
            Domain parameterDomain = localFieldParameters.values.get( argumentIndexes[i] ).domain;

            if( parameterDomain.getId() != signature.get(i).getId() )
            {
                // Domain mismatch
                return false;
            }
        }
        
        return true;
    }


    public int addDerivedParameter( String parameterName, Field parameterField, int[] argumentIndexes )
        throws FieldmlException
    {
        if( parameterField.getInputParameterCount() > argumentIndexes.length )
        {
            throw new BadFieldmlParameterException();
        }

        if( getParameterIndex( parameterName ) > -1 )
        {
            throw new BadFieldmlParameterException();
        }

        for( int i = 0; i < parameterField.getInputParameterCount(); i++ )
        {
            if( argumentIndexes[i] >= localFieldParameters.values.size() )
            {
                // ERROR derived parameter references an unknown parameter.
                // NOTE although a derived parameter could forward-reference a
                // parameter, the easiest way to prevent circular dependancies
                // is to insist that derived parameters only refer to already
                // defined ones.
                throw new BadFieldmlParameterException();
            }
        }

        ImmutableList<Domain> signature = getSignature();

        if( !checkSignature( signature, argumentIndexes ) )
        {
            throw new BadFieldmlParameterException();
        }

        hasDerivedParameters = true;

        derivedParameterNames.add( parameterName );
        localFieldParameters.addDomain( parameterField.valueDomain );
        localParameterEvaluationFields.add( parameterField );
        localParameterEvaluationIndexes.add( Arrays.copyOf( argumentIndexes, parameterField.getInputParameterCount() ) );

        return localParameterEvaluationFields.size() - 1;
    }


    public int addIndirectParameter( String parameterName, int fieldComponentIndex, int fieldParameterComponentIndex, int[] argumentIndexes )
        throws FieldmlException
    {
        Domain parameterDomain = localFieldParameters.values.get( fieldParameterComponentIndex ).domain;
        
        if(!( parameterDomain instanceof DiscreteFieldDomain ) )
        {
            throw new BadFieldmlParameterException();
        }
        
        DiscreteFieldDomain fieldParameterDomain = (DiscreteFieldDomain)parameterDomain;
        
        ImmutableList<Domain> signature = fieldParameterDomain.getSignature();
        
        if( parameterField.getInputParameterCount() > argumentIndexes.length )
        {
            throw new BadFieldmlParameterException();
        }

        if( getParameterIndex( parameterName ) > -1 )
        {
            throw new BadFieldmlParameterException();
        }

        for( int i = 0; i < parameterField.getInputParameterCount(); i++ )
        {
            if( argumentIndexes[i] >= localFieldParameters.values.size() )
            {
                // ERROR derived parameter references an unknown parameter.
                throw new BadFieldmlParameterException();
            }
            Domain parameterDomain = localFieldParameters.values.get( argumentIndexes[i] ).domain;

            if( parameterDomain.getId() != parameterField.getInputParameterDomain( i ).getId() )
            {
                // Domain mismatch
                throw new BadFieldmlParameterException();
            }
        }

        hasDerivedParameters = true;

        derivedParameterNames.add( parameterName );
        localFieldParameters.addDomain( parameterField.valueDomain );
        localParameterEvaluationFields.add( parameterField );
        localParameterEvaluationIndexes.add( Arrays.copyOf( argumentIndexes, parameterField.getInputParameterCount() ) );

        return localParameterEvaluationFields.size() - 1;
    }


    abstract void evaluateComponents( FieldParameters parameters, Value value )
        throws FieldmlException;


    public void evaluate( FieldParameters parameters, int[] parameterIndexes, Value value )
        throws FieldmlException
    {
        if( getInputParameterCount() > parameterIndexes.length )
        {
            throw new BadFieldmlParameterException();
        }

        int inputParameterCount = 0;

        for( int i = 0; i < localFieldParameters.values.size(); i++ )
        {
            Field parameterField = localParameterEvaluationFields.get( i );
            Value parameterValue = localFieldParameters.values.get( i );

            if( parameterField == null )
            {
                localFieldParameters.values.set( i, parameters.values.get( parameterIndexes[inputParameterCount++] ) );
            }
            else
            {
                parameterField.evaluate( parameters, localParameterEvaluationIndexes.get( i ), parameterValue );
            }
        }

        evaluateComponents( localFieldParameters, value );
    }


    public int getParameterCount()
    {
        return localParameterEvaluationFields.size();
    }


    public int getDerivedParameterField( int derivedParameterIndex )
        throws FieldmlException
    {
        if( ( derivedParameterIndex < 0 ) || ( derivedParameterIndex >= localParameterEvaluationFields.size() ) )
        {
            throw new BadFieldmlParameterException();
        }
        if( localParameterEvaluationFields.get( derivedParameterIndex ) == null )
        {
            throw new BadFieldmlParameterException();
        }

        return localParameterEvaluationFields.get( derivedParameterIndex ).getId();
    }


    public int getDerivedParameterArguments( int derivedParameterIndex, int[] argumentIndexes )
        throws FieldmlException
    {
        if( ( derivedParameterIndex < 0 ) || ( derivedParameterIndex >= localParameterEvaluationIndexes.size() ) )
        {
            throw new BadFieldmlParameterException();
        }
        if( localParameterEvaluationIndexes.get( derivedParameterIndex ) == null )
        {
            throw new BadFieldmlParameterException();
        }

        int[] indexes = localParameterEvaluationIndexes.get( derivedParameterIndex );

        if( argumentIndexes.length < indexes.length )
        {
            throw new BadFieldmlParameterException();
        }

        System.arraycopy( indexes, 0, argumentIndexes, 0, indexes.length );

        return indexes.length;
    }


    public String getParameterName( int parameterIndex )
        throws FieldmlException
    {
        if( ( parameterIndex < 0 ) || ( parameterIndex >= localParameterEvaluationFields.size() ) )
        {
            throw new BadFieldmlParameterException();
        }

        if( derivedParameterNames.get( parameterIndex ) != null )
        {
            return derivedParameterNames.get( parameterIndex );
        }

        // Not a derived parameter, must be an input parameter.
        return super.getParameterName( parameterIndex );
    }


    public int getParameterIndex( String name )
    {
        int index = super.getParameterIndex( name );

        if( index < 0 )
        {
            index = derivedParameterNames.indexOf( name );
        }

        return index;
    }
}
