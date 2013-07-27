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
package ubic.basecode.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;
import ubic.basecode.datafilter.AbstractTestFilter;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.util.RegressionTesting;
import cern.colt.function.DoubleProcedure;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.math.Functions;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestMatrixStats {

    double[][] testrdm = { { 1, 2, 3, 4 }, { 11, 12, 13, 14 }, { 21, Double.NaN, 23, 24 } };
    private DoubleMatrix<String, String> smallT = null;

    private DoubleMatrix<String, String> testdata = null;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();

        testdata = f.read( AbstractTestFilter.class.getResourceAsStream( "/data/testdata.txt" ) );

        smallT = DoubleMatrixFactory.dense( testrdm );
        smallT.setRowNames( java.util.Arrays.asList( new String[] { "a", "b", "c" } ) );
        smallT.setColumnNames( java.util.Arrays.asList( new String[] { "w", "x", "y", "z" } ) );
    }

    @Test
    public void testColSumsWithMissing() throws Exception {
        DoubleMatrix<String, String> counts = new DenseDoubleMatrix<String, String>( new double[][] {
                { 1, 2, Double.NaN }, { 4, 5, 6 } } );
        DoubleMatrix1D expected = new DenseDoubleMatrix1D( new double[] { 5, 7, 6 } );
        DoubleMatrix1D actual = MatrixStats.colSums( counts );
        assertArrayEquals( expected.toArray(), actual.toArray(), 0.0001 );
    }

    @Test
    public final void testConvertLog() {
        DoubleMatrix<String, String> copy = testdata.copy();

        // convert copy to logE form.
        for ( int j = 0; j < copy.rows(); j++ ) {
            DoubleMatrix1D row = copy.viewRow( j );
            row.assign( Functions.log );
            for ( int i = 0; i < row.size(); i++ ) {
                copy.set( j, i, row.get( i ) );
            }
        }

        MatrixStats.convertToLog2( copy, Math.E );

        for ( int j = 0; j < copy.rows(); j++ ) {
            DoubleMatrix1D row = copy.viewRow( j );
            DoubleMatrix1D orow = testdata.viewRow( j );

            orow.assign( Functions.log2 );

            for ( int i = 0; i < orow.size(); i++ ) {
                assertEquals( orow.get( i ), row.get( i ), 1e-10 );
            }

        }

    }

    @Test
    public final void testConvertToLog2Cpm() {
        double expectedReturn = 17.665;
        MatrixStats.convertToLog2Cpm( testdata, null );
        assertEquals( "return value", expectedReturn, testdata.get( 9, 3 ), 0.001 );
    }

    @Test
    public final void testCorrelationMatrix() throws Exception {
        DoubleMatrix<String, String> actualReturn = MatrixStats.correlationMatrix( testdata );
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> expectedReturn = f.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/correlation-matrix-testoutput.txt" ) );

        assertTrue( RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.001 ) );

    }

    @Test
    public final void testCorrelationMatrixThreshold() throws Exception {
        double threshold = 0.5;
        DoubleMatrix<String, String> actualReturn = MatrixStats.correlationMatrix( testdata, threshold );
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> expectedReturn = f.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/correlation-matrix-testoutput.txt" ) );
        for ( int i = 0; i < expectedReturn.rows(); i++ ) {

            for ( int j = 0; j < expectedReturn.columns(); j++ ) {

                if ( i == j ) {
                    expectedReturn.set( i, j, Double.NaN );
                    continue;
                }

                double v = Math.abs( expectedReturn.get( i, j ) );
                if ( v <= threshold ) {
                    expectedReturn.set( i, j, Double.NaN );
                }
            }
        }

        assertTrue( RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.001 ) );

    }

    @Test
    public void testCountsPerMillionWithMissing() throws Exception {
        DoubleMatrix<String, String> counts = new DenseDoubleMatrix<String, String>( new double[][] {
                { 1, 2, Double.NaN }, { 4, 5, 6 } } );
        DoubleMatrix1D libSize = MatrixStats.colSums( counts );
        MatrixStats.convertToLog2Cpm( counts, libSize );
        DoubleMatrix<String, String> actual = counts;
        DoubleMatrix<String, String> expected = new DenseDoubleMatrix<String, String>( new double[][] {
                { 17.93157, 18.2535, Double.NaN }, { 19.51653, 19.3910, 19.82465 } } );

        for ( int i = 0; i < expected.rows(); i++ ) {
            assertArrayEquals( expected.viewRow( i ).toArray(), actual.viewRow( i ).toArray(), 0.0001 );
        }
    }

    @Test
    public final void testDoubleStandardize() {
        DoubleMatrix<String, String> standardize = MatrixStats.doubleStandardize( testdata );
        assertEquals( 30, standardize.rows() );
        assertEquals( -0.472486, standardize.get( 3, 4 ), 0.01 );
        assertEquals( -0.4903036, standardize.get( 26, 2 ), 0.01 );

        MatrixRowStats.means( standardize ).forEach( new DoubleProcedure() {
            @Override
            public boolean apply( double element ) {
                assertEquals( 0.0, element, 0.001 );
                return true;
            }
        } );

        MatrixRowStats.means( standardize.transpose() ).forEach( new DoubleProcedure() {
            @Override
            public boolean apply( double element ) {
                assertEquals( 0.0, element, 0.002 );
                return true;
            }
        } );

        MatrixRowStats.sampleStandardDeviations( standardize ).forEach( new DoubleProcedure() {
            @Override
            public boolean apply( double element ) {
                assertEquals( 1.0, element, 0.001 );
                return true;
            }
        } );

        MatrixRowStats.sampleStandardDeviations( standardize.transpose() ).forEach( new DoubleProcedure() {
            @Override
            public boolean apply( double element ) {
                assertEquals( 1.0, element, 0.05 );
                return true;
            }
        } );
    }

    @Test
    public final void testLogTransform() {
        MatrixStats.logTransform( testdata );
        assertEquals( Math.log( 44625.7 ) / Math.log( 2 ), testdata.get( 9, 3 ), 0.001 );
    }

    @Test
    public final void testMax() {
        double expectedReturn = 44625.7;
        double actualReturn = MatrixStats.max( testdata );
        assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
    }

    @Test
    public final void testMin() {
        double expectedReturn = -965.3;
        double actualReturn = MatrixStats.min( testdata );
        assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
    }

    @Test
    public final void testNan() {
        boolean[][] actual = MatrixStats.nanStatusMatrix( testrdm );
        assertFalse( actual[0][0] );
        assertFalse( actual[0][3] );
        assertTrue( actual[2][1] );
    }

    @Test
    public final void testRbfNormalize() {
        double[][] actual = { { 0.001, 0.2, 0.13, 0.4 }, { 0.11, 0.12, 0.00013, 0.14 }, { 0.21, 0.0001, 0.99, 0.24 } };
        DenseDoubleMatrix<String, String> av = new DenseDoubleMatrix<String, String>( actual );
        MatrixStats.rbfNormalize( av, 1 );
        double[][] expected = { { 0.2968, 0.2432, 0.2609, 0.1991 }, { 0.2453, 0.2429, 0.2738, 0.2381 },
                { 0.273, 0.3368, 0.1252, 0.265 } };
        assertEquals( true,
                RegressionTesting.closeEnough( new DenseDoubleMatrix<String, String>( expected ), av, 0.001 ) );
        for ( int i = 0; i < 3; i++ ) {
            assertEquals( 1.0, av.viewRow( i ).zSum(), 0.0001 );
        }
    }

    @Test
    public final void testSelfSquare() {
        double[][] actual = MatrixStats.selfSquaredMatrix( testrdm );
        assertEquals( 1, actual[0][0], 0.000001 );
        assertEquals( 16, actual[0][3], 0.00001 );
    }

    @Test
    public final void testStandardize() {
        DoubleMatrix<String, String> standardize = MatrixStats.standardize( testdata );
        assertEquals( 30, standardize.rows() );
        assertEquals( -0.3972279, standardize.get( 3, 4 ), 0.0001 );
        assertEquals( -0.7385692, standardize.get( 13, 5 ), 0.0001 );

        MatrixRowStats.means( standardize ).forEach( new DoubleProcedure() {
            @Override
            public boolean apply( double element ) {
                assertEquals( 0.0, element, 0.001 );
                return true;
            }
        } );

        MatrixRowStats.sampleStandardDeviations( standardize ).forEach( new DoubleProcedure() {
            @Override
            public boolean apply( double element ) {
                assertEquals( 1.0, element, 0.001 );
                return true;
            }
        } );
    }
}
