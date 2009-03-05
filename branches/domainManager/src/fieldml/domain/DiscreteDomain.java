package fieldml.domain;

public class DiscreteDomain
    extends Domain
{
    private final int[] values;


    public DiscreteDomain( DomainManager manager, CompositeDomain parent, String name, int[] values, int startIndex, int count )
    {
        super( manager, parent, name );

        this.values = new int[count];

        System.arraycopy( values, startIndex, this.values, 0, count );
    }


    @Override
    public void importInto( CompositeDomain parentDomain, String newName )
    {
        new DiscreteDomain( getManager(), parentDomain, newName, values, 0, values.length ); 
    }
}
