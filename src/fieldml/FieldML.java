package fieldml;

import fieldml.domain.*;

// In some far-off future, these could all be JNI calls to a FieldML library written in C/C++.
public class FieldML
{
    public static int FieldML_CreateContinuousDomain( String name )
    {
        Domain domain = new ContinuousDomain( name );

        return domain.getId();
    }


    public static int FieldML_CreateDiscreteDomain( String name )
    {
        Domain domain = new DiscreteDomain( name );

        return domain.getId();
    }


    public static int FieldML_GetDomainId( String originalDomainName )
    {
        return Domain.getId( originalDomainName );
    }


    public static int FieldML_AddContinuousComponent( int domainId, String componentName, double min, double max )
    {
        Domain domain = Domain.get( domainId );

        if( !( domain instanceof ContinuousDomain ) )
        {
            // ERROR
            return -1;
        }
        else
        {
            ContinuousDomain continuousDomain = (ContinuousDomain)domain;
            
            return continuousDomain.addComponent( componentName, min, max );
        }
    }
    
    
    public static int FieldML_AddDiscreteComponent( int domainId, String componentName, int start, int count, int[] values )
    {
        Domain domain = Domain.get( domainId );

        if( !( domain instanceof DiscreteDomain ) )
        {
            // ERROR
            return -1;
        }
        else
        {
            DiscreteDomain discreteDomain = (DiscreteDomain)domain;
            
            return discreteDomain.addComponent( componentName, start, count, values );
        }
    }
}
