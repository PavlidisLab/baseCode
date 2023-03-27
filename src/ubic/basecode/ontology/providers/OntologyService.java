package ubic.basecode.ontology.providers;

import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologySearchException;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

public interface OntologyService {

    /**
     * Initialize this ontology service.
     */
    void initialize( boolean forceLoad, boolean forceIndexing );

    /**
     * Looks for any OntologyIndividuals that match the given search string.
     */
    Collection<OntologyIndividual> findIndividuals( String search ) throws OntologySearchException;

    /**
     * Looks for any OntologyIndividuals or ontologyTerms that match the given search string
     *
     * @return results, or an empty collection if the results are empty OR the ontology is not available to be
     * searched.
     */
    Collection<OntologyResource> findResources( String searchString ) throws OntologySearchException;

    /**
     * Looks for any ontologyTerms that match the given search string. Obsolete terms are filtered out.
     */
    Collection<OntologyTerm> findTerm( String search ) throws OntologySearchException;

    OntologyTerm findUsingAlternativeId( String alternativeId );

    Set<String> getAllURIs();

    /**
     * Looks through both Terms and Individuals for a OntologyResource that has a uri matching the uri given. If no
     * OntologyTerm is found only then will ontologyIndividuals be searched. returns null if nothing is found.
     */
    OntologyResource getResource( String uri );

    /**
     * Looks for a OntologyTerm that has the match in URI given
     */
    OntologyTerm getTerm( String uri );

    Collection<OntologyIndividual> getTermIndividuals( String uri );

    boolean isEnabled();

    /**
     * Used for determining if the Ontology has finished loading into memory. Although calls like getParents,
     * getChildren will still work (its much faster once the ontologies have been preloaded into memory.)
     */
    boolean isOntologyLoaded();

    /**
     * Start the initialization thread.
     * <p>
     * If the initialization thread is already running, this method does nothing. If the initialization thread
     * previously completed, the ontology will be reinitialized.
     *
     * @param forceLoad     Force loading of ontology, even if it is already loaded
     * @param forceIndexing If forceLoad is also true, indexing will be performed. If you know the index is
     *                      up to date, there's no need to do it again. Normally indexing is only done if there is no
     *                      index, or if the ontology has changed since last loaded.
     */
    void startInitializationThread( boolean forceLoad, boolean forceIndexing );

    boolean isInitializationThreadAlive();

    boolean isInitializationThreadCancelled();

    void cancelInitializationThread();

    /**
     * Wait for the initialization thread to finish.
     */
    void waitForInitializationThread() throws InterruptedException;

    /**
     * Index the ontology for performing full-text searches.
     *
     * @see #findIndividuals(String)
     * @see #findTerm(String)
     * @see #findResources(String)
     */
    void index( boolean force );

    /**
     * For testing! Overrides normal way of loading the ontology. This does not index the ontology unless 'force' is
     * true (if there is an existing index, it will be used)
     *
     * @param is         input stream from which the ontology model is loaded
     * @param forceIndex initialize the index. Otherwise it will only be initialized if it doesn't exist.
     */
    void loadTermsInNameSpace( InputStream is, boolean forceIndex );
}
