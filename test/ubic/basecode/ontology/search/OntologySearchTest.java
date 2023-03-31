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

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.shared.JenaException;
import org.junit.Test;
import ubic.basecode.ontology.jena.OntologyLoader;
import ubic.basecode.ontology.jena.OntologyTermImpl;
import ubic.basecode.ontology.jena.search.*;
import ubic.basecode.ontology.jena.search.OntologySearch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Most of these tests were moved over from Gemma.
 *
 * @author Paul
 */
public class OntologySearchTest {

    @Test
    public final void testIndexing() throws Exception {
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/mged.owl.gz" ) );
        OntModel model = OntologyLoader.loadMemoryModel( is, "owl-test" );

        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, true );

        Collection<OntClass> name = OntologySearch.matchClasses( model, index, "Bedding" ).toSet();

        assertEquals( 2, name.size() );
        index.close();

        index = OntologyIndexer.indexOntology( "MGEDTEST", model, true );
        name = OntologySearch.matchClasses( model, index, "Bedding" ).toSet();

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
        OntModel model = OntologyLoader.loadMemoryModel( is, "NIFTEST" );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "NIFTEST", model, true );

        Collection<OntClass> name = OntologySearch.matchClasses( model, index, "Organ" ).toSet();
        // for ( OntClass ontologyTerm : name ) {
        // log.debug( ontologyTerm.toString() );
        // }
        // should get : Organ, Human Tissue and Organ Resource for Research, United Network for Organ Sharing
        assertEquals( 3, name.size() );

        name = OntologySearch.matchClasses( model, index, "Anatomical entity" ).toSet();
        // for ( OntClass ontologyTerm : name ) {
        // log.debug( ontologyTerm.toString() );
        // }
        assertEquals( 1, name.size() );

        name = OntologySearch.matchClasses( model, index, "liver" ).toSet(); // this is an "example" that we want to avoid
        // leading to "Organ".

        // for ( OntClass ontologyTerm : name ) {
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
        OntModel model = OntologyLoader.loadMemoryModel( is, "EFTEST" );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "EFTEST", model, true );

        // positive control
        Collection<OntClass> searchResults = OntologySearch.matchClasses( model, index, "monocyte" ).toSet();
        assertTrue( "Should have found something for 'monocyte'", !searchResults.isEmpty() );
        assertEquals( 1, searchResults.size() );

        // this is a "definition" that we want to avoid leading to "Monocyte".
        searchResults = OntologySearch.matchClasses( model, index, "liver" ).toSet();
        for ( OntClass ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
    }

    @Test
    public final void testOmitDefinitions() throws Exception {
        InputStream is = this.getClass().getResourceAsStream( "/data/dotest.owl.xml" );
        OntModel model = OntologyLoader.loadMemoryModel( is, "DO_TEST" );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "DO_TEST", model, true );

        // positive control
        Collection<OntClass> searchResults = OntologySearch.matchClasses( model, index, "acute leukemia" ).toSet();
        assertTrue( "Should have found something for 'acute leukemia'", !searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "liver" ).toSet();
        for ( OntClass ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
    }

    @Test
    public final void testOmitDefinitions2() throws Exception {
        InputStream is = this.getClass().getResourceAsStream( "/data/nif.organism.test.owl.xml" );
        OntModel model = OntologyLoader.loadMemoryModel( is, "NIFORG_TEST" );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "NIFORG_TEST", model, true );

        // positive control
        Collection<OntClass> searchResults = OntologySearch.matchClasses( model, index, "Mammal" ).toSet();
        assertTrue( "Should have found something for 'Mammal'", !searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "skin" ).toSet();
        for ( OntClass ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'skin'" );
        }
        assertEquals( 0, searchResults.size() );

        searchResults = OntologySearch.matchClasses( model, index, "approximate" ).toSet();
        for ( OntClass ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'approximate'" );
        }
        assertEquals( 0, searchResults.size() );

        searchResults = OntologySearch.matchClasses( model, index, "Bug" ).toSet();
        for ( OntClass ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'Bug'" );
        }
        assertEquals( 0, searchResults.size() );

        searchResults = OntologySearch.matchClasses( model, index, "birnlex_2" )
                .toSet();
        assertEquals( 1, searchResults.size() );
        assertTrue( new OntologyTermImpl( searchResults.iterator().next(), null ).isObsolete() );

        index.close();
    }

    @Test
    public final void testOmitDefinitions3() throws Exception {
        InputStream is = this.getClass().getResourceAsStream( "/data/obi.test.owl.xml" );
        OntModel model = OntologyLoader.loadMemoryModel( is, "OBI_TEST" );

        SearchIndex index = OntologyIndexer.indexOntology( "OBI_TEST", model, true );

        // positive control
        Collection<OntClass> searchResults = OntologySearch.matchClasses( model, index, "irradiation" ).toSet();
        assertTrue( "Should have found something for 'irradiation'", !searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "skin" ).toSet();
        for ( OntClass ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'skin'" );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
        is.close();
    }

    @Test
    public final void testOmitDefinitions4() throws Exception {
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/NIF-GrossAnatomy.owl.gz" ) );

        OntModel model = OntologyLoader.loadMemoryModel( is, "NIFAN_TEST2" );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "NIFAN_TEST2", model, true );

        // positive control
        Collection<OntClass> searchResults = OntologySearch.matchClasses( model, index, "eye" ).toSet();
        assertTrue( "Should have found something for 'eye'", !searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "brain"
        searchResults = OntologySearch.matchClasses( model, index, "muscle" ).toSet();
        for ( OntClass ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'muscle'" );
        }
        assertEquals( 0, searchResults.size() );

        index.close();
    }

    @Test
    public void testOmitDefinition() throws IOException, OntologySearchException {
        OntModel model;
        try ( InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/uberon.owl.gz" ) ) ) ) {
            model = OntologyLoader.loadMemoryModel( is, "UBERON_TEST2" );
        }

        SearchIndex index = OntologyIndexer.indexOntology( "UBERON_TEST2", model, false );

        OntClass brain = model.getOntClass( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( brain );

        Collection<OntClass> searchResults = OntologySearch.matchClasses( model, index, "brain" ).toSet();
        assertEquals( 128, searchResults.size() );
    }

    /**
     * @throws Exception
     */
    @Test
    public final void testPersistence() throws Exception {
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/mged.owl.gz" ) );
        OntModel model = OntologyLoader.loadMemoryModel( is, "owl-test" );

        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, false );
        index.close();

        // now load it off disk
        index = OntologyIndexer.getSubjectIndex( "MGEDTEST" );

        assertNotNull( index );

        Collection<OntClass> name = OntologySearch.matchClasses( model, index, "bedding" ).toSet();
        assertEquals( 2, name.size() );

        // test wildcard. Works with stemmed term, wild card doesn't do anything
        name = OntologySearch.matchClasses( model, index, "bed*" ).toSet();
        assertEquals( 2, name.size() );

        // stemmed term.
        name = OntologySearch.matchClasses( model, index, "bed" ).toSet();
        assertEquals( 2, name.size() );

        name = OntologySearch.matchClasses( model, index, "beddin*" ).toSet();
        assertEquals( 2, name.size() );
        index.close();
    }

    @Test
    public final void matchClasses_whenIndexRaisesJenaException_thenWrapItWithOntologyJenaSearchException() {
        OntModel model = mock( OntModel.class );
        SearchIndex index = mock( SearchIndex.class );
        when( index.searchModelByIndex( any() ) ).thenThrow( new JenaException( "Some random exception raised by Jena." ) );
        OntologySearchJenaException e = assertThrows( OntologySearchJenaException.class, () -> OntologySearch.matchClasses( model, index, "test" ) );
        assertEquals( "test", e.getQuery() );
        assertEquals( "Some random exception raised by Jena.", e.getCause().getMessage() );
        verify( index ).searchModelByIndex( "test" );
    }

    @Test
    public final void matchIndividuals_whenIndexRaisesJenaException_thenWrapItWithOntologyJenaSearchException() {
        OntModel model = mock( OntModel.class );
        SearchIndex index = mock( SearchIndex.class );
        when( index.searchModelByIndex( any() ) ).thenThrow( new JenaException( "Some random exception raised by Jena." ) );
        OntologySearchJenaException e = assertThrows( OntologySearchJenaException.class, () -> OntologySearch.matchIndividuals( model, index, "test" ) );
        assertEquals( "test", e.getQuery() );
        assertEquals( "Some random exception raised by Jena.", e.getCause().getMessage() );
        verify( index ).searchModelByIndex( "test" );
    }

    @Test
    public final void matchResources_whenIndexRaisesJenaException_thenWrapItWithOntologyJenaSearchException() {
        OntModel model = mock( OntModel.class );
        SearchIndex index = mock( SearchIndex.class );
        when( index.searchModelByIndex( any() ) ).thenThrow( new JenaException( "Some random exception raised by Jena." ) );
        OntologySearchJenaException e = assertThrows( OntologySearchJenaException.class, () -> OntologySearch.matchIndividuals( model, index, "test" ) );
        assertEquals( "test", e.getQuery() );
        assertEquals( "Some random exception raised by Jena.", e.getCause().getMessage() );
        verify( index ).searchModelByIndex( "test" );
    }
}
