package fieldml.field;

import fieldml.domain.ContinuousDomain;
import fieldml.field.component.IndexMappedRealComponent;
import fieldml.field.component.RealComponent;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

public class RealField
    extends Field
{
    private final RealComponent[] components;


    public RealField( FieldmlObjectManager<Field> manager, ContinuousDomain valueDomain, String name )
    {
        super( manager, name, valueDomain );

        components = new RealComponent[valueDomain.getComponentCount()];
    }


    public void importComponent( String componentName, RealField field, int componentId )
    {
        int index = getComponentIndex( componentName );

        components[index] = field.components[componentId];
    }


    // Specifying an arbitrarily nested composition of binary operators on domain, constant and/or
    // imported arguments seems non-trivial. Perhaps passing an array of argument specifiers, and
    // an array of operator specifiers, and applying an RPN-style evaluation algorithm might work.
    public int setComponentEvaluation( String componentName )
    {
        if( indexParameter >= 0 )
        {
            // ERROR cannot use evaluated components with an indexed field.
            // NOTE at some point, we could permit some components to be indexed, and others evaluated.
            // BUT NOT THIS DAY!
            return -1;
        }

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


    public int setComponentValues( int parameterValue, double[] componentValues )
    {
        if( indexParameter < 0 )
        {
            // ERROR field must have an index domain
            return -1;
        }

        if( componentValues.length < getComponentCount() )
        {
            // ERROR not enough values
            return -1;
        }

        // MUSTDO ensure that assignValues is only called for indexed fields.

        for( int i = 0; i < components.length; i++ )
        {
            IndexMappedRealComponent component;

            if( components[i] == null )
            {
                // TODO Allow a particular component from a multi-component field to be used as an index.
                component = new IndexMappedRealComponent( indexParameter, 0 );
                components[i] = component;
            }
            else
            {
                component = (IndexMappedRealComponent)components[i];
            }

            component.setValue( parameterValue, componentValues[i] );
        }

        return 0;
    }
}
