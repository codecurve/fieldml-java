package fieldml.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks all top level domains 
 */
public class DomainManager {

	private final Map<Integer, Domain> domains = new HashMap<Integer, Domain>();
	private final Map<String, Integer> domainIds = new HashMap<String, Integer>();
	
	public Domain get( int id )
	{
	    return domains.get( id );
	}
	
	public int getId( String name )
	{
	    Integer id = domainIds.get( name );
	    
	    if( id == null )
	    {
	        return 0;
	    }
	    
	    return id;
	}
	
	private int generateNewUniqueId() {
		return domains.size() + DomainManager.ID_FUDGE;
	}
	
	static final int ID_FUDGE = 10000;

	public int add(Domain domain) {
		int id = generateNewUniqueId();
        domains.put( id, domain );
        domainIds.put( domain.getFullName(), id );
		return id;
	}

}