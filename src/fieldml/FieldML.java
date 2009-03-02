package fieldml;

import fieldml.domain.*;

// In some far-off future, these could all be JNI calls to a FieldML library written in C/C++.
public class FieldML
{
    public static int FieldML_CreateCompositeDomain( int parentId, String name )
    {
        Domain parent = Domain.get( parentId );

        if( !( parent instanceof CompositeDomain ) )
        {

        }

        return new CompositeDomain( (CompositeDomain)parent, name ).getId();
    }


    public static int FieldML_CreateContinuousDomain( int parentId, double min, double max )
    {
        return 0;
    }


    public static int FieldML_CreateDiscreteDomain( int parentId, String name, int[] values, int startIndex, int count )
    {
        Domain parent = Domain.get( parentId );

        if( !( parent instanceof CompositeDomain ) )
        {

        }

        return new DiscreteDomain( (CompositeDomain)parent, name, values, startIndex, count ).getId();
    }
}
