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

import java.io.IOException;
import java.lang.Thread.State;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;

import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologyIndexer;
import ubic.basecode.ontology.search.OntologySearch;
import ubic.basecode.ontology.search.SearchIndex;
import ubic.basecode.util.Configuration;

/**
 * @author kelsey
 */
public abstract class AbstractOntologyService {

    protected class OntologyInitializationThread extends Thread {

        AtomicBoolean cancel = new AtomicBoolean( false );

        private boolean forceReindexing = false;

        public OntologyInitializationThread( boolean forceRefresh ) {
            super();
            this.forceReindexing = forceRefresh;
        }

        public void cancel() {
            this.cancel.set( true );
            this.interrupt();
        }

        public boolean isCancelled() {
            return cancel.get();
        }

        public boolean isForceReindexing() {
            return forceReindexing;
        }

        @Override
        public void run() {

            terms = new HashMap<>();
            individuals = new HashMap<>();

            if ( isCancelled() ) {
                log.warn( "Cancelled initialization" );
                return;
            }

            log.info( "Loading ontology: " + getOntologyName() + " from " + getOntologyUrl() + " ..." );
            StopWatch loadTime = new StopWatch();
            loadTime.start();

            model = getModel(); // can take a while.
            assert model != null;

            try {

                //Checks if the current ontology has changed since it was last loaded.
                boolean changed = OntologyLoader.hasChanged( getOntologyName() );
                boolean indexExists = OntologyIndexer.getSubjectIndex( getOntologyName() ) != null;

                /*
                 * Indexing is slow, don't do it if we don't have to.
                 */
                index( forceReindexing || changed || !indexExists );

                indexReady.set( true );

                if ( isCancelled() ) {
                    log.error( "Cancelled initialization" );
                    return;
                }

                /*
                 * This creates a cache of URI (String) --> OntologyTerms. ?? Does Jena provide an easier way to do
                 * this?
                 */

                loadTermsInNameSpace( getOntologyUrl(), model );

                cleanup();

                cacheReady.set( true );

                isInitialized.set( true );
                loadTime.stop();

                log.info( "Finished loading " + getOntologyName() + " in " + String.format( "%.2f", loadTime.getTime() / 1000.0 )
                        + "s" );

            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
                isInitialized.set( false );
            } finally {
                // no-op
            }
        }

        public void setForceReindexing( boolean forceReindexing ) {
            this.forceReindexing = forceReindexing;
        }

        private void cleanup() {
            OntologyLoader.deleteOldCache( getOntologyName() );
        }
    }

    protected static Logger log = LoggerFactory.getLogger( AbstractOntologyService.class );

    protected AtomicBoolean cacheReady = new AtomicBoolean( false );

    protected SearchIndex index;

    protected AtomicBoolean indexReady = new AtomicBoolean( false );
    protected Map<String, OntologyIndividual> individuals;

    protected OntologyInitializationThread initializationThread;
    protected AtomicBoolean isInitialized = new AtomicBoolean( false );
    protected OntModel model = null;

    protected AtomicBoolean modelReady = new AtomicBoolean( false );

    protected Map<String, OntologyTerm> terms = null;

    private Map<String, OntologyTerm> alternativeIDs = new HashMap<>();

    /**
     *
     */
    public AbstractOntologyService() {
        super();

        initializationThread = new OntologyInitializationThread( false );
        initializationThread.setName( getOntologyName() + "_load_thread_" + RandomStringUtils.randomAlphanumeric( 5 ) );
        // To prevent VM from waiting on this thread to shutdown (if shutting down).
        initializationThread.setDaemon( true );

    }

    // private boolean enabled = false;

    /**
     * Do not do this except before re-indexing.
     */
    public void closeIndex() {
        if ( index == null ) return;
        index.close();
    }

    /**
     * Looks for any OntologyIndividuals that match the given search string.
     *
     * @param  search
     * @return
     */
    public Collection<OntologyIndividual> findIndividuals( String search ) {

        if ( !isOntologyLoaded() ) return null;

        if ( index == null ) {
            log.warn( "attempt to search " + this.getOntologyName() + " when index is null" );
            return null;
        }

        OntModel m = getModel();

        Collection<OntologyIndividual> indis = OntologySearch.matchIndividuals( m, index, search );

        return indis;
    }

    /**
     * Looks for any OntologyIndividuals or ontologyTerms that match the given search string
     *
     * @param  search
     * @return        results, or an empty collection if the results are empty OR the ontology is not available to be
     *                searched.
     */
    public Collection<OntologyResource> findResources( String searchString ) {

        if ( !isOntologyLoaded() ) {
            log.warn( "Ontology is not ready: " + this.getClass() );
            return new HashSet<>();
        }

        assert index != null : "attempt to search " + this.getOntologyName() + " when index is null";

        OntModel m = getModel();

        Collection<OntologyResource> results = OntologySearch.matchResources( m, index, searchString );

        return results;
    }

    /**
     * Looks for any ontologyTerms that match the given search string. Obsolete terms are filtered out.
     *
     * @param  search
     * @return
     */
    public Collection<OntologyTerm> findTerm( String search ) {

        if ( !isOntologyLoaded() ) return new HashSet<>();

        if ( log.isDebugEnabled() ) log.debug( "Searching " + this.getOntologyName() + " for '" + search + "'" );

        assert index != null : "attempt to search " + this.getOntologyName() + " when index is null";

        OntModel m = getModel();

        Collection<OntologyTerm> matches = OntologySearch.matchClasses( m, index, search );

        return matches;
    }

    public OntologyTerm findUsingAlternativeId( String alternativeId ) {

        if ( alternativeIDs.isEmpty() ) {
            log.info( "init search by alternativeID" );
            initSearchByAlternativeId();
        }

        if ( alternativeIDs.get( alternativeId ) != null ) {
            return alternativeIDs.get( alternativeId );
        }

        return null;
    }

    public Set<String> getAllURIs() {
        if ( terms == null ) return null;
        return new HashSet<>( terms.keySet() );
    }

    /**
     * Looks through both Terms and Individuals for a OntologyResource that has a uri matching the uri given. If no
     * OntologyTerm is found only then will ontologyIndividuals be searched. returns null if nothing is found.
     *
     * @param  uri
     * @return
     */
    public OntologyResource getResource( String uri ) {

        if ( ( uri == null ) || ( !isInitialized.get() ) ) return null;

        OntologyResource resource = terms.get( uri );

        if ( resource == null ) resource = individuals.get( uri );

        return resource;
    }

    /**
     * Looks for a OntologyTerm that has the match in URI given
     *
     * @param  uri
     * @return
     */
    public OntologyTerm getTerm( String uri ) {

        if ( !isInitialized.get() || terms == null ) return null;

        if ( uri == null ) throw new IllegalArgumentException( "URI cannot be null" );

        OntologyTerm term = terms.get( uri );

        return term;
    }

    /**
     * @param  uri
     * @return
     */
    public Collection<OntologyIndividual> getTermIndividuals( String uri ) {

        if ( terms == null ) {
            log.warn( "No term for URI=" + uri + " in " + this.getOntologyName()
                    + " no terms loaded; make sure ontology is loaded and uri is valid" );
            return new HashSet<>();
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

    }

    /**
     * Create the search index.
     *
     * @param force
     */
    public void index( boolean force ) {
        StopWatch timer = new StopWatch();
        timer.start();
        OntModel m = getModel();
        assert m != null;

        index = OntologyIndexer.indexOntology( getOntologyName(), m, force );

    }

    /**
     * @return
     */
    public boolean isEnabled() {
        if ( isOntologyLoaded() ) return true; // could have forced, without setting config
        String configParameter = "load." + getOntologyName();
        return Configuration.getBoolean( configParameter );
    }

    public boolean isInitializationThreadAlive() {
        return initializationThread.isAlive();
    }

    /**
     * Used for determining if the Ontology has finished loading into memory. Although calls like getParents,
     * getChildren will still work (its much faster once the ontologies have been preloaded into memory.)
     *
     * @returns boolean
     */
    public boolean isOntologyLoaded() {
        return isInitialized.get();
    }

    /**
     * 
     * @param forceLoad
     * @param forceIndexing If forceLoad is also true, indexing will be performed. If you know the index is
     *                      up to date, there's no need to do it again. Normally indexing is only done if there is no
     *                      index, or if the ontology has changed since last loaded.
     */
    public void startInitializationThread( boolean forceLoad, boolean forceIndexing ) {
        assert initializationThread != null;
        synchronized ( initializationThread ) {
            if ( initializationThread.isAlive() ) {
                log.warn( getOntologyName() + " initialization is already running, not restarting." );
                return;
            } else if ( initializationThread.isInterrupted() ) {
                log.warn( getOntologyName() + " initialization was interrupted, not restarting." );
                return;
            } else if ( !initializationThread.getState().equals( State.NEW ) ) {
                log.warn( getOntologyName() + " initialization was not ready to run: state="
                        + initializationThread.getState() + ", not restarting." );
                return;
            }

            if ( !forceLoad && this.isOntologyLoaded() ) {
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
            initializationThread.setForceReindexing( forceLoad && forceIndexing );
            initializationThread.start();
        }
    }

    /**
     * @param newTerms
     */
    protected void addTerms( Collection<OntologyResource> newTerms ) {

        if ( newTerms == null || newTerms.isEmpty() ) {
            log.warn( "No terms!" );
            return;
        }

        if ( terms == null ) terms = new HashMap<>();
        if ( individuals == null ) individuals = new HashMap<>();

        int i = 0;
        for ( OntologyResource term : newTerms ) {
            if ( term.getUri() == null ) continue;
            if ( term instanceof OntologyTerm ) terms.put( term.getUri(), ( OntologyTerm ) term );
            if ( term instanceof OntologyIndividual ) individuals.put( term.getUri(), ( OntologyIndividual ) term );

            if ( ++i % 1000 == 0 && initializationThread.isCancelled() ) {
                log.error( "Cancelled initialization" );
                this.isInitialized.set( false );
                return;
            }
        }
    }

    protected synchronized OntModel getModel() {
        if ( model == null ) {
            model = loadModel();
        }
        return model;
    }

    /**
     * The simple name of the ontology. Used for indexing purposes. (ie this will determine the name of the underlying
     * index for searching the ontology)
     *
     * @return
     */
    protected abstract String getOntologyName();

    /**
     * Defines the location of the ontology eg: http://mged.sourceforge.net/ontologies/MGEDOntology.owl
     *
     * @return
     */
    protected abstract String getOntologyUrl();

    /**
     * Delegates the call as to load the model into memory or leave it on disk. Simply delegates to either
     * OntologyLoader.loadMemoryModel( url ); OR OntologyLoader.loadPersistentModel( url, spec );
     *
     * @param  url
     * @return
     * @throws IOException
     */
    protected abstract OntModel loadModel();

    /**
     * @param  url
     * @param  m
     * @throws IOException
     */
    protected void loadTermsInNameSpace( String url, OntModel m ) {
        Collection<OntologyResource> t = OntologyLoader.initialize( url, m );
        addTerms( t );
    }

    /*
     * this add alternative id in 2 ways
     *
     * Example :
     *
     * http://purl.obolibrary.org/obo/HP_0000005 with alternative id : HP:0001453
     *
     * by default way use in file 1- HP:0001453 -----> http://purl.obolibrary.org/obo/HP_0000005
     *
     * trying to use the value uri 2- http://purl.obolibrary.org/obo/HP_0001453 ----->
     * http://purl.obolibrary.org/obo/HP_0000005
     */
    private void initSearchByAlternativeId() {

        // lets find the baseUrl, to change to valueUri
        String randomUri = terms.values().iterator().next().getUri();
        String baseOntologyUri = randomUri.substring( 0, randomUri.lastIndexOf( "/" ) + 1 );

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

}