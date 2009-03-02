package fieldml;

import java.io.FileReader;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class FieldmlHandler
    extends DefaultHandler
{
    private enum ParsingState
    {
        NONE,
        COMPOSITE_DOMAIN,
        DISCRETE_DOMAIN,
        COMPOSITE_FIELD,
    }

    private final StringBuilder characters;

    private final Deque<Integer> domainStack;

    private final Deque<Integer> fieldStack;

    private ParsingState state;


    public void startElement( String uri, String localName, String qName, Attributes attributes )
    {
        if( qName.compareTo( "domain" ) == 0 )
        {
            if( ( state != ParsingState.NONE ) && ( state != ParsingState.COMPOSITE_DOMAIN ) )
            {
            }
            else if( attributes.getValue( "name" ) == null )
            {
            }

            int id;

            if( domainStack.size() > 0 )
            {
                id = FieldML.FieldML_CreateCompositeDomain( domainStack.peek(), attributes.getValue( "name" ) );
            }
            else
            {
                id = FieldML.FieldML_CreateCompositeDomain( 0, attributes.getValue( "name" ) );
            }

            domainStack.push( id );
        }
        else if( qName.compareTo( "discrete_domain" ) == 0 )
        {
            if( ( state != ParsingState.NONE ) && ( state != ParsingState.COMPOSITE_DOMAIN ) )
            {
            }
            else if( attributes.getValue( "name" ) == null )
            {
            }

            int id;

            if( domainStack.size() > 0 )
            {
                id = FieldML.FieldML_CreateDiscreteDomain( domainStack.peek(), attributes.getValue( "name" ) );
            }
            else
            {
                id = FieldML.FieldML_CreateDiscreteDomain( 0, attributes.getValue( "name" ) );
            }

            domainStack.push( id );

            state = ParsingState.DISCRETE_DOMAIN;
        }

        System.out.println( "+ " + qName );

        characters.setLength( 0 );
    }


    public void endElement( String uri, String localName, String qName )
    {
        if( qName.compareTo( "domain" ) == 0 )
        {
            domainStack.pop();

            if( domainStack.size() == 0 )
            {
                state = ParsingState.NONE;
            }
        }
        else if( qName.compareTo( "discrete_domain" ) == 0 )
        {
            int[] values = getValues( characters );

            FieldML.FieldML_AddDiscreteDomainValues( domainStack.peek(), values, 0, values.length );

            characters.setLength( 0 );

            domainStack.pop();

            if( domainStack.size() == 0 )
            {
                state = ParsingState.NONE;
            }
            else
            {
                state = ParsingState.COMPOSITE_DOMAIN;
            }
        }
        
        System.out.println( "- " + qName );
    }


    public void characters( char[] ch, int start, int length )
    {
        characters.append( String.copyValueOf( ch, start, length ) );
    }


    private FieldmlHandler()
    {
        characters = new StringBuilder();

        domainStack = new ArrayDeque<Integer>();

        fieldStack = new ArrayDeque<Integer>();
    }


    public static void main( String[] args )
    {
        try
        {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            FieldmlHandler handler = new FieldmlHandler();
            xr.setContentHandler( handler );
            xr.setErrorHandler( handler );

            // Parse each file provided on the
            // command line.
            for( int i = 0; i < args.length; i++ )
            {
                FileReader r = new FileReader( args[i] );
                xr.parse( new InputSource( r ) );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
