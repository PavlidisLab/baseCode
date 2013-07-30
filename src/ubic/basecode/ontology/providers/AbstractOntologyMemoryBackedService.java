/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.ontology.providers;

import java.io.IOException;
import java.io.InputStream;

import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.search.OntologyIndexer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;

/**
 * This class has some stuff that's specific to in-memory ontologies. Unlike database backed ontologies we don't use a
 * pool keeping only one instance of model in memory.
 * 
 * @author paul
 * @version $Id$
 */
public abstract class AbstractOntologyMemoryBackedService extends AbstractOntologyService {

    /**
     * For testing! Overrides normal way of loading the ontology.
     * 
     * @param is
     * @throws IOException
     */
    public synchronized void loadTermsInNameSpace( InputStream is ) {
        if ( initializationThread.isAlive() ) {
            log.warn( this.getOntologyName() + " initialization is already running, trying to cancel ..." );
            initializationThread.cancel();

            // wait for the thread to die.
            int maxWait = 10;
            int wait = 0;
            while ( initializationThread.isAlive() ) {
                try {
                    Thread.sleep( 5000 );
                    log.warn( "Waiting for auto-initialization to stop so manual initialization can begin ..." );
                } catch ( InterruptedException e ) {
                    // no-op.
                }
                if ( ++wait >= maxWait ) {
                    log.error( "Got tired of waiting" );
                    break;
                }
                if ( initializationThread.isInterrupted() ) {
                    log.warn( "Got interrupt." );
                    break;
                }
            }
        }

        this.indexReady.set( false );
        this.modelReady.set( false );
        this.isInitialized.set( false );
        this.cacheReady.set( false );
        if ( this.terms != null ) this.terms.clear();
        if ( this.individuals != null ) this.individuals.clear();

        model = OntologyLoader.loadMemoryModel( is, this.getOntologyUrl(), OntModelSpec.OWL_MEM );

        index = OntologyIndexer.indexOntology( getOntologyName(), model, true );

        addTerms( OntologyLoader.initialize( this.getOntologyUrl(), model ) );

        assert index != null;

        indexReady.set( true );
        cacheReady.set( true );
        modelReady.set( true );
        isInitialized.set( true );
        log.info( this.getClass().getSimpleName() + " ready" );
    }

    @Override
    protected synchronized OntModel loadModel() {
        return OntologyLoader.loadMemoryModel( this.getOntologyUrl() );
    }

}
