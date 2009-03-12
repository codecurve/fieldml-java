package fieldml;


/**
 * In some far-off future, these could all be JNI calls to a FieldML library written in C/C++.
 * Because the API is to be called from Fortran as well, only primitive types can be used as parameters.
 * 
 */
public interface FieldML
{
    //Domain methods
    public int FieldML_CreateContinuousDomain( String name );

    public int FieldML_CreateDiscreteDomain( String name );

    public int FieldML_GetDomainId( String originalDomainName );

    public int FieldML_AddContinuousDomainComponent( int domainId, String componentName, double min, double max );

    public int FieldML_AddDiscreteDomainComponent( int domainId, String componentName, int start, int count, int[] values );
    
    //Field methods
    public int FieldML_CreateField( String name, int valueDomainId );
    
    public int FieldML_GetFieldParameterCount( int fieldId );
    
    public int FieldML_GetFieldParameterDomainIds( int fieldId, int[] domainIds );
    
    public int FieldML_AssignDiscreteComponentValues( int fieldId, int parameterValue, int[] componentValues );
    
    public int FieldML_AssignContinuousComponentValues( int fieldId, int parameterValue, double[] componentValues );
    
    public int FieldML_AddInputParameter( int fieldId, int domainId, boolean isIndexParameter );
    
    // Although the code only allows single-parameter mapping fields (i.e. domain -> domain), representing
    // the idea of changing a parameter's domain, it could be changed to allow multi-domain -> domain fields.
    // However, this would mean that a single mapped_parameter tag could induce any number of actual parameters,
    // as the mapping field itself could use a mapped_parameter, and so on.
    public int FieldML_AddDerivedParameter( int fieldId, int mappingFieldId, int[] parameterIndexes, boolean isIndexParameter );
    
    //Cache methods. These could be extended to permit multi-member caches.
    public int FieldML_CreateCache( int[] domainIds, int parameterCount );

    public int FieldML_DestroyCache( int cacheId );
    
    public int FieldML_SetContinousCacheValues( int cacheId, int domainNumber, double[] values );
    
    public int FieldML_SetDiscreteCacheValues( int cacheId, int domainNumber, int[] values ); 
    
    //Field-evaluation methods
    public int FieldML_EvaluateDiscreteField( int fieldId, int cacheId, int[] values );
    
    public int FieldML_EvaluateContinuousField( int fieldId, int cacheId, double[] values );
}
