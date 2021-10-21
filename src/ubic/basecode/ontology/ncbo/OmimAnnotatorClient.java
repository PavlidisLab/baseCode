package ubic.basecode.ontology.ncbo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ubic.basecode.util.Configuration;

/**
 * to use the OMIM api
 * TODO Document Me
 * 
 * @author Nicolas
 */
public class OmimAnnotatorClient {

    private static String OMIM_API_URL = "http://api.omim.org/api/entry";

    // this OMIM_API_KEY needs to be added to basecode.properties
    private static String OMIM_API_KEY = Configuration.getString( "omim.api.key" );

    private static Logger log = LoggerFactory.getLogger( OmimAnnotatorClient.class );

    /**
     * Giving a set of OMIM id return for each id its list of related publication
     * 
     * @param omimIds      the omimIds we want to find the publication
     * @param mimToPubmeds the results, pass by reference for multiple calls, limit of size 10
     */
    public static Map<Long, Collection<Long>> findLinkedPublications( Collection<Long> omimIds,
            Map<Long, Collection<Long>> mimToPubmeds ) throws InterruptedException {

        if ( omimIds.size() > 10 || omimIds.isEmpty() ) {
            throw new IllegalArgumentException( "Size of the Omim ids must be between 1 and 10,  Size Found: "
                    + omimIds.size() );
        }

        // From OMIM documentation : The rate of requests is currently limited to 4 requests per second.
        // if too many request are made, the ip gets ban, sleep makes this not possible
        Thread.sleep( 700 );

        String omimIdsAsString = StringUtils.removeEnd( StringUtils.join( omimIds, "," ), "," );

        try {

            String url = OMIM_API_URL + "?mimNumber=" + omimIdsAsString + "&format=xml&include=referenceList&apiKey="
                    + OMIM_API_KEY;

            log.info( "request url: " + url );

            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet( url );
            HttpResponse response = httpclient.execute( httpGet );

            // term was not found
            if ( response.getStatusLine().getStatusCode() == 404 ) {
                throw new Exception( "404 returned" );
            }

            return findPublications( response, mimToPubmeds );
        } catch ( Exception e ) {
            log.error( ExceptionUtils.getStackTrace( e ) );
            return null;
        }
    }

    private static Map<Long, Collection<Long>> findPublications( HttpResponse response,
            Map<Long, Collection<Long>> mimToPubmeds ) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse( response.getEntity().getContent() );
        NodeList nodes = document.getElementsByTagName( "reference" );

        // for each response receive, populate the return objects
        for ( int temp = 0; temp < nodes.getLength(); temp++ ) {

            Node nNode = nodes.item( temp );
            Element eElement = ( Element ) nNode;

            if ( eElement.getElementsByTagName( "pubmedID" ).item( 0 ) != null
                    && eElement.getElementsByTagName( "mimNumber" ).item( 0 ) != null ) {

                Long pubmedID = new Long( eElement.getElementsByTagName( "pubmedID" ).item( 0 ).getTextContent() );
                Long mimNumber = new Long( eElement.getElementsByTagName( "mimNumber" ).item( 0 ).getTextContent() );

                Collection<Long> pubmeds = new HashSet<>();

                if ( mimToPubmeds.get( mimNumber ) != null ) {
                    pubmeds = mimToPubmeds.get( mimNumber );
                }
                pubmeds.add( pubmedID );

                mimToPubmeds.put( mimNumber, pubmeds );
            }
        }
        return mimToPubmeds;
    }

}
