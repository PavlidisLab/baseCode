/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.ontology.Configuration;
import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologyIndexer;
import ubic.basecode.ontology.search.OntologySearch;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.larq.IndexLARQ;

/**
 * @author kelsey
 * @version $Id$
 */
public abstract class AbstractOntologyService {

    private class KeepAliveThread extends Thread {
        @Override
        public void run() {
            for ( ;; ) {
                try {
                    Thread.sleep( KEEPALIVE_PING_DELAY );
                } catch ( InterruptedException e ) {
                    log.info( "Ending keep-alive" );
                    return;
                }
                if ( isOntologyLoaded() ) {
                    log.info( "sending keep-alive query to " + getOntologyName() );
                    try {
                        findResources( KEEPALIVE_SEARCH_TERM );
                    } catch ( Exception e ) {
                        log.error( "error sending keep-alive query to " + getOntologyName(), e );
                    }
                }
            }
        }
    }

    protected static final Log log = LogFactory.getLog( AbstractOntologyService.class );
    /*
     * the number of milliseconds between keep-alive queries; this should be less than or equal to MySQL wait_timeout
     * server variable. Currently 4 hours. (half of wait_timeout)
     */
    private static final int KEEPALIVE_PING_DELAY = 14400 * 1000;

    /*
     * a term to search for; matters not at all...
     */
    private static final String KEEPALIVE_SEARCH_TERM = "dummy";

    /*
     * a collection of the keep-alive threads for each concrete subclass; a single variable here wouldn't work because
     * all of the subclasses would be using the same variable.
     */
    private static Map<Class<?>, Thread> keepAliveThreads = Collections
            .synchronizedMap( new HashMap<Class<?>, Thread>() );

    protected AtomicBoolean cacheReady = new AtomicBoolean( false );
    protected IndexLARQ index;
    protected AtomicBoolean indexReady = new AtomicBoolean( false );

    protected Map<String, OntologyIndividual> individuals;
    protected OntModel model;
    protected AtomicBoolean modelReady = new AtomicBoolean( false );
    protected String ontology_URL;
    protected String ontologyName;

    protected AtomicBoolean ready = new AtomicBoolean( false );

    protected AtomicBoolean running = new AtomicBoolean( false );

    protected Map<String, OntologyTerm> terms;

    private boolean enabled = false;

    public AbstractOntologyService() {
        super();
        ontology_URL = getOntologyUrl();
        ontologyName = getOntologyName();
    }

    /**
     * Looks for any OntologyIndividuals that match the given search string
     * 
     * @param search
     * @return
     */
    public Collection<OntologyIndividual> findIndividuals( String search ) {

        if ( !isOntologyLoaded() ) return null;

        assert index != null : "attempt to search " + this.getOntologyName() + " when index is null";
        // if ( index == null ) index = OntologyIndexer.indexOntology( ontology_name, model );

        Collection<OntologyIndividual> indis = OntologySearch.matchIndividuals( model, index, search );

        return indis;
    }

    /**
     * Looks for any OntologyIndividuals or ontologyTerms that match the given search string
     * 
     * @param search
     * @return results, or an empty collection if the results are empty OR the ontology is not available to be searched.
     */
    public Collection<OntologyResource> findResources( String search ) {

        if ( !isOntologyLoaded() ) return new HashSet<OntologyResource>();

        assert index != null : "attempt to search " + this.getOntologyName() + " when index is null";

        Collection<OntologyResource> res = OntologySearch.matchResources( model, index, search.trim() + "*" );

        return res;
    }

    /**
     * Looks for any ontologyTerms that match the given search string
     * 
     * @param search
     * @return
     */
    public Collection<OntologyTerm> findTerm( String search ) {

        if ( !isOntologyLoaded() ) return new HashSet<OntologyTerm>();

        if ( log.isDebugEnabled() ) log.debug( "Searching " + this.getOntologyName() + " for '" + search + "'" );

        assert index != null : "attempt to search " + this.getOntologyName() + " when index is null";
        Collection<OntologyTerm> matches = OntologySearch.matchClasses( model, index, search );
        return matches;
    }

    public Set<String> getAllURIs() {
        if ( terms == null ) return null;
        return new HashSet<String>( terms.keySet() );
    }

    /**
     * Looks through both Terms and Individuls for a OntologyResource that has a uri matching the uri given If no
     * OntologyTerm is found only then will ontologyIndividuals be searched. returns null if nothing is found.
     * 
     * @param uri
     * @return
     */
    public OntologyResource getResource( String uri ) {

        if ( ( uri == null ) || ( !ready.get() ) ) return null;

        OntologyResource resource = terms.get( uri );

        if ( resource == null ) resource = individuals.get( uri );

        return resource;
    }

    /**
     * Looks for a OntologyTerm that has the matchine URI given
     * 
     * @param uri
     * @return
     */
    public OntologyTerm getTerm( String uri ) {

        if ( ( uri == null ) || ( !ready.get() ) ) return null;

        OntologyTerm term = terms.get( uri );

        return term;
    }

    /**
     * @param uri
     * @return
     */
    public Collection<OntologyIndividual> getTermIndividuals( String uri ) {

        if ( terms == null ) {
            log.warn( "No term for URI=" + uri + " in " + this.getOntologyName()
                    + " no terms loaded; make sure ontology is loaded and uri is valid" );
            return new HashSet<OntologyIndividual>();
        }

        OntologyTerm term = terms.get( uri );
        if ( term == null ) {
            /*
             * Either the onology hasn't been loaded, or the id was not valid.
             */
            log.warn( "No term for URI=" + uri + " in " + this.getOntologyName()
                    + "; make sure ontology is loaded and uri is valid" );
            return new HashSet<OntologyIndividual>();
        }
        return term.getIndividuals( true );

    }

    public synchronized void init( boolean force ) {

        if ( running.get() ) {
            log.warn( ontology_URL + " initialization is already running" );
            return;
        }

        if ( this.isOntologyLoaded() ) {
            return;
        }

        String configParameter = "load." + ontologyName;
        boolean loadOntology = Configuration.getBoolean( configParameter );

        // if loading ontologies is disabled in the configuration, return
        if ( !force && !loadOntology ) {
            log.debug( "Loading " + ontologyName + " is disabled (force=" + force + ", " + configParameter + "="
                    + loadOntology + ")" );
            return;
        }

        // detect configuration problems.
        if ( StringUtils.isBlank( this.ontology_URL ) ) {
            log.error( "URL not defined, ontology cannot be loaded (" + this.getClass().getSimpleName() + ")" );
            return;
        }

        enabled = true;

        // Load the model for searching

        Thread loadThread = new Thread( new Runnable() {
            public void run() {

                running.set( true );

                terms = new HashMap<String, OntologyTerm>();
                individuals = new HashMap<String, OntologyIndividual>();

                log.info( "Loading " + ontologyName + " Ontology..." );
                StopWatch loadTime = new StopWatch();
                loadTime.start();

                boolean interrupted = false;
                int waitMs = 1000;
                while ( !interrupted && !modelReady.get() ) {
                    try {
                        /*
                         * We use the OWL_MEM_TRANS_INF spec so we can do 'getChildren' and get _all_ the children in
                         * one query.
                         */
                        model = loadModel( ontology_URL );
                        modelReady.set( true );
                    } catch ( Exception e ) {
                        log.error( "error loading model for " + ontologyName, e );
                        try {
                            log.error( "waiting " + waitMs + "ms before trying to reload" );
                            Thread.sleep( waitMs );
                            waitMs *= 2;
                        } catch ( InterruptedException ie ) {
                            interrupted = true;
                        }
                    }
                }

                try {

                    /*
                     * Indexing will be slow the first time (can take hours for large ontologies).
                     */
                    log.info( "Loading Index for " + ontologyName );
                    index = OntologyIndexer.indexOntology( ontologyName, model );

                    if ( loadTime.getTime() > 5000 ) {
                        log.info( "Done Loading Index for " + ontologyName + " Ontology in " + loadTime.getTime()
                                / 1000 + "s" );
                    }

                    indexReady.set( true );

                    /*
                     * This creates a cache of URI (String) --> OntologyTerms. ?? Does Jena provide an easier way to do
                     * this?
                     */
                    loadTermsInNameSpace( ontology_URL );

                    if ( loadTime.getTime() > 5000 ) {
                        log.info( ontology_URL + "  loaded, total of " + terms.size() + " items in "
                                + loadTime.getTime() / 1000 + "s" );
                    }

                    cacheReady.set( true );

                    ready.set( true );
                    running.set( false );
                    loadTime.stop();

                    if ( loadTime.getTime() > 5000 ) {
                        log.info( "Finished loading ontology " + ontologyName + " in " + loadTime.getTime() / 1000
                                + "s" );
                    }

                } catch ( Exception e ) {
                    log.error( e, e );
                    ready.set( false );
                    running.set( false );
                }
            }

        }, this.ontologyName + "_load_thread" );

        if ( running.get() ) return;
        loadThread.setDaemon( true ); // So vm doesn't wait on these threads to shutdown (if shutting down)
        loadThread.start();

        startKeepAliveThread();
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Used for determining if the Gene Ontology has finished loading into memory yet Although calls like getParents,
     * getChildren will still work (its much faster once the ontologies have been preloaded into memory.)
     * 
     * @returns boolean
     */
    public synchronized boolean isOntologyLoaded() {
        return ready.get();
    }

    /**
     * For testing! Overrides normal way of loading the ontology.
     * 
     * @param is
     * @throws IOException
     */
    public synchronized void loadTermsInNameSpace( InputStream is ) {
        if ( running.get() ) {
            log.warn( ontology_URL + " initialization is already running, will not load from input stream" );
            return;
        }

        running.set( true );

        this.model = OntologyLoader.loadMemoryModel( is, this.ontology_URL, OntModelSpec.OWL_MEM );
        assert this.model != null;
        index = OntologyIndexer.indexOntology( ontologyName, model );

        addTerms( OntologyLoader.initialize( this.ontology_URL, model ) );

        assert index != null;

        indexReady.set( true );
        running.set( false );
        ready.set( true );
    }

    /**
     * Use this to turn this ontology on or off.
     * 
     * @param enabled If false, the ontology will not be loaded.
     */
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
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
     * @param url
     * @return
     * @throws IOException
     */
    protected abstract OntModel loadModel( String url );

    /**
     * @param url
     * @throws IOException
     */
    protected void loadTermsInNameSpace( String url ) {
        Collection<OntologyResource> t = OntologyLoader.initialize( url, model );
        addTerms( t );
    }

    /**
     * @param newTerms
     */
    private void addTerms( Collection<OntologyResource> newTerms ) {

        if ( newTerms == null || newTerms.isEmpty() ) {
            log.warn( "No terms!" );
            return;
        }

        if ( terms == null ) terms = new HashMap<String, OntologyTerm>();
        if ( individuals == null ) individuals = new HashMap<String, OntologyIndividual>();

        for ( OntologyResource term : newTerms ) {
            if ( term.getUri() == null ) continue;
            if ( term instanceof OntologyTerm ) terms.put( term.getUri(), ( OntologyTerm ) term );
            if ( term instanceof OntologyIndividual ) individuals.put( term.getUri(), ( OntologyIndividual ) term );
        }
    }

    private synchronized void startKeepAliveThread() {

        if ( keepAliveThreads.containsKey( this.getClass() ) ) {
            log.info( "Didn't start keep alive thread for: " + this.getClass() + " because already started" );
            return;
        }

        Thread keepAliveThread = new KeepAliveThread();
        keepAliveThread.setDaemon( true ); // needed or else won't shut down cleanly
        keepAliveThread.start();
        keepAliveThreads.put( this.getClass(), keepAliveThread );
    }
}