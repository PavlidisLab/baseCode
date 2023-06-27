package ubic.basecode.ontology.search;

import ubic.basecode.ontology.providers.OntologyService;

/**
 * Base class for exceptions raised by {@link OntologyService#findTerm(String)} and others.
 * @author poirigui
 */
public class OntologySearchException extends Exception {
    private final String query;

    public OntologySearchException( String message, String query, Throwable cause ) {
        super( message, cause );
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
