package fieldml.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fieldml.exception.BadFieldmlParameterException;
import fieldml.exception.FieldmlException;
import fieldml.util.FieldmlObjectManager;

public class DiscreteDomain
    extends Domain
{
    private final List<int[]> componentValues = new ArrayList<int[]>();


    public DiscreteDomain( FieldmlObjectManager<Domain> manager, String name )
    {
        super( manager, name );

    }


    public void addComponent( String componentName, int[] values, int valueCount )
        throws FieldmlException
    {
        if( values.length < valueCount )
        {
            throw new BadFieldmlParameterException();
        }
        if( valueCount < 1 )
        {
            throw new BadFieldmlParameterException();
        }

        super.addComponent( componentName );

        //TODO Check that each value is unique.
        componentValues.add( Arrays.copyOfRange( values, 0, valueCount - 1 ) );
    }


    public int getComponentValueCount( int componentIndex )
        throws FieldmlException
    {
        if( ( componentIndex < 0 ) || ( componentIndex >= componentValues.size() ) )
        {
            throw new BadFieldmlParameterException();
        }

        return componentValues.get( componentIndex ).length;
    }


    public int getComponentValues( int componentIndex, int[] values )
        throws FieldmlException
    {
        if( ( componentIndex < 0 ) || ( componentIndex >= componentValues.size() ) )
        {
            throw new BadFieldmlParameterException();
        }

        int[] sourceValues = componentValues.get( componentIndex );

        if( values.length < sourceValues.length )
        {
            throw new BadFieldmlParameterException();
        }

        System.arraycopy( sourceValues, 0, values, 0, sourceValues.length );
        
        return sourceValues.length;
    }
}
