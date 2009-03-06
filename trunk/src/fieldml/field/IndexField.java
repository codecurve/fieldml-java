package fieldml.field;

import fieldml.domain.DiscreteDomain;
import fieldml.util.FieldmlObjectManager;

/**
 * IndexField defines a non-composite index-valued field. Index-valued fields are separate from integer/real valued
 * fields, as few (if any) mathematical operations on them make sense. It would also be incorrent to interpret such fields
 * as representing even dimensionless values such as radians or temperature. Typically, an IndexField's domain
 * is an instance of DiscreteDomain, and serves as an index into another field.
 */
public class IndexField
    extends Field
{
    private final DiscreteDomain valueDomain; 
    
    public IndexField( FieldmlObjectManager<Field> manager, DiscreteDomain valueDomain, String name )
    {
        super( manager, name );
        
        this.valueDomain = valueDomain;
    }
}
