package fieldml.domain;

import java.util.*;

public class DiscreteDomain
    extends Domain
{
    private final ArrayList<int[]> componentValues;


    public DiscreteDomain( DomainManager manager, String name )
    {
        super( manager, name );

        componentValues = new ArrayList<int[]>();
    }
    
    
    public int addComponent( String componentName, int valueStart, int valueCount, int[] values )
    {
        super.addComponent( componentName );
        
        componentValues.add( Arrays.copyOfRange( values, valueStart, valueStart + valueCount - 1 ) );
        
        return componentValues.size();
    }
}
