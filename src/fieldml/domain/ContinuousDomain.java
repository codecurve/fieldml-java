package fieldml.domain;

/**
  Continuous domains represent a real-valued domain with a lower bound, upper and lower bounds, or
  completely unbounded. Unlike discrete domains, it is not possible to obtain a 'complete set' of
  values for a continuous domain. However, the client may request a 'representative sample' of the
  domain with a resolution and clamping of their choice (including the ability to simply request
  the domain's extrema, subject to user-defined clamping). Thus, for instance, a client wishing to
  render a field over a continuous domain can obtain a finite number of parameters to pass to
  relevant fields, and use the output to derive an arbitarily fine (or coarse) geometry.
*/
public class ContinuousDomain
    extends Domain
{
    //These can be Double.NEGATIVE_INFINITY or Double.POSITIVE_INFINITY
    //Open intervals are not currently supported, but this is should be fairly trivial.
    //There seems no compelling reason to assert an 'orientation' on the domain, i.e. that min < max.
    //As such, they may be better named "value1" and "value2".
    private final double minValue;
    private final double maxValue;
    
    public ContinuousDomain( DomainManager manager, CompositeDomain parent, String name, double minValue, double maxValue )
    {
        super( manager, parent, name );
        
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void importInto( CompositeDomain parentDomain, String newName )
    {
        new ContinuousDomain( getManager(), parentDomain, newName, minValue, maxValue );
    }

}
