package fieldml.field;

import fieldml.domain.ContinuousDomain;
import fieldml.field.component.RealComponent;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

public class ComputedRealField
    extends ComputedField
    implements RealField
{
    private final RealComponent[] components;


    public ComputedRealField( FieldmlObjectManager<Field> manager, ContinuousDomain valueDomain, String name )
    {
        super( manager, name, valueDomain );

        components = new RealComponent[valueDomain.getComponentCount()];
    }


    public void importComponent( String componentName, ComputedRealField field, int componentId )
    {
        int index = getComponentIndex( componentName );

        components[index] = field.components[componentId];
    }


    // Specifying an arbitrarily nested composition of binary operators on domain, constant and/or
    // imported arguments seems non-trivial. Perhaps passing an array of argument specifiers, and
    // an array of operator specifiers, and applying an RPN-style evaluation algorithm might work.
    public int setComponentEvaluation( String componentName )
    {
        // ERROR feature not supported
        return -1;
    }


    @Override
    int evaluateComponents( FieldParameters parameters, Value value )
    {
        // Auditting parameter and value correctness can be done when parsing input or processing
        // FieldML API calls, and therefore need not be done here.
        for( int i = 0; i < getComponentCount(); i++ )
        {
            value.realValues[i] = components[i].evaluate( parameters );
        }

        return 0;
    }
}
