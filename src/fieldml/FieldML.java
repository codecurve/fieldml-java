package fieldml;

import fieldml.domain.ContinuousDomain;
import fieldml.domain.DiscreteDomain;
import fieldml.domain.Domain;
import fieldml.field.Field;
import fieldml.field.IndexField;
import fieldml.field.RealField;
import fieldml.util.FieldmlObjectManager;

/**
 * In some far-off future, these could all be JNI calls to a FieldML library written in C/C++.
 * Because the API is to be called from Fortran as well, only primitive types can be used as parameters.
 * 
 */
public class FieldML
{
    private static final FieldmlObjectManager<Domain> domainManager;
    private static final FieldmlObjectManager<Field> fieldManager;
    
    static
    {
        domainManager = new FieldmlObjectManager<Domain>();
        fieldManager = new FieldmlObjectManager<Field>();
    }
    
    public static int FieldML_CreateContinuousDomain( String name )
    {
        Domain domain = new ContinuousDomain( domainManager, name );

        return domain.getId();
    }


    public static int FieldML_CreateDiscreteDomain( String name )
    {
        Domain domain = new DiscreteDomain( domainManager, name );

        return domain.getId();
    }


    public static int FieldML_GetDomainId( String originalDomainName )
    {
        return domainManager.getId( originalDomainName );
    }


    public static int FieldML_AddContinuousDomainComponent( int domainId, String componentName, double min, double max )
    {
        Domain domain = domainManager.get( domainId );

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


    public static int FieldML_AddDiscreteDomainComponent( int domainId, String componentName, int start, int count, int[] values )
    {
        Domain domain = domainManager.get( domainId );

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
    
    
    public static int FieldML_CreateField( String name, int valueDomainId )
    {
        Domain domain = domainManager.get( valueDomainId );
        
        if( domain instanceof DiscreteDomain )
        {
            DiscreteDomain discreteDomain = (DiscreteDomain)domain;
            
            Field field = new IndexField( fieldManager, discreteDomain, name );

            return field.getId();
            
        }
        else if( domain instanceof ContinuousDomain )
        {
            ContinuousDomain continuousDomain = (ContinuousDomain)domain;
            
            Field field = new RealField( fieldManager, continuousDomain, name );

            return field.getId();
        }
        
        //ERROR
        return -1;
    }
    
    
    public static int FieldML_AssignComponentValues( int fieldId, int parameterValue, int[] componentValues )
    {
        Field field = fieldManager.get( fieldId );
        
        if( ! ( field instanceof IndexField ) )
        {
            //ERROR
            return -1;
        }
        
        if( componentValues.length != field.getComponentCount() )
        {
            //ERROR
            return -1;
        }
        
        IndexField indexField = (IndexField)field;
        
        indexField.assignValues( parameterValue, componentValues );
        
        return 0;
    }
}
