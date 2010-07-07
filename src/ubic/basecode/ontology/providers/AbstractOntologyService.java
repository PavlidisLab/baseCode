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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import ubic.basecode.ontology.Configuration;
import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologyIndexer;
import ubic.basecode.ontology.search.OntologySearch;

import com.hp.hpl.jena.db.impl.GraphRDBMaker;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.larq.IndexLARQ;

/**
 * @author kelsey
 * @version $Id$
 */
public abstract class AbstractOntologyService {

    protected static final Log log = LogFactory.getLog( AbstractOntologyService.class );

    protected AtomicBoolean cacheReady = new AtomicBoolean( false );
    protected IndexLARQ index;
    protected AtomicBoolean indexReady = new AtomicBoolean( false );

    protected Map<String, OntologyIndividual> individuals;

    protected AtomicBoolean modelReady = new AtomicBoolean( false );
    protected String ontology_URL;
    protected String ontologyName;

    private final GenericObjectPool pool;

    protected AtomicBoolean ready = new AtomicBoolean( false );

    protected AtomicBoolean running = new AtomicBoolean( false );

    protected Map<String, OntologyTerm> terms;

    private boolean enabled = false;

    /**
     * 
     */
    public AbstractOntologyService() {
        super();
        ontology_URL = getOntologyUrl();
        ontologyName = getOntologyName();

        /*
         * We use a pool, because each of these holds on to a database connection forever.
         */
        this.pool = new GenericObjectPool( new PoolableObjectFactory() {

            @Override
            public void activateObject( Object obj ) throws Exception {
                // no op
            }

            @Override
            public void destroyObject( Object obj ) throws Exception {
                // ( ( OntModel ) obj ).close();
                // obj = null;
            }

            @Override
            public Object makeObject() throws Exception {
                log.warn( "Making model for: " + ontology_URL );
                OntModel model = loadModel( ontology_URL );
                if ( !( model.getImportModelMaker().getGraphMaker() instanceof GraphRDBMaker ) ) {
                    pool.setMinIdle( 1 );
                    pool.setMinEvictableIdleTimeMillis( -1 );
                }
                modelReady.set( true );
                return model;
            }

            @Override
            public void passivateObject( Object obj ) throws Exception {
                // no op
            }

            @Override
            public boolean validateObject( Object obj ) {
                return true;
            }
        } );

        /*
         * These could be made into configuration settings (BASECODE)
         */
        pool.setMaxActive( 10 );
        pool.setMinIdle( 0 );
        pool.setMaxWait( 30000 );
        pool.setMinEvictableIdleTimeMillis( 1000L * 60L * 60L );
        pool.setTimeBetweenEvictionRunsMillis( 1000L * 60L );
        pool.setMaxIdle( 8 );

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

        OntModel model = getModel();

        Collection<OntologyIndividual> indis = OntologySearch.matchIndividuals( model, index, search );

        releaseModel( model );

        return indis;
    }

    private OntModel getModel() {
        OntModel model = null;
        try {
            model = ( OntModel ) pool.borrowObject();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return model;
    }

    private void releaseModel( OntModel m ) {
        try {
            pool.returnObject( m );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
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

        OntModel model = getModel();

        // Add wildcard only if the last word is longer than one character. This is to prevent lucene from
        // blowing up. See bug#1145
        search = search.trim();
        String[] words = search.split("\\s+");
        if (words[words.length - 1].length() > 1) {
        	search += "*";
        }
        	        
        Collection<OntologyResource> res = OntologySearch.matchResources( model, index, search );

        releaseModel( model );

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

        OntModel model = getModel();

        Collection<OntologyTerm> matches = OntologySearch.matchClasses( model, index, search );

        releaseModel( model );

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
                OntModel model = null;

                while ( !interrupted && !modelReady.get() ) {
                    model = getModel();
                    try {
                        Thread.sleep( 1000 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }

                assert model != null;

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

                    loadTermsInNameSpace( ontology_URL, model );

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
                } finally {
                    releaseModel( model );
                }

            }

        }, this.ontologyName + "_load_thread" );

        if ( running.get() ) return;
        loadThread.setDaemon( true ); // So vm doesn't wait on these threads to shutdown (if shutting down)
        loadThread.start();
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

        OntModel model = OntologyLoader.loadMemoryModel( is, this.ontology_URL, OntModelSpec.OWL_MEM );

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
     * @param m
     * @throws IOException
     */
    protected void loadTermsInNameSpace( String url, OntModel m ) {
        Collection<OntologyResource> t = OntologyLoader.initialize( url, m );
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

}