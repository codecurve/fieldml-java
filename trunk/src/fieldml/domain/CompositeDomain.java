package fieldml.domain;

import java.util.*;

public class CompositeDomain
    extends Domain
{
    /**
     * A composite domain can have named components, each of which is necessarily a domain itself.
     * 
     * The term subdomain is reserved for subsets of domains, rather than components.
     */
    private final Map<String, Domain> components = new HashMap<String, Domain>(); 


    public CompositeDomain( CompositeDomain parent, String name )
    {
        super( parent, name );
    }


    @Override
    public void importInto( CompositeDomain parentDomain, String newName )
    {
        CompositeDomain newDomain = new CompositeDomain( parentDomain, newName );
        
        for( String childName : components.keySet() )
        {
            //Imported children retain their name. Potential ambiguity is resolved by
            //ensuring that their (newly created) parent has a unique name.
            Domain childComponent = components.get( childName );
			childComponent.importInto( newDomain, childName );
        }
    }


	public void insert(String name, Domain domain) {
		components.put( name, domain );
	}
}
