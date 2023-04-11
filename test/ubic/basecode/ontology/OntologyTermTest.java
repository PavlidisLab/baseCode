/*
 * The baseCode project
 *
 * Copyright (c) 2012 University of British Columbia
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
package ubic.basecode.ontology;

import com.hp.hpl.jena.vocabulary.OWL2;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.providers.*;
import ubic.basecode.ontology.search.OntologySearchException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.*;

/**
 * @author Paul
 */
public class OntologyTermTest extends AbstractOntologyTest {

    private static final Logger log = LoggerFactory.getLogger( OntologyTermTest.class );

    private static OntologyService uberon;

    @BeforeClass
    public static void initializeUberon() throws IOException {
        uberon = new UberonOntologyService() {
            @Override
            protected String getCacheName() {
                return super.getCacheName() + "_TEST";
            }
        };
        try ( InputStream is = new GZIPInputStream( requireNonNull( OntologyTermTest.class.getResourceAsStream( "/data/uberon.owl.gz" ) ) ) ) {
            // FIXME: indexing Uberon is very slow, so we disable it so if the tests are breaking, try force-indexing
            uberon.initialize( is, false );
        }
    }

    @Test
    public void testGetChildren() throws Exception {
        // DOID:4159
        DiseaseOntologyService s = new DiseaseOntologyService();
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/doid.short.owl.gz" ) ) );
        s.initialize( is, false );

        OntologyTerm t = s.getTerm( "http://purl.obolibrary.org/obo/DOID_4159" );
        assertNotNull( t );

        // test of getting basic properties thrown in here
        assertEquals( "skin cancer", t.getLabel() );

        Collection<OntologyTerm> children = t.getChildren( true );
        assertEquals( 2, children.size() );
        boolean found = false;
        for ( OntologyTerm par : children ) {
            if ( par.getUri().equals( "http://purl.obolibrary.org/obo/DOID_8923" ) ) {
                found = true;
            }
        }
        assertTrue( found );

        Collection<OntologyTerm> allchildren = t.getChildren( false );
        found = false;
        assertEquals( 6, allchildren.size() );
        for ( OntologyTerm chil : allchildren ) {
            if ( chil.getUri().equals( "http://purl.obolibrary.org/obo/DOID_10054" ) ) {
                found = true;
            }
            // exercise other things.
            log.info( "Annotations: " + StringUtils.join( chil.getAnnotations(), "," ) );
            log.info( "Comment: " + chil.getComment() );
            log.info( "Individuals: " + StringUtils.join( chil.getIndividuals(), "," ) );
            log.info( "Individuals: " + StringUtils.join( chil.getIndividuals( true ), "," ) );
            log.info( "Individuals: " + StringUtils.join( chil.getIndividuals( false ), "," ) );
            log.info( "Restrictions: " + StringUtils.join( chil.getRestrictions(), "," ) );
            log.info( "AlternativeIDs: " + StringUtils.join( chil.getAlternativeIds(), "," ) );
        }
        assertTrue( found );
    }

    /**
     * FIXME this uses NIF, which we are no longer using actively - not a big deal since this just tests mechanics
     */
    @Test
    public void testGetChildrenHasProperPart() throws Exception {
        NIFSTDOntologyService s = new NIFSTDOntologyService();
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream(
                "/data/NIF-GrossAnatomy.small.owl.xml.gz" ) ) );
        s.initialize( is, false );

        OntologyTerm t = s.getTerm( "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_734" );
        assertNotNull( t );

        Collection<OntologyTerm> c = t.getChildren( true );
        assertEquals( 6, c.size() );
        // Dorsal hypothalamic area [birnlex_777]
        // Lateral hypothalamic area [birnlex_4037]
        // Medial forebrain bundle [birnlex_908]
        // Postcommissural fornix [birnlex_914]
        // Posterior hypothalamic region [birnlex_1651]
        // Dorsal longitudinal fasciculus of hypothalamus [birnlex_898]
        // Anterior hypothalamic region [birnlex_1005]
        // Intermediate hypothalamic region [birnlex_1015]
        // Regional part of hypothalamus [birnlex_995]
        OntologyTerm t1 = s.getTerm( "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_898" );
        assertNotNull( t1 );
        assertTrue( c.contains( t1 ) );

        Collection<OntologyTerm> c2 = t.getChildren( false );
        assertEquals( 6, c2.size() );
        OntologyTerm t2 = s.getTerm( "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_4037" );
        assertNotNull( t2 );
        assertTrue( c.contains( t2 ) );
    }

    @Test
    public void testGetParents() throws Exception {

        DiseaseOntologyService s = new DiseaseOntologyService();
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/doid.short.owl.gz" ) ) );

        s.initialize( is, false );

        /*
         * Note that this test uses the 'new style' URIs for DO, but at this writing we actually use purl.org not
         * purl.obolibrary.org.
         */

        OntologyTerm t = s.getTerm( "http://purl.obolibrary.org/obo/DOID_10040" );
        assertNotNull( t );
        Collection<OntologyTerm> parents = t.getParents( true );
        assertEquals( 1, parents.size() );
        OntologyTerm p = parents.iterator().next();
        assertEquals( "http://purl.obolibrary.org/obo/DOID_8923", p.getUri() );

        Collection<OntologyTerm> parents2 = t.getParents( false );
        assertEquals( 7, parents2.size() );
        boolean found = false;
        for ( OntologyTerm par : parents2 ) {
            if ( par.getUri().equals( "http://purl.obolibrary.org/obo/DOID_0060122" ) ) {
                found = true;
            }

            // exercise other things.
            log.info( "Annotations: " + StringUtils.join( par.getAnnotations(), "," ) );
            log.info( "Comment: " + par.getComment() );
            log.info( "Individuals: " + StringUtils.join( par.getIndividuals(), "," ) );
            log.info( "Individuals: " + StringUtils.join( par.getIndividuals( true ), "," ) );
            log.info( "Individuals: " + StringUtils.join( par.getIndividuals( false ), "," ) );
            log.info( "Restrictions: " + StringUtils.join( par.getRestrictions(), "," ) );
            log.info( "AlternativeIDs: " + StringUtils.join( par.getAlternativeIds(), "," ) );
        }

        assertTrue( found );
    }

    @Test
    public void testUberon() {
        OntologyTerm t = uberon.getTerm( "http://purl.obolibrary.org/obo/BFO_0000001" );
        assertNotNull( t );
        assertTrue( t.isRoot() );
        assertFalse( t.isObsolete() );

        OntologyTerm t2 = uberon.getTerm( "http://purl.obolibrary.org/obo/BFO_0000002" );
        assertNotNull( t2 );
        assertFalse( t2.isRoot() );
        assertFalse( t2.isObsolete() );

        OntologyTerm t3 = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0007234" );
        assertNotNull( t3 );
        assertTrue( t3.isObsolete() );
    }

    @Test
    public void testGetParentsFromMultipleTerms() {
        OntologyTerm brain = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        OntologyTerm liver = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0002107" );
        Collection<OntologyTerm> children = uberon.getParents( Arrays.asList( brain, liver ), false, true );
        assertEquals( 30, children.size() );
        assertFalse( children.contains( uberon.getTerm( OWL2.Nothing.getURI() ) ) );
    }

    @Test
    public void testGetParentsHasProperPart() throws Exception {
        NIFSTDOntologyService s = new NIFSTDOntologyService();

        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream(
                "/data/NIF-GrossAnatomy.small.owl.xml.gz" ) ) );
        assertNotNull( is );
        s.initialize( is, false );

        // Mammillary princeps fasciculus: part of white matter, hypothalamus, etc.
        OntologyTerm t = s
                .getTerm( "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_1492" );

        assertNotNull( t );

        Collection<OntologyTerm> parents = t.getParents( true );
        assertEquals( 3, parents.size() );

        // Nerve tract [birnlex_1649]
        // White matter [nlx_anat_101177]
        // Posterior hypothalamic region [birnlex_1651]
        boolean found = false;
        for ( OntologyTerm p : parents ) {
            if ( p.getUri().equals(
                    "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#nlx_anat_101177" ) ) {
                found = true;
            }
        }
        assertTrue( found );
        // Part of neuraxis [nlx_anat_1010010] x
        // Nerve tract [birnlex_1649] x
        // White matter [nlx_anat_101177] x
        // Posterior hypothalamic region [birnlex_1651] x
        // Forebrain [birnlex_1509] x
        // Hypothalamus [birnlex_734] x
        // Regional part of brain [birnlex_1167] x
        // Diencephalon [birnlex_1503] x

        Collection<OntologyTerm> parents2 = t.getParents( false );
        assertEquals( 7, parents2.size() );

        // does not includes 'continuant' and 'independent continuant' or parents of those terms.
        assertFalse( parents2.contains( s.getTerm( "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_1503" ) ) );
        assertFalse( parents2.contains( ( OntologyTerm ) s.getResource( "http://www.ifomis.org/bfo/1.1/snap#Continuant" ) ) );
    }

    @Test
    public void testRejectNonEnglish() throws Exception {
        CellLineOntologyService s = new CellLineOntologyService();
        InputStream is = new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream( "/data/clo_merged.sample.owl.xml.gz" ) ) );
        s.initialize( is, false );

        OntologyTerm t = s.getTerm( "http://purl.obolibrary.org/obo/CLO_0000292" );
        assertNotNull( t );
        assertEquals( "immortal larynx-derived cell line cell", t.getLabel() );

    }

    @Test
    public void testGetParentsHasPart() {
        OntologyTerm t = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( t );
        Collection<OntologyTerm> parents = t.getParents( true );
        assertEquals( 3, parents.size() );
        // does not contain itself
        assertFalse( parents.contains( t ) );
        // via subclass
        assertTrue( parents.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0004121" ) ) );
        assertTrue( parents.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000062" ) ) );
        // via part of, central nervous system
        assertTrue( parents.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0001017" ) ) );
        assertFalse( parents.contains( uberon.getTerm( OWL2.Thing.getURI() ) ) );
    }

    @Test
    public void testGetChildrenHasPart() {
        OntologyTerm t = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( t );
        assertEquals( 76, t.getChildren( true ).size() );
        Collection<OntologyTerm> children = t.getChildren( false );
        assertEquals( 1496, children.size() );
        // via subclass of, insect adult brain
        assertTrue( children.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_6003624" ) ) );
        // via part of, nucleus of brain
        assertTrue( children.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0002308" ) ) );
        // does not contain owl:Nothing
        assertFalse( children.contains( uberon.getTerm( OWL2.Nothing.getURI() ) ) );
    }

    @Test
    public void testGetChildrenFromMultipleTerms() {
        OntologyTerm brain = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        OntologyTerm liver = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0002107" );
        Collection<OntologyTerm> children = uberon.getChildren( Arrays.asList( brain, liver ), false, true );
        assertEquals( 1562, children.size() );
    }

    @Test
    public void testGetChildrenFromMultipleTermsWithSearch() throws OntologySearchException {
        Collection<OntologyTerm> terms = uberon.findTerm( "brain" );
        Collection<OntologyTerm> matches = uberon.getChildren( terms, false, true );
        assertEquals( 1870, matches.size() );
    }

    @Test
    public void testFindTerm() throws OntologySearchException {
        assertEquals( 123, uberon.findTerm( "brain" ).size() );
        assertEquals( 128, uberon.findTerm( "brain", true ).size() );
    }
}
