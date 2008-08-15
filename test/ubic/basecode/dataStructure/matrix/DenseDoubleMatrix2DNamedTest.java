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

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import junit.framework.TestCase;

/**
 * @author Paul
 * @version $Id$
 */
public class DenseDoubleMatrix2DNamedTest extends TestCase {

    DenseDoubleMatrix<String, String> testM;
    double[][] testData = { { 1, 2, 3, 4 }, { 11, 12, 13, 14 }, { 21, Double.NaN, 23, 24 } };

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testM = DoubleMatrixFactory.dense( testData );
        testM.setRowNames( java.util.Arrays.asList( new String[] { "a", "b", "c" } ) );
        testM.setColumnNames( java.util.Arrays.asList( new String[] { "w", "x", "y", "z" } ) );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getRow(int)}.
     */
    public void testGetRow() {
        double[] actual = testM.getRow( 1 );
        assertEquals( 4, actual.length );
        assertEquals( 11.0, actual[0], 0.000001 );
        assertEquals( 12.0, actual[1], 0.000001 );
        assertEquals( 13.0, actual[2], 0.000001 );
        assertEquals( 14.0, actual[3], 0.000001 );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getColumn(int)}.
     */
    public void testGetColumn() {
        double[] actual = testM.getColumn( 1 );
        assertEquals( 3, actual.length );
        assertEquals( 2, actual[0], 0.000001 );
        assertEquals( 12.0, actual[1], 0.000001 );
        assertEquals( Double.NaN, actual[2] );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getRowArrayList(int)}.
     */
    public void testGetRowArrayList() {
        DoubleArrayList actual = testM.getRowArrayList( 2 );
        assertEquals( 4, actual.size() );
        assertEquals( 21.0, actual.get( 0 ), 0.000001 );
        assertEquals( Double.NaN, actual.get( 1 ) );
        assertEquals( 23.0, actual.get( 2 ), 0.000001 );
        assertEquals( 24.0, actual.get( 3 ), 0.000001 );
    }

    /**
     * Test method for
     * {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getRowByName(java.lang.Object)}.
     */
    public void testGetRowByName() {
        double[] actual = testM.getRowByName( "b" );
        assertEquals( 4, actual.length );
        assertEquals( 11.0, actual[0], 0.000001 );
        assertEquals( 12.0, actual[1], 0.000001 );
        assertEquals( 13.0, actual[2], 0.000001 );
        assertEquals( 14.0, actual[3], 0.000001 );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#set(int, int, double)}.
     */
    public void testSet() {
        testM.set( 2, 2, 666.0 );
        double[] actual = testM.getRow( 2 );
        assertEquals( 666.0, actual[2], 0.00001 );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#toString()}.
     */
    public void testToString() {
        String actual = testM.toString();
        assertEquals(
                "label\tw\tx\ty\tz\na\t1.000\t2.000\t3.000\t4.000\nb\t11.00\t12.00\t13.00\t14.00\nc\t21.00\t\t23.00\t24.00\n",
                actual );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#get(int, int)}.
     */
    public void testGet() {
        assertEquals( 24.0, testM.get( 2, 3 ) );
        assertEquals( 1.0, testM.get( 0, 0 ) );
        assertEquals( 13.0, testM.get( 1, 2 ) );
        assertEquals( Double.NaN, testM.get( 2, 1 ) );
        assertEquals( 23.0, testM.get( 2, 2 ) );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getByKeys(Object, Object)}.
     */
    public void testGetByKeys() {
        assertEquals( 24.0, testM.getByKeys( "c", "z") );
        assertEquals( 1.0, testM.getByKeys( "a", "w" ) );
        assertEquals( 13.0, testM.getByKeys( "b", "y" ) );
        assertEquals( Double.NaN, testM.getByKeys( "c", "x" ) );
        assertEquals( 23.0, testM.getByKeys( "c", "y" ) );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#viewRow(int)}.
     */
    public void testViewRow() {
        DoubleMatrix1D actual = testM.viewRow( 0 );
        assertEquals( 4, actual.size() );
        assertEquals( 1.0, actual.get( 0 ), 0.000001 );
        assertEquals( 2.0, actual.get( 1 ), 0.000001 );
        assertEquals( 3.0, actual.get( 2 ), 0.000001 );
        assertEquals( 4.0, actual.get( 3 ), 0.000001 );

    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#columns()}.
     */
    public void testColumns() {
        assertEquals( 4, testM.columns() );
    }

    /**
     * Test method for
     * {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getColByName(java.lang.Object)}.
     */
    public void testGetColByName() {
        double[] actual = testM.getColumnByName( "x" );
        assertEquals( 2, actual[0], 0.000001 );
        assertEquals( 12, actual[1], 0.000001 );
        assertEquals( Double.NaN, actual[2] );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getColObj(int)}.
     */
    public void testGetColObj() {
        Double[] actual = testM.getColObj( 0 );
        assertEquals( 3, actual.length );
        assertEquals( 1.0, actual[0], 0.000001 );
        assertEquals( 11.0, actual[1], 0.000001 );
        assertEquals( 21.0, actual[2], 0.000001 );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getObject(int, int)}.
     */
    public void testGetObject() {
        assertEquals( 4.0, testM.getObject( 0, 3 ), 0.000001 );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#getRowObj(int)}.
     */
    public void testGetRowObj() {
        Double[] actual = testM.getRowObj( 0 );
        assertEquals( 4, actual.length );
        assertEquals( 1.0, actual[0], 0.000001 );
        assertEquals( 2.0, actual[1], 0.000001 );
        assertEquals( 3.0, actual[2], 0.000001 );
        assertEquals( 4.0, actual[3], 0.000001 );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#isMissing(int, int)}.
     */
    public void testIsMissing() {
        assertFalse( testM.isMissing( 2, 2 ) );
        assertTrue( testM.isMissing( 2, 1 ) );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.DenseDoubleMatrix#rows()}.
     */
    public void testRows() {
        assertEquals( 3, testM.rows() );
    }

    public void testSize() {
        assertEquals( 12, testM.size() );
    }

    public void testToArray() {
        double[][] actual = testM.asArray();
        for ( int i = 0; i < testData.length; i++ ) {
            int len = testData[i].length;
            for ( int j = 0; j < len; j++ ) {
                assertEquals( testData[i][j], actual[i][j] );
            }
        }

    }

    public void testCopy() {
        DoubleMatrix<String, String> actual = testM.copy();
        for ( int i = 0; i < testData.length; i++ ) {
            assertEquals( testM.getRowName( i ), actual.getRowName( i ) );
            int len = testData[i].length;
            for ( int j = 0; j < len; j++ ) {
                assertEquals( testM.getColName( j ), actual.getColName( j ) );
                assertEquals( testData[i][j], actual.get( i, j ) );
            }
        }

    }

    public void testViewColumn() {
        DoubleMatrix1D actual = testM.viewColumn( 0 );
        assertEquals( 3, actual.size() );
        assertEquals( 1.0, actual.get( 0 ), 0.000001 );
        assertEquals( 11.0, actual.get( 1 ), 0.000001 );
        assertEquals( 21.0, actual.get( 2 ), 0.000001 );
    }

}
