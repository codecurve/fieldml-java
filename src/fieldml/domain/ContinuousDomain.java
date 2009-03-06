package fieldml.domain;

import java.util.ArrayList;
import java.util.List;

import fieldml.util.FieldmlObjectManager;

/**
 * Continuous domains represent a real-valued domain with a lower bound, upper and lower bounds, or completely unbounded. Unlike
 * discrete domains, it is not possible to obtain a 'complete set' of values for a continuous domain. However, the client may
 * request a 'representative sample' of the domain with a resolution and clamping of their choice (including the ability to
 * simply request the domain's extrema, subject to user-defined clamping). Thus, for instance, a client wishing to render a
 * field over a continuous domain can obtain a finite number of parameters to pass to relevant fields, and use the output to
 * derive an arbitrarily fine (or coarse) geometry.
 */
public class ContinuousDomain
    extends Domain
{
    // These can be Double.NEGATIVE_INFINITY or Double.POSITIVE_INFINITY
    // Open intervals are not currently supported, but this is should be fairly trivial.
    // There seems no compelling reason to assert an 'orientation' on the domain, i.e. that min < max.
    private List<double[]> componentExtrema = new ArrayList<double[]>();


    public ContinuousDomain( FieldmlObjectManager<Domain> manager, String name )
    {
        super( manager, name );
    }


    public int addComponent( String componentName, double min, double max )
    {
        super.addComponent( componentName );

        double[] extrema = new double[2];
        extrema[0] = min;
        extrema[1] = max;
        
        componentExtrema.add( extrema );
        
        return componentExtrema.size();
    }
}
