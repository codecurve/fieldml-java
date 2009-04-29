package fieldml.field.component;

import fieldml.field.FieldValues;

public abstract class IndexComponent
    extends Component
{
    public abstract int evaluate( FieldValues parameters );
}
