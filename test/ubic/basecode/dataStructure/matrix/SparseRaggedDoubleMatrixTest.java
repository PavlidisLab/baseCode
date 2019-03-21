/*
 * The baseCode project
 * 
 * Copyright (c) 2008-2019 University of British Columbia
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

import java.io.InputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.io.reader.SparseRaggedMatrixReader;
import ubic.basecode.io.reader.TestSparseDoubleMatrixReader;
import ubic.basecode.util.RegressionTesting;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * @author pavlidis
 * 
 */
public class SparseRaggedDoubleMatrixTest {
    InputStream is = null;
    InputStream isa = null;
    SparseRaggedDoubleMatrix<String, String> matrix = null;
    SparseRaggedMatrixReader reader = null;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        reader = new SparseRaggedMatrixReader();
        is = TestSparseDoubleMatrixReader.class.getResourceAsStream( "/data/JW-testmatrix.txt" );
        isa = TestSparseDoubleMatrixReader.class.getResourceAsStream( "/data/adjacencylist-testmatrix.txt" );
        matrix = ( SparseRaggedDoubleMatrix<String, String> ) reader.read( is, 1 );
    }

    @Test
    public void testAddrow() {
        DoubleMatrix1D m = new RCDoubleMatrix1D( new double[] { 0.3, 0.0, 0.8 } );
        matrix.addRow( "newrow", m );
        assertEquals( 4, matrix.rows() );
    }

    @Test
    public void testColumns() {
        int actualReturn = matrix.columns();
        int expectedReturn = 3;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testGetColRange() {
        DoubleMatrix<String, String> range = matrix.getColRange( 1, 2 );
        assertEquals( 2, range.columns() );
        assertEquals( 3, range.rows() );
    }

    /* getRow returns a double[] */
    @Test
    public void testGetRow() {
        DoubleArrayList actualReturn = new DoubleArrayList( matrix.getRow( 2 ) );
        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 0.3, 0.0, 0.8 } );
        assertTrue( RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.0001 ) );
    }

    @Test
    public void testGetRowArrayList() {

        DoubleArrayList actualReturn = matrix.getRowArrayList( 2 );

        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 0.3, 0.8 } );

        assertTrue( RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.0001 ) );

    }

    //
    @Test
    public void testGetRowMatrix1D() {
        DoubleMatrix1D actualReturn = matrix.viewRow( 2 );
        DoubleMatrix1D expectedReturn = new RCDoubleMatrix1D( new double[] { 0.3, 0.0, 0.8 } );
        assertTrue( RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.0001 ) );
    }

    @Test
    public void testGetRowRange() {
        DoubleMatrix<String, String> rowRange = matrix.getRowRange( 1, 2 );
        assertEquals( 3, rowRange.columns() );
        assertEquals( 2, rowRange.rows() );
    }

    @Test
    public void testRows() {

        int actualReturn = matrix.rows();
        int expectedReturn = 3;

        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testSubsetRows() {
        DoubleMatrix<String, String> subsetRows = matrix.subsetRows( Arrays.asList( new String[] { "1" } ) );
        assertEquals( 1, subsetRows.rows() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubsetRowsFail() {
        matrix.subsetRows( Arrays.asList( new String[] { "foo" } ) );
    }

}