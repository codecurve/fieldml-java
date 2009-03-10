package fieldml.field.component;

import fieldml.field.FieldParameters;

public abstract class IndexComponent
    extends Component
{
    public abstract int evaluate( FieldParameters parameters );
}
