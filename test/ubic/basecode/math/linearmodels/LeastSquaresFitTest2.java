/*
 * The baseCode project
 *
 * Copyright (c) 2017 University of British Columbia
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

package ubic.basecode.math.linearmodels;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import ubic.basecode.io.writer.MatrixWriter;
import ubic.basecode.math.MatrixStats;

/**
 * @author paul
 */
public class LeastSquaresFitTest2 {

    /**
     * Weighted regression test - Based on a few genes from GSE9376, but samples scrambled
     *
     * @throws Exception
     */
    @Test
    public void testMatrixWeightedMeanVariance() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f
                .read(this.getClass().getResourceAsStream("/data/lmtest.countdata1.txt"));
        DoubleMatrix1D librarySize = MatrixStats.colSums(testMatrix);
        testMatrix = MatrixStats.convertToLog2Cpm(testMatrix, librarySize);

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read(this.getClass()
                .getResourceAsStream("/data/lmtest.design1.txt"));

        DesignMatrix d = new DesignMatrix(sampleInfo, true);

        MeanVarianceEstimator est = new MeanVarianceEstimator(d, testMatrix, librarySize);
        DoubleMatrix2D w2D = est.getWeights();
        DoubleMatrix<String, String> normalizedData = new DenseDoubleMatrix<>(est.getNormalizedValue().toArray());
        normalizedData.setRowNames(testMatrix.getRowNames());
        normalizedData.setColumnNames(testMatrix.getColNames());

        /*
         * Sanity check that the data are the same as voom at this point
         */
        assertArrayEquals(est.getNormalizedValue().viewRow(0).toArray(),
                new double[]{10.99327, 10.99189, 10.04471, 10.17934, 10.56090, 10.48717, 10.48942, 10.64346, 10.50560, 10.55924},
                0.00001);

        /*
         * Sanity check: without using weights.
         */
        LeastSquaresFit fitNoWeights = new LeastSquaresFit(d, normalizedData); //
        double[] expectedWithoutUsingWeights = new double[]{
                10.67662, -0.2674902, -0.1271957};
        assertArrayEquals(expectedWithoutUsingWeights, fitNoWeights.getCoefficients().viewDice().toArray()[0], 0.00001);

        /*
         * Weighted
         */
        //Save weights for testing in R - ours are slighly different
        //        DenseDoubleMatrix wwwww = new DenseDoubleMatrix<>( w2D.toArray() );
        //        wwwww.setRowNames( testMatrix.getRowNames() );
        //        wwwww.setColumnNames( testMatrix.getColNames() );
        //        DecimalFormat formatter = ( DecimalFormat ) NumberFormat.getNumberInstance( Locale.US );
        //        formatter.applyPattern( "0.00000000" );
        //        MatrixWriter w = new MatrixWriter( "lmtest.weights1.txt", formatter );
        //        w.writeMatrix( wwwww, true );

        LeastSquaresFit fit = new LeastSquaresFit(d, normalizedData, w2D); //
        DoubleMatrix2D actuals = fit.getCoefficients().viewDice();

        /*
         * Test is based on our weights since the exact computation of them is a bit different.
         */

        double[] expectedCoef = new double[]{10.70859, -0.2846247, -0.1576213};
        assertArrayEquals(expectedCoef, actuals.viewRow(0).toArray(), 0.0001);
        List<LinearModelSummary> sums = fit.summarize(true);
        LinearModelSummary f1 = sums.get(0);
        GenericAnovaResult anova = f1.getAnova();

        assertEquals(0.1149918, f1.getStdevUnscaled()[0], 0.00001);
        assertEquals(0.1692047, f1.getStdevUnscaled()[1], 0.00001);
        assertEquals(0.1506129, f1.getStdevUnscaled()[2], 0.00001);
        assertEquals(7, anova.getResidualDf(), 0.0001);

        Double[] effects = f1.getEffects();
        assertEquals(-165.7957918, effects[0], 0.00001);
        assertEquals(-1.3325308, effects[1], 0.00001);
        assertEquals(-1.0465329, effects[2], 0.00001);
        assertEquals(-0.7134057, effects[3], 0.00001);
        assertEquals(1.524718, f1.getSigma(), 0.00001);

        //  System.err.println( anova );
        assertEquals(0.6174535, anova.getMainEffectF("genotype"), 0.001); // F=1.4354/2.3248
        assertEquals(0.5663, anova.getMainEffectP("genotype"), 0.0001);

        assertEquals(7, f1.getResidualDof().intValue()); // ok

        assertEquals(0.15, f1.getRSquared(), 0.01);
        assertEquals(-0.09291, f1.getAdjRSquared(), 0.0001);
        assertEquals(0.6174535, f1.getF(), 0.0001);
        assertEquals(0.5663, f1.getP(), 0.0001);


// same as above, checking my sanity and checking that running summarize more than once doesn't mess anything up.
        fit.summarize();
        List<LinearModelSummary> summariesA = fit.summarize(true);
        LinearModelSummary f1A = summariesA.get(0);
        GenericAnovaResult reA = f1A.getAnova();
        assertEquals(0.6174535, reA.getMainEffectF("genotype"), 1e-4);


        // with bayes F statistic for main effect. This code is the same as above except with the addition of ebayes.
        LeastSquaresFit fitE = new LeastSquaresFit(d, normalizedData, w2D);
        ModeratedTstat.ebayes(fitE);
        List<LinearModelSummary> summariesE = fitE.summarize(true);
        LinearModelSummary f1E = summariesE.get(0);
        GenericAnovaResult reE = f1E.getAnova();
        assertEquals(0.73359941990489119235, reE.getMainEffectF("genotype"), 1e-2);

    }

}
