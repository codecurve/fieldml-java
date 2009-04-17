package fieldml.field;

import java.util.ArrayList;

import fieldml.domain.Domain;
import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.util.FieldmlObject;
import fieldml.util.FieldmlObjectManager;
import fieldml.util.general.ImmutableList;
import fieldml.util.general.MutableArrayList;
import fieldml.value.Value;

public abstract class Field
    implements FieldmlObject
{
    Domain valueDomain;

    /**
     * A globally unique integer identifying the field, useful for internal
     * (inter-process) and external (client-server) communication. In order to
     * remain globally unique, this id number cannot be user-supplied. Fields
     * can be imported from external sources, and can therefore have id numbers
     * which are not known in advance by the user of the API when creating their
     * own fields.
     */
    private final int id;

    /**
     * A locally unique string.
     */
    private final String name;

    private final ArrayList<Parameter> parameters;


    public Field( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
        throws FieldmlException
    {
        this.name = name;
        this.valueDomain = valueDomain;

        parameters = new ArrayList<Parameter>();

        id = manager.add( this );
    }


    @Override
    public String toString()
    {
        return "Field " + name + " (" + id + ")";
    }


    public int getId()
    {
        return id;
    }


    public String getName()
    {
        return name;
    }


    public int getComponentCount()
    {
        return valueDomain.getComponentCount();
    }


    int getComponentIndex( String componentName )
    {
        return valueDomain.getComponentIndex( componentName );
    }


    public Domain getValueDomain()
    {
        return valueDomain;
    }


    /**
     * Evaluate this field using the given parameters. The parameters are
     * provided by a FieldParameters object, and a list of indexes into that
     * object. This allows the caller to re-use the same FieldParameters object
     * to build up a set of values without having to construct a correctly
     * ordered list of parameters for each field evaluation they wish to do.
     * 
     * The field evaluates into the given Value object, which may in turn be an
     * entry in the given FieldParameters object.
     * 
     * This makes the FieldParameters analogous to a heap, and the indexes into
     * it analogous to a list of references.
     */
    public abstract void evaluate( FieldParameters parameters, int[] parameterIndexes, Value value )
        throws FieldmlException;


    protected void addParameter( Parameter parameter )
        throws FieldmlException
    {
        for( Parameter p : parameters )
        {
            if( p.getName().equals( parameter.getName() ) )
            {
                throw new BadFieldmlParameterException();
            }
        }

        parameters.add( parameter );
    }


    public int getInputParameterCount()
    {
        return parameters.size();
    }


    public void getInputParameterDomains( int[] domainIds )
        throws FieldmlException
    {
        int index = 0;

        for( Parameter p : parameters )
        {
            if( !( p instanceof InputParameter ) )
            {
                continue;
            }

            if( domainIds.length <= index )
            {
                throw new BadFieldmlParameterException();
            }

            domainIds[index++] = p.getDomain().getId();
        }
    }


    public Domain getParameterDomain( int parameterIndex )
        throws FieldmlException
    {
        Parameter parameter = getParameter( parameterIndex );
        
        return parameter.getDomain();
    }


    public String getParameterName( int parameterIndex )
        throws FieldmlException
    {
        Parameter parameter = getParameter( parameterIndex );
        
        return parameter.getName();
    }


    public int getParameterCount()
    {
        return getInputParameterCount();
    }


    public ImmutableList<Domain> getSignature()
    {
        MutableArrayList<Domain> signature = new MutableArrayList<Domain>();

        signature.add( valueDomain );

        for( Parameter parameter : parameters )
        {
            if( parameter instanceof InputParameter )
            {
                signature.add( parameter.getDomain() );
            }
        }

        return signature;
    }


    public int getParameterType( int parameterIndex )
        throws FieldmlException
    {
        Parameter parameter = getParameter( parameterIndex );
        
        return parameter.getType();
    }


    public Parameter getParameter( int parameterIndex )
        throws FieldmlException
    {
        if( ( parameterIndex < 0 ) || ( parameterIndex >= parameters.size() ) )
        {
            throw new BadFieldmlParameterException();
        }

        return parameters.get( parameterIndex );
    }
}
