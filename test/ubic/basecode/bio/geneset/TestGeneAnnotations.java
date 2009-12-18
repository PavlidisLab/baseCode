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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipInputStream;

import javax.swing.table.TableModel;

import ubic.basecode.math.Constants;

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
    List<String> probes;
    List<String> geneIds;
    List<Collection<String>> goIds;
    static GONames goNames;

    public void testSelectSetsByGeen() throws Exception {
        GeneAnnotations ga = new GeneAnnotations( im, null, null, goNames );
        ga.selectSetsByGene( "LARS2" );
        assertEquals( "34764_at", ga.getGeneProbes( "LARS2" ).iterator().next() );
        List<String> selectedSets = ga.getSelectedSets();
        assertTrue( ga.hasGeneSet( "GO:0005739" ) );
        assertEquals( 2, ga.numGenesInGeneSet( "GO:0005739" ) );
        assertEquals( 18, selectedSets.size() );
        assertTrue( selectedSets.contains( "GO:0005739" ) );
        assertEquals( 2, ga.getGeneSetProbes( "GO:0005739" ).size() );
    }

    public void testGoNames() throws Exception {
        assertEquals( "cellular_component", goNames.getAspectForId( "GO:0005739" ) );
        assertEquals( 10, goNames.getChildren( "GO:0005739" ).size() );
        assertEquals( 1, goNames.getParents( "GO:0005739" ).size() );
        assertEquals( "A semiautonomous, self replicating organelle that occurs"
                + " in varying numbers, shapes, and sizes in the cytoplasm"
                + " of virtually all eukaryotic cells. It is notably the site of tissue respiration.", goNames
                .getDefinitionForId( "GO:0005739" ) );
    }

    public void testMeanSetsPerGenes() throws Exception {
        GeneAnnotations ga = new GeneAnnotations( im, null, null, goNames );
        assertEquals( 22.30434782, GeneSetMapTools.meanSetsPerGene( ga, false ), Constants.SMALLISH );
    }

    public void testMeanGenesPerSet() throws Exception {
        GeneAnnotations ga = new GeneAnnotations( im, null, null, goNames );
        assertEquals( 5.89655, GeneSetMapTools.meanGeneSetSize( ga, false ), Constants.SMALLISH );
    }

    public void testRemoveBySize() throws Exception {
        GeneAnnotations ga = new GeneAnnotations( im, null, null, goNames );
        assertEquals( 87, ga.getGeneSets().size() );
        GeneSetMapTools.removeBySize( ga, null, 2, 5 );
        assertEquals( 53, ga.getGeneSets().size() );
    }

    public void testConstructPruned() throws Exception {
        GeneAnnotations ga = new GeneAnnotations( im, null, null, goNames );
        Set<String> keepers = new HashSet<String>();
        keepers.add( "36949_at" );
        keepers.add( "41208_at" );
        keepers.add( "34764_at" );
        keepers.add( "33338_at" );
        GeneAnnotations pruned = new GeneAnnotations( ga, keepers );
        assertEquals( 4, pruned.numGenes() );
        assertEquals( 64, pruned.numGeneSets() );
        assertEquals( 4, pruned.numProbes() );
    }

    public void testRemoveAspect() throws Exception {
        GeneAnnotations ga = new GeneAnnotations( im, null, null, goNames );
        GeneSetMapTools.removeAspect( ga, goNames, null, "cellular_component" );
        assertEquals( null, ga.getGeneSetProbes( "GO:0005739" ) );
    }

    public void testGeneAnnotationsApiA() throws Exception {
        GeneAnnotations val = new GeneAnnotations( probes, geneIds, null, goIds );
        int actualValue = val.numGenes();
        int expectedValue = 3;
        assertEquals( expectedValue, actualValue );
    }

    public void testAddGeneSet() throws Exception {
        GeneAnnotations val = new GeneAnnotations( im, null, null, goNames );
        List<String> newGeneSet = new ArrayList<String>();
        newGeneSet.add( "34764_at" );
        newGeneSet.add( "32636_f_at" );
        val.addGeneSet( "Foo", newGeneSet );
        Collection<String> geneGeneSets = val.getGeneGeneSets( "LARS2" );
        assertTrue( val.hasGeneSet( "Foo" ) );
        assertTrue( geneGeneSets.contains( "Foo" ) );

    }

    public void testToTableModel() throws Exception {
        GeneAnnotations val = new GeneAnnotations( probes, geneIds, null, goIds );
        TableModel actualValue = val.toTableModel();
        assertEquals( 3, actualValue.getColumnCount() );
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

    public final void testReadAffyCsv() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.readAffyCsv( is, null );
        ga.setUp( null );
        Collection<String> geneSets = ga.getGeneSets();
        assertTrue( geneSets.size() > 0 );
    }

    public void testReadAgilent() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.readAgilent( ia, null );
        int actualValue = ga.getProbeToGeneSetMap().get( "A_52_P311491" ).size();
        int expectedValue = 5;
        assertEquals( expectedValue, actualValue );
    }

    public void testReadCommaDelimited() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.read( imb, null );
        int actualValue = ga.getProbeToGeneSetMap().get( "32304_at" ).size();
        int expectedValue = 31;
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
        int actualValue = ga.getProbeToGeneSetMap().get( "32304_at" ).size();
        int expectedValue = 31;
        assertEquals( expectedValue, actualValue );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        probes = new ArrayList<String>();
        probes.add( "a" );
        probes.add( "b" );
        probes.add( "c" );
        geneIds = new ArrayList<String>();
        geneIds.add( "aGene" );
        geneIds.add( "bGene" );
        geneIds.add( "cGene" );
        goIds = new ArrayList<Collection<String>>();
        goIds.add( new HashSet<String>() );
        goIds.add( new HashSet<String>() );
        goIds.add( new HashSet<String>() );

        goIds.get( 0 ).add( "1" );
        goIds.get( 1 ).add( "1" );
        goIds.get( 2 ).add( "1" );
        goIds.get( 0 ).add( "2" );
        goIds.get( 1 ).add( "2" );
        is = TestGeneAnnotations.class.getResourceAsStream( "/data/HG-U133_Plus_2_annot_sample.csv" );
        im = TestGeneAnnotations.class.getResourceAsStream( "/data/geneAnnotation.sample.txt" );
        imb = TestGeneAnnotations.class.getResourceAsStream( "/data/geneAnnotation.sample-goidddelimittest.txt" );
        ia = TestGeneAnnotations.class.getResourceAsStream( "/data/agilentannots.test.txt" );

        if ( is == null ) throw new IllegalStateException();
        if ( ia == null ) throw new IllegalStateException();
    }

    static {
        try {
            ZipInputStream z = new ZipInputStream( TestGeneAnnotations.class
                    .getResourceAsStream( "/data/go_200406-termdb.zip" ) );
            z.getNextEntry();
            goNames = new GONames( z );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
