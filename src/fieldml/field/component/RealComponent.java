package fieldml.field.component;

import fieldml.field.FieldParameters;

public abstract class RealComponent
    extends Component
{
    public abstract double evaluate( FieldParameters parameters );
}
