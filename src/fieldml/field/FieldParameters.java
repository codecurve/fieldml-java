package fieldml.field;

import java.util.ArrayList;

import fieldml.domain.Domain;
import fieldml.util.FieldmlObject;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

/**
 * This class essentially maintains a list of dynamic casts, allowing clients to access values by index without having to use
 * instanceof or casting.
 */
public class FieldParameters
    implements FieldmlObject
{
    public final ArrayList<Value> values;
    
    public int count;

    private final int id;


    public FieldParameters( FieldmlObjectManager<FieldParameters> manager, ArrayList<Domain> domains )
    {
        values = new ArrayList<Value>();

        for( Domain parameterDomain : domains )
        {
            values.add( new Value( parameterDomain ) );
        }
        
        this.id = manager.add( this );
    }
    
    
    FieldParameters()
    {
        values = new ArrayList<Value>();

        id = 0;
    }


    void addDomain( Domain domain )
    {
        values.add( new Value( domain ) );
    }


    @Override
    public int getId()
    {
        return id;
    }


    @Override
    public String getName()
    {
        // Cache names are never visible to the API user.
        return toString();
    }
}
