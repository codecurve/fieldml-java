package fieldml.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks all top level fieldml objects
 */
public class FieldmlObjectManager<T extends FieldmlObject> 
{
    private static final int ID_FUDGE = 10000;

    private final Map<Integer, T> objects = new HashMap<Integer, T>();

    private final Map<String, Integer> objectIds = new HashMap<String, Integer>();


    public T get( int id )
    {
        return objects.get( id );
    }
    
    
    public T get( String name )
    {
        return objects.get( objectIds.get( name ) );
    }


    public int getId( String name )
    {
        Integer id = objectIds.get( name );

        if( id == null )
        {
            return 0;
        }

        return id;
    }


    private int generateNewUniqueId()
    {
        return objects.size() + ID_FUDGE;
    }


    public int add( T object )
    {
        int id = generateNewUniqueId();
        objects.put( id, object );
        objectIds.put( object.getName(), id );
        return id;
    }


    public int remove( int id )
    {
        FieldmlObject object = objects.get( id );
        
        if( object != null )
        {
            objects.remove( id );
            objectIds.remove( object.getName() );
        }
        return 0;
    }

}
