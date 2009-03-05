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
    
    // TODO Not self documenting.  Why are only some of the array elements from "values" copied?
    public int addComponent( String componentName, int valueStart, int valueCount, int[] values )
    {
        super.addComponent( componentName );
        
        // TODO Bounds check needed?
        componentValues.add( Arrays.copyOfRange( values, valueStart, valueStart + valueCount - 1 ) );
        
        return componentValues.size();
    }
}
