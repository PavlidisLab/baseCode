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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestDoubleMatrixReader extends TestCase {

    InputStream is = null;
    ZipInputStream isbig = null; // missing, with bad rows.
    InputStream ism = null;
    InputStream ismb = null; // missing, with bad rows.
    DoubleMatrix<String, String> matrix = null;
    DoubleMatrixReader reader = null;

    public void testReadBlankCorner() throws Exception {
        InputStream nis = TestDoubleMatrixReader.class.getResourceAsStream( "/data/testdata-blankcorner.txt" );
        matrix = reader.read( nis );
        assertTrue( matrix.containsColumnName( "sample1" ) && matrix.containsColumnName( "sample12" ) );

        assertEquals( 12, matrix.getColNames().size() );
        assertEquals( 12, matrix.columns() );

    }

    public void testReadInputStreamColumnCount() throws Exception {

        matrix = reader.read( is );
        int actualReturn = matrix.columns();
        int expectedReturn = 12;
        assertEquals( expectedReturn, actualReturn );

    }

    public void testReadInputStreamGotColName() throws Exception {
        matrix = reader.read( is );
        boolean actualReturn = matrix.containsColumnName( "sample1" ) && matrix.containsColumnName( "sample12" );
        boolean expectedReturn = true;
        assertEquals( expectedReturn, actualReturn );

    }

    public void testReadInputStreamGotColNameSkipColumn() throws Exception {
        matrix = reader.read( is, null, 7 );
        assertEquals( 5, matrix.columns() );
        assertTrue( !matrix.containsColumnName( "sample2" ) && matrix.containsColumnName( "sample12" ) );
    }

    public void testReadInputStreamGotRowName() throws Exception {

        matrix = reader.read( is );
        boolean actualReturn = matrix.containsRowName( "gene1_at" ) && matrix.containsRowName( "AFFXgene30_at" );
        boolean expectedReturn = true;
        assertEquals( expectedReturn, actualReturn );

    }

    public void testReadInputStreamMissing() throws Exception {

        matrix = reader.read( ism );
        int actualReturn = matrix.rows();
        int expectedReturn = 30;
        assertEquals( 12, matrix.getRow( 3 ).length );
        assertEquals( expectedReturn, actualReturn );

    }

    public void testReadInputStreamMissingBad() {
        try {
            matrix = reader.read( ismb );
            fail( "Should have gotten an IO error" );
        } catch ( IOException e ) {
            //
        }
    }

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
    public void testReadInputStreamRowCount() throws Exception {

        matrix = reader.read( is );
        int actualReturn = matrix.rows();
        int expectedReturn = 30;
        assertEquals( "return value", expectedReturn, actualReturn );

    }

    public void testSmallNumbers() throws Exception {
        reader = new DoubleMatrixReader();
        is = TestStringMatrixReader.class.getResourceAsStream( "/data/multtest.test.randord.txt" );
        matrix = reader.read( is );
        double d = matrix.get( 101, 0 );
        assertEquals( 7.6e-07, d, 1e-6 );
        assertEquals( 0.5, matrix.get( 296, 0 ), 1e-6 );
        assertEquals( 2.6e-10, matrix.get( 206, 0 ), 1e-11 );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        is.close();
        ism.close();
        ismb.close();
        isbig.close();
        matrix = null;
    }

}