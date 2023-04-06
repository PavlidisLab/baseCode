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
     *
     * @param forceLoad     Force loading of ontology, even if it is already loaded
     * @param forceIndexing If forceLoad is also true, indexing will be performed. If you know the index is up-to-date,
     *                      there's no need to do it again. Normally indexing is only done if there is no index, or if
     *                      the ontology has changed since last loaded.
     */
    void initialize( boolean forceLoad, boolean forceIndexing );

    /**
     * Initialize this ontology service from a stream.
     * <p>
     * Note that when this method of initialization is used, the ontology cache is not created on-disk.
     */
    void initialize( InputStream stream, boolean forceIndexing );

    /**
     * Looks for any individuals that match the given search string.
     * <p>
     * Obsolete terms are filtered out.
     */
    default Collection<OntologyIndividual> findIndividuals( String search ) throws OntologySearchException {
        return findIndividuals( search, false );
    }

    /**
     * Looks for any individuals that match the given search string.
     *
     * @param search        search query
     * @param keepObsoletes retain obsolete terms
     */
    Collection<OntologyIndividual> findIndividuals( String search, boolean keepObsoletes ) throws OntologySearchException;

    /**
     * Looks for any resources (terms or individuals) that match the given search string
     * <p>
     * Obsolete terms are filtered out.
     *
     * @return results, or an empty collection if the results are empty OR the ontology is not available to be
     * searched.
     */
    default Collection<OntologyResource> findResources( String searchString ) throws OntologySearchException {
        return findResources( searchString, false );
    }

    /**
     * Looks for any resources (terms or individuals) that match the given search string
     *
     * @param search        search query
     * @param keepObsoletes retain obsolete terms
     */
    Collection<OntologyResource> findResources( String search, boolean keepObsoletes ) throws OntologySearchException;

    /**
     * Looks for any terms that match the given search string.
     * <p>
     * Obsolete terms are filtered out.
     */
    default Collection<OntologyTerm> findTerm( String search ) throws OntologySearchException {
        return findTerm( search, false );
    }


    /**
     * Looks for any terms that match the given search string.
     *
     * @param search        search query
     * @param keepObsoletes retain obsolete terms
     */
    Collection<OntologyTerm> findTerm( String search, boolean keepObsoletes ) throws OntologySearchException;

    /**
     * Find a term using an alternative ID.
     */
    OntologyTerm findUsingAlternativeId( String alternativeId );

    /**
     * Obtain all the resource URIs in this ontology.
     */
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

    /**
     * Obtain all the individuals for a given term URI.
     */
    Collection<OntologyIndividual> getTermIndividuals( String uri );

    /**
     * Obtain all the parents of a given set of terms, excluding obsolete terms.
     *
     * @see #getParents(Collection, boolean, boolean, boolean)
     */
    default Set<OntologyTerm> getParents( Collection<OntologyTerm> terms, boolean direct, boolean includeAdditionalProperties ) {
        return getParents( terms, direct, includeAdditionalProperties, false );
    }

    /**
     * Obtain all the parents of a given set of terms.
     *
     * @param terms                       set of terms whose parents are retrieved
     * @param direct                      only retain direct parents
     * @param includeAdditionalProperties also include parents matched via additional properties
     * @param keepObsoletes               retain obsolete terms
     * @return a set of parent terms
     */
    Set<OntologyTerm> getParents( Collection<OntologyTerm> terms, boolean direct, boolean includeAdditionalProperties, boolean keepObsoletes );

    /**
     * Obtain all the children of a given set of terms, excluding obsolete terms.
     *
     * @see #getChildren(Collection, boolean, boolean, boolean)
     */
    default Set<OntologyTerm> getChildren( Collection<OntologyTerm> terms, boolean direct, boolean includeAdditionalProperties ) {
        return getChildren( terms, direct, includeAdditionalProperties, false );
    }

    /**
     * Obtain all the children of a given set of terms.
     *
     * @param terms                       set of terms whose children are retrieved
     * @param direct                      only retain direct children
     * @param includeAdditionalProperties also include children matched via additional properties
     * @param keepObsoletes               retain obsolete terms
     * @return a set of child terms
     */
    Set<OntologyTerm> getChildren( Collection<OntologyTerm> terms, boolean direct, boolean includeAdditionalProperties, boolean keepObsoletes );


    /**
     * Check if this ontology is enabled.
     */
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

    /**
     * Check if the initialization thread is alive.
     */
    boolean isInitializationThreadAlive();

    /**
     * Check if the initialization thread is cancelled.
     */
    boolean isInitializationThreadCancelled();

    /**
     * Cancel a running initialization thread.
     */
    void cancelInitializationThread();

    /**
     * Wait for the initialization thread to finish.
     */
    void waitForInitializationThread() throws InterruptedException;

    /**
     * Index the ontology for performing full-text searches.
     *
     * @param force if true, perform indexing even if an index already exists
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
     * @deprecated use {@link #initialize(InputStream, boolean)} instead and possibly {@link #cancelInitializationThread()}
     * prior to get any running initialization thread out of the way
     */
    @Deprecated
    void loadTermsInNameSpace( InputStream is, boolean forceIndex );
}
