package fieldml.field;

import fieldml.util.FieldmlObjectManager;

/**
 * RealField defines a non-composite real-valued field.
 */
public class RealField
    extends Field
{
    public RealField( FieldmlObjectManager<Field> manager, String name )
    {
        super( manager, name );
    }
    
}
