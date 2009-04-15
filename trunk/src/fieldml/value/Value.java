package fieldml.value;

import fieldml.domain.ContinuousDomain;
import fieldml.domain.DiscreteFieldDomain;
import fieldml.domain.DiscreteIndexDomain;
import fieldml.domain.Domain;

public class Value
{
    // As this class is involved in computationally-heavy work, getters and setters aren't used.
    // Only one of these arrays will be non-null at any time. This makes it eerily close to a
    // C-style tagged union.
    public final int[] indexValues;

    public final double[] realValues;
    
    public final int[] fieldIdValues;

    public final Domain domain;


    public Value( Domain domain )
    {
        this.domain = domain;

        if( domain instanceof DiscreteIndexDomain )
        {
            indexValues = new int[domain.getComponentCount()];
            realValues = null;
            fieldIdValues = null;
        }
        else if( domain instanceof ContinuousDomain )
        {
            indexValues = null;
            realValues = new double[domain.getComponentCount()];
            fieldIdValues = null;
        }
        else if( domain instanceof DiscreteFieldDomain )
        {
            indexValues = null;
            realValues = null;
            fieldIdValues = new int[domain.getComponentCount()];
        }
        else
        {
            //ERROR
            indexValues = null;
            realValues = null;
            fieldIdValues = null;
        }
    }
}
