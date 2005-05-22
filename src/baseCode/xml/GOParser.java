package baseCode.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import baseCode.bio.GOEntry;
import baseCode.bio.geneset.GONames;
import baseCode.dataStructure.graph.DirectedGraph;
import baseCode.dataStructure.graph.DirectedGraphNode;

/**
 * Read in the GO XML file provided by the Gene Ontology Consortium.
 * <p>
 * Copyright (c) Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class GOParser {

    private DirectedGraph m;

    /**
     * Get the graph that was created.
     * 
     * @return a DirectedGraph. Nodes contain OntologyEntry instances.
     */
    public DirectedGraph getGraph() {
        return m;
    }

    /**
     * Get a simple Map that contains keys that are the GO ids, values are the names. This can replace the functionality
     * of the GONameReader in classScore.
     * 
     * @return Map
     */
    public Map getGONameMap() {
        Map nodes = m.getItems();
        Map result = new HashMap();
        for ( Iterator it = nodes.keySet().iterator(); it.hasNext(); ) {
            DirectedGraphNode node = ( DirectedGraphNode ) nodes.get( it.next() );
            GOEntry e = ( GOEntry ) node.getItem();
            result.put( e.getId().intern(), e.getName().intern() );
        }
        return result;
    }

    public GOParser( InputStream i ) throws IOException, SAXException {

        if ( i.available() == 0 ) {
            throw new IOException( "XML stream contains no data." );
        }

        System.setProperty( "org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser" );

        XMLReader xr = XMLReaderFactory.createXMLReader();
        GOHandler handler = new GOHandler();
        xr.setFeature( "http://xml.org/sax/features/validation", false );
        xr.setFeature( "http://xml.org/sax/features/external-general-entities", false );
        xr.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
        xr.setContentHandler( handler );
        xr.setErrorHandler( handler );
        xr.setEntityResolver( handler );
        xr.setDTDHandler( handler );
        xr.parse( new InputSource( i ) );

        m = handler.getResults();
    }

}

class GOHandler extends DefaultHandler {
    private static Log log = LogFactory.getLog( GOHandler.class.getName() );
    private DirectedGraph m;

    public DirectedGraph getResults() {
        return m;
    }

    public GOHandler() {
        super();
        m = new DirectedGraph();
    }

    private boolean inTerm = false;
    private boolean inDef = false;
    private boolean inAcc = false;
    private boolean inName = false;
    private boolean inPartOf = false;
    private boolean inIsa = false;
    private boolean inSyn = false;

    private String currentAspect;
    private StringBuffer nameBuf;
    private StringBuffer accBuf;
    private StringBuffer defBuf;

    public void startElement( String uri, String name, String qName, Attributes atts ) {

        if ( name.equals( "term" ) ) {
            inTerm = true;
        } else if ( name.equals( "accession" ) ) {
            accBuf = new StringBuffer();
            inAcc = true;
        } else if ( name.equals( "definition" ) ) {
            defBuf = new StringBuffer();
            inDef = true;
        } else if ( name.equals( "is_a" ) ) {
            inIsa = true;
            String res = atts.getValue( "rdf:resource" );
            String parent = res.substring( res.lastIndexOf( '#' ) + 1, res.length() );

            if ( !m.containsKey( parent ) ) {
                initializeNewNode( parent );
            }
            String currentTerm = accBuf.toString();
            m.addParentTo( currentTerm, parent );

        } else if ( name.equals( "part_of" ) ) {
            inPartOf = true;
            String res = atts.getValue( "rdf:resource" );
            String parent = res.substring( res.lastIndexOf( '#' ) + 1, res.length() );

            if ( !m.containsKey( parent ) ) {
                initializeNewNode( parent );
            }
            String currentTerm = accBuf.toString();
            m.addParentTo( currentTerm, parent );
        } else if ( name.equals( "synonym" ) ) {
            inSyn = true;
        } else if ( name.equals( "name" ) ) {
            nameBuf = new StringBuffer();
            inName = true;
        }
    }

    /**
     * @param parent
     */
    private void initializeNewNode( String parent ) {
        m.addNode( parent, new GOEntry( parent, "No name yet", "No definition found", null ) );
    }

    public void endElement( String uri, String name, String qName ) {
        if ( name.equals( "term" ) ) {
            inTerm = false;
        } else if ( name.equals( "accession" ) ) {
            inAcc = false;
            String currentTerm = accBuf.toString();
            initializeNewNode( currentTerm );
        } else if ( name.equals( "definition" ) ) {
            String currentTerm = accBuf.toString();
            ( ( GOEntry ) m.getNodeContents( currentTerm ) ).setDefinition( defBuf.toString().intern() );
            inDef = false;
        } else if ( name.equals( "is_a" ) ) {
            inIsa = false;
        } else if ( name.equals( "part_of" ) ) {
            inPartOf = false;
        } else if ( name.equals( "synonym" ) ) {
            inSyn = false;
        } else if ( name.equals( "name" ) ) {
            inName = false;
            String currentTerm = accBuf.toString();

            String currentName = nameBuf.toString().intern();

            ( ( GOEntry ) m.getNodeContents( currentTerm ) ).setName( currentName );

            if ( currentName.equalsIgnoreCase( "molecular_function" )
                    || currentName.equalsIgnoreCase( "biological_process" )
                    || currentName.equalsIgnoreCase( "cellular_component" )
                    || currentName.equalsIgnoreCase( "obsolete_molecular_function" )
                    || currentName.equalsIgnoreCase( "obsolete_biological_process" )
                    || currentName.equalsIgnoreCase( "obsolete_cellullar_component" ) ) {
                currentAspect = currentName;
                ( ( GOEntry ) m.getNodeContents( currentTerm ) ).setAspect( currentAspect );
            }

        }
    }

    public void characters( char ch[], int start, int length ) {

        if ( inTerm ) {
            if ( inAcc ) {
                accBuf.append( ch, start, length );
            } else if ( inDef ) {
                defBuf.append( ch, start, length );
            } else if ( inName ) {
                nameBuf.append( ch, start, length );
            }
        }
    }

}