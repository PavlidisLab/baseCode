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

package ubic.basecode.ontology.providers;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.arp.ARPErrorNumbers;
import com.hp.hpl.jena.rdf.arp.ParseException;
import com.hp.hpl.jena.shared.JenaException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologyIndexer;
import ubic.basecode.ontology.search.OntologySearch;
import ubic.basecode.ontology.search.OntologySearchException;
import ubic.basecode.ontology.search.SearchIndex;
import ubic.basecode.util.Configuration;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author kelsey
 */
@SuppressWarnings("unused")
public abstract class AbstractOntologyService {

    protected static Logger log = LoggerFactory.getLogger( AbstractOntologyService.class );

    /**
     * Lock used to prevent reads while the ontology is being initialized.
     */
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    /* internal state protected by rwLock */
    private OntModel model;
    private Map<String, OntologyIndividual> individuals;
    private Map<String, OntologyTerm> terms;
    private Map<String, OntologyTerm> alternativeIDs;
    private SearchIndex index;

    private boolean isInitialized = false;

    /**
     * Initialize this ontology service.
     */
    public void initialize( boolean forceLoad, boolean forceIndexing ) {
        if ( !forceLoad && isInitialized ) {
            log.warn( getOntologyName() + " is already loaded, and force=false, not restarting" );
            return;
        }

        boolean loadOntology = isEnabled();

        // If loading ontologies is disabled in the configuration, return
        if ( !forceLoad && !loadOntology ) {
            log.debug( "Loading " + getOntologyName() + " is disabled (force=" + forceLoad + ", "
                    + "Configuration load." + getOntologyName() + "=" + loadOntology + ")" );
            return;
        }

        // Detect configuration problems.
        if ( StringUtils.isBlank( this.getOntologyUrl() ) ) {
            throw new IllegalStateException( "URL not defined, ontology cannot be loaded ("
                    + this.getClass().getSimpleName() + ")" );
        }

        // This thread indexes ontology and creates local cache for uri->ontology terms mappings.
        if ( !forceIndexing ) {
            log.info( getOntologyName() + " index will *not* be refreshed unless the ontology "
                    + "has changed or the index is misssing" );
        }

        log.info( "Loading ontology: " + getOntologyName() + " from " + getOntologyUrl() + " ..." );
        StopWatch loadTime = StopWatch.createStarted();

        // use temporary variables so we can minimize the critical region for replacing the service's state
        Map<String, OntologyTerm> terms = new HashMap<>();
        Map<String, OntologyIndividual> individuals = new HashMap<>();
        OntModel model;
        SearchIndex index;

        if ( Thread.currentThread().isInterrupted() ) {
            log.warn( String.format( "The current thread is interrupted, initialization of %s will be stop.", getOntologyName() ) );
            return;
        }

        model = loadModel(); // can take a while.
        assert model != null;

        //Checks if the current ontology has changed since it was last loaded.
        boolean changed = OntologyLoader.hasChanged( getOntologyName() );
        boolean indexExists = OntologyIndexer.getSubjectIndex( getOntologyName() ) != null;
        boolean forceReindexing = forceLoad && forceIndexing;

        /*
         * Indexing is slow, don't do it if we don't have to.
         */
        boolean force = forceReindexing || changed || !indexExists;

        index = OntologyIndexer.indexOntology( getOntologyName(), model, force );

        if ( Thread.currentThread().isInterrupted() ) {
            log.warn( String.format( "The current thread is interrupted, initialization of %s will be stop.", getOntologyName() ) );
            return;
        }

        /*
         * This creates a cache of URI (String) --> OntologyTerms. ?? Does Jena provide an easier way to do
         * this?
         */
        Collection<OntologyResource> t = OntologyLoader.initialize( getOntologyUrl(), model );
        addTerms( terms, individuals, t );

        Lock lock = rwLock.writeLock();
        try {
            lock.lock();
            this.model = model;
            this.terms = terms;
            this.individuals = individuals;
            this.index = index;
            this.isInitialized = true;
        } finally {
            lock.unlock();
        }

        // now that the terms have been replaced, we can clear old caches
        OntologyLoader.deleteOldCache( getOntologyName() );

        loadTime.stop();

        log.info( String.format( "Finished loading %s in %ss", getOntologyName(), String.format( "%.2f", loadTime.getTime() / 1000.0 ) ) );
    }

    /**
     * Do not do this except before re-indexing.
     */
    public void closeIndex() {
        if ( index == null ) return;
        index.close();
    }

    /**
     * Looks for any OntologyIndividuals that match the given search string.
     */
    public Collection<OntologyIndividual> findIndividuals( String search ) throws OntologySearchException {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( String.format( "Ontology %s is not ready, no individuals will be returned.", getOntologyName() ) );
                return Collections.emptySet();
            }
            if ( index == null ) {
                log.warn( "attempt to search " + this.getOntologyName() + " when index is null" );
                return Collections.emptySet();
            }
            return OntologySearch.matchIndividuals( model, index, search );
        } finally {
            lock.unlock();
        }
    }

    /**
     * Looks for any OntologyIndividuals or ontologyTerms that match the given search string
     *
     * @return results, or an empty collection if the results are empty OR the ontology is not available to be
     * searched.
     */
    public Collection<OntologyResource> findResources( String searchString ) throws OntologySearchException {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( String.format( "Ontology %s is not ready, no resources will be returned.", getOntologyName() ) );
                return Collections.emptySet();
            }
            if ( index == null ) {
                log.warn( "attempt to search " + this.getOntologyName() + " when index is null" );
                return Collections.emptySet();
            }
            return OntologySearch.matchResources( model, index, searchString );
        } finally {
            lock.unlock();
        }
    }

    /**
     * Looks for any ontologyTerms that match the given search string. Obsolete terms are filtered out.
     */
    public Collection<OntologyTerm> findTerm( String search ) throws OntologySearchException {
        if ( log.isDebugEnabled() ) log.debug( "Searching " + getOntologyName() + " for '" + search + "'" );
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( String.format( "Ontology %s is not ready, no terms will be returned.", getOntologyName() ) );
                return Collections.emptySet();
            }
            if ( index == null ) {
                log.warn( "attempt to search " + this.getOntologyName() + " when index is null" );
                return Collections.emptySet();
            }
            return OntologySearch.matchClasses( model, index, search );
        } finally {
            lock.unlock();
        }
    }

    public OntologyTerm findUsingAlternativeId( String alternativeId ) {
        Lock lock = alternativeIDs != null ? rwLock.readLock() : rwLock.writeLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( String.format( "Ontology %s is not ready, null will be returned for alternative ID match.", getOntologyName() ) );
                return null;
            }
            if ( alternativeIDs == null ) {
                log.info( "init search by alternativeID" );
                initSearchByAlternativeId();
            }
            return alternativeIDs.get( alternativeId );
        } finally {
            lock.unlock();
        }
    }

    public Set<String> getAllURIs() {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( String.format( "Ontology %s is not ready, no term  URIs will be returned.", getOntologyName() ) );
                return Collections.emptySet();
            }
            return new HashSet<>( terms.keySet() );
        } finally {
            lock.unlock();
        }
    }

    /**
     * Looks through both Terms and Individuals for a OntologyResource that has a uri matching the uri given. If no
     * OntologyTerm is found only then will ontologyIndividuals be searched. returns null if nothing is found.
     */
    public OntologyResource getResource( String uri ) {
        if ( uri == null ) return null;
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                return null;
            }
            OntologyResource resource = terms.get( uri );

            if ( resource == null ) resource = individuals.get( uri );

            return resource;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Looks for a OntologyTerm that has the match in URI given
     */
    public OntologyTerm getTerm( String uri ) {
        if ( uri == null ) throw new IllegalArgumentException( "URI cannot be null" );
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) return null;
            return terms.get( uri );
        } finally {
            lock.unlock();
        }
    }

    public Collection<OntologyIndividual> getTermIndividuals( String uri ) {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                return Collections.emptySet();
            }
            OntologyTerm term = terms.get( uri );
            if ( term == null ) {
                /*
                 * Either the ontology hasn't been loaded, or the id was not valid.
                 */
                log.warn( "No term for URI=" + uri + " in " + this.getOntologyName()
                        + "; make sure ontology is loaded and uri is valid" );
                return new HashSet<>();
            }
            return term.getIndividuals( true );
        } finally {
            lock.unlock();
        }
    }

    public boolean isEnabled() {
        // quick path: just lookup the configuration
        String configParameter = "load." + getOntologyName();
        if ( Configuration.getBoolean( configParameter ) ) {
            return true;
        }
        // could have forced, without setting config
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            return isInitialized;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Used for determining if the Ontology has finished loading into memory. Although calls like getParents,
     * getChildren will still work (its much faster once the ontologies have been preloaded into memory.)
     */
    public boolean isOntologyLoaded() {
        // it's fine not to use the read lock here
        return isInitialized;
    }

    private Thread initializationThread = null;

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
    public synchronized void startInitializationThread( boolean forceLoad, boolean forceIndexing ) {
        if ( initializationThread != null && initializationThread.isAlive() ) {
            log.warn( String.format( " Initialization thread for %s is currently running, not restarting.", getOntologyName() ) );
            return;
        }
        // create and start the initialization thread
        initializationThread = new Thread( () -> {
            try {
                this.initialize( forceLoad, forceIndexing );
            } catch ( JenaException e ) {
                if ( !( e.getCause() instanceof ParseException ) || ( ( ParseException ) e.getCause() ).getErrorNumber() != ARPErrorNumbers.ERR_INTERRUPTED ) {
                    throw e;
                }
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
                this.isInitialized = false;
            }
        }, getOntologyName() + "_load_thread_" + RandomStringUtils.randomAlphanumeric( 5 ) );
        // To prevent VM from waiting on this thread to shutdown (if shutting down).
        initializationThread.setDaemon( true );
        initializationThread.start();
    }

    public boolean isInitializationThreadAlive() {
        return initializationThread != null && initializationThread.isAlive();
    }

    public boolean isInitializationThreadCancelled() {
        return initializationThread != null && initializationThread.isInterrupted();
    }

    /**
     * Cancel the initialization thread.
     */
    public void cancelInitializationThread() {
        if ( initializationThread == null ) {
            throw new IllegalStateException( "The initialization thread has not started. Invoke startInitializationThread() first." );
        }
        initializationThread.interrupt();
    }

    /**
     * Wait for the initialization thread to finish.
     */
    public void waitForInitializationThread() throws InterruptedException {
        if ( initializationThread == null ) {
            throw new IllegalStateException( "The initialization thread has not started. Invoke startInitializationThread() first." );
        }
        initializationThread.join();
    }

    /**
     * The simple getOntologyName() of the ontology. Used for indexing purposes. (ie this will determine the getOntologyName() of the underlying
     * index for searching the ontology)
     */
    protected abstract String getOntologyName();

    /**
     * Defines the location of the ontology eg: <a href="http://mged.sourceforge.net/ontologies/MGEDOntology.owl">MGED</a>
     */
    protected abstract String getOntologyUrl();

    /**
     * Delegates the call as to load the model into memory or leave it on disk. Simply delegates to either
     * OntologyLoader.loadMemoryModel( url ); OR OntologyLoader.loadPersistentModel( url, spec );
     */
    protected abstract OntModel loadModel();

    /**
     * Index the ontology for performing full-text searches.
     *
     * @see #findIndividuals(String)
     * @see #findTerm(String)
     * @see #findResources(String)
     */
    public void index( boolean force ) {
        Lock lock = rwLock.writeLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( String.format( "Ontology %s is not initialized, cannot index it.", getOntologyName() ) );
                return;
            }
            index = OntologyIndexer.indexOntology( getOntologyName(), model, force );
        } finally {
            lock.unlock();
        }
    }

    /**
     * Initialize alternative IDs mapping.
     * <p>
     * this add alternative id in 2 ways
     * <p>
     * Example :
     * <p>
     * <a href="http://purl.obolibrary.org/obo/HP_0000005">HP_0000005</a> with alternative id : HP:0001453
     * <p>
     * by default way use in file 1- HP:0001453 -----> <a href="http://purl.obolibrary.org/obo/HP_0000005">HP_0000005</a>
     * <p>
     * trying <a href=" to use the value uri 2- http://purl.obol">HP_0001453</a>ibrary.org/obo/HP_0001453 ----->
     * <a href="http://purl.obolibrary.org/obo/HP_0000005">HP_0000005</a>
     */
    private void initSearchByAlternativeId() {
        // lets find the baseUrl, to change to valueUri
        String randomUri = terms.values().iterator().next().getUri();
        String baseOntologyUri = randomUri.substring( 0, randomUri.lastIndexOf( "/" ) + 1 );
        alternativeIDs = new HashMap<>();

        // for all Ontology terms that exist in the tree
        for ( OntologyTerm ontologyTerm : terms.values() ) {

            for ( String alternativeId : ontologyTerm.getAlternativeIds() ) {
                // first way
                alternativeIDs.put( alternativeId, ontologyTerm );

                String alternativeIdModified = alternativeId.replace( ':', '_' );

                // second way
                alternativeIDs.put( baseOntologyUri + alternativeIdModified, ontologyTerm );
            }
        }
    }

    /**
     * For testing! Overrides normal way of loading the ontology. This does not index the ontology unless 'force' is
     * true (if there is an existing index, it will be used)
     *
     * @param is         input stream from which the ontology model is loaded
     * @param forceIndex initialize the index. Otherwise it will only be initialized if it doesn't exist.
     */
    public void loadTermsInNameSpace( InputStream is, boolean forceIndex ) {
        Lock lock = rwLock.writeLock();
        try {
            lock.lock();
            this.isInitialized = false;

            if ( initializationThread != null && initializationThread.isAlive() ) {
                log.warn( this.getOntologyName() + " initialization is already running, trying to cancel ..." );
                initializationThread.interrupt();
                // wait for the thread to die.
                int maxWait = 10;
                int wait = 0;
                while ( initializationThread.isAlive() ) {
                    try {
                        initializationThread.join( 5000 );
                        log.warn( "Waiting for auto-initialization to stop so manual initialization can begin ..." );
                    } catch ( InterruptedException e ) {
                        Thread.currentThread().interrupt();
                        log.warn( String.format( "Got interrupted while waiting for the initialization thread of %s to finish.", getOntologyName() ) );
                        return;
                    }
                    ++wait;
                    if ( wait >= maxWait && !initializationThread.isAlive() ) {
                        throw new RuntimeException( String.format( "Got tired of waiting for %s's initialization thread.", getOntologyName() ) );
                    }
                }
            }

            this.model = OntologyLoader.loadMemoryModel( is, this.getOntologyUrl(), OntModelSpec.OWL_MEM );
            this.index = OntologyIndexer.getSubjectIndex( getOntologyName() );
            if ( index == null || forceIndex ) {
                this.index = OntologyIndexer.indexOntology( getOntologyName(), model, true /* force */ );
            }

            this.terms = new HashMap<>();
            this.individuals = new HashMap<>();

            Collection<OntologyResource> newTerms = OntologyLoader.initialize( this.getOntologyUrl(), model );
            if ( newTerms.isEmpty() ) {
                log.warn( "No terms!" );
            } else {
                addTerms( this.terms, this.individuals, newTerms );
            }

            isInitialized = true;
        } finally {
            lock.unlock();
        }

        log.info( this.getClass().getSimpleName() + " ready" );
    }

    private void addTerms( Map<String, OntologyTerm> terms, Map<String, OntologyIndividual> individuals, Collection<OntologyResource> newTerms ) {
        if ( newTerms == null || newTerms.isEmpty() ) {
            log.warn( "No terms!" );
            return;
        }
        int i = 0;
        for ( OntologyResource term : newTerms ) {
            if ( term.getUri() == null ) continue;
            if ( term instanceof OntologyTerm ) terms.put( term.getUri(), ( OntologyTerm ) term );
            if ( term instanceof OntologyIndividual ) individuals.put( term.getUri(), ( OntologyIndividual ) term );
            if ( ++i % 1000 == 0 ) {
                if ( Thread.currentThread().isInterrupted() ) {
                    log.error( "Cancelled initialization" );
                    this.isInitialized = false;
                    return;
                }
            }
        }
    }
}