package ubic.basecode.ontology.ncbo;

import java.net.ConnectException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

import ubic.basecode.util.Configuration;

public class AnnotatorClient {

    // ONTOLOGY ACCRONYM
    public static final String HP_ONTOLOGY = "HP";
    public static final String DOID_ONTOLOGY = "DOID";

    private static Logger log = LoggerFactory.getLogger( AnnotatorClient.class );

    // this API_KEY needs to be added to basecode.properties
    private static String API_KEY = Configuration.getString( "ncbo.api.key" );

    private static String ANNOTATOR_URL = "http://data.bioontology.org/annotator?";

    // set this to search other things than only DOID and HP
    private static String ontologies = HP_ONTOLOGY + "," + DOID_ONTOLOGY;

    public static Collection<AnnotatorResponse> findTerm( String term ) throws Exception {

        Collection<AnnotatorResponse> responsesFound = new TreeSet<AnnotatorResponse>();

        String termFormated = removeSpecialCharacters( term );

        String url = ANNOTATOR_URL + "apikey=" + API_KEY + "&max_level=0&ontologies=" + ontologies
                + "&format=xml&text=" + termFormated;

        log.info( "request url: " + url );

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = null;

        try {
            httpGet = new HttpGet( url );
        } catch ( IllegalArgumentException e ) {
            log.info( e.getMessage() );
            return responsesFound;
        }
        HttpResponse response = httpclient.execute( httpGet );
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
                        ontologyUsed, termFormated );

                responsesFound.add( annotatorResponse );
            }
        }

        return responsesFound;
    }

    public static String findOntologyUsed( String url ) {

        if ( url.indexOf( HP_ONTOLOGY ) != -1 ) {
            return HP_ONTOLOGY;
        } else if ( url.indexOf( DOID_ONTOLOGY ) != -1 ) {
            return DOID_ONTOLOGY;
        }

        return "UNKNOWN";

    }

    /**
     * return the label associated with an conceptid
     * 
     * @param ontologyId what virtual ontology to use
     * @param identifier the identifier, example : DOID_7(DOID),C535334(MESH),105500(OMIM)
     * @return the label for that term, example : ABCD syndrome
     */
    public static String findLabelUsingIdentifier( Long ontologyId, String identifier ) {
        try {
            String url = "http://rest.bioontology.org/bioportal/virtual/ontology/" + ontologyId + "?conceptid="
                    + identifier + "&apikey=" + API_KEY;

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet( url );
            HttpResponse response = httpclient.execute( httpGet );

            // term was not found
            if ( response.getStatusLine().getStatusCode() == 404 ) {
                log.info( "404 returned, the term Used: " + identifier + " was not found" );
                return null;
            }

            return findLabel( response );
        } catch ( ConnectException ce ) {
            try {
                log.error( "cannot connect waiting 30 seconds" );
                Thread.sleep( 30000 );
                return findLabelUsingIdentifier( ontologyId, identifier );
            } catch ( InterruptedException e ) {
                // will never go here
            }
        } catch ( Exception e ) {
            log.error( "meshId: '" + identifier + "'" );
            log.error( ExceptionUtils.getStackTrace( e ) );
        }
        return null;
    }

    /**
     * using the response return the label associated with the request
     */
    private static String findLabel( HttpResponse response ) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse( response.getEntity().getContent() );
        NodeList nodes = document.getElementsByTagName( "classBean" );
        String labelName = ( ( Element ) nodes.item( 0 ) ).getElementsByTagName( "label" ).item( 0 ).getTextContent();

        return labelName;
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

        return simpleTxt.trim().replaceAll( " ", "+" );
    }

    public static String getOntologies() {
        return ontologies;
    }

    public static void setOntologies( String ontologies ) {
        AnnotatorClient.ontologies = ontologies;
    }

}
