package fieldml.field.library;

import fieldml.domain.Domain;
import fieldml.field.Field;
import fieldml.field.FieldParameters;
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
    {
        super( manager, "library::bilinear_lagrange", domainManager.get( "library::bilinear_interpolation_parameters" ) );

        addParameterDomain( domainManager.get( "library::unit_square" ) );
    }


    @Override
    public int evaluate( FieldParameters parameters, int[] parameterIndexes, Value value )
    {
        double xi1 = parameters.values.get( parameterIndexes[0] ).realValues[0];
        double xi2 = parameters.values.get( parameterIndexes[0] ).realValues[1];

        value.realValues[0] = ( 1 - xi1 ) * ( 1 - xi2 );
        value.realValues[1] = xi1 * ( 1 - xi2 );
        value.realValues[2] = ( 1 - xi1 ) * xi2;
        value.realValues[3] = xi1 * xi2;

        return 0;
    }

}
