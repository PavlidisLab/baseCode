package ubic.basecode.ontology.ncbo;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ubic.basecode.util.Configuration;

/**
 * Use the NCBO annotator to find ontology terms matching strings.
 * 
 */
public class AnnotatorClient {

    // ONTOLOGY ACCRONYM
    public static final String HP_ONTOLOGY = "HP";
    public static final String DOID_ONTOLOGY = "DOID";

    private final static int MAX_TRIES = 3;

    private static Logger log = LoggerFactory.getLogger( AnnotatorClient.class );

    // this API_KEY needs to be added to properties
    private static String API_KEY = Configuration.getString( "ncbo.api.key" );

    private static String ANNOTATOR_URL = "http://data.bioontology.org/annotator?";

    // set this to search other things than only DOID and HP
    private static String ontologies = HP_ONTOLOGY + "," + DOID_ONTOLOGY;

    /**
     * 
     * @param  term
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IllegalStateException
     * @throws Exception
     */
    public static Collection<AnnotatorResponse> findTerm( String term )
            throws IOException, ParserConfigurationException, IllegalStateException, SAXException {
        if ( StringUtils.isBlank( API_KEY ) ) {
            throw new IllegalStateException( "NCBO ncbo.api.key needs to be configured" );
        }

        Collection<AnnotatorResponse> responsesFound = new TreeSet<>();

        String termClean = removeSpecialCharacters( term );

        if ( StringUtils.isBlank( termClean ) ) return responsesFound;

        String url = ANNOTATOR_URL + "apikey=" + API_KEY + "&max_level=0&ontologies=" + ontologies
                + "&format=xml&text=" + termClean;

        if ( log.isDebugEnabled() ) log.debug( "request url: " + url );

        int tries = 0;

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = null;
        while ( response == null && tries < MAX_TRIES ) {
            try {
                HttpGet httpGet = new HttpGet( url );
                response = httpclient.execute( httpGet );
            } catch ( IOException e ) {
                try {
                    Thread.sleep( 10000 ); // long wait...
                } catch ( InterruptedException e1 ) {

                }
                tries++;
            }
        }

        if ( response == null ) {
            log.warn( "Failed to get a response for " + url + " (original query=" + term + ")" );
            return responsesFound;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse( response.getEntity().getContent() );
        NodeList nodes = document.getElementsByTagName( "annotation" );

        // for each response receive, populate the return objects
        for ( int temp = 0; temp < nodes.getLength(); temp++ ) {

            Node nNode = nodes.item( temp );
            Element eElement = ( Element ) nNode;

            // this is what was found with the annotator
            String valueUri = eElement.getElementsByTagName( "id" ).item( 0 ).getTextContent();

            // populates all synonym given for that specific term
            for ( int i = 0; i < eElement.getElementsByTagName( "annotations" ).getLength(); i++ ) {

                Element infoE = ( Element ) eElement.getElementsByTagName( "annotations" ).item( i );

                String matchType = infoE.getElementsByTagName( "matchType" ).item( 0 ).getTextContent();
                String txtMatched = infoE.getElementsByTagName( "text" ).item( 0 ).getTextContent();
                String ontologyUsed = findOntologyUsed( valueUri );

                Integer from = new Integer( infoE.getElementsByTagName( "from" ).item( 0 ).getTextContent() );
                Integer to = new Integer( infoE.getElementsByTagName( "to" ).item( 0 ).getTextContent() );

                AnnotatorResponse annotatorResponse = new AnnotatorResponse( valueUri, matchType, txtMatched, from, to,
                        ontologyUsed, termClean );

                responsesFound.add( annotatorResponse );
            }
        }

        return responsesFound;
    }

    /**
     * FIXME only knows about HP and DOID
     * 
     * @param  url
     * @return
     */
    private static String findOntologyUsed( String url ) {

        if ( url.indexOf( HP_ONTOLOGY ) != -1 ) {
            return HP_ONTOLOGY;
        } else if ( url.indexOf( DOID_ONTOLOGY ) != -1 ) {
            return DOID_ONTOLOGY;
        }

        return "UNKNOWN";

    }

    /**
     * return the label associated with an conceptid. FIXME why are we doing things this way, it must be terribly slow
     * 
     * @param  ontologyId what virtual ontology to use
     * @param  identifier the identifier, knows about: OMIM, DOID, MESH
     * @return            the label for that term, example : ABCD syndrome
     */
    public static String findLabelForIdentifier( String ontologyId, String identifier ) {

        if ( StringUtils.isBlank( API_KEY ) ) {
            throw new IllegalStateException( "NCBO ncbo.api.key needs to be configured" );
        }

        // Examples
        // http://data.bioontology.org/ontologies/DOID
        // http://data.bioontology.org/ontologies/DOID/classes/http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FDOID_8986

        // http://data.bioontology.org/ontologies/MESH
        // http://data.bioontology.org/ontologies/MESH/classes/http%3A%2F%2Fpurl.bioontology.org%2Fontology%2FMESH%2FC000624633

        // http://data.bioontology.org/ontologies/OMIM

        // http://data.bioontology.org/ontologies/OMIM/classes/http%3A%2F%2Fpurl.bioontology.org%2Fontology%2FOMIM%2F600374 

        for ( int i = 0; i < MAX_TRIES; i++ ) {
            try {
                String url;

                // These Ontology identifiers could potentially be retrieved, using OntologyLookup
                switch ( ontologyId ) {
                    case "OMIM":
                    case "MESH":
                        url = "http://data.bioontology.org/ontologies/" + ontologyId
                                + "/classes/http%3A%2F%2Fpurl.bioontology.org%2Fontology%2FMESH%2F"
                                + identifier
                                + "/?apikey=" + API_KEY + "&format=xml";
                        break;
                    case "DOID":
                        url = "http://data.bioontology.org/ontologies/" + ontologyId + "/classes/http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2F"
                                + identifier
                                + "/?apikey=" + API_KEY + "&format=xml";
                        break;
                    default:
                        throw new IllegalArgumentException( "Don't know how to deal with " + ontologyId );
                }

                log.debug( url );

                DefaultHttpClient httpclient = new DefaultHttpClient();

                HttpGet httpGet = new HttpGet( url );
                HttpResponse response = httpclient.execute( httpGet );

                // term was not found
                if ( response.getStatusLine().getStatusCode() == 404 ) {
                    log.debug( "404 returned, the term: " + identifier + " was not found" );
                    return null;
                }

                return findLabel( response );
            } catch ( ConnectException ce ) {
                try {
                    Thread.sleep( 500 );
                } catch ( InterruptedException e ) {
                }
            } catch ( Exception e ) {
                log.error( "Identifier: '" + identifier + "'" );
                log.error( ExceptionUtils.getStackTrace( e ) );
            }
        }
        return null;
    }

    /**
     * using the response return the label associated with the request
     */
    private static String findLabel( HttpResponse response ) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream content = response.getEntity().getContent()) {
            Document document = builder.parse( content );
            NodeList nodes = document.getElementsByTagName( "prefLabel" );
            if ( nodes == null ) {
                log.debug( "No definition found" );
                return null;
            }
            String labelName = ( ( Element ) nodes.item( 0 ) ).getTextContent();

            return labelName;
        }
    }

    // this is an attempt to clean up the string from characters we dont want
    public static String removeSpecialCharacters( String txt ) {

        String simpleTxt = txt.trim();

        // remove txt between ( and )
        int index1 = simpleTxt.indexOf( "(" );
        int index2 = simpleTxt.indexOf( ")" );
        if ( index1 != -1 && index2 != -1 ) {
            simpleTxt = simpleTxt.substring( 0, index1 ) + simpleTxt.substring( index2 + 1, simpleTxt.length() );
        }

        // what to keep
        Pattern pt = Pattern.compile( "[^\\w\\s-,]+" );
        Matcher match = pt.matcher( simpleTxt );
        while ( match.find() ) {
            String s = match.group();
            simpleTxt = simpleTxt.replaceAll( "\\" + s, "" );
        }

        return simpleTxt.trim().replaceAll( "\\s+", "+" );
    }

}
