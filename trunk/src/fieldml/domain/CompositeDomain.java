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
    final Map<String, Domain> components;


    public CompositeDomain( CompositeDomain parent, String name )
    {
        super( parent, name );

        components = new HashMap<String, Domain>();
    }
}
