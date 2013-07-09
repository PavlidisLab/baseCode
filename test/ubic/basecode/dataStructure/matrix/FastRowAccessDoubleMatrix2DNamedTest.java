/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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
package ubic.basecode.dataStructure.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.io.reader.DoubleMatrixReader;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * @author pavlidis
 * @version $Id$
 */
public class FastRowAccessDoubleMatrix2DNamedTest {

    DoubleMatrixReader f = new DoubleMatrixReader();
    DoubleMatrix<String, String> testdata;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        testdata = f.read( FastRowAccessDoubleMatrix2DNamedTest.class.getResourceAsStream( "/data/testdata.txt" ) );
        assert testdata instanceof FastRowAccessDoubleMatrix<?, ?>;

    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.columns()'
     */
    @Test
    public void testColumns() {
        assertEquals( 12, testdata.columns() );
    }

    @Test
    public void testConstructFromArray() {
        FastRowAccessDoubleMatrix<String, String> actual = new FastRowAccessDoubleMatrix<String, String>(
                testdata.asArray() );
        double[][] testM = testdata.asArray();

        for ( int i = 0; i < testM.length; i++ ) {
            int len = testM[i].length;
            for ( int j = 0; j < len; j++ ) {
                assertEquals( testM[i][j], actual.get( i, j ), 0.00001 );
            }
        }
    }

    @Test
    public void testCopy() {
        DoubleMatrix<String, String> actual = testdata.copy();
        double[][] testM = testdata.asArray();

        for ( int i = 0; i < testM.length; i++ ) {
            assertEquals( testdata.getRowName( i ), actual.getRowName( i ) );
            int len = testM[i].length;
            for ( int j = 0; j < len; j++ ) {
                assertEquals( testdata.getColName( j ), actual.getColName( j ) );
                assertEquals( testM[i][j], actual.get( i, j ), 0.00001 );
            }
        }

    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.get(int, int)'
     */
    @Test
    public void testGet() {
        assertEquals( 27873.8, testdata.get( 2, 4 ), 0.0001 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getColObj(int)'
     */
    @Test
    public void testGetColObj() {
        Double[] c = testdata.getColObj( 4 );
        assertEquals( 30, c.length );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getRow(int)'
     */
    @Test
    public void testGetRow() {
        double[] res = testdata.getRow( 2 );
        assertEquals( 27873.8, res[4], 0.001 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getRowArrayList(int)'
     */
    public void testGetRowArrayList() {
        DoubleArrayList res = testdata.getRowArrayList( 2 );
        assertEquals( 27873.8, res.get( 4 ), 0.001 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getRowObj(int)'
     */
    @Test
    public void testGetRowObj() {
        Double[] rowRange = testdata.getRowObj( 4 );
        assertEquals( 12, rowRange.length );
    }

    @Test
    public void testGetRowRange() {
        DoubleMatrix<String, String> rowRange = testdata.getRowRange( 1, 4 );
        assertEquals( 12, rowRange.columns() );
        assertEquals( 4, rowRange.rows() );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.isMissing(int, int)'
     */
    @Test
    public void testIsMissing() throws Exception {
        assertTrue( !testdata.isMissing( 5, 3 ) );
        DoubleMatrix<String, String> md = f.read( FastRowAccessDoubleMatrix2DNamedTest.class
                .getResourceAsStream( "/data/testdatamissing.txt" ) );
        assertTrue( md.isMissing( 5, 3 ) );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.rows()'
     */
    @Test
    public void testRows() {
        assertTrue( testdata.rows() == 30 );
    }

    @Test
    public void testSet() {
        testdata.set( 1, 2, 3.0 );
        assertEquals( 3.0, testdata.get( 1, 2 ), 0.00001 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.set(int, int, double)'
     */
    public void testSetIntIntDouble() {
        testdata.set( 1, 2, 3.0 );
        assertEquals( 3.0, testdata.get( 1, 2 ), 0.00001 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.set(int, int, Object)'
     */
    @Test
    public void testSetIntIntObject() {
        testdata.set( 1, 2, new Double( 3.0 ) );
        assertEquals( 3.0, testdata.get( 1, 2 ), 0.00001 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.viewRow(int)'
     */
    @Test
    public void testViewRow() {
        DoubleMatrix1D res = testdata.viewRow( 1 );
        assertEquals( 242.1, res.get( 2 ), 0.001 );
    }

}
