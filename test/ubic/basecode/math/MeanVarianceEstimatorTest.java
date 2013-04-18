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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * @author ptan
 * @version $Id $
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
            assertEquals( yOut[i], expectedY3[i], 0.0001 );
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
            assertArrayEquals( actuals.viewRow( expectedIndices[i] ).toArray(), expected[i], 0.1 );
        }
    }

}
