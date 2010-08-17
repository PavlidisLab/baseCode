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

import ubic.basecode.ontology.Configuration;
import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologyIndexer;
import ubic.basecode.ontology.search.OntologySearch;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.larq.IndexLARQ;

/**
 * @author kelsey
 * @version $Id$
 */
public abstract class AbstractOntologyService {

    protected static final Log log = LogFactory.getLog( AbstractOntologyService.class );

    protected String ontology_URL;
    protected String ontologyName;

    protected IndexLARQ index;
    protected Map<String, OntologyTerm> terms;
    protected Map<String, OntologyIndividual> individuals;

    protected AtomicBoolean cacheReady = new AtomicBoolean( false );
    protected AtomicBoolean indexReady = new AtomicBoolean( false );
    protected AtomicBoolean modelReady = new AtomicBoolean( false );

    protected AtomicBoolean isInitialized = new AtomicBoolean( false );
    
    protected OntologyIntializationThread initializationThread;

    // private boolean enabled = false;

    /**
     * 
     */
    public AbstractOntologyService() {
        super();
        ontology_URL = getOntologyUrl();
        ontologyName = getOntologyName();
        
        initializationThread = new OntologyIntializationThread();
        initializationThread.setName(ontologyName + "_load_thread");
        // To prevent VM from waiting on this thread to shutdown (if shutting down).
        initializationThread.setDaemon( true );
        
    }
    
    protected class OntologyIntializationThread extends Thread {        
        public void run() {

            terms = new HashMap<String, OntologyTerm>();
            individuals = new HashMap<String, OntologyIndividual>();

            log.info( "Loading " + ontologyName + " Ontology..." );
            StopWatch loadTime = new StopWatch();
            loadTime.start();

            OntModel model = getModel();
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

                isInitialized.set( true );
                //isInitializationThreadRunning.set( false );
                loadTime.stop();

                if ( loadTime.getTime() > 5000 ) {
                    log.info( "Finished loading ontology " + ontologyName + " in " + loadTime.getTime() / 1000
                            + "s" );
                }

            } catch ( Exception e ) {
                log.error( e, e );
                isInitialized.set( false );
                //isInitializationThreadRunning.set( false );
            } finally {
                releaseModel( model );
            }
        }    
    }
    
    /**
     * Looks for any OntologyIndividuals that match the given search string.
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

    /**
     * Looks for any OntologyIndividuals or ontologyTerms that match the given search string
     * 
     * @param search
     * @return results, or an empty collection if the results are empty OR the ontology is not available to be searched.
     */
    public Collection<OntologyResource> findResources( String searchString ) {

        if ( !isOntologyLoaded() ) return new HashSet<OntologyResource>();

        assert index != null : "attempt to search " + this.getOntologyName() + " when index is null";

        OntModel model = getModel();

        Collection<OntologyResource> results = OntologySearch.matchResources( model, index, searchString );

        releaseModel( model );

        return results;
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
     * Looks through both Terms and Individuals for a OntologyResource that has a uri matching the uri given. If no
     * OntologyTerm is found only then will ontologyIndividuals be searched. returns null if nothing is found.
     * 
     * @param uri
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
     * @param uri
     * @return
     */
    public OntologyTerm getTerm( String uri ) {

        if ( ( uri == null ) || ( !isInitialized.get() ) ) return null;

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
             * Either the ontology hasn't been loaded, or the id was not valid.
             */
            log.warn( "No term for URI=" + uri + " in " + this.getOntologyName()
                    + "; make sure ontology is loaded and uri is valid" );
            return new HashSet<OntologyIndividual>();
        }
        return term.getIndividuals( true );

    }

    //TODO: finish this convenience method
//    public void startInitializationThreadAndWait ( boolean force ) {
//        startInitializationThread ( force );
//        while ( !isOntologyLoaded() ) {
//            Thread.sleep( 5000 );
//            log.info( "Waiting for mgedontology to load" );
//        }
//    }
        
    public synchronized void startInitializationThread ( boolean force ) {
                
        if ( initializationThread.isAlive() ) {
            log.warn( ontology_URL + " initialization is already running." );
            return;
        }

        if ( this.isOntologyLoaded() ) {
            return;
        }

        String configParameter = "load." + ontologyName;
        boolean loadOntology = Configuration.getBoolean( configParameter );

        // If loading ontologies is disabled in the configuration, return
        if ( !force && !loadOntology ) {
            log.debug( "Loading " + ontologyName + " is disabled (force=" + force + ", " + configParameter + "="
                    + loadOntology + ")" );
            return;
        }

        // Detect configuration problems.
        if ( StringUtils.isBlank( this.ontology_URL ) ) {
            log.error( "URL not defined, ontology cannot be loaded (" + this.getClass().getSimpleName() + ")" );
            return;
        }

        // This thread indexes ontology and creates local cache for uri->ontology terms mappings.         
        initializationThread.start();
    }

    /**
     * Used for determining if the Gene Ontology has finished loading into memory. Although calls like getParents,
     * getChildren will still work (its much faster once the ontologies have been preloaded into memory.)
     * 
     * @returns boolean
     */
    public synchronized boolean isOntologyLoaded() {
        return isInitialized.get();
    }

    /**
     * Use this to turn this ontology on or off.
     * 
     * @param enabled If false, the ontology will not be loaded.
     */
    // public void setEnabled( boolean enabled ) {
    // this.enabled = enabled;
    // }
    //
    // public boolean isEnabled() {
    // return enabled;
    // }

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
    protected void addTerms( Collection<OntologyResource> newTerms ) {

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

    protected abstract OntModel getModel();

    protected abstract void releaseModel( OntModel m );

}