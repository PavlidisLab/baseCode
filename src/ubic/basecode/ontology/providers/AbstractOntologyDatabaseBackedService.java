package ubic.basecode.ontology.providers;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.search.OntologySearch;

import com.hp.hpl.jena.ontology.OntModel;

/* This class has some stuff that's specific to database backed ontologies. 
 * We use a pool because each jena ontology model holds on to a database connection forever.
 * Eventually connection is killed by MySQL server, so to stop this we do a dummy search when ontology model
 * is idle in the pool. This keeps connection alive.
 */
public abstract class AbstractOntologyDatabaseBackedService extends AbstractOntologyService {

    private final GenericObjectPool pool;

    public AbstractOntologyDatabaseBackedService() {
        super();
        this.pool = new GenericObjectPool( new PoolableObjectFactory() {

            @Override
            public void activateObject( Object obj ) {
                // no op
            }

            @Override
            public void destroyObject( Object obj ) {
                // ( ( OntModel ) obj ).close();
                // obj = null;
            }

            @Override
            public Object makeObject() {
                log.warn( "Making model for: " + ontology_URL );
                OntModel model = loadModel( ontology_URL );
                modelReady.set( true );
                return model;
            }

            @Override
            public void passivateObject( Object obj ) {
                // no op
            }

            // This is called regularly by eviction thread. It should keep db
            // connection alive.
            @Override
            public boolean validateObject( Object obj ) {
                OntModel model = ( OntModel ) obj;
                try {
                    OntologySearch.matchIndividuals( model, index, "test" );
                    return true;
                } catch ( Exception e ) {
                    return false;
                }
            }
        } );

        // These could be made into configuration settings (BASECODE)
        pool.setMaxActive( 3 );
        pool.setMaxIdle( 2 );
        pool.setMinIdle( 1 );

        // Max amount of time to wait for makeObject to return. How long does
        // loadModel take?
        pool.setMaxWait( 30000 );

        // Settings for eviction thread.
        pool.setMinEvictableIdleTimeMillis( 1000L * 60L * 60L );
        pool.setTimeBetweenEvictionRunsMillis( 1000L * 60L * 30L );
        pool.setTestWhileIdle( true );
    }

    @Override
    protected OntModel getModel() {
        OntModel model = null;
        try {
            model = ( OntModel ) pool.borrowObject();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return model;
    }

    @Override
    protected OntModel loadModel( String url ) {
        return OntologyLoader.loadPersistentModel( url, false );
    }

    @Override
    protected void releaseModel( OntModel m ) {
        try {
            pool.returnObject( m );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

}
