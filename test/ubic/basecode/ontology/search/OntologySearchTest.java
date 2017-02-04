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
package ubic.basecode.ontology.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.ontology.model.OntologyTerm;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;

/**
 * Most of these tests were moved over from Gemma.
 * 
 * @author Paul
 * @version $Id$
 */
public class OntologySearchTest {

    @Test
    public final void testIndexing() throws Exception {
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/mged.owl.gz" ) );
        OntModel model = OntologyLoader.loadMemoryModel( is, "owl-test", OntModelSpec.OWL_MEM_TRANS_INF );

        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, true );

        Collection<OntologyTerm> name = OntologySearch.matchClasses( model, index, "Bedding" );

        assertEquals( 2, name.size() );
        index.close();

        index = OntologyIndexer.indexOntology( "MGEDTEST", model, true );
        name = OntologySearch.matchClasses( model, index, "Bedding" );

        assertEquals( 2, name.size() );
        index.close();
    }

    /**
     * See bug 2920
     * 
     * @throws Exception
     */
    @Test
    public final void testOmitBadPredicates() throws Exception {

        InputStream is = this.getClass().getResourceAsStream( "/data/niforgantest.owl.xml" );
        OntModel model = OntologyLoader.loadMemoryModel( is, "NIFTEST", OntModelSpec.OWL_MEM_TRANS_INF );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "NIFTEST", model, true );

        Collection<OntologyTerm> name = OntologySearch.matchClasses( model, index, "Organ" );
        // for ( OntologyTerm ontologyTerm : name ) {
        // log.debug( ontologyTerm.toString() );
        // }
        // should get : Organ, Human Tissue and Organ Resource for Research, United Network for Organ Sharing
        assertEquals( 3, name.size() );

        name = OntologySearch.matchClasses( model, index, "Anatomical entity" );
        // for ( OntologyTerm ontologyTerm : name ) {
        // log.debug( ontologyTerm.toString() );
        // }
        assertEquals( 1, name.size() );

        name = OntologySearch.matchClasses( model, index, "liver" ); // this is an "example" that we want to avoid
                                                                     // leading to "Organ".

        // for ( OntologyTerm ontologyTerm : name ) {
        // log.debug( ontologyTerm.toString() );
        // }
        assertEquals( 0, name.size() );

        index.close();
    }

    /**
     * See bug 3269
     * 
     * @throws Exception
     */
    @Test
    public final void testOmitBadPredicates2() throws Exception {
        InputStream is = this.getClass().getResourceAsStream( "/data/eftest.owl.xml" );
        OntModel model = OntologyLoader.loadMemoryModel( is, "EFTEST", OntModelSpec.OWL_MEM_TRANS_INF );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "EFTEST", model, true );

        // positive control
        Collection<OntologyTerm> searchResults = OntologySearch.matchClasses( model, index, "monocyte" );
        assertTrue( "Should have found something for 'monocyte'", !searchResults.isEmpty() );
        assertEquals( 1, searchResults.size() );

        // this is a "definition" that we want to avoid leading to "Monocyte".
        searchResults = OntologySearch.matchClasses( model, index, "liver" );
        for ( OntologyTerm ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
    }

    @Test
    public final void testOmitDefinitions() throws Exception {
        InputStream is = this.getClass().getResourceAsStream( "/data/dotest.owl.xml" );
        OntModel model = OntologyLoader.loadMemoryModel( is, "DO_TEST", OntModelSpec.OWL_MEM_TRANS_INF );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "DO_TEST", model, true );

        // positive control
        Collection<OntologyTerm> searchResults = OntologySearch.matchClasses( model, index, "acute leukemia" );
        assertTrue( "Should have found something for 'acute leukemia'", !searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "liver" );
        for ( OntologyTerm ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
    }

    @Test
    public final void testOmitDefinitions2() throws Exception {
        InputStream is = this.getClass().getResourceAsStream( "/data/nif.organism.test.owl.xml" );
        OntModel model = OntologyLoader.loadMemoryModel( is, "NIFORG_TEST", OntModelSpec.OWL_MEM_TRANS_INF );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "NIFORG_TEST", model, true );

        // positive control
        Collection<OntologyTerm> searchResults = OntologySearch.matchClasses( model, index, "Mammal" );
        assertTrue( "Should have found something for 'Mammal'", !searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "skin" );
        for ( OntologyTerm ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'skin'" );
        }
        assertEquals( 0, searchResults.size() );

        searchResults = OntologySearch.matchClasses( model, index, "approximate" );
        for ( OntologyTerm ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'approximate'" );
        }
        assertEquals( 0, searchResults.size() );

        searchResults = OntologySearch.matchClasses( model, index, "Bug" );
        for ( OntologyTerm ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'Bug'" );
        }
        assertEquals( 0, searchResults.size() );

        searchResults = OntologySearch.matchClasses( model, index, "birnlex_2" );
        for ( OntologyTerm ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'birnlex_2'" );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
    }

    @Test
    public final void testOmitDefinitions3() throws Exception {
        InputStream is = this.getClass().getResourceAsStream( "/data/obi.test.owl.xml" );
        OntModel model = OntologyLoader.loadMemoryModel( is, "OBI_TEST", OntModelSpec.OWL_MEM_TRANS_INF );

        SearchIndex index = OntologyIndexer.indexOntology( "OBI_TEST", model, true );

        // positive control
        Collection<OntologyTerm> searchResults = OntologySearch.matchClasses( model, index, "irradiation" );
        assertTrue( "Should have found something for 'irradiation'", !searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "skin" );
        for ( OntologyTerm ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'skin'" );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
    }

    @Test
    public final void testOmitDefinitions4() throws Exception {
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/NIF-GrossAnatomy.owl.gz" ) );

        OntModel model = OntologyLoader.loadMemoryModel( is, "NIFAN_TEST2", OntModelSpec.OWL_MEM_TRANS_INF );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "NIFAN_TEST2", model, true );

        // positive control
        Collection<OntologyTerm> searchResults = OntologySearch.matchClasses( model, index, "eye" );
        assertTrue( "Should have found something for 'eye'", !searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "brain"
        searchResults = OntologySearch.matchClasses( model, index, "muscle" );
        for ( OntologyTerm ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'muscle'" );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
    }

    /**
     * @throws Exception
     */
    @Test
    public final void testPersistence() throws Exception {
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/mged.owl.gz" ) );
        OntModel model = OntologyLoader.loadMemoryModel( is, "owl-test", OntModelSpec.OWL_MEM_TRANS_INF );

        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, true );
        index.close();

        // now load it off disk
        index = OntologyIndexer.getSubjectIndex( "MGEDTEST" );

        Collection<OntologyTerm> name = OntologySearch.matchClasses( model, index, "bedding" );
        assertEquals( 2, name.size() );

        // test wildcard. Works with stemmed term, wild card doesn't do anything
        name = OntologySearch.matchClasses( model, index, "bed*" );
        assertEquals( 2, name.size() );

        // stemmed term.
        name = OntologySearch.matchClasses( model, index, "bed" );
        assertEquals( 2, name.size() );

        name = OntologySearch.matchClasses( model, index, "beddin*" );
        assertEquals( 2, name.size() );
        index.close();
    }

}
