package baseCode.bio.geneset;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestGeneAnnotations extends TestCase {

    InputStream is;
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
        if ( is == null ) throw new IllegalStateException();
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
        List geneSets = new ArrayList( ga.getGeneSetToGeneMap().keySet() );
        Collections.sort( geneSets );
        assertTrue( geneSets.size() > 0 );
    }

    public void testGeneAnnotationsApiA() throws Exception {
        GeneAnnotations val = new GeneAnnotations( probes, geneIds, null, goIds );
        int actualValue = val.numGenes();
        int expectedValue = 2;
        assertEquals( expectedValue, actualValue );
    }

    public void testGeneAnnotationsApiB() throws Exception {
        GeneAnnotations val = new GeneAnnotations( probes, geneIds, null, goIds );
        int actualValue = val.numProbesForGene( "aGene" );
        int expectedValue = 2;
        assertEquals( expectedValue, actualValue );
    }

    public void testGeneAnnotationsApiC() throws Exception {
        GeneAnnotations val = new GeneAnnotations( probes, geneIds, null, goIds );
        int actualValue = ( ( Collection ) val.getGeneToGeneSetMap().get( "aGene" ) ).size();
        int expectedValue = 2;
        assertEquals( expectedValue, actualValue );
    }

}
