/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
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
package ubic.basecode.bio.geneset;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author paul
 * @version $Id$
 */
public class TestMultifunctionality {

    @Test
    public void testMf1() throws Exception {
        InputStream is = TestGeneAnnotations.class.getResourceAsStream( "/data/HG-U133_Plus_2_annot_sample.csv" );
        GeneAnnotations ga = new GeneAnnotations();
        ga.readAffyCsv( is, null );
        ga.setUp( null );

        Multifunctionality mf = new Multifunctionality( ga );

        double actual = mf.getMultifunctionalityScore( "PAX8" );
        assertEquals( 0.022, actual, 0.001 );

        int actualNumG = mf.getNumGoTerms( "PAX8" );
        assertEquals( 5, actualNumG );

        double actualR = mf.getMultifunctionalityRank( "PAX8" );
        assertEquals( 0.72, actualR, 0.01 );

        double actualGoMF = mf.getGOTermMultifunctionalityRank( "GO:0005634" );
        assertEquals( 0.222, actualGoMF, 0.001 );

        List<String> li = new ArrayList<String>();
        li.add( "EYA3" );
        li.add( "EPHB3" );
        li.add( "MAPK1" );
        li.add( "PTPN21" );
        li.add( "CYP2A6" );
        li.add( "CCL5" );
        li.add( "GSC" );
        li.add( "PAX8" );
        li.add( "THRA" );
        li.add( "PXK" );
        li.add( "GUCA1A" );
        li.add( "DDR1" );
        li.add( "RFC2" );
        li.add( "HSPA6" );
        li.add( "LOC201158" );
        li.add( "SLC39A5" );
        li.add( "ALG10" );
        li.add( "UBE1L" );
        li.add( "C6orf199" );
        li.add( "foonotagene" );
        double cgm = mf.correlationWithGeneMultifunctionality( li );
        assertEquals( 0.80175, cgm, 0.001 );
    }

}
