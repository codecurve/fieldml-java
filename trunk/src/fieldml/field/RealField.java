package fieldml.field;

import java.util.ArrayList;

import fieldml.domain.ContinuousDomain;
import fieldml.field.component.RealComponent;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.RealValue;
import fieldml.value.Value;

/**
 * RealField defines a non-composite real-valued field.
 */
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


    public void evaluate( ArrayList<Value> parameters, RealValue value )
    {
        // Auditting parameter and value correctness can be done when parsing input or processing
        // FieldML API calls, and therefore need not be done here.
        for( int i = 0; i < valueDomain.getComponentCount(); i++ )
        {
            value.values[i] = components[i].evaluate( parameters );
        }
    }
}
