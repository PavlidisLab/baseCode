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
import ubic.basecode.ontology.AbstractOntologyTest;
import ubic.basecode.ontology.jena.OntologyLoader;
import ubic.basecode.ontology.jena.OntologyTermImpl;
import ubic.basecode.ontology.jena.search.OntologyIndexer;
import ubic.basecode.ontology.jena.search.OntologySearch;
import ubic.basecode.ontology.jena.search.OntologySearchJenaException;
import ubic.basecode.ontology.jena.search.SearchIndex;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
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
public class OntologySearchTest extends AbstractOntologyTest {

    @Test
    public final void testIndexing() throws Exception {
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/mged.owl.gz" ) ) );
        OntModel model = OntologyLoader.loadMemoryModel( is, "owl-test", false );

        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, true );

        Collection<OntologySearch.SearchResult<OntClass>> name = OntologySearch.matchClasses( model, index, "Bedding" ).toSet();

        assertEquals( 2, name.size() );
        index.close();

        index = OntologyIndexer.indexOntology( "MGEDTEST", model, true );
        name = OntologySearch.matchClasses( model, index, "Bedding" ).toSet();

        assertEquals( 2, name.size() );
        index.close();
    }

    /**
     * See bug 2920
     */
    @Test
    public final void testOmitBadPredicates() throws Exception {

        OntModel model;
        try ( InputStream is = this.getClass().getResourceAsStream( "/data/niforgantest.owl.xml" ) ) {
            assertNotNull( is );
            model = OntologyLoader.loadMemoryModel( is, "NIFTEST" );
        }

        SearchIndex index = OntologyIndexer.indexOntology( "NIFTEST", model, true );

        Collection<OntologySearch.SearchResult<OntClass>> name = OntologySearch.matchClasses( model, index, "Organ" ).toSet();
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
     */
    @Test
    public final void testOmitBadPredicates2() throws Exception {
        OntModel model;
        try ( InputStream is = this.getClass().getResourceAsStream( "/data/eftest.owl.xml" ) ) {
            assertNotNull( is );
            model = OntologyLoader.loadMemoryModel( is, "EFTEST" );
        }

        SearchIndex index = OntologyIndexer.indexOntology( "EFTEST", model, true );

        // positive control
        Collection<OntologySearch.SearchResult<OntClass>> searchResults = OntologySearch.matchClasses( model, index, "monocyte" ).toSet();
        assertFalse( "Should have found something for 'monocyte'", searchResults.isEmpty() );
        assertEquals( 1, searchResults.size() );

        // this is a "definition" that we want to avoid leading to "Monocyte".
        searchResults = OntologySearch.matchClasses( model, index, "liver" ).toSet();
        for ( OntologySearch.SearchResult<OntClass> ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() );
        }

        index.close();
    }

    @Test
    public final void testOmitDefinitions() throws Exception {
        OntModel model;
        try ( InputStream is = this.getClass().getResourceAsStream( "/data/dotest.owl.xml" ) ) {
            assertNotNull( is );
            model = OntologyLoader.loadMemoryModel( is, "DO_TEST" );
        }

        SearchIndex index = OntologyIndexer.indexOntology( "DO_TEST", model, true );

        // positive control
        Set<OntologySearch.SearchResult<OntClass>> searchResults = OntologySearch.matchClasses( model, index, "acute leukemia" ).toSet();
        assertFalse( "Should have found something for 'acute leukemia'", searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "liver" ).toSet();
        for ( OntologySearch.SearchResult<OntClass> ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() );
        }

        index.close();
    }

    @Test
    public final void testOmitDefinitions2() throws Exception {
        OntModel model;
        try ( InputStream is = this.getClass().getResourceAsStream( "/data/nif.organism.test.owl.xml" ) ) {
            assertNotNull( is );
            model = OntologyLoader.loadMemoryModel( is, "NIFORG_TEST", false );
        }

        SearchIndex index = OntologyIndexer.indexOntology( "NIFORG_TEST", model, true );

        // positive control
        Collection<OntologySearch.SearchResult<OntClass>> searchResults = OntologySearch.matchClasses( model, index, "Mammal" ).toSet();
        assertFalse( "Should have found something for 'Mammal'", searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "skin" ).toSet();
        for ( OntologySearch.SearchResult<OntClass> ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'skin'" );
        }

        searchResults = OntologySearch.matchClasses( model, index, "approximate" ).toSet();
        for ( OntologySearch.SearchResult<OntClass> ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'approximate'" );
        }

        searchResults = OntologySearch.matchClasses( model, index, "Bug" ).toSet();
        for ( OntologySearch.SearchResult<OntClass> ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'Bug'" );
        }

        searchResults = OntologySearch.matchClasses( model, index, "birnlex_2" )
                .toSet();
        assertEquals( 1, searchResults.size() );
        assertTrue( new OntologyTermImpl( searchResults.iterator().next().result, null ).isObsolete() );

        index.close();
    }

    @Test
    public final void testOmitDefinitions3() throws Exception {
        OntModel model;
        try ( InputStream is = this.getClass().getResourceAsStream( "/data/obi.test.owl.xml" ) ) {
            assertNotNull( is );
            model = OntologyLoader.loadMemoryModel( is, "OBI_TEST" );
        }

        SearchIndex index = OntologyIndexer.indexOntology( "OBI_TEST", model, true );

        // positive control
        Set<OntologySearch.SearchResult<OntClass>> searchResults = OntologySearch.matchClasses( model, index, "irradiation" ).toSet();
        assertFalse( "Should have found something for 'irradiation'", searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = OntologySearch.matchClasses( model, index, "skin" ).toSet();
        for ( OntologySearch.SearchResult<OntClass> ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'skin'" );
        }

        index.close();
    }

    @Test
    public final void testOmitDefinitions4() throws Exception {
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/NIF-GrossAnatomy.owl.gz" ) ) );

        OntModel model = OntologyLoader.loadMemoryModel( is, "NIFAN_TEST2", false );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "NIFAN_TEST2", model, true );

        // positive control
        Collection<OntologySearch.SearchResult<OntClass>> searchResults = OntologySearch.matchClasses( model, index, "eye" ).toSet();
        assertFalse( "Should have found something for 'eye'", searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "brain"
        searchResults = OntologySearch.matchClasses( model, index, "muscle" ).toSet();
        for ( OntologySearch.SearchResult<OntClass> ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'muscle'" );
        }

        index.close();
    }


    @Test
    public final void testPersistence() throws Exception {
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/mged.owl.gz" ) ) );
        OntModel model = OntologyLoader.loadMemoryModel( is, "owl-test", false );

        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, false );
        index.close();

        // now load it off disk
        index = OntologyIndexer.getSubjectIndex( "MGEDTEST" );

        assertNotNull( index );

        Collection<OntologySearch.SearchResult<OntClass>> name = OntologySearch.matchClasses( model, index, "bedding" ).toSet();
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
        when( index.search( any() ) ).thenThrow( new JenaException( "Some random exception raised by Jena." ) );
        OntologySearchJenaException e = assertThrows( OntologySearchJenaException.class, () -> OntologySearch.matchClasses( model, index, "test" ) );
        assertEquals( "test", e.getQuery() );
        assertEquals( "Some random exception raised by Jena.", e.getCause().getMessage() );
        verify( index ).search( "test" );
    }

    @Test
    public final void matchIndividuals_whenIndexRaisesJenaException_thenWrapItWithOntologyJenaSearchException() {
        OntModel model = mock( OntModel.class );
        SearchIndex index = mock( SearchIndex.class );
        when( index.search( any() ) ).thenThrow( new JenaException( "Some random exception raised by Jena." ) );
        OntologySearchJenaException e = assertThrows( OntologySearchJenaException.class, () -> OntologySearch.matchIndividuals( model, index, "test" ) );
        assertEquals( "test", e.getQuery() );
        assertEquals( "Some random exception raised by Jena.", e.getCause().getMessage() );
        verify( index ).search( "test" );
    }

    @Test
    public final void matchResources_whenIndexRaisesJenaException_thenWrapItWithOntologyJenaSearchException() {
        OntModel model = mock( OntModel.class );
        SearchIndex index = mock( SearchIndex.class );
        when( index.search( any() ) ).thenThrow( new JenaException( "Some random exception raised by Jena." ) );
        OntologySearchJenaException e = assertThrows( OntologySearchJenaException.class, () -> OntologySearch.matchIndividuals( model, index, "test" ) );
        assertEquals( "test", e.getQuery() );
        assertEquals( "Some random exception raised by Jena.", e.getCause().getMessage() );
        verify( index ).search( "test" );
    }
}
