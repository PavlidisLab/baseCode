package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.shared.JenaException;

/**
 * Exception raised when retrying a search without a wildcard still fails.
 * @author poirigui
 */
class RetryWithoutWildcardFailedException extends OntologySearchJenaException {

    private final JenaException firstAttemptCause;

    public RetryWithoutWildcardFailedException( String message, String query, JenaException firstAttemptCause, JenaException cause ) {
        super( message, query, cause );
        this.firstAttemptCause = firstAttemptCause;
    }

    /**
     * Obtain the cause or error for the first attempt.
     */
    public JenaException getFirstAttemptCause() {
        return firstAttemptCause;
    }
}
