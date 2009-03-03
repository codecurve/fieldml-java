package fieldml;

import java.io.FileReader;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class FieldmlSaxHandler
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

    private String domainName;

    private final Deque<Integer> idStack;

    private ParsingState state;


    public void startElement( String uri, String localName, String qName, Attributes attributes )
    {
        if( qName.compareTo( "domain" ) == 0 )
        {
            if( ( state != ParsingState.NONE ) && ( state != ParsingState.COMPOSITE_DOMAIN ) )
            {
                // ERROR
            }
            else if( attributes.getValue( "name" ) == null )
            {
                // ERROR
            }

            int id;

            if( idStack.size() > 0 )
            {
                id = FieldML.FieldML_CreateCompositeDomain( idStack.peek(), attributes.getValue( "name" ) );
            }
            else
            {
                id = FieldML.FieldML_CreateCompositeDomain( 0, attributes.getValue( "name" ) );
            }

            idStack.push( id );
        }
        else if( qName.compareTo( "discrete_domain" ) == 0 )
        {
            /*
             * Creation of discrete domains must be ferred until we know all the values.
             */
            if( ( state != ParsingState.NONE ) && ( state != ParsingState.COMPOSITE_DOMAIN ) )
            {
                // ERROR
            }
            else if( attributes.getValue( "name" ) == null )
            {
                // ERROR
            }

            domainName = attributes.getValue( "name" );

            state = ParsingState.DISCRETE_DOMAIN;
        }
        else if( qName.compareTo( "import_domain" ) == 0 )
        {
            // We could allow importing domains at file scope. This is functionally equivalent to aliases.
            if( state != ParsingState.COMPOSITE_DOMAIN )
            {
                // ERROR
            }
            else if( ( attributes.getValue( "id" ) == null ) || ( attributes.getValue( "domain" ) == null ) )
            {
                // ERROR
            }

            String newName = attributes.getValue( "id" );
            String originalDomainName = attributes.getValue( "domain" );

            int originalDomainId = FieldML.FieldML_GetDomainId( originalDomainName );
            
            FieldML.FieldML_ImportDomain( idStack.peek(), originalDomainId, newName );
        }

        characters.setLength( 0 );
    }


    public void endElement( String uri, String localName, String qName )
    {
        if( qName.compareTo( "domain" ) == 0 )
        {
            idStack.pop();

            if( idStack.size() == 0 )
            {
                state = ParsingState.NONE;
            }
        }
        else if( qName.compareTo( "discrete_domain" ) == 0 )
        {
            // Parse a space (and/or comma?) separated list of values. Currently, only integer values are supported.
            int[] values = getValues( characters );

            int id;

            if( idStack.size() > 0 )
            {
                id = FieldML.FieldML_CreateDiscreteDomain( idStack.peek(), domainName, values, 0, values.length );
            }
            else
            {
                id = FieldML.FieldML_CreateDiscreteDomain( 0, domainName, values, 0, values.length );
            }

            characters.setLength( 0 );

            if( idStack.size() == 0 )
            {
                state = ParsingState.NONE;
            }
            else
            {
                state = ParsingState.COMPOSITE_DOMAIN;
            }
        }
    }


    public void characters( char[] ch, int start, int length )
    {
        characters.append( String.copyValueOf( ch, start, length ) );
    }


    private FieldmlSaxHandler()
    {
        characters = new StringBuilder();

        idStack = new ArrayDeque<Integer>();
    }


    public static void main( String[] args )
    {
        try
        {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            FieldmlSaxHandler handler = new FieldmlSaxHandler();
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
