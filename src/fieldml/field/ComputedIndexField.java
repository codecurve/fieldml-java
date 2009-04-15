package fieldml.field;

import fieldml.domain.DiscreteIndexDomain;
import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.field.component.IndexComponent;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

/**
 * IndexField defines a non-composite index-valued field. Index-valued fields
 * are separate from integer/real valued fields, as few (if any) mathematical
 * operations on them make sense. It would also be incorrect to interpret such
 * fields as representing even dimensionless values such as radians or
 * temperature. Typically, an IndexField's domain is a single-component instance
 * of DiscreteDomain, and serves as an index into another field.
 * 
 * In fact, until a good use-case can be found for a multiple-index
 * discrete-domain field, the API will infer "parameter 1, component 1" when
 * assigning and evaluating.
 */
public class ComputedIndexField
    extends ComputedField
    implements IndexField
{
    private final IndexComponent[] components;


    public ComputedIndexField( FieldmlObjectManager<Field> manager, DiscreteIndexDomain valueDomain, String name )
        throws FieldmlException
    {
        super( manager, name, valueDomain );

        components = new IndexComponent[valueDomain.getComponentCount()];
    }


    // Specifying an arbitrarily nested composition of binary operators on
    // domain, constant and/or
    // imported arguments seems non-trivial. Perhaps passing an array of
    // argument specifiers, and
    // an array of operator specifiers, and applying an RPN-style evaluation
    // algorithm might work.
    public void setComponentEvaluation( String componentName )
        throws FieldmlException
    {
        throw new BadFieldmlParameterException();
    }


    @Override
    void evaluateComponents( FieldParameters parameters, Value value )
        throws FieldmlException
    {
        if( ( value.indexValues == null ) || ( value.indexValues.length < getComponentCount() ) )
        {
            throw new BadFieldmlParameterException();
        }

        for( int i = 0; i < getComponentCount(); i++ )
        {
            value.indexValues[i] = components[i].evaluate( parameters );
        }
    }
}
