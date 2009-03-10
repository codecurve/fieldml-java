package fieldml.value;

import fieldml.domain.DiscreteDomain;

public class IndexValue
    extends Value
{
    // As this class is involved in computationally-heavy work, getters and setters aren't used.
    public final int[] values;
    
    public final int domainId;
    
    public IndexValue( DiscreteDomain domain )
    {
        domainId = domain.getId();
        values = new int[domain.getComponentCount()];
    }
}
