package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.shared.JenaException;
import ubic.basecode.ontology.search.OntologySearchException;

/**
 * Base class for Jena-related ontology search exceptions.
 */
class OntologySearchJenaException extends OntologySearchException {

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
