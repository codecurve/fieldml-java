package fieldml.field;

import fieldml.domain.Domain;
import fieldml.exception.FieldmlException;

public abstract class Parameter
{
    private final String name;

    private final Domain domain;


    public Parameter( String name, Domain domain )
    {
        this.name = name;
        this.domain = domain;
    }


    public String getName()
    {
        return name;
    }


    public Domain getDomain()
    {
        return domain;
    }


    public abstract void evaluate( FieldParameters inputParameters, int[] argumentIndexes, FieldParameters localParameters )
        throws FieldmlException;


    public abstract int getType();
}
