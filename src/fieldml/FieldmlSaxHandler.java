package fieldml;

import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.Deque;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import fieldml.domain.DomainManager;

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
    
    private DomainManager manager; 

    @Override
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
                id = FieldML.FieldML_CreateCompositeDomain( manager, idStack.peek(), attributes.getValue( "name" ) );
            }
            else
            {
                id = FieldML.FieldML_CreateCompositeDomain( manager, 0, attributes.getValue( "name" ) );
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

            int originalDomainId = FieldML.FieldML_GetDomainId( manager, originalDomainName );
            
            FieldML.FieldML_ImportDomain( manager, idStack.peek(), originalDomainId, newName );
        }

        characters.setLength( 0 );
    }


    @Override
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

            int id; // TODO: fix warning: what are we meant to do with returned id's?

            if( idStack.size() > 0 )
            {
                id = FieldML.FieldML_CreateDiscreteDomain( manager, idStack.peek(), domainName, values, 0, values.length );
            }
            else
            {
                id = FieldML.FieldML_CreateDiscreteDomain( manager, 0, domainName, values, 0, values.length );
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


    private int[] getValues(StringBuilder characters) {
    	// TODO: This is a terribly lazy hack because this method has not been coded yet, so for interim, hard coding return value.
		return new int[]{1,2,3};
	}


	public void characters( char[] ch, int start, int length )
    {
        characters.append( String.copyValueOf( ch, start, length ) );
    }


    private FieldmlSaxHandler(DomainManager manager)
    {
        characters = new StringBuilder();
        idStack = new ArrayDeque<Integer>();
        this.manager = manager;
    }


    public static void main( String[] args )
    {
        try
        {
        	DomainManager manager = new DomainManager();
            XMLReader xr = XMLReaderFactory.createXMLReader();
            FieldmlSaxHandler handler = new FieldmlSaxHandler(manager);
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
