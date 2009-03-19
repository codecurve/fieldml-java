package fieldml;


/**
 * In some far-off future, these could all be JNI calls to a FieldML library written in C/C++.
 * Because the API is to be called from Fortran as well, only primitive types can be used as parameters.
 * 
 */
public interface FieldML
{
    // Error codes. Because some FieldML API functions return ids, valid ids
    // are defined to be strictly positive, and error codes are defined to be
    // strictly negative, with the value 0 being used to indicate success for
    // calls that do not return ids.
    public static final int NO_ERROR = 0;
    public static final int ERR_GENERIC_ERROR = -1;
    public static final int ERR_NO_SUCH_OBJECT = -2;
    public static final int ERR_WRONG_OBJECT_TYPE = -3;
    public static final int ERR_BAD_PARAMETER = -4;
    
    //Domain methods
    public int FieldML_CreateContinuousDomain( String name );

    public int FieldML_CreateDiscreteDomain( String name );

    public int FieldML_GetDomainId( String originalDomainName );
    
    public int FieldML_GetDomainName( int domainId, char[] name );

    public int FieldML_AddContinuousDomainComponent( int domainId, String componentName, double min, double max );

    public int FieldML_AddDiscreteDomainComponent( int domainId, String componentName, int[] values, int count );
    
    public int FieldML_GetDomainComponentCount( int domainId );
    
    public int FieldML_GetDomainComponentName( int domainId, int componentIndex, char[] name );
    
    public int FieldML_GetDiscreteDomainComponentValueCount( int domainId, int componentIndex );
    
    public int FieldML_GetDiscreteDomainComponentValues( int domainId, int componentIndex, int[] values );
    
    public int FieldML_GetContinuousDomainComponentExtrema( int domainId, int componentIndex, double[] values );
    
    //Field methods
    public int FieldML_GetInputParameterCount( int fieldId );
    
    public int FieldML_GetInputParameterDomains( int fieldId, int[] domainIds );
    
    public int FieldML_GetInputParameterDomain( int fieldId, int parameterIndex );
    
    public int FieldML_GetParameterName( int fieldId, int parameterIndex, char[] name );
    
    //Ordinary fields
    public int FieldML_CreateField( String name, int valueDomainId );
    
    public int FieldML_GetFieldName( int fieldId, char[] name );
    
    public int FieldML_AddInputParameter( int fieldId, String parameterName, int domainId );
    
    public int FieldML_AddDerivedParameter( int fieldId, String parameterName, int mappingFieldId, int[] argumentIndexes );
    
    public int FieldML_GetParameterCount( int fieldId );
    
    public int FieldML_GetDerivedParameterIndexes( int fieldId, int[] parameterIndexes );
    
    public int FieldML_GetInputParameterIndexes( int fieldId, int[] parameterIndex );
    
    public int FieldML_GetDerivedParameterField( int fieldId, int derivedParameterIndex );
    
    public int FieldML_GetDerivedParameterArguments( int fieldId, int derivedParameterIndex, int[] argumentIndexes );
    
    //Mapped fields
    public int FieldML_CreateMappedField( String name, int valueDomainId );

    public int FieldML_SetMappingParameter( int fieldId, int domainId, int componentIndex );
    
    public int FieldML_GetMappingParameterDomain( int fieldId );
    
    public int FieldML_GetMappingParameterComponentIndex( int fieldId );
        
    public int FieldML_AssignDiscreteComponentValues( int fieldId, int parameterValue, int[] componentValues );
    
    public int FieldML_AssignContinuousComponentValues( int fieldId, int parameterValue, double[] componentValues );
    
    public int FieldML_GetDiscreteComponentValues( int fieldId, int parameterValue, int[] componentValues );
    
    public int FieldML_GetContinuousComponentValues( int fieldId, int parameterValue, double[] componentValues );
    
    //Cache methods. These could be extended to permit multi-member caches.
    public int FieldML_CreateCache( int[] domainIds, int parameterCount );

    public int FieldML_DestroyCache( int cacheId );
    
    public int FieldML_SetContinousCacheValues( int cacheId, int domainNumber, double[] values );
    
    public int FieldML_SetDiscreteCacheValues( int cacheId, int domainNumber, int[] values ); 
    
    //Field-evaluation methods
    public int FieldML_EvaluateDiscreteField( int fieldId, int cacheId, int[] values );
    
    public int FieldML_EvaluateContinuousField( int fieldId, int cacheId, double[] values );
}
