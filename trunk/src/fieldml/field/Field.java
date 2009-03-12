package fieldml.field;

import java.util.ArrayList;
import java.util.Arrays;

import fieldml.domain.DiscreteDomain;
import fieldml.domain.Domain;
import fieldml.util.FieldmlObject;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

public abstract class Field
    implements FieldmlObject
{
    private Domain valueDomain;

    int indexParameter;

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

    /**
     * The domains for each input parameter. These will not necessarily be the same as the domains of fieldParameters
     */
    private final ArrayList<Domain> parameterDomains;

    /**
     * Fields used to evaluate derived parameters.
     */
    private final ArrayList<Field> parameterEvaluationFields;

    /**
     * parameter indexes required for evaluating the fields used for derived parameters.
     * 
     * Using a 'third party' to determine parameter indexes allows us to re-use the fieldParameters object to evaluate all
     * derived parameters belonging to this field.
     */
    private final ArrayList<int[]> parameterEvaluationIndexes;

    /**
     * The cache used to store the values of all this fields parameters, and thence to evaluate its components.
     */
    private final FieldParameters fieldParameters;


    public Field( FieldmlObjectManager<Field> manager, String name, Domain valueDomain )
    {
        this.name = name;
        this.valueDomain = valueDomain;

        id = manager.add( this );

        parameterDomains = new ArrayList<Domain>();

        parameterEvaluationFields = new ArrayList<Field>();
        parameterEvaluationIndexes = new ArrayList<int[]>();

        fieldParameters = new FieldParameters();
        
        indexParameter = -1;
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


    public int getComponentCount()
    {
        return valueDomain.getComponentCount();
    }


    int getComponentIndex( String componentName )
    {
        return valueDomain.getComponentId( componentName );
    }


    public Domain getValueDomain()
    {
        return valueDomain;
    }


    private int setIndexDomain( int parameterIndex )
    {
        if( indexParameter >= 0 )
        {
            // ERROR only one index domain is allowed
            return -1;
        }

        Domain domain = fieldParameters.values.get( parameterIndex ).domain;
        if( !( domain instanceof DiscreteDomain ) )
        {
            // ERROR index domains must be discrete
            return -1;
        }

        indexParameter = parameterIndex;

        return 0;
    }


    public int addInputParameter( Domain domain, boolean isIndexDomain )
    {
        parameterDomains.add( domain );

        fieldParameters.addDomain( domain );
        parameterEvaluationFields.add( null );
        parameterEvaluationIndexes.add( null );

        if( isIndexDomain )
        {
            return setIndexDomain( fieldParameters.count - 1 );
        }

        return 0;
    }


    public int addDerivedParameter( Field parameterField, int[] parameterIndexes, boolean isIndexDomain )
    {
        for( int i = 0; i < parameterIndexes.length; i++ )
        {
            if( parameterIndexes[i] >= fieldParameters.count )
            {
                // ERROR derived parameter references an unknown parameter.
                // NOTE although a derived parameter could forward-reference a parameter, the only
                // way to prevent circular dependancies is to insist that derived parameters only
                // refer to already defined ones.
                return -1;
            }
        }

        fieldParameters.addDomain( parameterField.valueDomain );
        parameterEvaluationFields.add( parameterField );
        parameterEvaluationIndexes.add( Arrays.copyOf( parameterIndexes, parameterIndexes.length ) );

        if( isIndexDomain )
        {
            return setIndexDomain( fieldParameters.count - 1 );
        }

        return 0;
    }


    public int evaluate( FieldParameters parameters, int[] parameterIndexes, Value value )
    {
        for( int i = 0; i < fieldParameters.count; i++ )
        {
            Field parameterField = parameterEvaluationFields.get( i );
            Value parameterValue = fieldParameters.values.get( i );

            if( parameterField == null )
            {
                fieldParameters.values.set( i, value );
            }
            else
            {
                parameterField.evaluate( parameters, parameterEvaluationIndexes.get( i ), parameterValue );
            }
        }
        
        return evaluateComponents( fieldParameters, value );
    }


    public int getParameterCount()
    {
        return parameterDomains.size();
    }


    public int getParameterDomainIds( int[] domainIds )
    {
        if( domainIds.length < parameterDomains.size() )
        {
            // ERROR
            return -1;
        }

        for( int i = 0; i < parameterDomains.size(); i++ )
        {
            domainIds[i] = parameterDomains.get( i ).getId();
        }

        return 0;
    }


    abstract int evaluateComponents( FieldParameters parameters, Value value );
}
