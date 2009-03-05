package fieldml;

import fieldml.domain.CompositeDomain;
import fieldml.domain.DiscreteDomain;
import fieldml.domain.Domain;
import fieldml.domain.DomainManager;

// In some far-off future, these could all be JNI calls to a FieldML library written in C/C++.
public class FieldML
{
	private static boolean isValidParent(Domain parent) {
		return ( parent != null ) && ( parent instanceof CompositeDomain );
	}

	
    public static int FieldML_CreateCompositeDomain( DomainManager manager, int parentId, String name )
    {
        Domain parent = manager.get( parentId );

        if( !isValidParent(parent) )
        {
            //ERROR
        }

        return new CompositeDomain( manager, (CompositeDomain)parent, name ).getId();
    }


    public static int FieldML_CreateContinuousDomain( DomainManager manager, int parentId, double min, double max )
    {
        //STUB Create an infinite, semi-infinite or finite continuous domain.
        return 0;
    }


    public static int FieldML_CreateDiscreteDomain( DomainManager manager, int parentId, String name, int[] values, int startIndex, int count )
    {
        Domain parent = manager.get( parentId );

        if( !isValidParent(parent) )
        {
            //ERROR
        }

        return new DiscreteDomain( manager, (CompositeDomain)parent, name, values, startIndex, count ).getId();
    }


    public static int FieldML_GetDomainId( DomainManager manager, String originalDomainName )
    {
        return manager.getId( originalDomainName );
    }


    public static void FieldML_ImportDomain( DomainManager manager, Integer parentId, int originalDomainId, String newName )
    {
        Domain parent = manager.get( parentId );

        if( !isValidParent(parent) )
        {
            //ERROR

        }
        
        CompositeDomain compositeParent = (CompositeDomain)parent;
        
        Domain child = manager.get( originalDomainId );
        
        if( child == null )
        {
            //ERROR
        }

        child.importInto( compositeParent, newName );
    }
}
