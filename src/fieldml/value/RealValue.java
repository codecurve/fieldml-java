package fieldml.value;

import fieldml.domain.ContinuousDomain;

public class RealValue
    extends Value
{
    // As this class is involved in computationally-heavy work, getters and setters aren't used.
    public final double[] values;
    
    public RealValue( ContinuousDomain domain )
    {
        values = new double[domain.getComponentCount()];
    }
}
