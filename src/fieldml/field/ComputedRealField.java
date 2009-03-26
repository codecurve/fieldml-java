package fieldml.field;

import fieldml.domain.ContinuousDomain;
import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.field.component.RealComponent;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

public class ComputedRealField
    extends ComputedField
    implements RealField
{
    private final RealComponent[] components;


    public ComputedRealField( FieldmlObjectManager<Field> manager, ContinuousDomain valueDomain, String name )
        throws FieldmlException
    {
        super( manager, name, valueDomain );

        components = new RealComponent[valueDomain.getComponentCount()];
    }


    // Specifying an arbitrarily nested composition of binary operators on
    // domain, constant and/or imported arguments seems non-trivial. Perhaps
    // passing an array of argument specifiers, and an array of operator
    // specifiers, and applying an RPN-style evaluation algorithm might work.
    public void setComponentEvaluation( String componentName )
        throws FieldmlException
    {
        throw new BadFieldmlParameterException();
    }


    @Override
    void evaluateComponents( FieldParameters parameters, Value value )
        throws FieldmlException
    {
        if( ( value.realValues == null ) || ( value.realValues.length < getComponentCount() ) )
        {
            throw new BadFieldmlParameterException();
        }

        for( int i = 0; i < getComponentCount(); i++ )
        {
            value.realValues[i] = components[i].evaluate( parameters );
        }
    }
}
