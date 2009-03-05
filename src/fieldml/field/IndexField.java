package fieldml.field;

/**
 * IndexField defines a non-composite index-valued field. Index-valued fields are separate from integer/real valued
 * fields, as few (if any) mathematical operations on them make sense. It would also be incorrent to interpret such fields
 * as representing even dimensionless values such as radians or temperature. Typically, an IndexField's domain
 * is an instance of DiscreteDomain, and serves as an index into another field.
 */
public class IndexField
    extends Field
{
    public IndexField( String name )
    {
        super( name );
    }
}
