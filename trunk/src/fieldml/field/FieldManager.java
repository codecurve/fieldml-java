package fieldml.field;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks all top level domains
 */
public class FieldManager
{
    private int generateNewUniqueId()
    {
        return fields.size() + ID_FUDGE;
    }

    
    // Field IDs will coincide with domain ids. Detecting the user-error of domain/field ID confusion
    // could be done with the right code, but is not really our problem.
    private final int ID_FUDGE = 10000;

    private final Map<Integer, Field> fields = new HashMap<Integer, Field>();

    private final Map<String, Integer> fieldIds = new HashMap<String, Integer>();


    public Field get( int id )
    {
        return fields.get( id );
    }


    public int getId( String name )
    {
        Integer id = fieldIds.get( name );

        if( id == null )
        {
            return 0;
        }

        return id;
    }

    
    public int add( Field field )
    {
        int id = generateNewUniqueId();
        fields.put( id, field );
        fieldIds.put( field.getName(), id );
        return id;
    }
    
}
