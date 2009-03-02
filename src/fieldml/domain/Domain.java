package fieldml.domain;

import java.util.*;

public abstract class Domain
{
    private static final int ID_FUDGE = 10000;
    
    private static final char DOMAIN_DELIMITER = '.';

    private static final Map<Integer, Domain> domains;

    static
    {
        domains = new HashMap<Integer, Domain>();
    }
    
    public static Domain get( int id )
    {
        return domains.get( id );
    }

    /**
     * A globally unique integer identifying the domain, useful for internal (inter-process) and
     * external (client-server) communication. In order to remain globally unique, this id number
     * cannot be user-supplied. Domains can be imported from external sources, and can therefore have
     * id numbers which are not known in advance by the user of the API when creating their own domains.
     */
    private final int id;

    /**
     * A locally unique string.
     */
    private final String name;

    private final CompositeDomain parent;


    public Domain( CompositeDomain parent, String name )
    {
        id = domains.size() + ID_FUDGE;

        domains.put( id, this );

        this.parent = parent;
        if( parent != null )
        {
            parent.components.put( name, this );
        }
        
        this.name = name;
    }


    public String toString()
    {
        return "Domain " + getFullName() + " (" + id + ")";
    }


    public String getFullName()
    {
        return parent.getFullName() + DOMAIN_DELIMITER + name;
    }


    public CompositeDomain getParent()
    {
        return parent;
    }


    public int getId()
    {
        return id;
    }
    
    public abstract void importInto( CompositeDomain parentDomain, String newName );
}
