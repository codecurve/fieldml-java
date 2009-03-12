package fieldml.value;

import fieldml.domain.ContinuousDomain;
import fieldml.domain.DiscreteDomain;
import fieldml.domain.Domain;

public class Value
{
    // As this class is involved in computationally-heavy work, getters and setters aren't used.
    // Only one of these arrays will be non-null at any time. This makes it eerily close to a
    // C-style tagged union.
    public final int[] indexValues;

    public final double[] realValues;

    public final Domain domain;


    public Value( Domain domain )
    {
        this.domain = domain;

        if( domain instanceof DiscreteDomain )
        {
            indexValues = new int[domain.getComponentCount()];
            realValues = null;
        }
        else if( domain instanceof ContinuousDomain )
        {
            indexValues = null;
            realValues = new double[domain.getComponentCount()];
        }
        else
        {
            //ERROR
            indexValues = null;
            realValues = null;
        }
    }
}
