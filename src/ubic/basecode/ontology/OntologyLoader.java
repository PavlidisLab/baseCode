/*
 * The baseCode project
 * 
 * Copyright (c) 2010 University of British Columbia
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
package ubic.basecode.ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyIndividualImpl;
import ubic.basecode.ontology.model.OntologyProperty;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.model.OntologyTermImpl;
import ubic.basecode.ontology.model.PropertyFactory;

import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Reads ontologies from OWL resources
 * 
 * @author paul
 * @version $Id$
 */
public class OntologyLoader {
    private static Log log = LogFactory.getLog( OntologyLoader.class );

    /**
     * @return
     */
    public static ModelMaker getRDBMaker() {
        PersistentOntology po = new PersistentOntology();

        ModelMaker maker = po.getRDBMaker( false );
        return maker;
    }

    /**
     * Deletes all cached ontologies from the system. Use with care!
     */
    protected static void wipePersistentStore() {

        IDBConnection conn = OntologyDataSource.getConnection();

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

        ExtendedIterator<OntClass> classIt = model.listClasses();
        int count = 0;
        log.debug( "Reading classes for ontology: " + url );
        while ( classIt.hasNext() ) {
            OntClass element = classIt.next();
            if ( element.isAnon() ) continue;
            OntologyTerm ontologyTerm = new OntologyTermImpl( element );
            result.add( ontologyTerm );
            if ( ++count % 1000 == 0 ) {
                log.debug( "Loaded " + count + " terms, last was " + ontologyTerm );
            }
        }

        log.debug( "Loaded " + count + " terms" );

        ExtendedIterator<com.hp.hpl.jena.ontology.ObjectProperty> propIt = model.listObjectProperties();
        count = 0;
        log.debug( "Reading object properties..." );
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
        log.debug( "Reading datatype properties..." );
        while ( dtPropIt.hasNext() ) {
            com.hp.hpl.jena.ontology.DatatypeProperty element = dtPropIt.next();
            OntologyProperty ontologyTerm = PropertyFactory.asProperty( element );
            if ( ontologyTerm == null ) continue; // couldn't be converted for some reason.
            result.add( ontologyTerm );
            if ( ++count % 1000 == 0 ) {
                log.debug( "Loaded " + count + " datatype properties, last was " + ontologyTerm );
            }
        }

        log.debug( "Loaded " + count + " properties" );

        ExtendedIterator<Individual> indiIt = model.listIndividuals();
        count = 0;
        log.debug( "Reading individuals..." );
        while ( indiIt.hasNext() ) {
            Individual element = indiIt.next();
            if ( element.isAnon() ) continue;
            OntologyIndividual ontologyTerm = new OntologyIndividualImpl( element );
            result.add( ontologyTerm );
            if ( ++count % 1000 == 0 ) {
                log.debug( "Loaded " + count + " individuals, last was " + ontologyTerm );
            }
        }
        log.debug( "Loaded " + count + " individuals" );
        return result;
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
        StopWatch timer = new StopWatch();
        timer.start();
        OntModel model = getMemoryModel( url, spec );
        model.read( url );
        if ( timer.getTime() > 100 ) {
            log.debug( "Load model: " + timer.getTime() + "ms" );
        }
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
     * Get model backed by persistent store.
     * 
     * @param url
     * @return
     */
    private static OntModel getRDBModel( String url ) {
        StopWatch timer = new StopWatch();
        timer.start();
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
        try {
            return ModelFactory.createOntologyModel( spec, base );
        } finally {
            if ( timer.getTime() > 100 ) {
                log.debug( "Load model: " + timer.getTime() + "ms" );
            }
            maker.close();
        }

    }

    /**
     * @param url
     * @param force
     * @return
     */
    private static OntModel persistModelIfNecessary( String url, boolean force ) {
        log.debug( "Getting model ..." );
        OntModel model = getRDBModel( url );
        if ( model.isEmpty() ) {
            log.info( url + ": New ontology, loading..." );
            model.read( url );
        } else if ( force ) {
            log.info( url + ": Reloading..." );
            model.read( url );
        } else {
            log.debug( url + ": Ontology already exists in persistent store" );
        }

        return model;
    }
}
