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
package ubic.basecode.math.linearmodels;

import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import ubic.basecode.math.MatrixStats;
import ubic.basecode.math.Smooth;
import ubic.basecode.math.linearmodels.DesignMatrix;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import ubic.basecode.util.RegressionTesting;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * @author ptan
 */
public class MeanVarianceEstimatorTest {

    /**
     * Tests two things: 1. Input data's X values are not sorted 2. Interpolated X values are outside the domain.
     *
     * @throws Exception R code:
     *                   x = c(9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0)
     *                   y = c(1.0, 0.25, 0.1111111111111111, 0.0625, 0.04, 0.027777777777777776, 0.02040816326530612, 0.015625, 0.012345679012345678, 0.01)
     *                   interpolate = c(9.5, 8.5, 7.5, 6.5, 5.5, 4.5, 3.5, 2.5, 1.5, 0.5)
     *                   f<-approxfun(x,y,rule=2)
     *                   f(c(interpolate))
     */
    @Test
    public void testInterpolate() throws Exception {
        double x[] = new double[10];
        double y[] = new double[x.length];
        double xInterpolate[] = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = 10 - (i + 1);
            y[i] = 1.0 / Math.pow((i + 1), 2);
            xInterpolate[i] = 10 - (i + 1) + 0.5;
        }

        double expected[] = new double[]{1.000000000000000000000, 0.625000000000000000000, 0.180555555555555552472,
                0.086805555555555552472, 0.051250000000000003886
                , 0.033888888888888885065, 0.024092970521541946793, 0.018016581632653058676, 0.013985339506172839164, 0.011172839506172840135};

        double yOut[] = Smooth.interpolate(x, y, xInterpolate);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], yOut[i], 1e-8);
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
        DoubleMatrix<String, String> testMatrix = f.read(this.getClass()
                .getResourceAsStream("/data/lmtest11.dat.txt"));

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read(this.getClass().getResourceAsStream(
                "/data/lmtest11.des.txt"));

        // add a duplicate entry with a different row id but similar expression levels
        DoubleMatrix<String, String> duplMatrix = new DenseDoubleMatrix<>(testMatrix.asDoubles());
        duplMatrix.viewRow(1).assign(duplMatrix.viewRow(0));
        DoubleMatrix1D libSize = MatrixStats.colSums(duplMatrix);
        duplMatrix = MatrixStats.convertToLog2Cpm(duplMatrix, libSize);

        DesignMatrix d = new DesignMatrix(sampleInfo, true);
        MeanVarianceEstimator est = new MeanVarianceEstimator(d, duplMatrix, libSize);
        DoubleMatrix2D actuals = est.getWeights();

        int[] expectedIndices = new int[]{0, 1, 149};
        double[][] expected = new double[][]{
                {0.755036, 0.812033, 0.938803, 0.973691, 0.970172, 0.968853, 0.755036},
                {0.755036, 0.812033, 0.938803, 0.973691, 0.970172, 0.968853, 0.755036},
                {29.462294, 32.386751, 36.690275, 37.844764, 40.823251, 40.776166, 23.402288}};
        for (int i = 0; i < expectedIndices.length; i++) {
            assertArrayEquals(expected[i], actuals.viewRow(expectedIndices[i]).toArray(), 0.1);
        }
    }

    /**
     * Test calculation of weights for LeastSquaresFitting. See test-squeezevar.R
     *
     * @throws Exception
     */
    @Test
    public void testVoom() throws Exception {
        DoubleMatrixReader reader = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = reader.read(this.getClass()
                .getResourceAsStream("/data/lmtest11.dat.txt"));
        DoubleMatrix1D libSize = MatrixStats.colSums(testMatrix);

        assertArrayEquals(new double[]{6792, 8101, 10133, 10672, 11346, 11329, 4005}, libSize.toArray(), 0.000001);

        testMatrix = MatrixStats.convertToLog2Cpm(testMatrix, libSize);
        DoubleMatrix expectedlog2cpm = reader.read(this.getClass()
                .getResourceAsStream("/data/lmtest11.log2cpm.txt"));
        assertTrue(RegressionTesting.closeEnough(expectedlog2cpm, testMatrix, 1e-4));

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read(this.getClass().getResourceAsStream(
                "/data/lmtest11.des.txt"));

        DesignMatrix d = new DesignMatrix(sampleInfo, true);
        MeanVarianceEstimator est = new MeanVarianceEstimator(d, testMatrix, libSize);

        DoubleMatrix2D actuals = est.getWeights();
        // Our lowess result is quite close
        DoubleMatrix2D expectedLowess = new DenseDoubleMatrix2D(reader.read(this.getClass().getResourceAsStream(
                "/data/lmtest11.lowess.txt")).asArray());
        DoubleMatrix2D actualLoess = est.getLoess();
        assertTrue(RegressionTesting.closeEnough(expectedLowess, actualLoess, 0.01));

        DoubleMatrix2D expectedWeights = new DenseDoubleMatrix2D(reader.read(this.getClass()
                .getResourceAsStream("/data/lmtest11.voomweights.txt")).asArray());
        // The tolerance for this test is low because our weights are slightly different from the limma:voom ones probably because of the interpolation.
        assertTrue(RegressionTesting.closeEnough(expectedWeights, actuals, 0.12));

    }

    /**
     * Data has missing values, no Design matrix provided so plot a generic mean-variance plot
     *
     * @throws Exception
     */
    @Test
    public void testMeanVarianceNoDesignWithColsRowAllMissing() throws Exception {

        double[][] dataPrimitive = new double[][]{{Double.NaN, -0.2, -0.3, -4, -5},
                {Double.NaN, 0, 7, Double.NaN, 8}, {Double.NaN, 9, -0.3, 0.5, Double.NaN},
                {Double.NaN, 3, -0.3, -0.1, Double.NaN},
                {Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN}};
        DoubleMatrix<String, String> data = new DenseDoubleMatrix<>(dataPrimitive);
        DoubleMatrix1D libSize = MatrixStats.colSums(data);
        data = MatrixStats.convertToLog2Cpm(data, libSize);
        MeanVarianceEstimator est = new MeanVarianceEstimator(new DenseDoubleMatrix2D(data.asArray()));
        DoubleMatrix2D actuals = null;

        actuals = est.getMeanVariance();
        assertEquals(5, actuals.rows());
        assertEquals(16.55, actuals.get(0, 0), 0.01);
        assertEquals(7.260, actuals.get(0, 1), 0.001);
        assertEquals(16.42, actuals.get(3, 0), 0.01);
        assertEquals(2.688, actuals.get(3, 1), 0.001);
        assertEquals(Double.NaN, actuals.get(4, 0), 0.01);
        assertEquals(Double.NaN, actuals.get(4, 1), 0.001);

        actuals = est.getLoess();
        assertEquals(4, actuals.rows());
        assertEquals(16.42, actuals.get(0, 0), 0.01);
        assertEquals(2.688, actuals.get(0, 1), 0.001);
        assertEquals(18.76, actuals.get(3, 0), 0.01);
        assertEquals(6.321, actuals.get(3, 1), 0.001);
    }

    /**
     * Data has missing values, no Design matrix provided so plot a generic mean-variance plot (no voom)
     *
     * @throws Exception
     */
    @Test
    public void testMeanVarianceNoDesignWithMissing() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read(this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt"));
        DoubleMatrix<String, String> data = new DenseDoubleMatrix<>(testMatrix.asDoubles());
        DoubleMatrix1D libSize = MatrixStats.colSums(data);
        data = MatrixStats.convertToLog2Cpm(data, libSize);
        MeanVarianceEstimator est = new MeanVarianceEstimator(new DenseDoubleMatrix2D(data.asArray()));
        DoubleMatrix2D actuals = null;

        actuals = est.getMeanVariance();
        assertEquals(15.33, actuals.get(0, 0), 0.01);
        assertEquals(0.003220, actuals.get(0, 1), 0.001);
        assertEquals(15.28, actuals.get(4, 0), 0.01);
        assertEquals(0.001972, actuals.get(4, 1), 0.001);
        assertEquals(15.97, actuals.get(18, 0), 0.01);
        assertEquals(0.010072, actuals.get(18, 1), 0.001);

        actuals = est.getLoess();
        assertEquals(15.24, actuals.get(0, 0), 0.01);
        assertEquals(0.002620, actuals.get(0, 1), 0.001);
        assertEquals(15.37, actuals.get(4, 0), 0.01);
        assertEquals(0.006238, actuals.get(4, 1), 0.001);
        assertEquals(16.72, actuals.get(18, 0), 0.01);
        assertEquals(0.007320, actuals.get(18, 1), 0.001);

        actuals = est.getWeights();
        assertNull(actuals);
    }

}
