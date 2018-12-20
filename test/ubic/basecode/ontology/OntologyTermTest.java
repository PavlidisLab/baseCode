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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.providers.DiseaseOntologyService;
import ubic.basecode.ontology.providers.NIFSTDOntologyService;

/**
 * @author  Paul
 * @version $Id$
 */
public class OntologyTermTest {

    private static Logger log = LoggerFactory.getLogger( OntologyTermTest.class );

    @Test
    public void testGetChildren() throws Exception {
        // DOID:4159
        DiseaseOntologyService s = new DiseaseOntologyService();
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/doid.short.owl.gz" ) );
        s.loadTermsInNameSpace( is , false);

        OntologyTerm t = s.getTerm( "http://purl.obolibrary.org/obo/DOID_4159" );
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
     * 
     * @throws Exception
     */
    @Test
    public void testGetChildrenHasProperPart() throws Exception {
        NIFSTDOntologyService s = new NIFSTDOntologyService();
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream(
                "/data/NIF-GrossAnatomy.small.owl.xml.gz" ) );
        s.loadTermsInNameSpace( is, false );

        OntologyTerm t = s
                .getTerm( "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_734" );

        assertNotNull( t );

        Collection<OntologyTerm> c = t.getChildren( true );
        assertEquals( 9, c.size() );
        // Dorsal hypothalamic area [birnlex_777]
        // Lateral hypothalamic area [birnlex_4037]
        // Medial forebrain bundle [birnlex_908]
        // Postcommissural fornix [birnlex_914]
        // Posterior hypothalamic region [birnlex_1651]
        // Dorsal longitudinal fasciculus of hypothalamus [birnlex_898]
        // Anterior hypothalamic region [birnlex_1005]
        // Intermediate hypothalamic region [birnlex_1015]
        // Regional part of hypothalamus [birnlex_995]
        boolean found = false;
        for ( OntologyTerm o : c ) {
            // System.err.println( o );
            if ( o.getUri().equals(
                    "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_898" ) ) {
                found = true;
            }
        }
        assertTrue( found );

        Collection<OntologyTerm> c2 = t.getChildren( false );
        assertEquals( 11, c2.size() );
        found = false;
        for ( OntologyTerm o : c2 ) {
            System.err.println( o );
            if ( o.getUri().equals(
                    "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_4037" ) ) {
                found = true;
            }

            // exercise other things.
            log.info( "Annotations: " + StringUtils.join( o.getAnnotations(), "," ) );
            log.info( "Comment: " + o.getComment() );
            log.info( "Individuals: " + StringUtils.join( o.getIndividuals(), "," ) );
            log.info( "Individuals: " + StringUtils.join( o.getIndividuals( true ), "," ) );
            log.info( "Individuals: " + StringUtils.join( o.getIndividuals( false ), "," ) );
            // Log.info( "Restrictions: "+ StringUtils.join( o.getRestrictions(), "," ) );
            log.info( "AlternativeIDs: " + StringUtils.join( o.getAlternativeIds(), "," ) );
        }
        assertTrue( found );
    }

    @Test
    public void testGetParents() throws Exception {

        DiseaseOntologyService s = new DiseaseOntologyService();
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/doid.short.owl.gz" ) );

        s.loadTermsInNameSpace( is, false );

        /*
         * Note that this test uses the 'new style' URIs for DO, but at this writing we actually use purl.org not
         * purl.obolibrary.org.
         */

        OntologyTerm t = s.getTerm( "http://purl.obolibrary.org/obo/DOID_10040" );
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
    public void testGetParentsHasProperPart() throws Exception {
        NIFSTDOntologyService s = new NIFSTDOntologyService();

        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream(
                "/data/NIF-GrossAnatomy.small.owl.xml.gz" ) );
        assertNotNull( is );
        s.loadTermsInNameSpace( is, false );

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
        assertEquals( 12, parents2.size() );

        // does not includes 'continuant' and 'independent continuant' or parents of those terms.
        found = false;
        for ( OntologyTerm p : parents2 ) {
            // System.err.println( p + " " + p.getUri() );

            if ( p.getUri().equals(
                    "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_1503" ) ) {
                found = true;
            }

            assertTrue( !p.getUri().equals( "http://www.ifomis.org/bfo/1.1/snap#Continuant" ) );

        }
        assertTrue( found );
    }
}
