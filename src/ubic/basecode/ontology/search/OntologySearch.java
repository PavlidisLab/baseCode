/*
 * The basecode project
 *
 * Copyright (c) 2007-2019 University of British Columbia
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
package ubic.basecode.ontology.search;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.JenaException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.ontology.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pavlidis
 *
 */
public class OntologySearch {

    // Lucene cannot properly parse these characters... gives a query parse error.
    // OntologyTerms don't contain them anyway
    private final static char[] INVALID_CHARS = { ':', '(', ')', '?', '^', '[', ']', '{', '}', '!', '~', '"', '\'' };

    private static final Logger log = LoggerFactory.getLogger( OntologySearch.class );

    /**
     * Find classes that match the query string. Obsolete terms are not returned.
     *
     * @param  model       that goes with the index
     * @param  index       to search
     * @param  queryString
     * @return Collection of OntologyTerm objects
     */
    public static Collection<OntologyTerm> matchClasses( OntModel model, SearchIndex index, String queryString ) throws OntologySearchException {

        Set<OntologyTerm> results = new HashSet<>();
        NodeIterator iterator = runSearch( index, queryString );

        while ( iterator.hasNext() ) {
            RDFNode r = iterator.next();
            r = r.inModel( model );
            log.debug( "Search results: {}.", r );
            if ( r.isURIResource() && r.canAs( OntClass.class ) ) {
                try {
                    OntClass cl = r.as( OntClass.class );
                    OntologyTermImpl impl2 = new OntologyTermImpl( cl );
                    if ( impl2.isTermObsolete() ) continue;
                    results.add( impl2 );
                    log.debug( "{}", impl2 );
                } catch ( JenaException e ) {
                    throw new OntologySearchJenaException( String.format( "Failed to convert Jena resource %s to %s.", r, OntClass.class ), queryString, e );
                }
            }
        }

        return results;
    }

    /**
     * Find individuals that match the query string
     *
     * @param  model       that goes with the index
     * @param  index       to search
     * @param  queryString
     * @return Collection of OntologyTerm objects
     */
    public static Collection<OntologyIndividual> matchIndividuals( OntModel model, SearchIndex index, String queryString ) throws OntologySearchException {

        Set<OntologyIndividual> results = new HashSet<>();
        NodeIterator iterator;

        queryString = queryString.trim();

        // Add wildcard only if the last word is longer than one character. This is to prevent lucene from
        // blowing up. See bug#1145
        String[] words = queryString.split( "\\s+" );
        int lastWordLength = words[words.length - 1].length();
        if ( lastWordLength > 1 ) {
            try { // Use wildcard search.
                iterator = runSearch( index, queryString + "*" );
            } catch ( OntologySearchJenaException e ) { // retry without wildcard
                log.warn( "Failed to perform search with wildcard. Retrying search without wildcard.", e );
                try {
                    iterator = runSearch( index, queryString );
                } catch ( OntologySearchJenaException e1 ) {
                    throw new RetryWithoutWildcardFailedException( "Failed to search while retrying without wildcard.", queryString, e.getCause(), e1.getCause() );
                }
            }
        } else {
            iterator = runSearch( index, queryString );
        }

        while ( iterator.hasNext() ) {
            RDFNode r = iterator.next();
            r = r.inModel( model );
            log.debug( "Search results: {}", r );
            if ( r.isResource() && r.canAs( Individual.class ) ) {
                try {
                    Individual cl = r.as( Individual.class );
                    OntologyIndividual impl2 = new OntologyIndividualImpl( cl );
                    results.add( impl2 );
                    log.debug( "{}", impl2 );
                } catch ( JenaException je ) {
                    throw new OntologySearchJenaException( String.format( "Failed to convert Jena resource %s to %s.", r, Individual.class ), queryString, je );
                }
            }
        }

        return results;

    }

    /**
     * Find OntologyIndividuals and OntologyTerms that match the query string. Search with a wildcard is attempted
     * whenever possible.
     *
     * @param  model       that goes with the index
     * @param  index       to search
     * @param  queryString
     * @return Collection of OntologyResource objects
     */
    public static Collection<OntologyResource> matchResources( OntModel model, SearchIndex index, String queryString ) throws OntologySearchException {

        Set<OntologyResource> results = new HashSet<>();
        NodeIterator iterator = null;

        queryString = queryString.trim();

        // Add wildcard only if the last word is longer than one character. This is to prevent lucene from
        // blowing up. See bug#1145
        String[] words = queryString.split( "\\s+" );
        int lastWordLength = words[words.length - 1].length();
        if ( lastWordLength > 1 ) {
            try { // Use wildcard search.
                iterator = runSearch( index, queryString + "*" );
            } catch ( OntologySearchJenaException e ) { // retry without wildcard
                // retry without wildcard
                log.warn( "Failed to search in {}. Retrying search without wildcard.", model, e );
                try {
                    iterator = runSearch( index, queryString );
                } catch ( OntologySearchJenaException e1 ) {
                    throw new RetryWithoutWildcardFailedException( "Failed to search while retrying without wildcard.", queryString, e.getCause(), e1.getCause() );
                }
            }
        } else {
            iterator = runSearch( index, queryString );
        }

        while ( iterator.hasNext() ) {
            RDFNode r = iterator.next();
            r = r.inModel( model );
            log.debug( "Search results: {}.", r );
            if ( r.isURIResource() && r.canAs( OntClass.class ) ) {
                try {
                    OntClass cl = r.as( OntClass.class );
                    OntologyTermImpl impl2 = new OntologyTermImpl( cl );
                    if ( impl2.isTermObsolete() ) continue;
                    results.add( impl2 );
                    if ( log.isDebugEnabled() ) log.debug( impl2.toString() );
                } catch ( JenaException e ) {
                    // these are completely uninformative exceptions at the moment.
                    throw new OntologySearchJenaException( e.getMessage(), queryString, e );
                }
            } else if ( r.isResource() && r.canAs( Individual.class ) ) {
                try {
                    Individual cl = r.as( Individual.class );
                    OntologyIndividual impl2 = new OntologyIndividualImpl( cl );
                    results.add( impl2 );
                    if ( log.isDebugEnabled() ) log.debug( impl2.toString() );
                } catch ( JenaException e ) {
                    // these are completely uninformative exceptions at the moment.
                    throw new OntologySearchJenaException( e.getMessage(), queryString, e );
                }
            } else {
                log.debug( "This search term not included in the results: {}.", r );
            }

        }
        return results;

    }

    /**
     * Will remove characters that jena is unable to parse. Will also escape and remove leading and trailing white space
     * (which also causes jena to die)
     *
     * @param  toStrip the string to clean
     * @return
     */
    public static String stripInvalidCharacters( String toStrip ) {
        String result = StringUtils.strip( toStrip );
        for ( char badChar : INVALID_CHARS ) {
            result = StringUtils.remove( result, badChar );
        }
        /*
         * Queries cannot start with '*' or ?
         */
        result = result.replaceAll( "^\\**", "" );
        result = result.replaceAll( "^\\?*", "" );

        return StringEscapeUtils.escapeJava( result ).trim();
    }

    private static NodeIterator runSearch( SearchIndex index, String queryString ) throws OntologySearchJenaException {
        String strippedQuery = StringUtils.strip( queryString );

        if ( StringUtils.isBlank( strippedQuery ) ) {
            throw new IllegalArgumentException( "Query cannot be blank" );
        }

        String query = queryString.replaceAll( " AND ", " " );
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile( "([^\"]\\S*|\".+?\")\\s*" ).matcher( query );
        while ( m.find() ) {
            list.add( m.group( 1 ) );
        }
        String enhancedQuery = StringUtils.join( list, " " + Operator.AND + " " );

        // Note: LARQ does not allow you to change the default operator without making it non-thread-safe.
        index.getLuceneQueryParser().setDefaultOperator( Operator.AND );

        StopWatch timer = StopWatch.createStarted();
        try {
            return index.searchModelByIndex( enhancedQuery );
        } catch ( JenaException e ) {
            throw new OntologySearchJenaException( "Failed to search with enhanced query.", enhancedQuery, e );
        } finally {
            timer.stop();
            if ( timer.getTime() > 100 ) {
                log.info( "Ontology resource search for: {} (parsed to: {}) took {} ms.", queryString, enhancedQuery, timer.getTime() );
            }
        }
    }
}
