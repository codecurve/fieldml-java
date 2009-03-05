package fieldml.field;

import java.util.HashMap;
import java.util.Map;

public abstract class Field
{
    //Field IDs will coincide with domain ids. Detecting the user-error of domain/field ID confusion
    //could be done with the right code, but is not really our problem.
    private static final int ID_FUDGE = 10000;

    private static final char FIELD_DELIMITER = '.';

    private static final Map<Integer, Field> fields;

    private static final Map<String, Integer> fieldIds;

    static
    {
        fields = new HashMap<Integer, Field>();

        fieldIds = new HashMap<String, Integer>();
    }


    public static Field get( int id )
    {
        return fields.get( id );
    }


    public static int getId( String name )
    {
        Integer id = fieldIds.get( name );
        
        if( id == null )
        {
            return 0;
        }
        
        return id;
    }

    /**
     * A globally unique integer identifying the field, useful for internal (inter-process) and external (client-server)
     * communication. In order to remain globally unique, this id number cannot be user-supplied. Fields can be imported from
     * external sources, and can therefore have id numbers which are not known in advance by the user of the API when creating
     * their own fields.
     */
    private final int id;

    /**
     * A locally unique string.
     */
    private final String name;

    private final CompositeField parent;


    public Field( CompositeField parent, String name )
    {
        this.name = name;

        id = fields.size() + ID_FUDGE;

        fields.put( id, this );
        fieldIds.put( getFullName(), id );

        this.parent = parent;
        if( parent != null )
        {
            parent.components.put( name, this );
        }
    }


    public String toString()
    {
        return "Field " + getFullName() + " (" + id + ")";
    }


    public String getFullName()
    {
        return parent.getFullName() + FIELD_DELIMITER + name;
    }


    public CompositeField getParent()
    {
        return parent;
    }


    public int getId()
    {
        return id;
    }
}
