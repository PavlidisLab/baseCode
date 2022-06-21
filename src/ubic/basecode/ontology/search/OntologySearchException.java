package ubic.basecode.ontology.search;

/**
 * Base class for exceptions raised by {@link OntologySearch}.
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
