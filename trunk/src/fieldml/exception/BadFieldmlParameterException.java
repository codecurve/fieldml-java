package fieldml.exception;

import fieldml.FieldML;

public class BadFieldmlParameterException
    extends FieldmlException
{
    public BadFieldmlParameterException()
    {
        super( "Bad parameter", FieldML.ERR_BAD_PARAMETER );
    }
}
