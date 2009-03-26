package junit;

import junit.framework.TestCase;
import fieldml.FieldML;
import fieldml.implementation.FieldMLJava;

public class FieldMLJavaFieldTest
    extends TestCase
{
    private static final String testDomainName1 = "domain 1";
    private static final String testDomainName2 = "domain 2";
    
    private static final String testFieldName1 = "field 1";
    private static final String testFieldName2 = "field 2";
    private static final String testFieldName3 = "field 3";
    
    private static final String testComponentName1 = "component 1";

    private static final String testParameterName1 = "parameter 1";
    private static final String testParameterName2 = "parameter 2";
    private static final String testParameterName3 = "parameter 3";

    private static final double low1 = 0;
    private static final double high1 = 1;
    
    private static final int[] discrete1 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    private FieldML fieldml;

    private int discreteDomainId;
    
    private int continuousDomainId;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        fieldml = new FieldMLJava();
        
        discreteDomainId = fieldml.FieldML_CreateDiscreteDomain( testDomainName1 );
        fieldml.FieldML_AddDiscreteDomainComponent( discreteDomainId, testComponentName1, discrete1, discrete1.length );
        int count = fieldml.FieldML_GetDomainComponentCount( discreteDomainId );
        assertEquals( 1, count );
        
        continuousDomainId = fieldml.FieldML_CreateContinuousDomain( testDomainName2 );
        fieldml.FieldML_AddContinuousDomainComponent( continuousDomainId, testComponentName1, low1, high1 );
        count = fieldml.FieldML_GetDomainComponentCount( continuousDomainId );
        assertEquals( 1, count );
    }


    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }


    public void testFieldML_CreateField()
    {
        //Create a field on a continuous domain.
        int fieldId1 = fieldml.FieldML_CreateField( testFieldName1, continuousDomainId );
        assertTrue( fieldId1 >= 0 );
        
        //Create a field with the same name as an existing field.
        int err = fieldml.FieldML_CreateField( testFieldName1, continuousDomainId );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );
        
        //Create a field on a discrete domain.
        int fieldId2 = fieldml.FieldML_CreateField( testFieldName2, discreteDomainId );
        assertTrue( fieldId2 >= 0 );
        
        int fieldId3 = fieldml.FieldML_GetFieldId( testFieldName1 );
        assertEquals( fieldId1, fieldId3 );
        
        int domainId = fieldml.FieldML_GetValueDomain( fieldId1 );
        assertEquals( domainId, continuousDomainId );
    }
    
    
    public void testFieldML_AddInputParameter()
    {
        //Create a field on a continuous domain.
        int fieldId1 = fieldml.FieldML_CreateField( testFieldName1, continuousDomainId );
        assertTrue( fieldId1 >= 0 );
        
        //Add an input parameter.
        int index = fieldml.FieldML_AddInputParameter( fieldId1, testParameterName1, discreteDomainId );
        assertEquals( 0, index );
        
        //Add an input parameter with an unknown domain.
        int err = fieldml.FieldML_AddInputParameter( fieldId1, testParameterName2, discreteDomainId + 1000 );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
        
        //Add an input parameter with a duplicate name.
        err = fieldml.FieldML_AddInputParameter( fieldId1, testParameterName1, discreteDomainId );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );
        
        //Add an input parameter to a non-existant field.
        
        //Add an input parameter with an unknown domain.
        err = fieldml.FieldML_AddInputParameter( fieldId1 + 1000, testParameterName1, discreteDomainId );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
        
        //Add a second input parameter.
        int index2 = fieldml.FieldML_AddInputParameter( fieldId1, testParameterName2, discreteDomainId );
        assertEquals( 1, index2 );
    }
    
    
    public void testFieldML_AddDerivedParameter()
    {
        //Create a field on a continuous domain.
        int fieldId1 = fieldml.FieldML_CreateField( testFieldName1, continuousDomainId );
        assertTrue( fieldId1 >= 0 );
        int index1 = fieldml.FieldML_AddInputParameter( fieldId1, testParameterName1, discreteDomainId );
        assertTrue( index1 >= 0 );
        int index2 = fieldml.FieldML_AddInputParameter( fieldId1, testParameterName2, continuousDomainId );
        assertTrue( index2 >= 0 );
        
        //Create a second field on a discrete domain.
        int fieldId2 = fieldml.FieldML_CreateField( testFieldName2, discreteDomainId );
        assertTrue( fieldId2 >= 0 );
        int err = fieldml.FieldML_AddInputParameter( fieldId2, testParameterName1, continuousDomainId );
        assertTrue( err >= 0 );
        err = fieldml.FieldML_AddInputParameter( fieldId2, testParameterName2, continuousDomainId );
        assertTrue( err >= 0 );
        
        int[] indexes = new int[64];
        
        //Add a derived parameter with mismatched parameter domains for the field.
        indexes[0] = 0;
        indexes[1] = 0;
        err = fieldml.FieldML_AddDerivedParameter( fieldId1, testParameterName2, fieldId2, indexes );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );

        //Add a derived parameter with invalid indexes.
        indexes[0] = 0;
        indexes[1] = 5;
        err = fieldml.FieldML_AddDerivedParameter( fieldId1, testParameterName2, fieldId2, indexes );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );

        //Add a derived parameter for an invalid field.
        indexes[0] = 1;
        indexes[1] = 1;
        err = fieldml.FieldML_AddDerivedParameter( fieldId1 + 1000, testParameterName2, fieldId2, indexes );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );

        //Add a derived parameter with an invalid field id.
        indexes[0] = 1;
        indexes[1] = 1;
        err = fieldml.FieldML_AddDerivedParameter( fieldId1, testParameterName2, fieldId2 + 1000, indexes );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );

        //Add a derived parameter with a duplicate name.
        indexes[0] = 1;
        indexes[1] = 1;
        err = fieldml.FieldML_AddDerivedParameter( fieldId1, testParameterName1, fieldId2, indexes );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );

        //Add a derived parameter with correct parameters for the field.
        indexes[0] = 1;
        indexes[1] = 1;
        err = fieldml.FieldML_AddDerivedParameter( fieldId1, testParameterName3, fieldId2, indexes );
        assertEquals( 2, err );
    }
    
    public void testFieldML_GetDerivedParameter()
    {
        //Create a field on a continuous domain with derived fields.
        int fieldId1 = fieldml.FieldML_CreateField( testFieldName1, continuousDomainId );
        assertTrue( fieldId1 >= 0 );
        int index1 = fieldml.FieldML_AddInputParameter( fieldId1, testParameterName1, continuousDomainId );
        assertTrue( index1 >= 0 );
        
        //Create a second field on a discrete domain.
        int fieldId2 = fieldml.FieldML_CreateField( testFieldName2, discreteDomainId );
        assertTrue( fieldId2 >= 0 );
        int err = fieldml.FieldML_AddInputParameter( fieldId2, testParameterName1, continuousDomainId );
        assertTrue( err >= 0 );
        err = fieldml.FieldML_AddInputParameter( fieldId2, testParameterName2, continuousDomainId );
        assertTrue( err >= 0 );
        
        int[] indexes = new int[64];
        
        //Add two derived parameters.
        indexes[0] = index1;
        indexes[1] = index1;
        int index2 = fieldml.FieldML_AddDerivedParameter( fieldId1, testParameterName2, fieldId2, indexes );
        assertTrue( index2 >= 0 );

        //Get the field for an input parameter.
        err = fieldml.FieldML_GetDerivedParameterField( fieldId1, index1 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );

        //Get the field for a parameter of a non-existant field.
        err = fieldml.FieldML_GetDerivedParameterField( fieldId1 + 1000, index2 );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );

        //Get the field for a non-existant parameter.
        err = fieldml.FieldML_GetDerivedParameterField( fieldId1, index1 + 1000 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );
        
        //Get the field for a derived parameter.
        int id = fieldml.FieldML_GetDerivedParameterField( fieldId1, index2 );
        assertEquals( fieldId2, id );
        
        //Get the indexes for an input parameter.
        err = fieldml.FieldML_GetDerivedParameterArguments( fieldId1, index1, indexes );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );

        //Get the indexes for a parameter of a non-existant field.
        err = fieldml.FieldML_GetDerivedParameterArguments( fieldId1 + 1000, index2, indexes );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );

        //Get the indexes for a non-existant parameter.
        err = fieldml.FieldML_GetDerivedParameterArguments( fieldId1, index1 + 1000, indexes );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );
        
        //Get the indexes for a derived parameter.
        indexes[0] = index1 + 255;
        indexes[1] = index1 + 255;
        err = fieldml.FieldML_GetDerivedParameterArguments( fieldId1, index2, indexes );
        assertEquals( 2, err );
        assertEquals( index1, indexes[0] );
        assertEquals( index1, indexes[1] );
    }
    
    
    public void testFieldML_CreateMappedField()
    {
        //Create a mapped field on a continuous domain.
        int fieldId1 = fieldml.FieldML_CreateMappedField( testFieldName1, continuousDomainId );
        assertTrue( fieldId1 >= 0 );
        
        //Create a mapped field with the same name as an existing field.
        int err = fieldml.FieldML_CreateMappedField( testFieldName1, continuousDomainId );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );
        
        //Create a mapped field on a discrete domain.
        int fieldId2 = fieldml.FieldML_CreateMappedField( testFieldName2, discreteDomainId );
        assertTrue( fieldId2 >= 0 );
        
        int fieldId3 = fieldml.FieldML_GetFieldId( testFieldName1 );
        assertEquals( fieldId1, fieldId3 );
    }
    
    
    public void testFieldML_SetMappingParameter()
    {
        //Create a mapped field on a continuous domain.
        int fieldId1 = fieldml.FieldML_CreateMappedField( testFieldName1, continuousDomainId );
        assertTrue( fieldId1 >= 0 );

        //Set a mapping parameter with a continuous domain.
        int err = fieldml.FieldML_SetMappingParameter( fieldId1, continuousDomainId, 0 );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );
        
        //Set a mapping parameter with an invalid component index.
        err = fieldml.FieldML_SetMappingParameter( fieldId1, discreteDomainId, 1000 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );
        
        //Set a mapping parameter for a non-existant domain.
        err = fieldml.FieldML_SetMappingParameter( fieldId1 + 1000, discreteDomainId, 1000 );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );

        //Set a mapping parameter correctly.
        err = fieldml.FieldML_SetMappingParameter( fieldId1, discreteDomainId, 0 );
        assertEquals( FieldML.NO_ERROR, err );

        //Set a mapping parameter for a non-mapped field.
        int fieldId2 = fieldml.FieldML_CreateField( testFieldName2, continuousDomainId );
        assertTrue( fieldId2 >= 0 );
        err = fieldml.FieldML_SetMappingParameter( fieldId2, discreteDomainId, 0 );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );
        
        //Test the relevant getters.
        err = fieldml.FieldML_GetMappingParameterComponentIndex( fieldId2 );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );

        err = fieldml.FieldML_GetMappingParameterComponentIndex( fieldId1 );
        assertEquals( 0, err );

        err = fieldml.FieldML_GetMappingParameterDomain( fieldId2 );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );

        err = fieldml.FieldML_GetMappingParameterDomain( fieldId1 );
        assertEquals( discreteDomainId, err );
    }
    
    
    public void testFieldML_AssignComponentValues()
    {
        //Create a mapped field on a continuous domain.
        int fieldId1 = fieldml.FieldML_CreateMappedField( testFieldName1, continuousDomainId );
        assertTrue( fieldId1 >= 0 );
        int err = fieldml.FieldML_SetMappingParameter( fieldId1, discreteDomainId, 0 );
        assertEquals( FieldML.NO_ERROR, err );

        //Create a mapped field on a discrete domain.
        int fieldId2 = fieldml.FieldML_CreateMappedField( testFieldName2, discreteDomainId );
        assertTrue( fieldId2 >= 0 );
        err = fieldml.FieldML_SetMappingParameter( fieldId2, discreteDomainId, 0 );
        assertEquals( FieldML.NO_ERROR, err );

        //Create a non-mapped field.
        int fieldId3 = fieldml.FieldML_CreateField( testFieldName3, continuousDomainId );
        assertTrue( fieldId3 >= 0 );

        int[] iValues = { 2, 4, 6, 8 };
        double[] dValues = { 1.5, 2.5, 3.5 };
        
        //Assign index values to a real-valued field.
        err = fieldml.FieldML_AssignDiscreteComponentValues( fieldId1, 1, iValues );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );
        
        //Assign index values to a non-existant field.
        err = fieldml.FieldML_AssignDiscreteComponentValues( fieldId1 + 1000, 1, iValues );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
        
        //Assign index values to a non-mapped field.
        err = fieldml.FieldML_AssignDiscreteComponentValues( fieldId3, 1, iValues );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );
        
        //Assign real values to an index-valued field.
        err = fieldml.FieldML_AssignContinuousComponentValues( fieldId2, 1, dValues );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );
        
        //Assign real values to a non-existant field.
        err = fieldml.FieldML_AssignContinuousComponentValues( fieldId2 + 1000, 1, dValues );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
        
        //Assign real values to a non-mapped field.
        err = fieldml.FieldML_AssignContinuousComponentValues( fieldId3, 1, dValues );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );
        
        //Assign index values.
        err = fieldml.FieldML_AssignDiscreteComponentValues( fieldId2, 1, iValues );
        assertEquals( FieldML.NO_ERROR, err );

        //Assign real values.
        err = fieldml.FieldML_AssignContinuousComponentValues( fieldId1, 1, dValues );
        assertEquals( FieldML.NO_ERROR, err );
    }
    
    
    public void testFieldML_GetComponentValues()
    {
        int[] iValues = { 2, 4, 6, 8 };
        double[] dValues = { 1.5, 2.5, 3.5 };
        
        int[] testIValues = new int[10];
        double[] testDValues = new double[10];
        
        //Create a mapped field on a continuous domain.
        int fieldId1 = fieldml.FieldML_CreateMappedField( testFieldName1, continuousDomainId );
        assertTrue( fieldId1 >= 0 );
        int err = fieldml.FieldML_SetMappingParameter( fieldId1, discreteDomainId, 0 );
        assertEquals( FieldML.NO_ERROR, err );
        err = fieldml.FieldML_AssignContinuousComponentValues( fieldId1, 1, dValues );
        assertEquals( FieldML.NO_ERROR, err );

        //Create a mapped field on a discrete domain.
        int fieldId2 = fieldml.FieldML_CreateMappedField( testFieldName2, discreteDomainId );
        assertTrue( fieldId2 >= 0 );
        err = fieldml.FieldML_SetMappingParameter( fieldId2, discreteDomainId, 0 );
        assertEquals( FieldML.NO_ERROR, err );
        err = fieldml.FieldML_AssignDiscreteComponentValues( fieldId2, 1, iValues );
        assertEquals( FieldML.NO_ERROR, err );

        //Create a non-mapped field.
        int fieldId3 = fieldml.FieldML_CreateField( testFieldName3, continuousDomainId );
        assertTrue( fieldId3 >= 0 );

        //Get index values from a real-valued field.
        err = fieldml.FieldML_GetDiscreteComponentValues( fieldId1, 1, testIValues );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );

        //Get index values from a non-existant field.
        err = fieldml.FieldML_GetDiscreteComponentValues( fieldId1 + 1000, 1, testIValues );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );

        //Get index values from a non-mapped field.
        err = fieldml.FieldML_GetDiscreteComponentValues( fieldId3, 1, testIValues );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );

        //Get real values from an index-valued field.
        err = fieldml.FieldML_GetContinuousComponentValues( fieldId2, 1, testDValues );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );

        //Get real values from a non-existant field.
        err = fieldml.FieldML_GetContinuousComponentValues( fieldId1 + 1000, 1, testDValues );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );

        //Get real values from a non-mapped field.
        err = fieldml.FieldML_GetContinuousComponentValues( fieldId3, 1, testDValues );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );
        
        //Get real values.
        err = fieldml.FieldML_GetContinuousComponentValues( fieldId1, 1, testDValues );
        assertEquals( FieldML.NO_ERROR, err );
        for( int i = 0; i < fieldml.FieldML_GetDomainComponentCount( continuousDomainId ); i++ )
        {
            assertEquals( dValues[i], testDValues[i] );
        }
        
        //Get index values.
        err = fieldml.FieldML_GetDiscreteComponentValues( fieldId2, 1, testIValues );
        assertEquals( FieldML.NO_ERROR, err );
        for( int i = 0; i < fieldml.FieldML_GetDomainComponentCount( discreteDomainId ); i++ )
        {
            assertEquals( iValues[i], testIValues[i] );
        }
}
}
