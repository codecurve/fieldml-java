package fieldml.field.library;

import fieldml.domain.Domain;
import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.field.Field;
import fieldml.field.FieldParameters;
import fieldml.field.InputParameter;
import fieldml.field.RealField;
import fieldml.util.FieldmlObjectManager;
import fieldml.value.Value;

/**
 * Source code: <code>
  <field id="library::bilinear_lagrange" value_domain="library::bilinear_interpolation_parameters">
    <parameter id="xi" domain="library::unit_square" />
    
    <evaluate_component id="node1">
      <multiply>
        <subtract>
          <constant_value value="1" />
          <parameter_value id="xi" component="xi1" />
        </subtract>
        <subtract>
          <constant_value value="1" />
          <parameter_value id="xi" component="xi2" />
        </subtract>
      </multiply>
    </evaluate_component>

    <evaluate_component id="node2">
      <multiply>
        <parameter_value id="xi" component="xi1" />
        <subtract>
          <constant_value value="1" />
          <parameter_value id="xi" component="xi2" />
        </subtract>
      </multiply>
    </evaluate_component>

    <evaluate_component id="node3">
      <multiply>
        <subtract>
          <constant_value value="1" />
          <parameter_value id="xi" component="xi1" />
        </subtract>
        <parameter_value id="xi" component="xi2" />
      </multiply>
    </evaluate_component>

    <evaluate_component id="node4">
      <multiply>
        <parameter_value id="xi" component="xi1" />
        <parameter_value id="xi" component="xi2" />
      </multiply>
    </evaluate_component>
  </field>
</code>
 */
public class BilinearLagrange
    extends Field
    implements RealField
{
    public BilinearLagrange( FieldmlObjectManager<Field> manager, FieldmlObjectManager<Domain> domainManager )
        throws FieldmlException
    {
        super( manager, "library::bilinear_lagrange", domainManager.get( "library::bilinear_interpolation_parameters" ) );

        addParameter( new InputParameter( "xi", domainManager.get( "library::unit_square" ), 0 ) );
    }


    @Override
    public void evaluate( FieldParameters parameters, int[] parameterIndexes, Value value )
        throws FieldmlException
    {
        if( parameterIndexes.length < 1 )
        {
            throw new BadFieldmlParameterException();
        }
        Value parameter0 = parameters.values.get( parameterIndexes[0] );
        
        if( ( parameter0.realValues == null ) || ( parameter0.realValues.length < 2 ) ||
            ( value.realValues == null ) || ( value.realValues.length < 4 ) )
        {
            throw new BadFieldmlParameterException();
        }
        
        double xi1 = parameter0.realValues[0];
        double xi2 = parameter0.realValues[1];

        value.realValues[0] = ( 1 - xi1 ) * ( 1 - xi2 );
        value.realValues[1] = xi1 * ( 1 - xi2 );
        value.realValues[2] = ( 1 - xi1 ) * xi2;
        value.realValues[3] = xi1 * xi2;
    }
}
