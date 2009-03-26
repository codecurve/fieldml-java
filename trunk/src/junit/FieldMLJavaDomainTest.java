package junit;

import junit.framework.TestCase;
import fieldml.FieldML;
import fieldml.implementation.FieldMLJava;

public class FieldMLJavaDomainTest
    extends TestCase
{
    private static final String testDomainName1 = "domain 1";
    private static final String testDomainName2 = "domain 2";
    
    private static final String testComponentName1 = "component 1";
    private static final String testComponentName2 = "component 2";
    
    private static final double low1 = 0;
    private static final double high1 = 1;
    
    private static final double low2 = -Math.PI;
    private static final double high2 = Math.PI;
    
    private static final int[] discrete1 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private static final int[] discrete2 = { 2, 4, 8, 16, 32, 64 };

    private FieldML fieldml;


    protected void setUp()
        throws Exception
    {
        super.setUp();

        fieldml = new FieldMLJava();
    }


    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }


    public void testFieldML_CreateContinuousDomain()
    {
        //Create a continuous domain.
        int id = fieldml.FieldML_CreateContinuousDomain( testDomainName1 );
        assertTrue( id >= 0 );
        //assertEquals( "test name", fieldml.FieldML_GetDomainName( id ) );
        
        //Create a continous domain with the same name as an existing domain.
        int secondId = fieldml.FieldML_CreateContinuousDomain( testDomainName1 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, secondId );
        
        //Create a discrete domain with the same name as an existing domain.
        int thirdId = fieldml.FieldML_CreateDiscreteDomain( testDomainName1 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, thirdId );
        
        //Create a second continuous domain.
        int fourthId = fieldml.FieldML_CreateContinuousDomain( testDomainName2 );
        assertTrue( fourthId >= 0 );
        
        int domainId2 = fieldml.FieldML_GetDomainId( testDomainName1 );
        assertEquals( id, domainId2 );
    }
    
    
    public void testFieldML_AddContinousDomainComponent()
    {
        //Create a continuous domain.
        int domainId1 = fieldml.FieldML_CreateContinuousDomain( testDomainName1 );
        assertTrue( domainId1 >= 0 );
        
        //Add a new component.
        int index1 = fieldml.FieldML_AddContinuousDomainComponent( domainId1, testComponentName1, low1, high1 );
        assertEquals( 0, index1 );

        //Add a new component with the same name as the first component.
        int index2 = fieldml.FieldML_AddContinuousDomainComponent( domainId1, testComponentName1, low1, high1 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, index2 );

        //Add a new component with a different name from the first component.
        int index3 = fieldml.FieldML_AddContinuousDomainComponent( domainId1, testComponentName2, low2, high2 );
        assertEquals( 1, index3 );
        
        //Add a continous component to a discrete domain.
        int domainId2 = fieldml.FieldML_CreateDiscreteDomain( testDomainName2 );
        assertTrue( domainId2 >= 0 );
        int err = fieldml.FieldML_AddContinuousDomainComponent( domainId2, testComponentName1, low1, high1 );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );

        //Add a continuous component to a non-existant domain.
        err = fieldml.FieldML_AddContinuousDomainComponent( domainId2 + 1000, testComponentName1, low1, high1 );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
    }
    
    
    public void testFieldML_GetContinuousDomainComponentExtrema()
    {
        //Create a continuous domain.
        int domainId1 = fieldml.FieldML_CreateContinuousDomain( testDomainName1 );
        assertTrue( domainId1 >= 0 );
        
        //Add a new component.
        int index1 = fieldml.FieldML_AddContinuousDomainComponent( domainId1, testComponentName1, low1, high1 );
        assertEquals( 0, index1 );

        //Add a new component, with a different range.
        int index2 = fieldml.FieldML_AddContinuousDomainComponent( domainId1, testComponentName2, low2, high2 );
        assertEquals( 1, index2 );

        double[] values = new double[2];
        
        //Get the range for the first component.
        int err = fieldml.FieldML_GetContinuousDomainComponentExtrema( domainId1, index1, values );
        assertEquals( FieldML.NO_ERROR, err );
        assertEquals( low1, values[0] );
        assertEquals( high1, values[1] );
        
        err = fieldml.FieldML_GetContinuousDomainComponentExtrema( domainId1, index1 + 100, values );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );

        //Get the range for the second component.
        err = fieldml.FieldML_GetContinuousDomainComponentExtrema( domainId1, index2, values );
        assertEquals( FieldML.NO_ERROR, err );
        assertEquals( low2, values[0] );
        assertEquals( high2, values[1] );

        //Get the range for a discrete domain component.
        int domainId2 = fieldml.FieldML_CreateDiscreteDomain( testDomainName2 );
        assertTrue( domainId2 >= 0 );
        index1 = fieldml.FieldML_AddDiscreteDomainComponent( domainId2, testComponentName1, discrete1, 2);
        assertEquals( FieldML.NO_ERROR, err );
        err = fieldml.FieldML_GetContinuousDomainComponentExtrema( domainId2, index1, values );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );
        
        //Get the extrema for a non-existant domain.
        err = fieldml.FieldML_GetContinuousDomainComponentExtrema( domainId2 + 1000, index1, values );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
    }
    
    
    public void testFieldML_CreateDiscreteDomain()
    {
        //Create a discrete domain.
        int id = fieldml.FieldML_CreateDiscreteDomain( testDomainName1 );
        assertTrue( id >= 0 );
        //assertEquals( "test name", fieldml.FieldML_GetDomainName( id ) );
        
        //Create a discrete domain with the same name as an existing domain.
        int secondId = fieldml.FieldML_CreateDiscreteDomain( testDomainName1 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, secondId );
        
        //Create a continuous domain with the same name as an existing domain.
        int thirdId = fieldml.FieldML_CreateContinuousDomain( testDomainName1 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, thirdId );
        
        //Create a second discrete domain.
        int fourthId = fieldml.FieldML_CreateDiscreteDomain( testDomainName2 );
        assertTrue( fourthId >= 0 );
        
        //Get the domain id for an existant domain.
        int domainId2 = fieldml.FieldML_GetDomainId( testDomainName1 );
        assertEquals( id, domainId2 );
    }
    
    public void testFieldML_AddDiscreteDomainComponent()
    {
        //Create a continuous domain.
        int domainId1 = fieldml.FieldML_CreateDiscreteDomain( testDomainName1 );
        assertTrue( domainId1 >= 0 );
        
        //Add a new component.
        int index1 = fieldml.FieldML_AddDiscreteDomainComponent( domainId1, testComponentName1, discrete1, discrete1.length );
        assertEquals( 0, index1 );

        //Add a new component with the same name as the first component.
        int index2 = fieldml.FieldML_AddDiscreteDomainComponent( domainId1, testComponentName1,discrete1, discrete1.length );
        assertEquals( FieldML.ERR_BAD_PARAMETER, index2 );

        //Add a new component with a different name from the first component.
        int index3 = fieldml.FieldML_AddDiscreteDomainComponent( domainId1, testComponentName2, discrete2, discrete2.length );
        assertEquals( 1, index3 );
        
        //Add a discrete component to a continuous domain.
        int domainId2 = fieldml.FieldML_CreateContinuousDomain( testDomainName2 );
        assertTrue( domainId2 >= 0 );
        int err = fieldml.FieldML_AddDiscreteDomainComponent( domainId2, testComponentName1, discrete1, discrete1.length );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, err );

        //Add a discrete component to a non-existant domain.
        err = fieldml.FieldML_AddContinuousDomainComponent( domainId2 + 1000, testComponentName1, low1, high1 );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
    }


    public void testFieldML_GetDiscreteDomainComponentValues()
    {
        //Create a discrete domain.
        int domainId1 = fieldml.FieldML_CreateDiscreteDomain( testDomainName1 );
        assertTrue( domainId1 >= 0 );
        
        //Add a new component.
        int index1 = fieldml.FieldML_AddDiscreteDomainComponent( domainId1, testComponentName1, discrete1, discrete1.length );
        assertEquals( 0, index1 );

        //Add a new component, with a different range.
        int index2 = fieldml.FieldML_AddDiscreteDomainComponent( domainId1, testComponentName2, discrete2, discrete2.length );
        assertEquals( 1, index2 );

        int[] values = new int[1024];
        
        //Get the values for the first component.
        int count = fieldml.FieldML_GetDiscreteDomainComponentValueCount( domainId1, index1 );
        assertEquals( discrete1.length, count );
        count = fieldml.FieldML_GetDiscreteDomainComponentValues( domainId1, index1, values );
        assertEquals( discrete1.length, count );
        for( int i = 0; i < count; i++)
        {
            assertEquals( discrete1[i], values[i] );
        }

        //Get the values for the second component.
        count = fieldml.FieldML_GetDiscreteDomainComponentValueCount( domainId1, index2 );
        assertEquals( discrete2.length, count );
        count = fieldml.FieldML_GetDiscreteDomainComponentValues( domainId1, index2, values );
        assertEquals( discrete2.length, count );
        for( int i = 0; i < count; i++)
        {
            assertEquals( discrete2[i], values[i] );
        }
        
        int err = fieldml.FieldML_GetDiscreteDomainComponentValueCount( domainId1, index2 + 100 );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );
        err = fieldml.FieldML_GetDiscreteDomainComponentValues( domainId1, index2 + 100, values );
        assertEquals( FieldML.ERR_BAD_PARAMETER, err );

        //Get the values for a continous domain component.
        int domainId2 = fieldml.FieldML_CreateContinuousDomain( testDomainName2 );
        assertTrue( domainId2 >= 0 );
        index1 = fieldml.FieldML_AddContinuousDomainComponent( domainId2, testComponentName1, low1, high1 );
        assertEquals( 0, index1 );
        count = fieldml.FieldML_GetDiscreteDomainComponentValues( domainId2, index1, values );
        assertEquals( FieldML.ERR_WRONG_OBJECT_TYPE, count );

        //Get the values for a component of a non-existant domain.
        err = fieldml.FieldML_GetDiscreteDomainComponentValues( domainId2 + 1000, index1, values );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
    }
    
    
    public void testFieldML_GetDomainComponentCount()
    {
        //Create a continuous domain.
        int domainId1 = fieldml.FieldML_CreateContinuousDomain( testDomainName1 );
        assertTrue( domainId1 >= 0 );
        
        int count = fieldml.FieldML_GetDomainComponentCount( domainId1 );
        assertEquals( 0, count );
        
        //Add a new component.
        int index1 = fieldml.FieldML_AddContinuousDomainComponent( domainId1, testComponentName1, low1, high1 );
        assertEquals( 0, index1 );

        count = fieldml.FieldML_GetDomainComponentCount( domainId1 );
        assertEquals( 1, count );
        
        //Add a new component, with a different range.
        int index2 = fieldml.FieldML_AddContinuousDomainComponent( domainId1, testComponentName2, low2, high2 );
        assertEquals( 1, index2 );

        count = fieldml.FieldML_GetDomainComponentCount( domainId1 );
        assertEquals( 2, count );

        //Get the component count for a non-existant domain.
        int err = fieldml.FieldML_GetDomainComponentCount( domainId1 + 1000 );
        assertEquals( FieldML.ERR_NO_SUCH_OBJECT, err );
    }
}
