/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
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
package ubic.basecode.dataStructure.matrix;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.TestDoubleMatrixReader;

/**
 * @author  Paul
 * @version $Id$
 */
public class MatrixUtilTest {

    DoubleMatrix2D testData;

    @Before
    public void setUp() throws Exception {

        DoubleMatrixReader reader = new DoubleMatrixReader();

        InputStream is = TestDoubleMatrixReader.class.getResourceAsStream( "/data/testdatamissing.txt" );
        testData = new DenseDoubleMatrix2D( reader.read( is ).asDoubles() );
        is.close();
    }

    @Test
    public void testDropColumn() {
        assertEquals( 12, testData.columns() );
        assertEquals( 11, MatrixUtil.dropColumn( testData, 1 ).columns() );
    }

    @Test
    public void testSelectColumns() {
        assertEquals( 12, testData.columns() );
        assertEquals( 3, MatrixUtil.selectColumns( testData, Arrays.asList( new Integer[] { 2, 3, 8 } ) ).columns() );
    }

    @Test
    public void testSelectColumnsAndRows() {
        Algebra A = new Algebra();
        DoubleMatrix2D square = A.mult( testData, A.transpose( testData ) );
        assertEquals( square.rows(), square.columns() );
        assertEquals( 3, MatrixUtil.selectColumnsAndRows( square, Arrays.asList( new Integer[] { 2, 3, 4 } ) ).rows() );
        assertEquals( 3,
                MatrixUtil.selectColumnsAndRows( square, Arrays.asList( new Integer[] { 2, 3, 4 } ) ).columns() );

    }

    @Test
    public void testSelectColumnsAndRowsBad() {
        try {
            MatrixUtil.selectColumnsAndRows( testData, Arrays.asList( new Integer[] { 2, 3, 4 } ) );
            fail( "Should have gotten illegalarg ex" );
        } catch ( IllegalArgumentException e ) {
            // expected
        }
    }

    @Test
    public void testSelectRows() {
        assertEquals( 3, MatrixUtil.selectRows( testData, Arrays.asList( new Integer[] { 2, 3, 4 } ) ).rows() );
    }

    @Test
    public void testRemoveMissing1() {
        DoubleMatrix1D v1 = testData.viewRow( 1 ); // first value is NaN
        DoubleMatrix1D actual = MatrixUtil.removeMissing( v1 );
        DoubleMatrix1D expected = new DenseDoubleMatrix1D(
                new double[] { 172.5, 242.1, -8.8, 148.8, 190.3, 155.1, 205.3, 337.8, 276, -64.2, 295.4 } );

        assertArrayEquals( expected.toArray(), actual.toArray(), 0.1 );

    }

    @Test
    public void testRemoveMissing2() {
        DoubleMatrix1D v1 = new DenseDoubleMatrix1D( new double[] { 1, 2, Double.NaN, 4, 5 } );

        DoubleMatrix1D v2 = new DenseDoubleMatrix1D( new double[] { 11, 12, 13, 14, 15 } );

        DoubleMatrix1D actual = MatrixUtil.removeMissing( v1, v2 );
        DoubleMatrix1D expected = new DenseDoubleMatrix1D(
                new double[] { 11, 12, 14, 15 } );

        assertArrayEquals( expected.toArray(), actual.toArray(), 0.1 );
    }

}
