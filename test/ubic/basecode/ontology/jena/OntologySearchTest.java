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
package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.*;
import static ubic.basecode.ontology.jena.JenaUtils.as;

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

        HashSet<OntologyIndexer.IndexableProperty> indexableProperties = new HashSet<>( OntologyIndexer.DEFAULT_INDEXABLE_PROPERTIES );
        indexableProperties.add( new OntologyIndexer.IndexableProperty( RDFS.comment, true ) );
        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, indexableProperties, Collections.emptySet(), true );

        Collection<SearchIndex.JenaSearchResult> name = index.searchClasses( model, "Bedding", 500 );

        assertEquals( 1, name.size() );
        index.close();

        index = OntologyIndexer.indexOntology( "MGEDTEST", model, indexableProperties, Collections.emptySet(), true );
        name = index.searchClasses( model, "Bedding", 500 );

        assertEquals( 1, name.size() );
        index.close();
    }

    @Test
    public void testStemming() throws Exception {
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/mged.owl.gz" ) ) );
        OntModel model = OntologyLoader.loadMemoryModel( is, "owl-test", false );
        HashSet<OntologyIndexer.IndexableProperty> indexableProperties = new HashSet<>( OntologyIndexer.DEFAULT_INDEXABLE_PROPERTIES );
        indexableProperties.add( new OntologyIndexer.IndexableProperty( RDFS.comment, true ) );
        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, indexableProperties, Collections.emptySet(), true );

        // bedding is stemmed to bed
        List<SearchIndex.JenaSearchResult> results = index.searchClasses( model, "bed", 500 );
        Assertions.assertThat( results ).extracting( sr -> sr.result.getURI() )
                .containsExactly( "http://mged.sourceforge.net/ontologies/MGEDOntology.owl#Bedding" );

        // plural query
        results = index.searchClasses( model, "beddings", 500 );
        Assertions.assertThat( results ).extracting( sr -> sr.result.getURI() )
                .containsExactly( "http://mged.sourceforge.net/ontologies/MGEDOntology.owl#Bedding" );

        // plural query
        results = index.searchClasses( model, "beds", 500 );
        Assertions.assertThat( results ).extracting( sr -> sr.result.getURI() )
                .containsExactly( "http://mged.sourceforge.net/ontologies/MGEDOntology.owl#Bedding" );

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

        HashSet<OntologyIndexer.IndexableProperty> indexableProperties = new HashSet<>( OntologyIndexer.DEFAULT_INDEXABLE_PROPERTIES );
        indexableProperties.add( new OntologyIndexer.IndexableProperty( RDFS.comment, true ) );
        SearchIndex index = OntologyIndexer.indexOntology( "NIFTEST", model, indexableProperties, Collections.emptySet(), true );

        Collection<SearchIndex.JenaSearchResult> name = index.searchClasses( model, "Organ", 500 );
        // for ( OntClass ontologyTerm : name ) {
        // log.debug( ontologyTerm.toString() );
        // }
        // should get : Organ, Human Tissue and Organ Resource for Research, United Network for Organ Sharing
        assertEquals( 3, name.size() );

        name = index.searchClasses( model, "Anatomical entity", 500 );
        // for ( OntClass ontologyTerm : name ) {
        // log.debug( ontologyTerm.toString() );
        // }
        assertEquals( 1, name.size() );

        name = index.searchClasses( model, "liver", 500 ); // this is an "example" that we want to avoid
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

        SearchIndex index = OntologyIndexer.indexOntology( "EFTEST", model, Collections.emptySet(), true );

        // positive control
        Collection<SearchIndex.JenaSearchResult> searchResults = index.searchClasses( model, "monocyte", 500 );
        assertFalse( "Should have found something for 'monocyte'", searchResults.isEmpty() );
        assertEquals( 1, searchResults.size() );

        // this is a "definition" that we want to avoid leading to "Monocyte".
        searchResults = index.searchClasses( model, "liver", 500 );
        for ( SearchIndex.JenaSearchResult ontologyTerm : searchResults ) {
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

        SearchIndex index = OntologyIndexer.indexOntology( "DO_TEST", model, Collections.emptySet(), true );

        // positive control
        List<SearchIndex.JenaSearchResult> searchResults = index.searchClasses( model, "acute leukemia", 500 );
        assertFalse( "Should have found something for 'acute leukemia'", searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = index.searchClasses( model, "liver", 500 );
        for ( SearchIndex.JenaSearchResult ontologyTerm : searchResults ) {
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

        HashSet<OntologyIndexer.IndexableProperty> indexableProperties = new HashSet<>( OntologyIndexer.DEFAULT_INDEXABLE_PROPERTIES );
        indexableProperties.add( new OntologyIndexer.IndexableProperty( RDFS.comment, true ) );
        SearchIndex index = OntologyIndexer.indexOntology( "NIFORG_TEST", model, indexableProperties, Collections.emptySet(), true );

        // positive control
        Collection<SearchIndex.JenaSearchResult> searchResults = index.searchClasses( model, "Mammal", 500 );
        assertFalse( "Should have found something for 'Mammal'", searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = index.searchClasses( model, "skin", 500 );
        for ( SearchIndex.JenaSearchResult ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'skin'" );
        }

        searchResults = index.searchClasses( model, "approximate", 500 );
        for ( SearchIndex.JenaSearchResult ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'approximate'" );
        }

        searchResults = index.searchClasses( model, "Bug", 500 );
        for ( SearchIndex.JenaSearchResult ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'Bug'" );
        }

        searchResults = index.searchClasses( model, "birnlex_2", 500 );
        Assertions.assertThat( searchResults ).hasSize( 1 )
                .extracting( sr -> as( sr.result, OntClass.class ) )
                .satisfiesOnlyOnce( r -> {
                    Assertions.assertThat( r ).hasValueSatisfying( r2 -> r2.hasLiteral( OWL2.deprecated, true ) );
                } );
        index.close();
    }

    @Test
    public final void testOmitDefinitions3() throws Exception {
        OntModel model;
        try ( InputStream is = this.getClass().getResourceAsStream( "/data/obi.test.owl.xml" ) ) {
            assertNotNull( is );
            model = OntologyLoader.loadMemoryModel( is, "OBI_TEST" );
        }

        SearchIndex index = OntologyIndexer.indexOntology( "OBI_TEST", model, Collections.emptySet(), true );

        // positive control
        List<SearchIndex.JenaSearchResult> searchResults = index.searchClasses( model, "irradiation", 500 );
        assertFalse( "Should have found something for 'irradiation'", searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "acute leukemia".
        searchResults = index.searchClasses( model, "skin", 500 );
        for ( SearchIndex.JenaSearchResult ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'skin'" );
        }

        index.close();
    }

    @Test
    public final void testOmitDefinitions4() throws Exception {
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/NIF-GrossAnatomy.owl.gz" ) ) );

        OntModel model = OntologyLoader.loadMemoryModel( is, "NIFAN_TEST2", false );
        is.close();

        SearchIndex index = OntologyIndexer.indexOntology( "NIFAN_TEST2", model, Collections.emptySet(), true );

        // positive control
        Collection<SearchIndex.JenaSearchResult> searchResults = index.searchClasses( model, "eye", 500 );
        assertFalse( "Should have found something for 'eye'", searchResults.isEmpty() );

        // this is a "definition" that we want to avoid leading to "brain"
        searchResults = index.searchClasses( model, "muscle", 500 );
        for ( SearchIndex.JenaSearchResult ontologyTerm : searchResults ) {
            fail( "Should not have found " + ontologyTerm.toString() + " for 'muscle'" );
        }

        index.close();
    }


    @Test
    public final void testPersistence() throws Exception {
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/mged.owl.gz" ) ) );
        OntModel model = OntologyLoader.loadMemoryModel( is, "owl-test", false );

        HashSet<OntologyIndexer.IndexableProperty> indexableProperties = new HashSet<>( OntologyIndexer.DEFAULT_INDEXABLE_PROPERTIES );
        indexableProperties.add( new OntologyIndexer.IndexableProperty( RDFS.comment, true ) );

        SearchIndex index = OntologyIndexer.indexOntology( "MGEDTEST", model, indexableProperties, Collections.emptySet(), false );
        index.close();

        // now load it off disk
        index = OntologyIndexer.getSubjectIndex( "MGEDTEST", indexableProperties, Collections.emptySet() );

        assertNotNull( index );

        Collection<SearchIndex.JenaSearchResult> name = index.searchClasses( model, "bedding", 500 );
        assertEquals( 1, name.size() );

        // test wildcard. Works with stemmed term, wild card doesn't do anything
        name = index.searchClasses( model, "bed*", 500 );
        assertEquals( 2, name.size() );

        // stemmed term.
        name = index.searchClasses( model, "bed", 500 );
        assertEquals( 1, name.size() );

        name = index.searchClasses( model, "beddin*", 500 );
        assertEquals( 2, name.size() );
        index.close();
    }
}
