package ubic.basecode.ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyIndividualImpl;
import ubic.basecode.ontology.model.OntologyProperty;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.model.OntologyTermImpl;
import ubic.basecode.ontology.model.PropertyFactory;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyLoader {
    private static Log log = LogFactory.getLog( OntologyLoader.class.getName() );

    private static String dbUrl = Configuration.getString( "gemma.jena.db.url" );
    private static String driver = Configuration.getString( "gemma.jena.db.driver" );
    private static String pwd = Configuration.getString( "gemma.jena.db.password" );
    private static String type = Configuration.getString( "gemma.jena.db.type" );
    private static String user = Configuration.getString( "gemma.jena.db.user" );

    /**
     * @return
     */
    public static ModelMaker getRDBMaker() {
        PersistentOntology po = new PersistentOntology();

        assert driver != null;
        try {
            Class.forName( driver );
        } catch ( Exception e ) {
            log.error( "Failed to load driver: " + driver );
            throw new RuntimeException( e );
        }

        ModelMaker maker = po.getRDBMaker( dbUrl, user, pwd, type, false );
        return maker;
    }

    /**
     * Deletes all cached ontologies from the system. Use with care!
     */
    protected static void wipePersistentStore() {

        try {
            Class.forName( driver );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        IDBConnection conn = new DBConnection( dbUrl, user, pwd, type );

        try {
            conn.cleanDB();
            conn.close();
        } catch ( SQLException e ) {
            throw new RuntimeException();
        }

    }

    /**
     * @param url
     * @param model
     * @return
     */
    public static Collection<OntologyResource> initialize( String url, OntModel model ) {

        Collection<OntologyResource> result = new HashSet<OntologyResource>();

        if ( StringUtils.isBlank( url ) ) {
            log.warn( "Null url" );
            return result;
        }

        ExtendedIterator<OntClass> classIt = model.listClasses();
        int count = 0;
        log.info( "Reading classes for ontology: " + url );
        while ( classIt.hasNext() ) {
            OntClass element = classIt.next();
            if ( element.isAnon() ) continue;
            OntologyTerm ontologyTerm = new OntologyTermImpl( element );
            result.add( ontologyTerm );
            if ( ++count % 1000 == 0 ) {
                log.debug( "Loaded " + count + " terms, last was " + ontologyTerm );
            }
        }

        log.info( "Loaded " + count + " terms" );

        ExtendedIterator<com.hp.hpl.jena.ontology.ObjectProperty> propIt = model.listObjectProperties();
        count = 0;
        log.info( "Reading object properties..." );
        while ( propIt.hasNext() ) {
            com.hp.hpl.jena.ontology.ObjectProperty element = propIt.next();
            OntologyProperty ontologyTerm = PropertyFactory.asProperty( element );
            if ( ontologyTerm == null ) continue; // couldn't be converted for some reason.
            result.add( ontologyTerm );
            if ( ++count % 1000 == 0 ) {
                log.debug( "Loaded " + count + " object properties, last was " + ontologyTerm );
            }
        }

        ExtendedIterator<com.hp.hpl.jena.ontology.DatatypeProperty> dtPropIt = model.listDatatypeProperties();
        log.info( "Reading datatype properties..." );
        while ( dtPropIt.hasNext() ) {
            com.hp.hpl.jena.ontology.DatatypeProperty element = dtPropIt.next();
            OntologyProperty ontologyTerm = PropertyFactory.asProperty( element );
            if ( ontologyTerm == null ) continue; // couldn't be converted for some reason.
            result.add( ontologyTerm );
            if ( ++count % 1000 == 0 ) {
                log.debug( "Loaded " + count + " datatype properties, last was " + ontologyTerm );
            }
        }

        log.info( "Loaded " + count + " properties" );

        ExtendedIterator<Individual> indiIt = model.listIndividuals();
        count = 0;
        log.info( "Reading individuals..." );
        while ( indiIt.hasNext() ) {
            Individual element = indiIt.next();
            if ( element.isAnon() ) continue;
            OntologyIndividual ontologyTerm = new OntologyIndividualImpl( element );
            result.add( ontologyTerm );
            if ( ++count % 1000 == 0 ) {
                log.debug( "Loaded " + count + " individuals, last was " + ontologyTerm );
            }
        }
        log.info( "Loaded " + count + " individuals" );
        return result;
    }

    public static OntModel load( String url ) {
        return loadPersistentModel( url, false );
    }

    /**
     * Added to allow loading of files
     */
    public static OntModel loadFromFile( File file, String base ) throws IOException {
        OntModel model = getRDBModel( base );
        model.read( new FileInputStream( file ), base );
        return model;
    }

    /**
     * Load an ontology into memory. Use this type of model when fast access is critical and memory is available.
     * 
     * @param is
     * @param url, used as a key
     * @return
     * @throws IOException
     */
    public static OntModel loadMemoryModel( InputStream is, String url, OntModelSpec spec ) {
        OntModel model = getMemoryModel( url, spec );
        model.read( is, null );
        return model;
    }

    /**
     * Load an ontology into memory. Use this type of model when fast access is critical and memory is available.
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public static OntModel loadMemoryModel( String url ) {
        return loadMemoryModel( url, OntModelSpec.OWL_MEM_TRANS_INF );
    }

    public static OntModel loadMemoryModel( String url, OntModelSpec spec ) {
        OntModel model = getMemoryModel( url, spec );
        model.read( url );
        return model;
    }

    /**
     * Load a model backed by a persistent store. This type of model is much slower than memory models but uses much
     * less memory.
     * 
     * @param url
     * @param force model to be loaded into the database, even if it already exists there.
     * @return
     */
    public static OntModel loadPersistentModel( String url, boolean force ) {
        return persistModelIfNecessary( url, force );
    }

    /**
     * @param url
     * @return
     */
    protected static Ontology getOntology( String url ) {
        OntModel model = getRDBModel( url );
        Ontology ont = null;
        Map<String, String> m = model.getNsPrefixMap();
        for ( String o : m.keySet() ) {
            if ( StringUtils.isBlank( o ) ) {
                String prefix = model.getNsPrefixURI( o );
                if ( prefix == null ) {
                    continue;
                }
                ont = model.getOntology( prefix.replace( "#", "" ) );
            }
        }
        return ont;
    }

    /**
     * Get model that is entirely in memory with default OntModelSpec.OWL_MEM_RDFS_INF.
     * 
     * @param url
     * @return
     */
    static OntModel getMemoryModel( String url ) {
        return getMemoryModel( url, OntModelSpec.OWL_MEM_RDFS_INF );
    }

    /**
     * Get model that is entirely in memory.
     * 
     * @param url
     * @param specification
     * @return
     */
    static OntModel getMemoryModel( String url, OntModelSpec specification ) {
        OntModelSpec spec = new OntModelSpec( specification );
        ModelMaker maker = ModelFactory.createMemModelMaker();
        Model base = maker.createModel( url, false );
        spec.setImportModelMaker( maker );

        return ModelFactory.createOntologyModel( spec, base );
    }

    /**
     * Get model backed by persistent store. Slower.
     * 
     * @param url
     * @return
     */
    private static OntModel getRDBModel( String url ) {
        if ( StringUtils.isBlank( url ) ) {
            throw new IllegalArgumentException( "OWL URL must not be blank" );
        }
        OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_DL_MEM_TRANS_INF );
        ModelMaker maker = getRDBMaker();
        spec.setImportModelMaker( maker );
        Model base;
        if ( url == null ) {
            base = maker.createDefaultModel();
        } else {
            base = maker.createModel( url, false );
        }

        return ModelFactory.createOntologyModel( spec, base );

    }

    /**
     * @param url
     * @param force
     * @return
     */
    private static OntModel persistModelIfNecessary( String url, boolean force ) {
        log.info( "Getting model ..." );
        OntModel model = getRDBModel( url );
        if ( model.isEmpty() ) {
            log.info( url + ": New ontology, loading..." );
            model.read( url );
        } else if ( force ) {
            log.info( url + ": Reloading..." );
            model.read( url );
        } else {
            log.info( url + ": Ontology already exists in persistent store" );
        }
        return model;
    }
}
