/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.basecode.bio.geneset;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestGeneAnnotations extends TestCase {

    InputStream is;
    InputStream im;
    InputStream ia;
    InputStream imb;
    List probes;
    List geneIds;
    List goIds;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        probes = new ArrayList();
        probes.add( "a" );
        probes.add( "b" );
        probes.add( "c" );
        geneIds = new ArrayList();
        geneIds.add( "aGene" );
        geneIds.add( "bGene" );
        geneIds.add( "cGene" );
        goIds = new ArrayList();
        goIds.add( new HashSet() );
        goIds.add( new HashSet() );
        goIds.add( new HashSet() );

        ( ( Collection ) goIds.get( 0 ) ).add( "1" );
        ( ( Collection ) goIds.get( 1 ) ).add( "1" );
        ( ( Collection ) goIds.get( 2 ) ).add( "1" );
        ( ( Collection ) goIds.get( 0 ) ).add( "2" );
        ( ( Collection ) goIds.get( 1 ) ).add( "2" );
        // ( ( Collection ) goIds.get( 2 ) ).add( "2" );
        is = TestGeneAnnotations.class.getResourceAsStream( "/data/HG-U133_Plus_2_annot_sample.csv" );
        im = TestGeneAnnotations.class.getResourceAsStream( "/data/geneAnnotation.sample.txt" );
        imb = TestGeneAnnotations.class.getResourceAsStream( "/data/geneAnnotation.sample-goidddelimittest.txt" );
        ia = TestGeneAnnotations.class.getResourceAsStream( "/data/agilentannots.test.txt" );
        if ( is == null ) throw new IllegalStateException();
        if ( ia == null ) throw new IllegalStateException();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testReadAffyCsv() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.readAffyCsv( is, null );
        ga.setUp( null );
        List geneSets = new ArrayList( ga.getGeneSets() );
        Collections.sort( geneSets );
        assertTrue( geneSets.size() > 0 );
    }

    public void testGeneAnnotationsApiA() throws Exception {
        GeneAnnotations val = new GeneAnnotations( probes, geneIds, null, goIds );
        int actualValue = val.numGenes();
        int expectedValue = 3;
        assertEquals( expectedValue, actualValue );
    }

    public void testGeneAnnotationsApiB() throws Exception {
        GeneAnnotations val = new GeneAnnotations( probes, geneIds, null, goIds );
        int actualValue = val.numProbesForGene( "aGene" );
        int expectedValue = 1;
        assertEquals( expectedValue, actualValue );
    }

    public void testGeneAnnotationsApiC() throws Exception {
        GeneAnnotations val = new GeneAnnotations( probes, geneIds, null, goIds );
        int actualValue = val.getGeneGeneSets( "aGene" ).size();
        int expectedValue = 2;
        assertEquals( expectedValue, actualValue );
    }

    public void testReadDescription() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.read( im, null );
        String actualValue = ga.getProbeDescription( "32304_at" );
        String expectedValue = "protein kinase C, alpha";
        assertEquals( expectedValue, actualValue );
    }

    public void testReadPipeDelimited() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.read( im, null );
        int actualValue = ( ( Collection ) ga.getProbeToGeneSetMap().get( "32304_at" ) ).size();
        int expectedValue = 31;
        assertEquals( expectedValue, actualValue );
    }

    public void testReadCommaDelimited() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.read( imb, null );
        int actualValue = ( ( Collection ) ga.getProbeToGeneSetMap().get( "32304_at" ) ).size();
        int expectedValue = 31;
        assertEquals( expectedValue, actualValue );
    }

    public void testReadAgilent() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.readAgilent( ia, null );
        int actualValue = ( ( Collection ) ga.getProbeToGeneSetMap().get( "A_52_P311491" ) ).size();
        int expectedValue = 5;
        assertEquals( expectedValue, actualValue );
    }
}
