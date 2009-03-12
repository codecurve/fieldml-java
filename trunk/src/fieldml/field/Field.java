package fieldml.field;

import java.util.ArrayList;

import fieldml.domain.Domain;
import fieldml.util.FieldmlObject;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

public abstract class Field
    implements FieldmlObject
{
    Domain valueDomain;

    /**
     * A globally unique integer identifying the field, useful for internal (inter-process) and external (client-server)
     * communication. In order to remain globally unique, this id number cannot be user-supplied. Fields can be imported from
     * external sources, and can therefore have id numbers which are not known in advance by the user of the API when creating
     * their own fields.
     */
    private final int id;

    /**
     * A locally unique string.
     */
    private final String name;

    /**
     * The domains for each input parameter. These will not necessarily be the same as the domains of fieldParameters
     */
    private final ArrayList<Domain> parameterDomains;


    public Field( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
    {
        this.name = name;
        this.valueDomain = valueDomain;

        parameterDomains = new ArrayList<Domain>();

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
        return valueDomain.getComponentId( componentName );
    }


    public Domain getValueDomain()
    {
        return valueDomain;
    }


    public abstract int evaluate( FieldParameters parameters, int[] parameterIndexes, Value value );


    protected void addParameterDomain( Domain domain )
    {
        parameterDomains.add( domain );
    }
    
    
    public int getParameterCount()
    {
        return parameterDomains.size();
    }


    public int getParameterDomainIds( int[] domainIds )
    {
        if( domainIds.length < parameterDomains.size() )
        {
            // ERROR
            return -1;
        }

        for( int i = 0; i < parameterDomains.size(); i++ )
        {
            domainIds[i] = parameterDomains.get( i ).getId();
        }

        return 0;
    }
}
