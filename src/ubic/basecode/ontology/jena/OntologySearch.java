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
package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.ontology.search.OntologySearchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hp.hpl.jena.sparql.util.ModelUtils.convertGraphNodeToRDFNode;
import static ubic.basecode.ontology.jena.JenaUtils.where;

/**
 * @author pavlidis
 */
class OntologySearch {

    private static final Logger log = LoggerFactory.getLogger( OntologySearch.class );

    /**
     * Find classes that match the query string.
     *
     * @param model that goes with the index
     * @param index to search
     * @return Collection of OntologyTerm objects
     */
    public static ExtendedIterator<SearchResult<OntClass>> matchClasses( OntModel model, SearchIndex index, String queryString ) throws OntologySearchException {
        return runSearch( model, index, queryString )
                .filterKeep( where( r -> r.result.isURIResource() && r.result.canAs( OntClass.class ) ) )
                .mapWith( r -> r.as( OntClass.class ) )
                .filterKeep( where( Objects::nonNull ) );
    }

    /**
     * Find individuals that match the query string
     *
     * @param model that goes with the index
     * @param index to search
     * @return Collection of OntologyTerm objects
     */
    public static ExtendedIterator<SearchResult<Individual>> matchIndividuals( OntModel model, SearchIndex index, String queryString ) throws OntologySearchException {
        return runSearchWithWildcard( model, index, queryString )
                .filterKeep( where( r -> r.result.isURIResource() && r.result.canAs( Individual.class ) ) )
                .mapWith( r -> r.as( Individual.class ) )
                .filterKeep( where( Objects::nonNull ) );
    }

    /**
     * Find OntologyIndividuals and OntologyTerms that match the query string. Search with a wildcard is attempted
     * whenever possible.
     *
     * @param model that goes with the index
     * @param index to search
     * @return Collection of OntologyResource objects
     */
    public static ExtendedIterator<SearchResult<Resource>> matchResources( OntModel model, SearchIndex index, String queryString ) throws OntologySearchException {
        return runSearchWithWildcard( model, index, queryString )
                .filterKeep( where( o -> o.result.isURIResource() && o.result.isResource() ) )
                .mapWith( r -> r.as( Resource.class ) )
                .filterKeep( where( Objects::nonNull ) );
    }

    private static ExtendedIterator<SearchResult<RDFNode>> runSearchWithWildcard( Model model, SearchIndex index, String queryString ) throws OntologySearchException {
        ExtendedIterator<SearchResult<RDFNode>> iterator;

        queryString = queryString.trim();

        // Add wildcard only if the last word is longer than one character. This is to prevent lucene from
        // blowing up. See bug#1145
        String[] words = queryString.split( "\\s+" );
        int lastWordLength = words[words.length - 1].length();
        if ( lastWordLength > 1 ) {
            try { // Use wildcard search.
                iterator = runSearch( model, index, queryString + "*" );
            } catch ( OntologySearchJenaException e ) { // retry without wildcard
                // retry without wildcard
                log.warn( "Failed to search in {}. Retrying search without wildcard.", model, e );
                try {
                    iterator = runSearch( model, index, queryString );
                } catch ( OntologySearchJenaException e1 ) {
                    throw new RetryWithoutWildcardFailedException( "Failed to search while retrying without wildcard.", queryString, e.getCause(), e1.getCause() );
                }
            }
        } else {
            iterator = runSearch( model, index, queryString );
        }

        return iterator;
    }

    private static ExtendedIterator<SearchResult<RDFNode>> runSearch( Model model, SearchIndex index, String queryString ) throws OntologySearchJenaException {
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
            return new Map1Iterator<>( o -> new SearchResult<>( o.getLuceneDocId(), convertGraphNodeToRDFNode( o.getNode(), model ), o.getScore() ),
                    index.search( enhancedQuery ) );
        } catch ( JenaException e ) {
            throw new OntologySearchJenaException( "Failed to search with enhanced query.", enhancedQuery, e );
        } finally {
            timer.stop();
            if ( timer.getTime() > 100 ) {
                log.warn( "Ontology resource search for: {} (parsed to: {}) took {} ms.", queryString, enhancedQuery, timer.getTime() );
            }
        }
    }

    public static class SearchResult<T extends RDFNode> {
        public final int docId;
        public final T result;
        public final double score;

        private SearchResult( int docId, T result, double score ) {
            this.docId = docId;
            this.result = result;
            this.score = score;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( obj instanceof SearchResult ) {
                return Objects.equals( result, ( ( SearchResult<?> ) obj ).result );
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash( result );
        }

        @Override
        public String toString() {
            return String.format( "%s [docId = %d, score = %f]", result, docId, score );
        }

        private <U extends Resource> SearchResult<U> as( Class<U> clazz ) {
            try {
                return new SearchResult<>( docId, result.as( clazz ), score );
            } catch ( ConversionException e ) {
                log.error( "Conversion failed for " + result, e );
                return null;
            }
        }
    }
}
