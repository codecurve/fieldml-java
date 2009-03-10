package fieldml.value;

import fieldml.domain.ContinuousDomain;

public class RealValue
    extends Value
{
    // As this class is involved in computationally-heavy work, getters and setters aren't used.
    public final double[] values;
    
    public final int domainId;
    
    public RealValue( ContinuousDomain domain )
    {
        domainId = domain.getId();
        values = new double[domain.getComponentCount()];
    }
}
