package fieldml.domain;

public class DiscreteDomain
    extends Domain
{
    private final int[] values;


    public DiscreteDomain( CompositeDomain parent, String name, int[] values, int startIndex, int count )
    {
        super( parent, name );

        this.values = new int[count];

        System.arraycopy( values, startIndex, this.values, 0, count );
    }


    @Override
    public void importInto( CompositeDomain parentDomain, String newName )
    {
        new DiscreteDomain( parentDomain, newName, values, 0, values.length ); 
    }
}
