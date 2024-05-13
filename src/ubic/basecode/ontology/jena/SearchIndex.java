package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import ubic.basecode.ontology.search.OntologySearchException;

import java.util.List;
import java.util.stream.Stream;

interface SearchIndex extends AutoCloseable {

    /**
     * Find RDF nodes matching the given query string.
     */
    List<JenaSearchResult> search( OntModel model, String queryString, int maxResults ) throws OntologySearchException;

    /**
     * Find classes that match the query string.
     *
     * @param model that goes with the index
     * @return Collection of OntologyTerm objects
     */
    List<JenaSearchResult> searchClasses( OntModel model, String queryString, int maxResults ) throws OntologySearchException;

    /**
     * Find individuals that match the query string
     *
     * @param model that goes with the index
     * @return Collection of OntologyTerm objects
     */
    List<JenaSearchResult> searchIndividuals( OntModel model, String queryString, int maxResults ) throws OntologySearchException;

    class JenaSearchResult {

        public final Resource result;
        public final double score;

        JenaSearchResult( Resource result, double score ) {
            this.result = result;
            this.score = score;
        }

        @Override
        public String toString() {
            return String.format( "%s score=%f", result, score );
        }
    }
}
