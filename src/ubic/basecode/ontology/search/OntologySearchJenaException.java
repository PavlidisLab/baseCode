package ubic.basecode.ontology.search;

import com.hp.hpl.jena.shared.JenaException;

/**
 * Base class for Jena-related ontology search exceptions.
 */
public class OntologySearchJenaException extends OntologySearchException {

    private final JenaException cause;

    public OntologySearchJenaException( String message, String query, JenaException cause ) {
        super( message, query, cause );
        this.cause = cause;
    }

    @Override
    public JenaException getCause() {
        return cause;
    }
}
