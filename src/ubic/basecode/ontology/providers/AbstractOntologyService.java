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

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.arp.ARPErrorNumbers;
import com.hp.hpl.jena.rdf.arp.ParseException;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.model.*;
import ubic.basecode.ontology.search.OntologyIndexer;
import ubic.basecode.ontology.search.OntologySearch;
import ubic.basecode.ontology.search.OntologySearchException;
import ubic.basecode.ontology.search.SearchIndex;
import ubic.basecode.util.Configuration;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author kelsey
 */
@SuppressWarnings("unused")
public abstract class AbstractOntologyService implements OntologyService {

    protected static Logger log = LoggerFactory.getLogger( AbstractOntologyService.class );

    /**
     * Lock used to prevent reads while the ontology is being initialized.
     */
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    /* internal state protected by rwLock */
    private OntModel model;
    private Map<String, String> alternativeIDs;
    private final Map<String, OntologyTerm> termCache = new ConcurrentHashMap<>( 1024 );
    private SearchIndex index;

    private boolean isInitialized = false;

    @Override
    public void initialize( boolean forceLoad, boolean forceIndexing ) {
        if ( !forceLoad && isInitialized ) {
            log.warn( getOntologyName() + " is already loaded, and force=false, not restarting" );
            return;
        }

        boolean loadOntology = isEnabled();

        // If loading ontologies is disabled in the configuration, return
        if ( !forceLoad && !loadOntology ) {
            log.debug( "Loading " + getOntologyName() + " is disabled (force=" + forceLoad + ", " + "Configuration load." + getOntologyName() + "=" + loadOntology + ")" );
            return;
        }

        // Detect configuration problems.
        if ( StringUtils.isBlank( this.getOntologyUrl() ) ) {
            throw new IllegalStateException( "URL not defined, ontology cannot be loaded (" + this.getClass().getSimpleName() + ")" );
        }

        // This thread indexes ontology and creates local cache for uri->ontology terms mappings.
        if ( !forceIndexing ) {
            log.info( getOntologyName() + " index will *not* be refreshed unless the ontology " + "has changed or the index is misssing" );
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

        Lock lock = rwLock.writeLock();
        try {
            lock.lock();
            this.model = model;
            this.termCache.clear();
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
            String termUri = alternativeIDs.get( alternativeId );
            return termUri != null ? getTerm( termUri ) : null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<String> getAllURIs() {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( String.format( "Ontology %s is not ready, no term  URIs will be returned.", getOntologyName() ) );
                return Collections.emptySet();
            }
            Set<String> allUris = new HashSet<>();
            ExtendedIterator<OntClass> iterator = model.listClasses();
            while ( iterator.hasNext() ) {
                allUris.add( iterator.next().getURI() );
            }
            ExtendedIterator<Individual> it2 = model.listIndividuals();
            while ( it2.hasNext() ) {
                allUris.add( it2.next().getURI() );
            }
            return allUris;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public OntologyResource getResource( String uri ) {
        if ( uri == null ) return null;
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                return null;
            }
            OntologyResource res;
            Resource resource = model.getResource( uri );
            if ( resource.getURI() == null ) {
                return null;
            }
            if ( resource instanceof OntClass ) {
                // use the cached term
                res = getTermInternal( uri );
            } else if ( resource instanceof Individual ) {
                res = new OntologyIndividualImpl( ( Individual ) resource );
            } else if ( resource instanceof OntProperty ) {
                res = PropertyFactory.asProperty( ( ObjectProperty ) resource );
            } else {
                res = null;
            }
            return res;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public OntologyTerm getTerm( String uri ) {
        if ( uri == null ) throw new IllegalArgumentException( "URI cannot be null" );
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) return null;
            return getTermInternal( uri );
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<OntologyIndividual> getTermIndividuals( String uri ) {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                return Collections.emptySet();
            }
            OntologyTerm term = getTermInternal( uri );
            if ( term == null ) {
                /*
                 * Either the ontology hasn't been loaded, or the id was not valid.
                 */
                log.warn( "No term for URI=" + uri + " in " + this.getOntologyName() + "; make sure ontology is loaded and uri is valid" );
                return new HashSet<>();
            }
            return term.getIndividuals( true );
        } finally {
            lock.unlock();
        }
    }

    @Override
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

    @Override
    public boolean isOntologyLoaded() {
        // it's fine not to use the read lock here
        return isInitialized;
    }

    private Thread initializationThread = null;

    @Override
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

    @Override
    public boolean isInitializationThreadAlive() {
        return initializationThread != null && initializationThread.isAlive();
    }

    @Override
    public boolean isInitializationThreadCancelled() {
        return initializationThread != null && initializationThread.isInterrupted();
    }

    /**
     * Cancel the initialization thread.
     */
    @Override
    public void cancelInitializationThread() {
        if ( initializationThread == null ) {
            throw new IllegalStateException( "The initialization thread has not started. Invoke startInitializationThread() first." );
        }
        initializationThread.interrupt();
    }

    @Override
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

    @Override
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
        alternativeIDs = new HashMap<>();
        // for all Ontology terms that exist in the tree
        ExtendedIterator<OntClass> iterator = model.listClasses();
        while ( iterator.hasNext() ) {
            OntClass ind = iterator.next();
            OntologyTerm ontologyTerm = new OntologyTermImpl( ind );
            // lets find the baseUri, to change to valueUri
            String baseOntologyUri = ontologyTerm.getUri().substring( 0, ontologyTerm.getUri().lastIndexOf( "/" ) + 1 );
            for ( String alternativeId : ontologyTerm.getAlternativeIds() ) {
                // first way
                alternativeIDs.put( alternativeId, ontologyTerm.getUri() );
                // second way
                String alternativeIdModified = alternativeId.replace( ':', '_' );
                alternativeIDs.put( baseOntologyUri + alternativeIdModified, ontologyTerm.getUri() );
            }
        }
    }

    private OntologyTerm getTermInternal( String uri ) {
        return termCache.computeIfAbsent( uri, u -> {
            OntClass ontCls = model.getOntClass( u );
            // null or bnode
            if ( ontCls == null || ontCls.getURI() == null ) {
                return null;
            }
            return new OntologyTermImpl( ontCls );
        } );
    }

    @Override
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

            this.model = OntologyLoader.loadMemoryModel( is, this.getOntologyUrl() );
            this.termCache.clear();
            this.index = OntologyIndexer.getSubjectIndex( getOntologyName() );
            if ( index == null || forceIndex ) {
                this.index = OntologyIndexer.indexOntology( getOntologyName(), model, true /* force */ );
            }

            isInitialized = true;
        } finally {
            lock.unlock();
        }

        log.info( this.getClass().getSimpleName() + " ready" );
    }
}