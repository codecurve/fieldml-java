package fieldml.domain;

import java.util.*;

import fieldml.exception.FieldmlException;

public class Domain
{
    private static final int ID_FUDGE = 10000;
    
    private static final char DOMAIN_DELIMITER = '.';

    private static final Map<Integer, Domain> domains;

    static
    {
        domains = new HashMap<Integer, Domain>();
    }

    /**
     * A domain can have named components, each of which is necessarily a domain itself.
     * 
     * The term subdomain is reserved for subsets of domains, rather than components.
     */
    private final Map<String, Domain> components;

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

    private final Domain parent;


    public Domain( Domain parent, String name )
        throws FieldmlException
    {
        components = new HashMap<String, Domain>();

        int delimiterPosition = name.indexOf( DOMAIN_DELIMITER );

        if( ( name.length() == 0 ) || ( delimiterPosition == 0 ) || ( delimiterPosition == name.length() - 1 ) )
        {
            throw new FieldmlException( "Error creating domain. Invalid name: " + name );
        }

        id = domains.size() + ID_FUDGE;

        domains.put( id, this );

        if( delimiterPosition != -1 )
        {
            String childName = name.substring( delimiterPosition + 1 );
            name = name.substring( 0, delimiterPosition - 1 );
            
            new Domain( this, childName );
        }

        this.parent = parent;
        if( parent != null )
        {
            parent.components.put( name, this );
        }
        
        this.name = name;
    }


    public Domain( String name )
        throws FieldmlException
    {
        this( null, name );
    }


    public String toString()
    {
        return "Domain " + getFullName() + " (" + id + ")";
    }


    public String getFullName()
    {
        return parent.getFullName() + DOMAIN_DELIMITER + name;
    }
}
