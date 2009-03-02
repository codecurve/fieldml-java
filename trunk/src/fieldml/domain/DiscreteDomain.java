package fieldml.domain;

public class DiscreteDomain
    extends Domain
{
    private final int[] entries;


    public DiscreteDomain( CompositeDomain parent, String name, int[] entries, int startIndex, int count )
    {
        super( parent, name );

        this.entries = new int[count];

        System.arraycopy( entries, startIndex, this.entries, 0, count );
    }
}
