package fieldml.domain;

import java.util.*;

public abstract class Domain
{
    private static final int ID_FUDGE = 10000;

    private static final Map<Integer, Domain> domains;

    private static final Map<String, Integer> domainIds;

    static
    {
        domains = new HashMap<Integer, Domain>();

        domainIds = new HashMap<String, Integer>();
    }


    public static Domain get( int id )
    {
        return domains.get( id );
    }


    public static int getId( String name )
    {
        Integer id = domainIds.get( name );

        if( id == null )
        {
            return 0;
        }

        return id;
    }

    /**
     * A globally unique integer identifying the domain, useful for internal (inter-process) and external (client-server)
     * communication. In order to remain globally unique, this id number cannot be user-supplied. Domains can be imported from
     * external sources, and can therefore have id numbers which are not known in advance by the user of the API when creating
     * their own domains.
     */
    private final int id;

    /**
     * A locally unique string.
     */
    private final String name;

    private final ArrayList<String> componentNames;


    public Domain( String name )
    {
        this.name = name;

        id = domains.size() + ID_FUDGE;

        domains.put( id, this );
        domainIds.put( name, id );

        componentNames = new ArrayList<String>();
    }


    public String toString()
    {
        return "Domain " + name + " (" + id + ")";
    }


    public int getId()
    {
        return id;
    }

    
    public int getComponentCount()
    {
        return componentNames.size();
    }

    public String getComponentName( int componentNumber )
    {
        if( ( componentNumber < 0 ) || ( componentNumber >= componentNames.size() ) )
        {
            return null;
        }

        return componentNames.get( componentNumber );
    }


    // This should not be directly invoked except by descendant classes.
    int addComponent( String componentName )
    {
        //TODO Uniqueness check!
        componentNames.add( componentName );
        
        return componentNames.size();
    }
}
