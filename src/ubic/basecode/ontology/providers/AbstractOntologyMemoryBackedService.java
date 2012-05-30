package ubic.basecode.ontology.providers;

import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;

import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.providers.AbstractOntologyService;
import ubic.basecode.ontology.search.OntologyIndexer;

/*  This class has some stuff that's specific to in-memory ontologies.
 *  Unlike database backed ontologies we don't use a pool keeping only one instance of model in memory. 
 */
public abstract class AbstractOntologyMemoryBackedService extends AbstractOntologyService {
    private OntModel model = null;

    /**
     * For testing! Overrides normal way of loading the ontology.
     * 
     * @param is
     * @throws IOException
     */
    public synchronized void loadTermsInNameSpace( InputStream is ) {
        if ( initializationThread.isAlive() ) {
            log.warn( ontology_URL + " initialization is already running, will not load from input stream" );
            return;
        }

        this.indexReady.set( false );
        this.modelReady.set( false );
        this.isInitialized.set( false );
        this.cacheReady.set( false );
        if ( this.terms != null ) this.terms.clear();
        if ( this.individuals != null ) this.individuals.clear();

        model = OntologyLoader.loadMemoryModel( is, this.ontology_URL, OntModelSpec.OWL_MEM );

        index = OntologyIndexer.indexOntology( ontologyName, model, true );

        addTerms( OntologyLoader.initialize( this.ontology_URL, model ) );

        assert index != null;

        indexReady.set( true );
        cacheReady.set( true );
        modelReady.set( true );
        isInitialized.set( true );
        log.info( this.getClass().getSimpleName() + " ready" );
    }

    @Override
    protected synchronized OntModel getModel() {
        if ( model == null ) {
            model = loadModel( this.getOntologyUrl() );
        }
        return model;
    }

    @Override
    protected synchronized void releaseModel( OntModel m ) {
        // do nothing
    }

    @Override
    protected synchronized OntModel loadModel( String url ) {
        return OntologyLoader.loadMemoryModel( url );
    }

}
