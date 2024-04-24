package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import ubic.basecode.ontology.search.OntologySearchException;

import java.util.Objects;

import static ubic.basecode.ontology.jena.JenaUtils.where;

interface SearchIndex extends AutoCloseable {

    /**
     * Find RDF nodes matching the given query string.
     */
    ExtendedIterator<JenaSearchResult> search( OntModel model, String queryString ) throws OntologySearchException;

    /**
     * Find classes that match the query string.
     *
     * @param model that goes with the index
     * @return Collection of OntologyTerm objects
     */
    default ExtendedIterator<JenaSearchResult> searchClasses( OntModel model, String queryString ) throws OntologySearchException {
        return search( model, queryString )
                .filterKeep( where( r -> r.result.isURIResource() && r.result.canAs( OntClass.class ) ) )
                .filterKeep( where( Objects::nonNull ) );
    }

    /**
     * Find individuals that match the query string
     *
     * @param model that goes with the index
     * @return Collection of OntologyTerm objects
     */
    default ExtendedIterator<JenaSearchResult> searchIndividuals( OntModel model, String queryString ) throws OntologySearchException {
        return search( model, queryString )
                .filterKeep( where( r -> r.result.isURIResource() && r.result.canAs( Individual.class ) ) )
                .filterKeep( where( Objects::nonNull ) );
    }

    /**
     * Find OntologyIndividuals and OntologyTerms that match the query string. Search with a wildcard is attempted
     * whenever possible.
     *
     * @param model that goes with the index
     * @return Collection of OntologyResource objects
     */
    default ExtendedIterator<JenaSearchResult> searchResources( OntModel model, String queryString ) throws OntologySearchException {
        return search( model, queryString )
                .filterKeep( where( o -> o.result.isURIResource() && o.result.isResource() ) )
                .filterKeep( where( Objects::nonNull ) );
    }

    class JenaSearchResult {

        public final RDFNode result;
        public final double score;

        JenaSearchResult( RDFNode result, double score ) {
            this.result = result;
            this.score = score;
        }

        @Override
        public String toString() {
            return String.format( "%s score=%f", result, score );
        }
    }
}
