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
package ubic.basecode.io.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.zip.ZipInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.util.FileTools;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestDoubleMatrixReader {

    InputStream is = null;
    ZipInputStream isbig = null; // missing, with bad rows.
    InputStream ism = null;
    InputStream ismb = null; // missing, with bad rows.
    DoubleMatrix<String, String> matrix = null;
    DoubleMatrixReader reader = null;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        reader = new DoubleMatrixReader();
        is = TestDoubleMatrixReader.class.getResourceAsStream( "/data/testdata.txt" );

        ism = TestDoubleMatrixReader.class.getResourceAsStream( "/data/testdatamissing.txt" );

        ismb = TestDoubleMatrixReader.class.getResourceAsStream( "/data/testdatamissing-badrows.txt" );

        isbig = new ZipInputStream(
                TestStringMatrixReader.class.getResourceAsStream( "/data/melanoma_and_sarcomaMAS5.zip" ) );
        isbig.getNextEntry();
    }

    /*
     * @see TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        is.close();
        ism.close();
        ismb.close();
        isbig.close();
        matrix = null;
    }

    @Test(expected = IOException.class)
    public void testBadFileName() throws Exception {
        String fn = FileTools.resourceToPath( "/data/testdata.txt" ) + "jalikj;lkj;";
        matrix = reader.read( fn );
    }

    @Test(expected = IOException.class)
    public void testReadBad() throws Exception {
        String fn = FileTools.resourceToPath( "/data/testdatamissingbad.txt" );
        reader.read( fn );
    }

    @Test
    public void testReadBlankCorner() throws Exception {
        InputStream nis = TestDoubleMatrixReader.class.getResourceAsStream( "/data/testdata-blankcorner.txt" );
        matrix = reader.read( nis );
        assertTrue( matrix.containsColumnName( "sample1" ) && matrix.containsColumnName( "sample12" ) );

        assertEquals( 12, matrix.getColNames().size() );
        assertEquals( 12, matrix.columns() );

    }

    @Test
    public void testReadChooseRows() throws Exception {
        String fn = FileTools.resourceToPath( "/data/testdatamissing.txt" );
        Collection<String> wanted = new HashSet<String>();
        wanted.add( "gene11_at" );
        wanted.add( "gene6_at" );
        wanted.add( "gene6_at" );
        wanted.add( "gene7_at" );
        wanted.add( "AFFXgene30_at" );
        matrix = reader.read( fn, wanted );
        int actualReturn = matrix.columns();
        int expectedReturn = 12;
        assertEquals( expectedReturn, actualReturn );
    }

    @Test(expected = IOException.class)
    public void testReadChooseRowsBadfileName() throws Exception {
        String fn = FileTools.resourceToPath( "/data/testdata.txt" ) + "jalikj;lkj;";
        Collection<String> wanted = new HashSet<String>();
        wanted.add( "gene11_at" );
        wanted.add( "dadadad" );
        wanted.add( "gene6_at" );
        wanted.add( "gene6_at" );
        wanted.add( "gene7_at" );
        wanted.add( "AFFXgene30_at" );
        matrix = reader.read( fn, wanted );
        int actualReturn = matrix.columns();
        int expectedReturn = 12;
        assertEquals( expectedReturn, actualReturn );
    }

    @Test
    public void testReadChooseRowsSkipcols() throws Exception {
        String fn = FileTools.resourceToPath( "/data/testdatamissing.txt" );
        Collection<String> wanted = new HashSet<String>();
        wanted.add( "gene11_at" );
        wanted.add( "dadadad" );
        wanted.add( "gene6_at" );
        wanted.add( "gene6_at" );
        wanted.add( "gene7_at" );
        wanted.add( "AFFXgene30_at" );
        matrix = reader.read( fn, wanted, 4 );
        int actualReturn = matrix.columns();
        int expectedReturn = 8;
        assertEquals( expectedReturn, actualReturn );

    }

    @Test
    public void testReadInputStreamColumnCount() throws Exception {

        matrix = reader.read( is );
        int actualReturn = matrix.columns();
        int expectedReturn = 12;
        assertEquals( expectedReturn, actualReturn );

    }

    @Test
    public void testReadInputStreamGotColName() throws Exception {
        matrix = reader.read( is );
        boolean actualReturn = matrix.containsColumnName( "sample1" ) && matrix.containsColumnName( "sample12" );
        boolean expectedReturn = true;
        assertEquals( expectedReturn, actualReturn );

    }

    @Test
    public void testReadInputStreamGotColNameSkipColumn() throws Exception {
        matrix = reader.read( is, null, 7 );
        assertEquals( 5, matrix.columns() );
        assertTrue( !matrix.containsColumnName( "sample2" ) && matrix.containsColumnName( "sample12" ) );
    }

    @Test
    public void testReadInputStreamGotRowName() throws Exception {

        matrix = reader.read( is );
        boolean actualReturn = matrix.containsRowName( "gene1_at" ) && matrix.containsRowName( "AFFXgene30_at" );
        boolean expectedReturn = true;
        assertEquals( expectedReturn, actualReturn );

    }

    @Test
    public void testReadInputStreamMissing() throws Exception {

        matrix = reader.read( ism );
        int actualReturn = matrix.rows();
        int expectedReturn = 30;
        assertEquals( 12, matrix.getRow( 3 ).length );
        assertEquals( expectedReturn, actualReturn );

    }

    @Test(expected = IOException.class)
    public void testReadInputStreamMissingBad() throws Exception {
        matrix = reader.read( ismb );
    }

    @Test
    public void testReadInputStreamMissingSpaces() throws Exception {
        InputStream isl = this.getClass().getResourceAsStream( "/data/luo-prostate.sample.txt" );
        matrix = reader.read( isl );
        int actualReturn = matrix.rows();
        int expectedReturn = 173;
        assertEquals( 25, matrix.getRow( 3 ).length );
        assertEquals( expectedReturn, actualReturn );

    }

    /*
     * Class under test for NamedMatrix read(InputStream)
     */
    @Test
    public void testReadInputStreamRowCount() throws Exception {

        matrix = reader.read( is );
        int actualReturn = matrix.rows();
        int expectedReturn = 30;
        assertEquals( "return value", expectedReturn, actualReturn );

    }

    @Test
    public void testSmallNumbers() throws Exception {
        reader = new DoubleMatrixReader();
        is = TestStringMatrixReader.class.getResourceAsStream( "/data/multtest.test.randord.txt" );
        matrix = reader.read( is );
        double d = matrix.get( 101, 0 );
        assertEquals( 7.6e-07, d, 1e-6 );
        assertEquals( 0.5, matrix.get( 296, 0 ), 1e-6 );
        assertEquals( 2.6e-10, matrix.get( 206, 0 ), 1e-11 );
    }

}