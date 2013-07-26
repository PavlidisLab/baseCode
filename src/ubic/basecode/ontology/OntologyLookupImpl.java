/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ubic.basecode.ontology;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.model.OntologyTermSimple;
import ubic.basecode.util.Configuration;

/**
 * Implementation of OntologyLookup using NCBO web services.
 * <p>
 * Findings: There is no way to figure out what ontology a term comes from, by just looking at the URI. NCBO does not
 * provide a direct way to do this, and I cannot figure out a way to engineer it from the given services.
 * <p>
 * An example of a difficult pattern is http://purl.obolibrary.org/obo/APO_0000001 - the only specific information is
 * the identifier at the end.
 * 
 * @author Paul
 * @version $Id$
 */
public class OntologyLookupImpl implements OntologyLookup {

    private static Log log = LogFactory.getLog( OntologyLookupImpl.class );

    private static String URL_BASE = "http://rest.bioontology.org/bioportal";

    private static String API_KEY = Configuration.getString( "ncbo.api.key" );

    private Map<String, String> ontologyIds = new HashMap<String, String>();

    private Map<String, String> url2OntologyId = new HashMap<String, String>();

    /*
     * Need to make a mapping of URI patterns we use to the ontology abbreviation; from that we can get the ID. Why
     * doesn't NCBI allow just a simple lookup since the URI is unambiguous?
     */

    static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public OntologyLookupImpl() {

        try {
            getOntologyIds();
        } catch ( XPathExpressionException e ) {
            throw new RuntimeException( e );
        } catch ( ParserConfigurationException e ) {
            throw new RuntimeException( e );
        } catch ( SAXException e ) {
            throw new RuntimeException( e );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.ontology.OntologyLookup#getDefinition(java.lang.String)
     */
    @Override
    public String getDefinition( String uri ) {
        // TODO Auto-generated method stub
        // norelations
        return null;
    }

    @Override
    public Collection<String> getSynonyms( String uri ) {

        String ontologyId = getOntologyId( uri );

        // Collection<OntologyTerm> matches = match( rawid, 1 );
        // if ( matches.isEmpty() ) {
        // throw new IllegalArgumentException( "Term " + rawid + " not found" );
        // }
        // OntologyTerm t = matches.iterator().next();
        // String ontologyId = t.getComment();
        // String u = t.getUri();
        //
        // if ( !uri.equals( u ) ) {
        // throw new IllegalArgumentException( "Found term doesn't match uri, given: " + uri + " != " + u );
        // }

        String toFetch = URL_BASE + "/virtual/ontology/" + ontologyId + "?conceptid=" + urlencode( uri )
                + "&norelations=1&apikey=" + API_KEY;
        String xml = this.ncboFetch( toFetch );
        // log.info( xml );

        Collection<String> results = new ArrayList<String>();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse( new InputSource( new StringReader( xml ) ) );
            XPathFactory xf = XPathFactory.newInstance();
            XPath xpath = xf.newXPath();
            XPathExpression synonymsEx = xpath.compile( "//classBean/conceptId" );

            NodeList synonyms = ( NodeList ) synonymsEx.evaluate( document, XPathConstants.NODESET );

            for ( int i = 0; i < synonyms.getLength(); i++ ) {
                String syn = synonyms.item( i ).getTextContent();

                log.info( syn );
                results.add( syn );
            }

            return results;
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.ontology.OntologyLookup#match(java.lang.String)
     */
    @Override
    public Collection<OntologyTerm> match( String query, int limit ) {
        // http://rest.bioontology.org/bioportal/search/?query=

        try {
            String versionFetch = URL_BASE + "/search/?query=" + query
                    + "&isexactmatch=1&includeproperties=1&maxnumhits=" + limit + "&apikey=" + API_KEY;
            String xml = ncboFetch( versionFetch );

            XPathFactory xf = XPathFactory.newInstance();
            XPath xpath = xf.newXPath();
            XPathExpression conceptEx = xpath.compile( "//searchBean/conceptId" );
            XPathExpression nameEx = xpath.compile( "//searchBean/preferredName" );
            XPathExpression obsoleteEx = xpath.compile( "//searchBean/isObsolete" );
            XPathExpression ontologyIdEx = xpath.compile( "//searchBean/ontologyId" );

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse( new InputSource( new StringReader( xml ) ) );
            NodeList concepts = ( NodeList ) conceptEx.evaluate( document, XPathConstants.NODESET );
            NodeList names = ( NodeList ) nameEx.evaluate( document, XPathConstants.NODESET );
            NodeList obsoleteStatus = ( NodeList ) obsoleteEx.evaluate( document, XPathConstants.NODESET );
            NodeList ontologyId = ( NodeList ) ontologyIdEx.evaluate( document, XPathConstants.NODESET );

            List<OntologyTerm> results = new ArrayList<OntologyTerm>();
            for ( int i = 0; i < concepts.getLength(); i++ ) {
                String id = concepts.item( i ).getTextContent();
                String name = names.item( i ).getTextContent();
                String obsolete = obsoleteStatus.item( i ).getTextContent();
                String oid = ontologyId.item( i ).getTextContent();
                log.info( id + " " + name + " " + obsolete );
                OntologyTermSimple o = new OntologyTermSimple( id, name, oid, "1".equals( obsolete ) );

                results.add( o );
            }

            return results;

        } catch ( Exception e ) {
            throw new RuntimeException( query, e );
        }
    }

    private Collection<String> extractOneField( String path, String xml ) {
        Collection<String> result;
        try {
            XPathFactory xf = XPathFactory.newInstance();
            XPath xpath = xf.newXPath();
            XPathExpression conceptEx = xpath.compile( path );
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse( new InputSource( new StringReader( xml ) ) );
            NodeList concepts = ( NodeList ) conceptEx.evaluate( document, XPathConstants.NODESET );
            result = new ArrayList<String>();
            for ( int i = 0; i < concepts.getLength(); i++ ) {
                String id = concepts.item( i ).getTextContent();
                result.add( id );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return result;
    }

    // not doable.
    @SuppressWarnings("unused")
    private String getOntologyid( String uri ) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getOntologyId( String uri ) {
        URL u;
        try {
            u = new URI( uri ).toURL();
        } catch ( Exception e ) {
            return null;
        }
        String base = u.getPath();

        if ( this.url2OntologyId.containsKey( base ) ) {
            return this.url2OntologyId.get( base );
        }
        for ( String id : ontologyIds.keySet() ) {
            /*
             * This will never work without limiting with ids we look at; there are hundreds to go through.
             */

            /*
             * Download a piece of the ontology
             */
            // http://rest.bioontology.org/bioportal/concepts/40644/all?pagesize=1&pagenum=1&apikey=7b14e900-1ba2-4e58-ac21-c5c4c74e7ece
            try {
                String xml = ncboFetch( URL_BASE + "/concepts/" + id + "/all?pagesize=1&pagenum=1&apikey=" + API_KEY );

                /*
                 * get the URL pattern it uses
                 */
                Collection<String> urls = extractOneField( "//classBean/fullId", xml );

                String onturi = urls.iterator().next();
                URL ou;

                ou = new URI( onturi ).toURL();
                log.info( ou );
                String p = onturi.replaceAll( "(?<=.+\\/).+", "" );
                log.info( p );
                url2OntologyId.put( p, id );
                if ( base.equals( p ) ) {
                    return id;
                }
            } catch ( Exception e ) {
                continue; // should blacklist the id.
            }

        }

        return null;
    }

    /**
     * useless.
     * 
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     */
    private void getOntologyIds() throws ParserConfigurationException, XPathExpressionException, SAXException,
            IOException {
        String versionFetch = URL_BASE + "/ontologies?apikey=" + API_KEY;
        String xml = ncboFetch( versionFetch );
        log.info( xml );
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse( new InputSource( new StringReader( xml ) ) );
        XPathFactory xf = XPathFactory.newInstance();
        XPath xpath = xf.newXPath();
        XPathExpression xpe = xpath.compile( "//ontologyBean/id" );
        XPathExpression oab = xpath.compile( "//ontologyBean/abbreviation" );

        NodeList ids = ( NodeList ) xpe.evaluate( document, XPathConstants.NODESET );
        NodeList abbs = ( NodeList ) oab.evaluate( document, XPathConstants.NODESET );
        for ( int i = 0; i < ids.getLength(); i++ ) {
            String id = ids.item( i ).getTextContent();
            String abb = abbs.item( i ).getTextContent();

            ontologyIds.put( abb, id );
        }
    }

    private String ncboFetch( String url ) {
        try {
            log.info( "Fetching: " + url );
            return Request.Get( url ).addHeader( "Accept", " application/xml" ).execute().returnContent().asString();
        } catch ( ClientProtocolException e ) {
            throw new RuntimeException( url, e );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param uri
     * @return
     * @throws UnsupportedEncodingException
     */
    private String urlencode( String uri ) {
        try {
            return URLEncoder.encode( uri, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( uri, e );
        }
    }
}
