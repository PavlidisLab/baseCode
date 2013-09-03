/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.ontology.ncbo;

import java.io.StringReader;
import java.util.Collection;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ubic.basecode.util.Configuration;

/**
 * This should behave the same as the web service from this link : http://bioportal.bioontology.org/annotator
 */
public class AnnotatorClient {

    public static final Long HP_ONTOLOGY = 1125l;
    public static final Long DOID_ONTOLOGY = 1009l;
    public static final Long OMIM_ONTOLOGY = 1348l;
    public static final Long MESH_ONTOLOGY = 3019l;

    private static String ANNOTATOR_URL = "http://rest.bioontology.org/obs/annotator";

    // this API_KEY needs to be added to basecode.properties
    private static String API_KEY = Configuration.getString( "ncbo.api.key" );

    private static String ONTOLOGY_USED = "";

    private static Logger log = LoggerFactory.getLogger( AnnotatorClient.class );

    /**
     * Create the Annotator Client
     * 
     * @param ontologiesToUse a list of id representing what Ontology to use
     */
    public AnnotatorClient( Collection<Long> ontologiesId ) {
        // must let it know what Ontology to search
        // TODO unsure how to use string (ontology name) rather that number
        ONTOLOGY_USED = StringUtils.removeEnd( StringUtils.join( ontologiesId, "," ), "," );
    }

    /**
     * for a specific term description return a Collection of ontology terms found
     * 
     * @param term the description of the term
     * @return Collection<AnnotatorResponse> the answers found by the nbco annotator
     */
    public Collection<AnnotatorResponse> findTerm( String term ) {

        String searchTerm = removeSpecificCharacters( term );

        // TreeSet used: term are ordered with the compareTo method of AnnotatorResponse
        // 1- exact match from DOID
        // 2- synonym from DOID
        // 3- exact match from other resources
        // 4- synonym from other resources
        // 5- other results
        // to change this rewrite own compareTo method to define required order
        Collection<AnnotatorResponse> responsesFound = new TreeSet<AnnotatorResponse>();

        try {

            // build the request with desire parameters
            Request request = Request.Post( ANNOTATOR_URL ).bodyForm(
                    Form.form().add( "textToAnnotate", searchTerm ).add( "ontologiesToKeepInResult", ONTOLOGY_USED )
                            .add( "withDefaultStopWords", "true" ).add( "levelMax", "0" ).add( "semanticTypes", "" )
                            .add( "mappingTypes", "" ).add( "wholeWordOnly", "true" )
                            .add( "isVirtualOntologyId", "true" ).add( "longestOnly", "false" )
                            .add( "filterNumber", "true" ).add( "isTopWordsCaseSensitive", "false" )
                            .add( "mintermSize", "3" ).add( "scored", "true" ).add( "withSynonyms", "true" )
                            .add( "ontologiesToExpand", "" ).add( "apikey", API_KEY ).build() );

            // Execute the POST method
            String contents = request.execute().returnContent().asString();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource( new StringReader( contents ) );
            Document document = builder.parse( is );

            NodeList nodes = document.getElementsByTagName( "annotationBean" );

            // for each response receive, populate the return objects
            for ( int temp = 0; temp < nodes.getLength(); temp++ ) {

                Node nNode = nodes.item( temp );

                Element eElement = ( Element ) nNode;

                // this id represents the ontology used
                String localOntologyId = eElement.getElementsByTagName( "localOntologyId" ).item( 0 ).getTextContent();
                String ontologyUsed = "";

                // a number identify what ontology the results is from
                // TODO might need to add some or find better way to do this
                // also the hp changed values before not sure why
                if ( localOntologyId.equalsIgnoreCase( "50579" ) ) {
                    ontologyUsed = "HP";
                } else if ( localOntologyId.equalsIgnoreCase( "50310" ) ) {
                    ontologyUsed = "DOID";
                } else {
                    log.warn( "localOntologyId: " + localOntologyId + "  term: " + term );
                    log.warn( "using the localOntologyId can find the ontology Used, if not DOID or HP, please add/update AnnotatorClient" );
                }

                // score given
                Integer score = Integer.valueOf( eElement.getElementsByTagName( "score" ).item( 0 ).getTextContent() );
                // value of phenotype
                String preferredName = eElement.getElementsByTagName( "preferredName" ).item( 0 ).getTextContent();
                // valueUri of phenotype
                String fullId = eElement.getElementsByTagName( "fullId" ).item( 0 ).getTextContent();
                // create the return representing a results found
                AnnotatorResponse annotatorResponse = new AnnotatorResponse( score, preferredName, fullId, searchTerm,
                        ontologyUsed );

                // populates all synonym given for that specific term
                for ( int i = 0; i < eElement.getElementsByTagName( "synonyms" ).getLength(); i++ ) {
                    annotatorResponse.addSynonyms( java.net.URLDecoder.decode(
                            eElement.getElementsByTagName( "synonyms" ).item( i ).getTextContent(), "UTF-8" )
                            .toLowerCase() );
                }

                // if the search term used is a synonym found
                if ( annotatorResponse.getSynonyms().contains( searchTerm.toLowerCase() ) ) {
                    annotatorResponse.setSynonym( true );
                }

                // add the reponse found
                responsesFound.add( annotatorResponse );
            }

        } catch ( Exception e ) {
            log.error( "term: '" + term + "'" );
            log.error( ExceptionUtils.getStackTrace( e ) );
        }
        return responsesFound;
    }

    private String removeSpecificCharacters( String txt ) {

        String newTxt = txt.replaceAll( "\\{", "" );
        newTxt = newTxt.replaceAll( "\\}", "" );
        newTxt = newTxt.replaceAll( "\\[", "" );
        newTxt = newTxt.replaceAll( "\\]", "" );
        newTxt = newTxt.replaceAll( "\\?", "" );

        return newTxt;
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
                throw new Exception( "404 returned, the term Used: " + identifier + " was not found" );
            }

            return findLabel( response );
        } catch ( Exception e ) {
            log.error( "meshId: '" + identifier + "'" );
            log.error( ExceptionUtils.getStackTrace( e ) );
            return null;
        }
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

}
