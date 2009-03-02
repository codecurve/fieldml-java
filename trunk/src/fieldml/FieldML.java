package fieldml;

import fieldml.domain.*;

// In some far-off future, these could all be JNI calls to a FieldML library written in C/C++.
public class FieldML
{
    public static int FieldML_CreateCompositeDomain( int parentId, String name )
    {
        Domain parent = Domain.get( parentId );

        if( ( parent == null ) || !( parent instanceof CompositeDomain ) )
        {
            //ERROR
        }

        return new CompositeDomain( (CompositeDomain)parent, name ).getId();
    }


    public static int FieldML_CreateContinuousDomain( int parentId, double min, double max )
    {
        //STUB Create an infinite, semi-infinite or finite continuous domain.
        return 0;
    }


    public static int FieldML_CreateDiscreteDomain( int parentId, String name, int[] values, int startIndex, int count )
    {
        Domain parent = Domain.get( parentId );

        if( ( parent == null ) || !( parent instanceof CompositeDomain ) )
        {
            //ERROR
        }

        return new DiscreteDomain( (CompositeDomain)parent, name, values, startIndex, count ).getId();
    }


    public static int FieldML_GetDomainId( String originalDomainName )
    {
        return Domain.getId( originalDomainName );
    }


    public static void FieldML_ImportDomain( Integer parentId, int originalDomainId, String newName )
    {
        Domain parent = Domain.get( parentId );

        if( ( parent == null ) || !( parent instanceof CompositeDomain ) )
        {
            //ERROR

        }
        
        CompositeDomain compositeParent = (CompositeDomain)parent;
        
        Domain child = Domain.get( originalDomainId );
        
        if( child == null )
        {
            //ERROR
        }

        child.importInto( compositeParent, newName );
    }
}
