package fieldml;

import fieldml.domain.ContinuousDomain;
import fieldml.domain.DiscreteDomain;
import fieldml.domain.Domain;
import fieldml.domain.DomainManager;

// In some far-off future, these could all be JNI calls to a FieldML library written in C/C++.
public class FieldML
{
    public static int FieldML_CreateContinuousDomain( DomainManager manager, String name )
    {
        Domain domain = new ContinuousDomain( manager, name );

        return domain.getId();
    }


    public static int FieldML_CreateDiscreteDomain( DomainManager manager, String name )
    {
        Domain domain = new DiscreteDomain( manager, name );

        return domain.getId();
    }


    public static int FieldML_GetDomainId( DomainManager manager, String originalDomainName )
    {
        return manager.getId( originalDomainName );
    }


    public static int FieldML_AddContinuousComponent( DomainManager manager, int domainId, String componentName, double min, double max )
    {
        Domain domain = manager.get( domainId );

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
    
    
    public static int FieldML_AddDiscreteComponent( DomainManager manager, int domainId, String componentName, int start, int count, int[] values )
    {
        Domain domain = manager.get( domainId );

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
