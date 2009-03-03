package fieldml.field;

import java.util.*;

public class CompositeField
    extends Field
{
    /**
     * A composite domain can have named components, each of which is necessarily a domain itself.
     * 
     * The term subdomain is reserved for subsets of domains, rather than components.
     */
    final Map<String, Field> components;


    public CompositeField( CompositeField parent, String name )
    {
        super( parent, name );

        components = new HashMap<String, Field>();
    }
}
