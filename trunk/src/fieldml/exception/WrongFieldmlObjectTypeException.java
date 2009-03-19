package fieldml.exception;

import fieldml.FieldML;

public class WrongFieldmlObjectTypeException
    extends FieldmlException
{
    public WrongFieldmlObjectTypeException()
    {
        super( "Wrong FieldML object type", FieldML.ERR_WRONG_OBJECT_TYPE );
    }
}
