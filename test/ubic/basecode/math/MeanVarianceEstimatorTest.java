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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import ubic.basecode.io.writer.MatrixWriter;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * @author ptan
 * @version $Id$
 */
public class MeanVarianceEstimatorTest {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog( MeanVarianceEstimatorTest.class );

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

    @Test
    public void testColSumsWithMissing() throws Exception {
        DoubleMatrix2D counts = new DenseDoubleMatrix2D( new double[][] { { 1, 2, Double.NaN }, { 4, 5, 6 } } );
        DoubleMatrix1D expected = new DenseDoubleMatrix1D( new double[] { 5, 7, 6 } );
        DoubleMatrix1D actual = MeanVarianceEstimator.colSums( counts );
        assertArrayEquals( expected.toArray(), actual.toArray(), 0.0001 );
    }

    @Test
    public void testCountsPerMillionWithMissing() throws Exception {
        DoubleMatrix2D counts = new DenseDoubleMatrix2D( new double[][] { { 1, 2, Double.NaN }, { 4, 5, 6 } } );
        DoubleMatrix1D libSize = MeanVarianceEstimator.colSums( counts );
        DoubleMatrix2D actual = MeanVarianceEstimator.countsPerMillion( counts, libSize );
        DoubleMatrix2D expected = new DenseDoubleMatrix2D( new double[][] { { 17.93157, 18.2535, Double.NaN },
                { 19.51653, 19.3910, 19.82465 } } );

        for ( int i = 0; i < expected.rows(); i++ ) {
            assertArrayEquals( expected.viewRow( i ).toArray(), actual.viewRow( i ).toArray(), 0.0001 );
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

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/lmtest11.des.txt" ) );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        MeanVarianceEstimator est = new MeanVarianceEstimator( d, testMatrix );
        DoubleMatrix2D actuals = est.getWeights();

        int[] expectedIndices = new int[] { 0, 99, 149 };
        double[][] expected = new double[][] { { 0.70312, 0.76108, 0.88965, 0.92339, 0.92094, 0.91994, 0.70312 },
                { 2.7991, 3.3145, 4.1011, 4.3082, 1.415, 1.4133, 0.70312 },
                { 29.57, 32.564, 36.943, 38.058, 41.13, 41.094, 23.419 } };

        for ( int i = 0; i < expectedIndices.length; i++ ) {
            assertArrayEquals( expected[i], actuals.viewRow( expectedIndices[i] ).toArray(), 0.1 );
        }

        String outputFilename = System.getProperty( "java.io.tmpdir" ) + File.separator
                + "meanVariance-testGetWeights.png";
        est.plot( outputFilename );
        assertTrue( new File( outputFilename ).exists() );
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
        DoubleMatrix2D duplMatrix = new DenseDoubleMatrix2D( testMatrix.asDoubles() );
        duplMatrix.viewRow( 1 ).assign( duplMatrix.viewRow( 0 ) );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        MeanVarianceEstimator est = new MeanVarianceEstimator( d, duplMatrix );
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
     * Data has missing values, no Design matrix provided so plot a generic mean-variance plot
     * 
     * @throws Exception
     */
    @Test
    public void testMeanVarianceNoDesignWithMissing() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt" ) );
        DoubleMatrix2D data = new DenseDoubleMatrix2D( testMatrix.asDoubles() );
        MeanVarianceEstimator est = new MeanVarianceEstimator( data );
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

        String outputFilename = System.getProperty( "java.io.tmpdir" ) + File.separator
                + "meanVariance-testMeanVarianceNoDesignWithMissing.png";
        est.plot( outputFilename );
        assertTrue( new File( outputFilename ).exists() );
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
        DoubleMatrix2D data = new DenseDoubleMatrix2D( dataPrimitive );
        MeanVarianceEstimator est = new MeanVarianceEstimator( data );
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

        StringWriter s = new StringWriter();

        DoubleMatrix<String, String> m = new DenseDoubleMatrix<String, String>( actuals.rows(), 2 );
        m = m.transpose();
        m.viewRow( 0 ).assign( actuals.viewColumn( 0 ) );
        m.viewRow( 1 ).assign( actuals.viewColumn( 1 ) );
        m.setRowName( "mean", 0 );
        m.setRowName( "variance", 1 );

        MatrixWriter<String, String> mw = new MatrixWriter<String, String>( s, new DecimalFormat( "#.##" ) );
        mw.writeMatrix( m, true );

        String outputFilename = System.getProperty( "java.io.tmpdir" ) + File.separator
                + "meanVariance-testMeanVarianceNoDesignWithColsRowAllMissing.png";
        est.plot( outputFilename );
        assertTrue( new File( outputFilename ).exists() );
    }
}
