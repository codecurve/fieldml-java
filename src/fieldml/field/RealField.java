package fieldml.field;

import fieldml.domain.ContinuousDomain;
import fieldml.field.component.RealComponent;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.RealValue;

public class RealField
    extends Field
{
    private final ContinuousDomain valueDomain;

    private final RealComponent[] components;


    public RealField( FieldmlObjectManager<Field> manager, ContinuousDomain valueDomain, String name )
    {
        super( manager, name );

        this.valueDomain = valueDomain;

        components = new RealComponent[valueDomain.getComponentCount()];
    }


    public void importComponent( String componentName, RealField field, int componentId )
    {
        int id = valueDomain.getComponentId( componentName );

        components[id] = field.components[componentId];
    }


    // Specifying an arbitrarily nested composition of binary operators on domain, constant and/or
    // imported arguments seems non-trivial. Perhaps passing an array of argument specifiers, and
    // an array of operator specifiers, and applying an RPN-style evaluation algorithm might work.
    public void evaluateComponent( String componentName )
    {
    }


    public void evaluate( FieldParameters parameters, RealValue value )
    {
        // Auditting parameter and value correctness can be done when parsing input or processing
        // FieldML API calls, and therefore need not be done here.
        for( int i = 0; i < valueDomain.getComponentCount(); i++ )
        {
            value.values[i] = components[i].evaluate( parameters );
        }
    }


    @Override
    public int getComponentCount()
    {
        return valueDomain.getComponentCount();
    }
}
