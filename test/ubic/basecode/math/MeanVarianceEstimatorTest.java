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
package ubic.basecode.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * @author ptan
 * @version $Id$
 */
public class MeanVarianceEstimatorTest {

    /**
     * Tests two things: 1. Input data's X values are not sorted 2. Interpolated X values are outside the domain.
     * 
     * @throws Exception
     */
    @Test
    public void testApprox() throws Exception {
        double x[] = new double[10];
        double y[] = new double[x.length];
        double xInterpolate[] = new double[x.length];
        for ( int i = 0; i < x.length; i++ ) {
            x[i] = 10 - ( i + 1 );
            y[i] = 1.0 / Math.pow( ( i + 1 ), 2 );
            xInterpolate[i] = 10 - ( i + 1 ) + 0.5;
        }

        double expectedY3[] = { 1.00000000, 0.62500000, 0.18055556, 0.08680556, 0.05125000, 0.03388889, 0.02409297,
                0.01801658, 0.01398534, 0.01117284 };

        double yOut[] = MeanVarianceEstimator.approx( x, y, xInterpolate );
        for ( int i = 0; i < expectedY3.length; i++ ) {
            assertEquals( expectedY3[i], yOut[i], 0.0001 );
        }
    }

    /**
     * Duplicate row
     * 
     * @throws Exception
     */
    @Test
    public void testDuplicateRows() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass()
                .getResourceAsStream( "/data/lmtest11.dat.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/lmtest11.des.txt" ) );

        // add a duplicate entry with a different row id but similar expression levels
        DoubleMatrix<String, String> duplMatrix = new DenseDoubleMatrix<String, String>( testMatrix.asDoubles() );
        duplMatrix.viewRow( 1 ).assign( duplMatrix.viewRow( 0 ) );
        DoubleMatrix1D libSize = MatrixStats.colSums( duplMatrix );
        MatrixStats.convertToLog2Cpm( duplMatrix, libSize );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        MeanVarianceEstimator est = new MeanVarianceEstimator( d, duplMatrix, libSize );
        DoubleMatrix2D actuals = est.getWeights();

        int[] expectedIndices = new int[] { 0, 1, 149 };
        double[][] expected = new double[][] {
                { 0.755036, 0.812033, 0.938803, 0.973691, 0.970172, 0.968853, 0.755036 },
                { 0.755036, 0.812033, 0.938803, 0.973691, 0.970172, 0.968853, 0.755036 },
                { 29.462294, 32.386751, 36.690275, 37.844764, 40.823251, 40.776166, 23.402288 } };
        for ( int i = 0; i < expectedIndices.length; i++ ) {
            assertArrayEquals( expected[i], actuals.viewRow( expectedIndices[i] ).toArray(), 0.1 );
        }
    }

    /**
     * Test calculation of weights for LeastSquaresFitting
     * 
     * @throws Exception
     */
    @Test
    public void testGetWeights() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass()
                .getResourceAsStream( "/data/lmtest11.dat.txt" ) );
        DoubleMatrix1D libSize = MatrixStats.colSums( testMatrix );
        MatrixStats.convertToLog2Cpm( testMatrix, libSize );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/lmtest11.des.txt" ) );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        MeanVarianceEstimator est = new MeanVarianceEstimator( d, testMatrix, libSize );
        DoubleMatrix2D actuals = est.getWeights();

        int[] expectedIndices = new int[] { 0, 99, 149 };
        double[][] expected = new double[][] { { 0.70312, 0.76108, 0.88965, 0.92339, 0.92094, 0.91994, 0.70312 },
                { 2.7991, 3.3145, 4.1011, 4.3082, 1.415, 1.4133, 0.70312 },
                { 29.57, 32.564, 36.943, 38.058, 41.13, 41.094, 23.419 } };

        for ( int i = 0; i < expectedIndices.length; i++ ) {
            assertArrayEquals( expected[i], actuals.viewRow( expectedIndices[i] ).toArray(), 0.1 );
        }
    }

    /**
     * Data has missing values, no Design matrix provided so plot a generic mean-variance plot
     * 
     * @throws Exception
     */
    @Test
    public void testMeanVarianceNoDesignWithColsRowAllMissing() throws Exception {

        double[][] dataPrimitive = new double[][] { { Double.NaN, -0.2, -0.3, -4, -5 },
                { Double.NaN, 0, 7, Double.NaN, 8 }, { Double.NaN, 9, -0.3, 0.5, Double.NaN },
                { Double.NaN, 3, -0.3, -0.1, Double.NaN },
                { Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN } };
        DoubleMatrix<String, String> data = new DenseDoubleMatrix<String, String>( dataPrimitive );
        DoubleMatrix1D libSize = MatrixStats.colSums( data );
        MatrixStats.convertToLog2Cpm( data, libSize );
        MeanVarianceEstimator est = new MeanVarianceEstimator( new DenseDoubleMatrix2D( data.asArray() ) );
        DoubleMatrix2D actuals = null;

        actuals = est.getMeanVariance();
        assertEquals( 5, actuals.rows() );
        assertEquals( 16.55, actuals.get( 0, 0 ), 0.01 );
        assertEquals( 7.260, actuals.get( 0, 1 ), 0.001 );
        assertEquals( 16.42, actuals.get( 3, 0 ), 0.01 );
        assertEquals( 2.688, actuals.get( 3, 1 ), 0.001 );
        assertEquals( Double.NaN, actuals.get( 4, 0 ), 0.01 );
        assertEquals( Double.NaN, actuals.get( 4, 1 ), 0.001 );

        actuals = est.getLoess();
        assertEquals( 4, actuals.rows() );
        assertEquals( 16.42, actuals.get( 0, 0 ), 0.01 );
        assertEquals( 2.688, actuals.get( 0, 1 ), 0.001 );
        assertEquals( 18.76, actuals.get( 3, 0 ), 0.01 );
        assertEquals( 6.321, actuals.get( 3, 1 ), 0.001 );
    }

    /**
     * Data has missing values, no Design matrix provided so plot a generic mean-variance plot
     * 
     * @throws Exception
     */
    @Test
    public void testMeanVarianceNoDesignWithMissing() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt" ) );
        DoubleMatrix<String, String> data = new DenseDoubleMatrix<String, String>( testMatrix.asDoubles() );
        DoubleMatrix1D libSize = MatrixStats.colSums( data );
        MatrixStats.convertToLog2Cpm( data, libSize );
        MeanVarianceEstimator est = new MeanVarianceEstimator( new DenseDoubleMatrix2D( data.asArray() ) );
        DoubleMatrix2D actuals = null;

        actuals = est.getMeanVariance();
        assertEquals( 15.33, actuals.get( 0, 0 ), 0.01 );
        assertEquals( 0.003220, actuals.get( 0, 1 ), 0.001 );
        assertEquals( 15.28, actuals.get( 4, 0 ), 0.01 );
        assertEquals( 0.001972, actuals.get( 4, 1 ), 0.001 );
        assertEquals( 15.97, actuals.get( 18, 0 ), 0.01 );
        assertEquals( 0.010072, actuals.get( 18, 1 ), 0.001 );

        actuals = est.getLoess();
        assertEquals( 15.24, actuals.get( 0, 0 ), 0.01 );
        assertEquals( 0.002620, actuals.get( 0, 1 ), 0.001 );
        assertEquals( 15.37, actuals.get( 4, 0 ), 0.01 );
        assertEquals( 0.006238, actuals.get( 4, 1 ), 0.001 );
        assertEquals( 16.72, actuals.get( 18, 0 ), 0.01 );
        assertEquals( 0.007320, actuals.get( 18, 1 ), 0.001 );

        actuals = est.getWeights();
        assertNull( actuals );
    }

    /**
     * Data has missing values and a Design matrix is also provided as well. Bug 3478.
     * 
     * @throws Exception
     */
    @Test
    public void testMeanVarianceWithDesignWithMissing() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt" ) );
        DoubleMatrix1D libSize = MatrixStats.colSums( testMatrix );
        MatrixStats.convertToLog2Cpm( testMatrix, libSize );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/example.metadata.small.txt" ) );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        MeanVarianceEstimator est = new MeanVarianceEstimator( d, testMatrix, libSize );
        DoubleMatrix2D actuals = est.getWeights();

        // quick mv() sanity checks
        actuals = est.getMeanVariance();
        assertEquals( 15.33, actuals.get( 0, 0 ), 0.01 );
        assertEquals( 0.010072, actuals.get( 18, 1 ), 0.001 );
        actuals = est.getLoess();
        assertEquals( 15.24, actuals.get( 0, 0 ), 0.01 );
        assertEquals( 0.007320, actuals.get( 18, 1 ), 0.001 );

        // the real test
        actuals = est.getWeights();
        assertEquals( 135.839, actuals.get( 0, 0 ), 0.001 );
        assertEquals( 69.075, actuals.get( 18, 1 ), 0.001 );
    }
}
