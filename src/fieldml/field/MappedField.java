package fieldml.field;

import fieldml.domain.Domain;
import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.util.FieldmlObjectManager;

public abstract class MappedField
    extends Field
{
    protected int keyComponentIndex;


    public MappedField( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
    {
        super( manager, name, valueDomain );
        
        keyComponentIndex = -1;
    }


    public void setMappingParameterDomain( Domain domain, int componentIndex )
        throws FieldmlException
    {
        if( getInputParameterCount() != 0 )
        {
            //We could allow the user to just change the parameter domain.
            throw new BadFieldmlParameterException();
        }

        addParameter( "mapping parameter", domain );

        keyComponentIndex = componentIndex;
    }


    public int getMappingParameterComponentIndex()
        throws FieldmlException
    {
        if( keyComponentIndex < 0 )
        {
            throw new BadFieldmlParameterException();
        }
        return keyComponentIndex;
    }


    public Domain getMappingParameterDomain()
        throws FieldmlException
    {
        if( getInputParameterCount() == 0 )
        {
            throw new BadFieldmlParameterException();
        }
        
        return getInputParameterDomain( 0 );
    }
}
