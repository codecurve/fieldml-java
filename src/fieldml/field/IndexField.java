package fieldml.field;

import fieldml.domain.DiscreteDomain;
import fieldml.field.component.IndexComponent;
import fieldml.field.component.IndexMappedIndexComponent;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

/**
 * IndexField defines a non-composite index-valued field. Index-valued fields are separate from integer/real valued fields, as
 * few (if any) mathematical operations on them make sense. It would also be incorrent to interpret such fields as representing
 * even dimensionless values such as radians or temperature. Typically, an IndexField's domain is a single-component instance of
 * DiscreteDomain, and serves as an index into another field.
 * 
 * In fact, until a good use-case can be found for a multiple-index discrete-domain field, the API will infer
 * "parameter 1, component 1" when assigning and evaluating.
 */
public class IndexField
    extends Field
{
    private final IndexComponent[] components;


    public IndexField( FieldmlObjectManager<Field> manager, DiscreteDomain valueDomain, String name )
    {
        super( manager, name, valueDomain );

        components = new IndexComponent[valueDomain.getComponentCount()];
    }


    public void importComponent( String componentName, IndexField field, int componentId )
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


    public int setComponentValues( int parameterValue, int[] componentValues )
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
            IndexMappedIndexComponent component;

            if( components[i] == null )
            {
                // TODO Allow a particular component from a multi-component field to be used as an index.
                component = new IndexMappedIndexComponent( indexParameter, 0 );
                components[i] = component;
            }
            else
            {
                component = (IndexMappedIndexComponent)components[i];
            }

            component.setValue( parameterValue, componentValues[i] );
        }

        return 0;
    }
}
