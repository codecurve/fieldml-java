package fieldml.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscreteDomain
    extends Domain
{
    private final List<int[]> componentValues = new ArrayList<int[]>();


    public DiscreteDomain( DomainManager manager, String name )
    {
        super( manager, name );

    }
    
    
    public int addComponent( String componentName, int valueStart, int valueCount, int[] values )
    {
        super.addComponent( componentName );
        
        componentValues.add( Arrays.copyOfRange( values, valueStart, valueStart + valueCount - 1 ) );
        
        return componentValues.size();
    }
}
