package fieldml.field.component;

import fieldml.field.FieldValues;

public abstract class RealComponent
    extends Component
{
    public abstract double evaluate( FieldValues parameters );
}
