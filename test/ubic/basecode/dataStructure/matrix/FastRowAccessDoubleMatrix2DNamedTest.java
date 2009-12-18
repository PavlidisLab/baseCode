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

import junit.framework.TestCase;
import ubic.basecode.io.reader.DoubleMatrixReader;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * @author pavlidis
 * @version $Id$
 */
public class FastRowAccessDoubleMatrix2DNamedTest extends TestCase {

    DoubleMatrix<String, String> testdata;
    DoubleMatrixReader f = new DoubleMatrixReader();

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.columns()'
     */
    public void testColumns() {
        assertTrue( testdata.columns() == 12 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.get(int, int)'
     */
    public void testGet() {
        assertEquals( 27873.8, testdata.get( 2, 4 ), 0.0001 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getColObj(int)'
     */
    public void testGetColObj() {

    }

    public void testCopy() {
        DoubleMatrix<String, String> actual = testdata.copy();
        double[][] testM = testdata.asArray();

        for ( int i = 0; i < testM.length; i++ ) {
            assertEquals( testdata.getRowName( i ), actual.getRowName( i ) );
            int len = testM[i].length;
            for ( int j = 0; j < len; j++ ) {
                assertEquals( testdata.getColName( j ), actual.getColName( j ) );
                assertEquals( testM[i][j], actual.get( i, j ) );
            }
        }

    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getRow(int)'
     */
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
    public void testGetRowObj() {

    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.isMissing(int, int)'
     */
    public void testIsMissing() {

    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.rows()'
     */
    public void testRows() {
        assertTrue( testdata.rows() == 30 );
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
    public void testSetIntIntObject() {
        testdata.set( 1, 2, new Double( 3.0 ) );
        assertEquals( 3.0, testdata.get( 1, 2 ), 0.00001 );
    }

    public void testSet() {
        testdata.set( 1, 2, 3.0 );
        assertEquals( 3.0, testdata.get( 1, 2 ), 0.00001 );
    }

    /*
     * Test method for 'basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.viewRow(int)'
     */
    public void testViewRow() {
        DoubleMatrix1D res = testdata.viewRow( 1 );
        assertEquals( 242.1, res.get( 2 ), 0.001 );
    }

    public void testConstructFromArray() {
        FastRowAccessDoubleMatrix<String, String> actual = new FastRowAccessDoubleMatrix<String, String>( testdata.asArray() );
        double[][] testM = testdata.asArray();

        for ( int i = 0; i < testM.length; i++ ) {
            int len = testM[i].length;
            for ( int j = 0; j < len; j++ ) {
                assertEquals( testM[i][j], actual.get( i, j ) );
            }
        }
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        testdata = f.read( FastRowAccessDoubleMatrix2DNamedTest.class.getResourceAsStream( "/data/testdata.txt" ) );
        assert testdata instanceof FastRowAccessDoubleMatrix;
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
