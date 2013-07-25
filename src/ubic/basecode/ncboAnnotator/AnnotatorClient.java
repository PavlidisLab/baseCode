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
package ubic.basecode.ncboAnnotator;

import java.io.StringReader;
import java.util.Collection;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ubic.basecode.ontology.Configuration;

/**
 * This should behave the same as the web service from this link : http://bioportal.bioontology.org/annotator
 */
public class AnnotatorClient {

    private static String ANNOTATOR_URL = "http://rest.bioontology.org/obs/annotator";

    // this API_KEY needs to be added to basecode.properties
    // The value I used in my configuration file is : ncbo.api.key=68835db8-b142-4c7d-9509-3c843849ad67
    private static String API_KEY = Configuration.getString( "ncbo.api.key" );

    private static String ONTOLOGY_USED = "";

    /**
     * Create the Annotator Client
     * 
     * @param ontologiesToUse a list of id representing what Ontology to use
     */
    // must let it know what Ontology to search
    public AnnotatorClient( Collection<Long> ontologiesToUse ) {
        // 1009 is disease ontology
        // 1125 is hp ontology
        // TODO unsure how to use string (ontology name) rather that number

        String ontologiesId = "";

        for ( Long id : ontologiesToUse ) {
            ontologiesId = ontologiesId + id + ",";
        }

        if ( ontologiesId.length() > 0 ) {
            // take out last ,
            ontologiesId.substring( 0, ontologiesId.length() - 1 );
        }
        ONTOLOGY_USED = ontologiesId;
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
        // 5 - other results
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
                if ( localOntologyId.equalsIgnoreCase( "50173" ) ) {
                    ontologyUsed = "HP";
                } else if ( localOntologyId.equalsIgnoreCase( "50310" ) ) {
                    ontologyUsed = "DOID";
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
            e.printStackTrace();
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

    // These are the setting PP used before, DO only
    // method.addParameter("withDefaultStopWords","true"); // default is false
    // method.addParameter("ontologiesToExpand", "1009");
    // method.addParameter("ontologiesToKeepInResult", "1009");
    // method.addParameter("isVirtualOntologyId", "true"); // default is false, true is recommended
    // method.addParameter("levelMax", "10"); // expand to root, 10 is enough.
    // method.addParameter("mappingTypes", "null"); //null, Automatic, Manual
    // method.addParameter("textToAnnotate", text);
    // method.addParameter("format", "tabDelimited"); //Options are 'text', 'xml', 'tabDelimited'
    // method.addParameter("apikey", "7b14e900-1ba2-4e58-ac21-c5c4c74e7ece") // Paul's
    // PostMethod method = new PostMethod( annotatorUrl );

}
