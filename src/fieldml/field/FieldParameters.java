package fieldml.field;

import java.util.ArrayList;

import fieldml.domain.ContinuousDomain;
import fieldml.domain.DiscreteDomain;
import fieldml.value.IndexValue;
import fieldml.value.RealValue;

/**
 * This class essentially maintains a list of dynamic casts, allowing clients to access
 * values by index without having to use instanceof or casting.
 */
public class FieldParameters
{
    public final ArrayList<RealValue> realValues;

    public final ArrayList<IndexValue> indexValues;


    public FieldParameters()
    {
        realValues = new ArrayList<RealValue>();
        indexValues = new ArrayList<IndexValue>();
    }


    public void addDomainValue( ContinuousDomain domain )
    {
        realValues.add( new RealValue( domain ) );
        indexValues.add( null );
    }


    public void addDomainValue( DiscreteDomain domain )
    {
        realValues.add( null );
        indexValues.add( new IndexValue( domain ) );
    }
}
