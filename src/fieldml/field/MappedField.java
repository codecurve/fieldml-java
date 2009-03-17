package fieldml.field;

import fieldml.domain.Domain;
import fieldml.util.FieldmlObjectManager;

public abstract class MappedField
    extends Field
{
    protected int keyComponentIndex;


    public MappedField( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
    {
        super( manager, name, valueDomain );
    }


    public int setMappingParameterDomain( Domain domain, int componentIndex )
    {
        if( getParameterCount() != 0 )
        {
            // ERROR Parameter domain is already set.
            return -1;
        }

        addParameterDomain( domain );

        keyComponentIndex = componentIndex;
        
        return 0;
    }
}
