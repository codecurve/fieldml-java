package fieldml.field;

import fieldml.util.FieldmlObject;
import fieldml.util.FieldmlObjectManager;

public abstract class Field
    implements FieldmlObject
{

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


    public Field( FieldmlObjectManager<Field> manager, String name )
    {
        this.name = name;

        id = manager.add( this );

    }


    @Override
    public String toString()
    {
        return "Field " + name + " (" + id + ")";
    }


    public int getId()
    {
        return id;
    }


    public String getName()
    {
        return name;
    }
}
