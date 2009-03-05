package fieldml.domain;


public abstract class Domain
{
    private static final char DOMAIN_DELIMITER = '.';

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

    private final CompositeDomain parent;

    private final DomainManager manager;

    public Domain( DomainManager manager, CompositeDomain parent, String name )
    {
        this.name = name;
        this.manager = manager;
        
        id = manager.add(this);


        // TODO Although convenient, adding a child automatically to its parent is a little side-effecty,
        //and leads to the slightly weird phenomenon of 'dangling constructors'. At the moment, there
        //seems no compelling reason to change this.
        this.parent = parent;
        if( parent != null )
        {
            parent.insert( name, this );
        }
    }

    @Override
	public String toString()
    {
        return "Domain " + getFullName() + " (" + id + ")";
    }


    public String getFullName()
    {
    	String parentName = "/";
    	if (parent != null ) {
    		parentName = parent.getFullName();
    	}
		return parentName + DOMAIN_DELIMITER + name;
    }


    public CompositeDomain getParent()
    {
        return parent;
    }


    public int getId()
    {
        return id;
    }

    // TODO this method needs Javadoc
    public abstract void importInto( CompositeDomain parentDomain, String newName );


	public DomainManager getManager() {
		return manager;
	}
}
