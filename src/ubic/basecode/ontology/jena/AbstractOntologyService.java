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

package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.arp.ARPErrorNumbers;
import com.hp.hpl.jena.rdf.arp.ParseException;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyModel;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.providers.OntologyService;
import ubic.basecode.ontology.search.OntologySearchException;
import ubic.basecode.util.Configuration;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ubic.basecode.ontology.jena.JenaUtils.where;

/**
 * Base class for Jena-based ontology services.
 *
 * @author kelsey
 */
@SuppressWarnings("unused")
public abstract class AbstractOntologyService implements OntologyService {

    protected static Logger log = LoggerFactory.getLogger( AbstractOntologyService.class );

    /**
     * Properties through which propagation is allowed for {@link #getParents(Collection, boolean, boolean)}}
     */
    private static final Set<String> DEFAULT_ADDITIONAL_PROPERTIES;

    static {
        DEFAULT_ADDITIONAL_PROPERTIES = new HashSet<>();
        DEFAULT_ADDITIONAL_PROPERTIES.add( BFO.partOf.getURI() );
        DEFAULT_ADDITIONAL_PROPERTIES.add( RO.properPartOf.getURI() );
    }

    /* settings (applicable for next initialization) */
    private LanguageLevel nextLanguageLevel = LanguageLevel.FULL;
    private InferenceMode nextInferenceMode = InferenceMode.TRANSITIVE;
    private boolean nextProcessImports = true;
    private boolean nextSearchEnabled = true;
    private Set<String> nextAdditionalPropertyUris = DEFAULT_ADDITIONAL_PROPERTIES;

    /**
     * Lock used to prevent reads while the ontology is being initialized.
     */
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    /* internal state protected by rwLock */
    private OntModel model;
    private Map<String, String> alternativeIDs;
    @Nullable
    private SearchIndex index;
    @Nullable
    private Set<Restriction> additionalRestrictions;
    private boolean isInitialized = false;
    @Nullable
    private LanguageLevel languageLevel = null;
    @Nullable
    private InferenceMode inferenceMode = null;
    @Nullable
    private Boolean processImports = null;
    @Nullable
    private Boolean searchEnabled = null;
    @Nullable
    private Set<String> additionalPropertyUris = null;

    @Override
    public LanguageLevel getLanguageLevel() {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            return this.languageLevel != null ? this.languageLevel : nextLanguageLevel;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setLanguageLevel( LanguageLevel languageLevel ) {
        this.nextLanguageLevel = languageLevel;
    }

    @Override
    public InferenceMode getInferenceMode() {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            return this.inferenceMode != null ? this.inferenceMode : nextInferenceMode;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setInferenceMode( InferenceMode inferenceMode ) {
        this.nextInferenceMode = inferenceMode;
    }

    @Override
    public boolean getProcessImports() {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            return processImports != null ? processImports : nextProcessImports;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setProcessImports( boolean processImports ) {
        this.nextProcessImports = processImports;
    }

    @Override
    public boolean isSearchEnabled() {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            return searchEnabled != null ? searchEnabled : nextSearchEnabled;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setSearchEnabled( boolean searchEnabled ) {
        this.nextSearchEnabled = searchEnabled;
    }

    @Override
    public Set<String> getAdditionalPropertyUris() {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            return additionalPropertyUris != null ? additionalPropertyUris : nextAdditionalPropertyUris;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setAdditionalPropertyUris( Set<String> additionalPropertyUris ) {
        this.nextAdditionalPropertyUris = additionalPropertyUris;
    }

    public void initialize( boolean forceLoad, boolean forceIndexing ) {
        initialize( null, forceLoad, forceIndexing );
    }

    public void initialize( InputStream stream, boolean forceIndexing ) {
        initialize( stream, true, forceIndexing );
    }

    private void initialize( @Nullable InputStream stream, boolean forceLoad, boolean forceIndexing ) {
        if ( !forceLoad && isInitialized ) {
            log.warn( "{} is already loaded, and force=false, not restarting", this );
            return;
        }

        // making a copy of all we need
        String ontologyUrl = getOntologyUrl();
        String ontologyName = getOntologyName();
        String cacheName = getCacheName();
        Set<Property> additionalProperties = nextAdditionalPropertyUris.stream()
                .map( ResourceFactory::createProperty ).collect( Collectors.toSet() );
        LanguageLevel languageLevel = nextLanguageLevel;
        InferenceMode inferenceMode = nextInferenceMode;
        boolean processImports = nextProcessImports;
        boolean searchEnabled = nextSearchEnabled;

        // Detect configuration problems.
        if ( StringUtils.isBlank( ontologyUrl ) ) {
            throw new IllegalStateException( "URL not defined for %s: ontology cannot be loaded. (" + this + ")" );
        }

        if ( cacheName == null && forceIndexing ) {
            throw new IllegalArgumentException( String.format( "No cache directory is set for %s, cannot force indexing.", this ) );
        }

        boolean loadOntology = isEnabled();

        // If loading ontologies is disabled in the configuration, return
        if ( !forceLoad && !loadOntology ) {
            log.debug( "Loading {} is disabled (force=false, Configuration load.{}=false)",
                    this, ontologyName );
            return;
        }

        log.info( "Loading ontology: {}...", this );
        StopWatch loadTime = StopWatch.createStarted();

        // use temporary variables, so that we can minimize the critical region for replacing the service's state
        OntModel model;
        SearchIndex index;

        // loading the model from disk or URL is lengthy
        if ( checkIfInterrupted() )
            return;

        try {
            OntologyModel m = stream != null ? loadModelFromStream( stream, processImports, languageLevel, inferenceMode ) : loadModel( processImports, languageLevel, inferenceMode ); // can take a while.
            if ( m instanceof OntologyModelImpl ) {
                model = ( ( OntologyModelImpl ) m ).getOntModel();
            } else {
                throw new RuntimeException( "Only Jena-based ontology models are supported." );
            }
        } catch ( Exception e ) {
            if ( isCausedByInterrupt( e ) ) {
                return;
            } else {
                throw new RuntimeException( String.format( "Failed to load ontology model for %s.", this ), e );
            }
        }

        // retrieving restrictions is lengthy
        if ( checkIfInterrupted() )
            return;

        // compute additional restrictions
        Set<Restriction> additionalRestrictions = model.listRestrictions()
                .filterKeep( new RestrictionWithOnPropertyFilter( additionalProperties ) )
                .toSet();

        // indexing is lengthy, don't bother if we're interrupted
        if ( checkIfInterrupted() )
            return;

        if ( searchEnabled && cacheName != null ) {
            //Checks if the current ontology has changed since it was last loaded.
            boolean changed = OntologyLoader.hasChanged( cacheName );
            boolean indexExists = OntologyIndexer.getSubjectIndex( cacheName ) != null;
            boolean forceReindexing = forceLoad && forceIndexing;
            // indexing is slow, don't do it if we don't have to.
            try {
                index = OntologyIndexer.indexOntology( cacheName, model,
                        forceReindexing || changed || !indexExists );
            } catch ( Exception e ) {
                if ( isCausedByInterrupt( e ) ) {
                    return;
                } else {
                    throw new RuntimeException( String.format( "Failed to generate index for %s.", this ), e );
                }
            }
        } else {
            index = null;
        }

        // if interrupted, we don't need to replace the model and clear the *old* cache
        if ( checkIfInterrupted() )
            return;

        Lock lock = rwLock.writeLock();
        try {
            lock.lock();
            this.model = model;
            this.additionalRestrictions = additionalRestrictions;
            this.index = index;
            this.isInitialized = true;
            this.languageLevel = languageLevel;
            this.inferenceMode = inferenceMode;
            this.processImports = processImports;
            this.searchEnabled = searchEnabled;
            this.additionalPropertyUris = additionalProperties.stream().map( Property::getURI ).collect( Collectors.toSet() );
            if ( cacheName != null ) {
                // now that the terms have been replaced, we can clear old caches
                try {
                    OntologyLoader.deleteOldCache( cacheName );
                } catch ( IOException e ) {
                    log.error( String.format( String.format( "Failed to delete old cache directory for %s.", this ), e ) );
                }
            }
        } finally {
            lock.unlock();
        }

        loadTime.stop();

        log.info( "Finished loading {} in {}s", this, String.format( "%.2f", loadTime.getTime() / 1000.0 ) );
    }

    private boolean checkIfInterrupted() {
        if ( Thread.interrupted() ) {
            log.warn( "The current thread is interrupted, initialization of {} will be stop.", this );
            return true;
        }
        return false;
    }

    private static boolean isCausedByInterrupt( Exception e ) {
        return hasCauseMatching( e, cause -> ( ( cause instanceof ParseException ) && ( ( ParseException ) cause ).getErrorNumber() == ARPErrorNumbers.ERR_INTERRUPTED ) ) ||
                hasCause( e, InterruptedException.class ) ||
                hasCause( e, InterruptedIOException.class ) ||
                hasCause( e, ClosedByInterruptException.class );
    }

    private static boolean hasCause( Throwable t, Class<? extends Throwable> clazz ) {
        return hasCauseMatching( t, clazz::isInstance );
    }

    private static boolean hasCauseMatching( Throwable t, Predicate<Throwable> predicate ) {
        return predicate.test( t ) || ( t.getCause() != null && hasCauseMatching( t.getCause(), predicate ) );
    }

    /**
     * Do not do this except before re-indexing.
     */
    public void closeIndex() {
        if ( index == null ) return;
        index.close();
    }

    @Override
    public Collection<OntologyIndividual> findIndividuals( String search, boolean keepObsoletes ) throws
            OntologySearchException {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( "Ontology {} is not ready, no individuals will be returned.", this );
                return Collections.emptySet();
            }
            if ( index == null ) {
                log.warn( "Attempt to search {} when index is null, no results will be returned.", this );
                return Collections.emptySet();
            }
            return OntologySearch.matchIndividuals( model, index, search )
                    .mapWith( i -> ( OntologyIndividual ) new OntologyIndividualImpl( i.result, additionalRestrictions, i.score ) )
                    .filterKeep( where( ontologyTerm -> keepObsoletes || !ontologyTerm.isObsolete() ) )
                    .toSet();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<OntologyResource> findResources( String searchString, boolean keepObsoletes ) throws
            OntologySearchException {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( "Ontology {} is not ready, no resources will be returned.", this );
                return Collections.emptySet();
            }
            if ( index == null ) {
                log.warn( "Attempt to search {} when index is null, no results will be returned.", this );
                return Collections.emptySet();
            }
            return OntologySearch.matchResources( model, index, searchString )
                    .filterKeep( where( r -> r.result.canAs( OntClass.class ) || r.result.canAs( Individual.class ) ) )
                    .mapWith( r -> {
                        try {
                            if ( r.result.canAs( OntClass.class ) ) {
                                return new OntologyTermImpl( r.result.as( OntClass.class ), additionalRestrictions, r.score );
                            } else if ( r.result.canAs( Individual.class ) ) {
                                return new OntologyIndividualImpl( r.result.as( Individual.class ), additionalRestrictions, r.score );
                            } else {
                                return ( OntologyResource ) null;
                            }
                        } catch ( ConversionException e ) {
                            log.error( "Conversion failed for " + r, e );
                            return null;
                        }
                    } )
                    .filterKeep( where( Objects::nonNull ) )
                    .filterKeep( where( ontologyTerm -> keepObsoletes || !ontologyTerm.isObsolete() ) )
                    .toSet();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<OntologyTerm> findTerm( String search, boolean keepObsoletes ) throws OntologySearchException {
        if ( log.isDebugEnabled() ) log.debug( "Searching " + this + " for '" + search + "'" );
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( "Ontology {} is not ready, no terms will be returned.", this );
                return Collections.emptySet();
            }
            if ( index == null ) {
                log.warn( "Attempt to search {} when index is null, no results will be returned.", this );
                return Collections.emptySet();
            }
            return OntologySearch.matchClasses( model, index, search )
                    .mapWith( r -> ( OntologyTerm ) new OntologyTermImpl( r.result, additionalRestrictions, r.score ) )
                    .filterKeep( where( ontologyTerm -> keepObsoletes || !ontologyTerm.isObsolete() ) )
                    .toSet();
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
                log.warn( "Ontology {} is not ready, null will be returned for alternative ID match.", this );
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
                log.warn( "Ontology {} is not ready, no term  URIs will be returned.", this );
                return Collections.emptySet();
            }
            Set<String> allUris = new HashSet<>();
            allUris.addAll( model.listClasses().mapWith( OntClass::getURI ).toSet() );
            allUris.addAll( model.listIndividuals().mapWith( Individual::getURI ).toSet() );
            return allUris;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public OntologyResource getResource( String uri ) {
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
                res = new OntologyTermImpl( ( OntClass ) resource, additionalRestrictions );
            } else if ( resource instanceof Individual ) {
                res = new OntologyIndividualImpl( ( Individual ) resource, additionalRestrictions );
            } else if ( resource instanceof OntProperty ) {
                res = PropertyFactory.asProperty( ( ObjectProperty ) resource, additionalRestrictions );
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
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) return null;
            OntClass ontCls = model.getOntClass( uri );
            // null or bnode
            if ( ontCls == null || ontCls.getURI() == null ) {
                return null;
            }
            return new OntologyTermImpl( ontCls, additionalRestrictions );
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
            OntologyTerm term = getTerm( uri );
            if ( term == null ) {
                /*
                 * Either the ontology hasn't been loaded, or the id was not valid.
                 */
                log.warn( "No term for URI={} in {}; make sure ontology is loaded and uri is valid", uri, this );
                return Collections.emptySet();
            }
            return term.getIndividuals( true );
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<OntologyTerm> getParents( Collection<OntologyTerm> terms, boolean direct,
            boolean includeAdditionalProperties, boolean keepObsoletes ) {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                return Collections.emptySet();
            }
            return JenaUtils.getParents( model, getOntClassesFromTerms( terms ), direct, includeAdditionalProperties ? additionalRestrictions : null )
                    .stream()
                    .map( o -> new OntologyTermImpl( o, additionalRestrictions ) )
                    .filter( o -> keepObsoletes || !o.isObsolete() )
                    .collect( Collectors.toSet() );
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<OntologyTerm> getChildren( Collection<OntologyTerm> terms, boolean direct,
            boolean includeAdditionalProperties, boolean keepObsoletes ) {
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                return Collections.emptySet();
            }
            return JenaUtils.getChildren( model, getOntClassesFromTerms( terms ), direct, includeAdditionalProperties ? additionalRestrictions : null )
                    .stream()
                    .map( o -> new OntologyTermImpl( o, additionalRestrictions ) )
                    .filter( o -> keepObsoletes || !o.isObsolete() )
                    .collect( Collectors.toSet() );
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
            log.warn( " Initialization thread for {} is currently running, not restarting.", this );
            return;
        }
        // create and start the initialization thread
        initializationThread = new Thread( () -> {
            try {
                this.initialize( forceLoad, forceIndexing );
            } catch ( Exception e ) {
                log.error( "Initialization for %s failed.", e );
            }
        }, getOntologyName() + "_load_thread_" + RandomStringUtils.randomAlphanumeric( 5 ) );
        // To prevent VM from waiting on this thread to shut down (if shutting down).
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
    protected abstract OntologyModel loadModel( boolean processImports, LanguageLevel languageLevel, InferenceMode inferenceMode ) throws IOException;


    /**
     * Load a model from a given input stream.
     */
    protected abstract OntologyModel loadModelFromStream( InputStream stream, boolean processImports, LanguageLevel languageLevel, InferenceMode inferenceMode ) throws IOException;

    /**
     * A name for caching this ontology, or null to disable caching.
     * <p>
     * Note that if null is returned, the ontology will not have full-text search capabilities.
     */
    @Nullable
    protected String getCacheName() {
        return getOntologyName();
    }

    @Override
    public void index( boolean force ) {
        String cacheName = getCacheName();
        if ( cacheName == null ) {
            log.warn( "This ontology does not support indexing; assign a cache name to be used." );
            return;
        }
        if ( !nextSearchEnabled ) {
            log.warn( "Search is not enabled for this ontology." );
            return;
        }
        SearchIndex index;
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            if ( !isInitialized ) {
                log.warn( "Ontology {} is not initialized, cannot index it.", this );
                return;
            }
            index = OntologyIndexer.indexOntology( getCacheName(), model, force );
        } catch ( IOException e ) {
            log.error( "Failed to generate index for {}.", this, e );
            return;
        } finally {
            lock.unlock();
        }
        // now we replace the index
        lock = rwLock.writeLock();
        try {
            lock.lock();
            this.index = index;
            this.searchEnabled = true;
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
            OntologyTerm ontologyTerm = new OntologyTermImpl( iterator.next(), additionalRestrictions );
            if ( ontologyTerm.getUri() == null ) {
                continue;
            }
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

    @Override
    public void loadTermsInNameSpace( InputStream is, boolean forceIndex ) {
        // wait for the initialization thread to finish
        if ( initializationThread != null && initializationThread.isAlive() ) {
            log.warn( "{} initialization is already running, trying to cancel ...", this );
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
                    log.warn( "Got interrupted while waiting for the initialization thread of {} to finish.", this );
                    return;
                }
                ++wait;
                if ( wait >= maxWait && !initializationThread.isAlive() ) {
                    throw new RuntimeException( String.format( "Got tired of waiting for %s's initialization thread.", this ) );
                }
            }
        }
        initialize( is, forceIndex );
    }

    @Override
    public String toString() {
        return String.format( "%s [url=%s] [language level=%s] [inference mode=%s] [imports=%b] [search=%b]",
                getOntologyName(), getOntologyUrl(), getLanguageLevel(), getInferenceMode(), getProcessImports(), isSearchEnabled() );
    }

    private Set<OntClass> getOntClassesFromTerms( Collection<OntologyTerm> terms ) {
        return terms.stream()
                .map( o -> {
                    if ( o instanceof OntologyTermImpl ) {
                        return ( ( OntologyTermImpl ) o ).getOntClass();
                    } else {
                        return o.getUri() != null ? model.getOntClass( o.getUri() ) : null;
                    }
                } )
                .filter( Objects::nonNull )
                .collect( Collectors.toSet() );
    }
}