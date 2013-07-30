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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyIndividualImpl;
import ubic.basecode.ontology.model.OntologyProperty;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.model.OntologyTermImpl;
import ubic.basecode.ontology.model.PropertyFactory;

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
    private static final int MAX_LOAD_TRIES = 3;

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
        InputStream s = null;

        int tries = 0;

        Throwable lastException = null;
        while ( tries < MAX_LOAD_TRIES ) {
            try {

                URLConnection urlc = new URL( url ).openConnection();

                // help ensure mis-configured web servers aren't causing trouble.
                urlc.setRequestProperty( "Accept", "application/rdf+xml" );
                urlc.connect();

                s = urlc.getInputStream();

                if ( tries > 0 ) {
                    log.info( "Retrying loading of " + url + " [" + tries + "/" + MAX_LOAD_TRIES + " of max tries" );
                } else {
                    log.info( "Loading ontology from " + url );
                }

                BufferedReader buf = new BufferedReader( new InputStreamReader( s ) );

                model.read( buf, url );
                if ( timer.getTime() > 100 ) {
                    log.debug( "Load model: " + timer.getTime() + "ms" );
                }
                s.close();
                break;
            } catch ( MalformedURLException e ) {
                throw new RuntimeException( e );
            } catch ( IOException e ) {
                // try to recover.
                lastException = e;
                log.error( e + " retrying?" );
                tries++;
            } finally {
                if ( s != null ) {
                    try {
                        s.close();
                    } catch ( IOException e ) {
                        log.error( e, e );
                    }
                }
            }
        }

        if ( lastException != null ) {
            throw new RuntimeException( lastException );
        }

        assert model != null;
        return model;
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

}
