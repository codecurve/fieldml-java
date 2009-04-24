package fieldml.field;

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
    private final FieldParameters localFieldParameters;

    private boolean hasNonInputParameters;
    
    private final FieldmlObjectManager<Field> manager;


    public ComputedField( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
        throws FieldmlException
    {
        super( manager, name, valueDomain );

        localFieldParameters = new FieldParameters();

        hasNonInputParameters = false;
        
        this.manager = manager;
    }


    public int addInputParameter( String parameterName, Domain domain )
        throws FieldmlException
    {
        // To keep parameter indexes consistent between Field.Foo calls and
        // ComputedField.Foo calls, we need to ensure that input parameters
        // always occur before derived parameters.
        if( hasNonInputParameters )
        {
            throw new BadFieldmlParameterException();
        }

        int parameterIndex = getParameterCount();

        addParameter( new InputParameter( parameterName, domain, parameterIndex ) );

        localFieldParameters.addDomain( domain );

        return parameterIndex;
    }


    private boolean checkArgumentIndexes( ImmutableList<Domain> signature, int[] argumentIndexes )
        throws FieldmlException
    {
        int parameterCount = signature.size() - 1;

        if( parameterCount > argumentIndexes.length )
        {
            throw new BadFieldmlParameterException();
        }

        for( int i = 0; i < parameterCount; i++ )
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

            Domain parameterDomain = localFieldParameters.values.get( argumentIndexes[i] ).domain;

            if( parameterDomain.getId() != signature.get( i + 1 ).getId() )
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
        ImmutableList<Domain> signature = parameterField.getSignature();

        if( !checkArgumentIndexes( signature, argumentIndexes ) )
        {
            throw new BadFieldmlParameterException();
        }

        int parameterIndex = getParameterCount();

        addParameter( new DirectParameter( parameterName, parameterField, argumentIndexes, parameterIndex ) );
        localFieldParameters.addDomain( parameterField.valueDomain );

        hasNonInputParameters = true;

        return parameterIndex;
    }


    public int addIndirectParameter( String parameterName, int fieldParameterIndex, int fieldParameterComponentIndex,
        int[] argumentIndexes )
        throws FieldmlException
    {
        if( fieldParameterIndex > getParameterCount() )
        {
            throw new BadFieldmlParameterException();
        }

        Domain parameterDomain = localFieldParameters.values.get( fieldParameterIndex ).domain;

        if( !( parameterDomain instanceof DiscreteFieldDomain ) )
        {
            throw new BadFieldmlParameterException();
        }

        DiscreteFieldDomain fieldParameterDomain = (DiscreteFieldDomain)parameterDomain;

        ImmutableList<Domain> signature = fieldParameterDomain.getSignature();

        checkArgumentIndexes( signature, argumentIndexes );

        addParameter( new IndirectParameter( manager, parameterName, signature.get( 0 ), fieldParameterIndex,
            fieldParameterComponentIndex, argumentIndexes, getParameterCount() ) );
        localFieldParameters.addDomain( signature.get( 0 ) );

        hasNonInputParameters = true;

        return getParameterCount();
    }


    abstract void evaluateComponents( FieldParameters parameters, Value value )
        throws FieldmlException;


    public void evaluate( FieldParameters inputParameters, int[] parameterIndexes, Value value )
        throws FieldmlException
    {
        if( getInputParameterCount() > parameterIndexes.length )
        {
            throw new BadFieldmlParameterException();
        }

        for( int i = 0; i < getParameterCount(); i++ )
        {
            Parameter parameter = getParameter( i );

            parameter.evaluate( inputParameters, parameterIndexes, localFieldParameters );
        }

        evaluateComponents( localFieldParameters, value );
    }


    public int getDerivedParameterField( int derivedParameterIndex )
        throws FieldmlException
    {
        if( ( derivedParameterIndex < 0 ) || ( derivedParameterIndex >= getParameterCount() ) )
        {
            throw new BadFieldmlParameterException();
        }

        if( derivedParameterIndex < getInputParameterCount() )
        {
            throw new BadFieldmlParameterException();
        }

        Parameter parameter = getParameter( derivedParameterIndex );

        if( !( parameter instanceof DirectParameter ) )
        {
            throw new BadFieldmlParameterException();
        }

        DirectParameter directParameter = (DirectParameter)parameter;

        return directParameter.getField().getId();
    }


    public int getDerivedParameterArguments( int derivedParameterIndex, int[] argumentIndexes )
        throws FieldmlException
    {
        if( ( derivedParameterIndex < 0 ) || ( derivedParameterIndex >= getParameterCount() ) )
        {
            throw new BadFieldmlParameterException();
        }

        if( derivedParameterIndex < getInputParameterCount() )
        {
            throw new BadFieldmlParameterException();
        }

        Parameter parameter = getParameter( derivedParameterIndex );

        int[] indexes = null;
        if( parameter instanceof DirectParameter )
        {
            indexes = ( (DirectParameter)parameter ).getIndexes();
        }
        else if( parameter instanceof IndirectParameter )
        {
            indexes = ( (IndirectParameter)parameter ).getIndexes();
        }
        else
        {
            throw new BadFieldmlParameterException();
        }

        System.arraycopy( indexes, 0, argumentIndexes, 0, indexes.length );

        return indexes.length;
    }
}
