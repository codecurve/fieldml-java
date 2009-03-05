package fieldml.domain;

import java.util.ArrayList;

public abstract class Domain
{

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

    private final ArrayList<String> componentNames = new ArrayList<String>();
    
    private final DomainManager manager;

    public Domain( DomainManager manager, String name )
    {
        this.name = name;
        this.manager = manager;
        id = this.manager.add(this);
    }


    @Override
    public String toString()
    {
        return "Domain " + getName() + " (" + id + ")";
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


	public String getName() {
		return name;
	}
}
