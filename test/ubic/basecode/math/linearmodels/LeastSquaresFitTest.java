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

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import org.apache.commons.math3.distribution.FDistribution;
import org.junit.Test;
import ubic.basecode.dataStructure.matrix.*;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import ubic.basecode.math.DescriptiveWithMissing;
import ubic.basecode.math.MatrixStats;

import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.*;

/**
 * @author paul
 */
public class LeastSquaresFitTest {

    @Test
    public void testLSFOneContinuousWithMissing3() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<>( 9, 1 );
        design.set( 0, 0, 0.12 );
        design.set( 1, 0, 0.24 );
        design.set( 2, 0, 0.48 );
        design.set( 3, 0, 0.96 );
        design.set( 4, 0, 0.12 );
        design.set( 5, 0, 0.24 );
        design.set( 6, 0, 0.48 );
        design.set( 7, 0, 0.96 );
        design.set( 8, 0, 0.96 );

        design.setRowNames( testMatrix.getColNames() );
        design.addColumnName( "Value" );

        LeastSquaresFit fit = new LeastSquaresFit( design, testMatrix );

        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );

        LinearModelSummary s = sums.get( "228980_at" ); // has missing
        assertNotNull( s );
        assertEquals( 0.1495, s.getFStat(), 0.01 );
        assertEquals( 0.7123, s.getOverallPValue(), 0.001 );
        assertEquals( 10.9180, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.712, s.getContrastCoefficients().get( 1, 3 ), 0.001 ); // pvalue
        assertEquals( 6, ( int ) s.getResidualsDof() );
        GenericAnovaResult a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.1495, a.getMainEffectFStat( "Value" ), 0.0001 );
        assertEquals( 1, ( int ) a.getMainEffectDof( "Value" ) );
        assertEquals( 6, ( int ) a.getResidualsDof() );

        FDistribution fd = new FDistribution( 1, 6 );
        double p = 1.0 - fd.cumulativeProbability( 0.1495 );
        assertEquals( 0.7123, p, 0.0001 );
        assertEquals( 0.7123, a.getMainEffectPValue( "Value" ), 0.0001 );

        s = sums.get( "1553129_at" );
        assertNotNull( s );
        assertEquals( 2.095, s.getFStat(), 0.01 );
        assertEquals( 0.1911, s.getOverallPValue(), 0.001 );
        assertEquals( 3.78719, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.191, s.getContrastCoefficients().get( 1, 3 ), 0.001 ); // this ordering might change?
        a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.1911, a.getMainEffectPValue( "Value" ), 0.0001 );

        s = fit.summarize( 14 );
        assertNotNull( s );
        assertEquals( "214502_at", s.getKey() );// has missing
        assertEquals( 1.992, s.getFStat(), 0.01 );
        assertEquals( 0.2172, s.getOverallPValue(), 0.001 );
        assertEquals( 4.2871, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.217, s.getContrastCoefficients().get( 1, 3 ), 0.001 );

        s = sums.get( "232018_at" );
        assertNotNull( s );
        assertEquals( 1.381, s.getFStat(), 0.01 );
        assertEquals( 0.2783, s.getOverallPValue(), 0.001 );
        assertEquals( 6.6537, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.278, s.getContrastCoefficients().get( 1, 3 ), 0.001 );
        a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.2783, a.getMainEffectPValue( "Value" ), 0.0001 );
        s = sums.get( "228980_at" ); // has missing
        assertNotNull( s );
        assertEquals( 0.1495, s.getFStat(), 0.01 );
        assertEquals( 0.7123, s.getOverallPValue(), 0.001 );
        assertEquals( 10.9180, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.712, s.getContrastCoefficients().get( 1, 3 ), 0.001 );
        a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.7123, a.getMainEffectPValue( "Value" ), 0.0001 );
    }

    @Test
    public void testLSFThreeLevelsOnecontinous2() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<>( 9, 2 );

        design.set( 0, 0, "A" );
        design.set( 1, 0, "A" );
        design.set( 2, 0, "A" );
        design.set( 3, 0, "B" );
        design.set( 4, 0, "B" );
        design.set( 5, 0, "B" );
        design.set( 6, 0, "C" );
        design.set( 7, 0, "C" );
        design.set( 8, 0, "C" );
        design.set( 0, 1, 0.12 );
        design.set( 1, 1, 0.24 );
        design.set( 2, 1, 0.48 );
        design.set( 3, 1, 0.96 );
        design.set( 4, 1, 0.12 );
        design.set( 5, 1, 0.24 );
        design.set( 6, 1, 0.48 );
        design.set( 7, 1, 0.96 );
        design.set( 8, 1, 0.96 );

        design.setRowNames( testMatrix.getColNames() );
        design.addColumnName( "Factor" );
        design.addColumnName( "Value" );

        LeastSquaresFit fit = new LeastSquaresFit( design, testMatrix );

        DoubleMatrix2D coeffs = fit.getCoefficients();
        assertEquals( 3.7458080, coeffs.get( 0, 0 ), 0.0001 );
        assertEquals( -0.4388889, coeffs.get( 1, 2 ), 0.0001 );
        assertEquals( 0.5709091, coeffs.get( 2, 10 ), 0.0001 );
        assertEquals( 0.04856061, coeffs.get( 2, 18 ), 0.0001 );
        assertEquals( -1.1363636, coeffs.get( 3, 10 ), 0.0001 );
        assertEquals( 0.11174242, coeffs.get( 3, 18 ), 0.0001 );

        DoubleMatrix2D fitted = fit.getFitted();

        assertEquals( 3.764747, fitted.get( 0, 0 ), 0.0001 );
        assertEquals( 6.043990, fitted.get( 1, 3 ), 0.0001 );
        assertEquals( 10.858586, fitted.get( 7, 2 ), 0.0001 );
        assertEquals( 6.307879, fitted.get( 18, 8 ), 0.0001 );

        List<GenericAnovaResult> anova = fit.anova();
        assertEquals( 19, anova.size() );
    }

    @Test
    public void testLSFThreeLevelsOneContinuousWithMissing3() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<>( 9, 2 );

        design.set( 0, 0, "A" );
        design.set( 1, 0, "A" );
        design.set( 2, 0, "A" );
        design.set( 3, 0, "B" );
        design.set( 4, 0, "B" );
        design.set( 5, 0, "B" );
        design.set( 6, 0, "C" );
        design.set( 7, 0, "C" );
        design.set( 8, 0, "C" );
        design.set( 0, 1, 0.12 );
        design.set( 1, 1, 0.24 );
        design.set( 2, 1, 0.48 );
        design.set( 3, 1, 0.96 );
        design.set( 4, 1, 0.12 );
        design.set( 5, 1, 0.24 );
        design.set( 6, 1, 0.48 );
        design.set( 7, 1, 0.96 );
        design.set( 8, 1, 0.96 );

        design.setRowNames( testMatrix.getColNames() );
        design.addColumnName( "Factor" );
        design.addColumnName( "Value" );

        LeastSquaresFit fit = new LeastSquaresFit( design, new DenseDoubleMatrix2D( testMatrix.asArray() ) );

        DoubleMatrix2D coeffs = fit.getCoefficients();
        assertEquals( 3.7458080, coeffs.get( 0, 0 ), 0.0001 );
        assertEquals( -0.4388889, coeffs.get( 1, 2 ), 0.0001 );
        assertEquals( 0.5709091, coeffs.get( 2, 10 ), 0.0001 );
        assertEquals( 0.04856061, coeffs.get( 2, 18 ), 0.0001 );
        assertEquals( -1.1363636, coeffs.get( 3, 10 ), 0.0001 );
        assertEquals( 0.11174242, coeffs.get( 3, 18 ), 0.0001 );

        DoubleMatrix2D fitted = fit.getFitted();

        assertEquals( 3.764747, fitted.get( 0, 0 ), 0.0001 );
        assertEquals( 6.043990, fitted.get( 1, 3 ), 0.0001 );
        assertEquals( 10.8333, fitted.get( 7, 2 ), 0.0001 );
        assertEquals( 6.307879, fitted.get( 18, 8 ), 0.0001 );

        List<GenericAnovaResult> anova = fit.anova();

        assertEquals( 19, anova.size() );
    }

    @Test
    public void testLSFTwoLevels() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<>( 9, 1 );

        design.set( 0, 0, "A" );
        design.set( 1, 0, "A" );
        design.set( 2, 0, "A" );
        design.set( 3, 0, "A" );
        design.set( 4, 0, "B" );
        design.set( 5, 0, "B" );
        design.set( 6, 0, "B" );
        design.set( 7, 0, "B" );
        design.set( 8, 0, "B" );
        design.setRowNames( testMatrix.getColNames() );
        design.addColumnName( "Factor" );

        LeastSquaresFit fit = new LeastSquaresFit( design, testMatrix );

        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );

        LinearModelSummary s = sums.get( "1553129_at" );
        assertEquals( 0.182999, s.getFStat(), 0.0001 );
        assertEquals( 0.6817, s.getOverallPValue(), 0.001 );
        assertEquals( 3.84250, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.682, s.getContrastCoefficients().get( 1, 3 ), 0.001 ); // pvalue.

        double[] effects = s.getEffects();
        assertEquals( -11.58333, effects[0], 0.0001 ); // hm.
        assertEquals( 0.04999, effects[1], 0.0001 ); //hm

        double[] stdevUnscaled = s.getStdevUnscaled(); //
        assertEquals( 0.5, stdevUnscaled[0], 0.0001 );
        assertEquals( 0.6708203932, stdevUnscaled[1], 0.0001 );

        double sigma = s.getSigma();
        assertEquals( 0.11673841331, sigma, 0.0001 );

        s = sums.get( "232018_at" );
        assertEquals( -18.9866667, s.getEffects()[0], 0.0001 ); // hm.
        assertEquals( 0.1714319, s.getEffects()[1], 0.0001 ); //hm
        assertEquals( 0.07879, s.getFStat(), 0.01 );
        assertEquals( 0.787, s.getOverallPValue(), 0.001 );
        assertEquals( 6.2650, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.787, s.getContrastCoefficients().get( 1, 3 ), 0.001 );
        sigma = s.getSigma();
        assertEquals( 0.61072556381, sigma, 0.0001 );
    }

    /**
     * Many missing values; Two factors, two levels + interaction.
     */
    @Test
    public void testLSFTwoLevels2() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/lmtest1.dat.manymissing.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass()
                .getResourceAsStream( "/data/lmtest1.des.txt" ) );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        d.addInteraction();

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix );
        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 100, sums.size() );

        for ( Map.Entry<String, LinearModelSummary> e : sums.entrySet() ) {
            String key = e.getKey();
            LinearModelSummary lms = e.getValue();
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
            double interactionEffectP = a.getInteractionEffectPValue();
            // FIXME: this is always non-null, we need assertions about the actual values
            //        assertNotNull( interactionEffectP );
        }
    }

    @Test
    public void testLSFTwoLevels3() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/anova-test-data.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/anova-test-des.txt" ) );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        d.addInteraction( "factor1", "factor2" );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix );
        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 100, sums.size() );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
            double interactionEffectP = a.getInteractionEffectPValue();
            // FIXME: this is always non-null, we need assertions about the actual values
            //        assertNotNull( interactionEffectP );
        }

        LinearModelSummary linearModelSummary3 = sums.get( "probe_4" );
        assertNotNull( linearModelSummary3.getAnova() );
        assertEquals( 0.0048, linearModelSummary3.getAnova().getMainEffectPValue( "factor1" ), 0.0001 );
        LinearModelSummary linearModelSummary2 = sums.get( "probe_10" );
        assertNotNull( linearModelSummary2.getAnova() );
        assertEquals( 5.158e-10, linearModelSummary2.getAnova().getMainEffectPValue( "factor1" ), 1e-12 );
        LinearModelSummary linearModelSummary1 = sums.get( "probe_98" );
        assertNotNull( linearModelSummary1.getAnova() );
        assertEquals( 0.6888, linearModelSummary1.getAnova().getMainEffectPValue( "factor2" ), 1e-4 );
        LinearModelSummary linearModelSummary = sums.get( "probe_10" );
        assertNotNull( linearModelSummary.getAnova() );
        assertEquals( 0.07970, linearModelSummary.getAnova().getMainEffectPValue( "factor2" ), 1e-4 );

    }

    @Test
    public void testLSFTwoLevelsOneContinuous() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<>( 9, 2 );

        design.set( 0, 0, "A" );
        design.set( 1, 0, "A" );
        design.set( 2, 0, "A" );
        design.set( 3, 0, "A" );
        design.set( 4, 0, "B" );
        design.set( 5, 0, "B" );
        design.set( 6, 0, "B" );
        design.set( 7, 0, "B" );
        design.set( 8, 0, "B" );
        design.set( 0, 1, 0.12 );
        design.set( 1, 1, 0.24 );
        design.set( 2, 1, 0.48 );
        design.set( 3, 1, 0.96 );
        design.set( 4, 1, 0.12 );
        design.set( 5, 1, 0.24 );
        design.set( 6, 1, 0.48 );
        design.set( 7, 1, 0.96 );
        design.set( 8, 1, 0.96 );

        design.setRowNames( testMatrix.getColNames() );
        design.addColumnName( "Factor" );
        design.addColumnName( "Value" );

        LeastSquaresFit fit = new LeastSquaresFit( design, testMatrix );

        DoubleMatrix2D coeffs = fit.getCoefficients();
        assertEquals( 3.77868, coeffs.get( 0, 0 ), 0.0001 );
        assertEquals( 0.24476, coeffs.get( 1, 2 ), 0.0001 );
        assertEquals( -0.680449, coeffs.get( 2, 10 ), 0.0001 );
        assertEquals( 0.114084, coeffs.get( 2, 18 ), 0.0001 );

        DoubleMatrix2D fitted = fit.getFitted();

        assertEquals( 3.795698, fitted.get( 0, 0 ), 0.0001 );
        assertEquals( 5.497165, fitted.get( 1, 3 ), 0.0001 );
        assertEquals( 10.879917, fitted.get( 7, 2 ), 0.0001 );
        assertEquals( 6.346546, fitted.get( 18, 8 ), 0.0001 );

        List<GenericAnovaResult> anova = fit.anova();
        assertEquals( 19, anova.size() );
        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );

        LinearModelSummary s = sums.get( "1553129_at" );
        assertEquals( 0.9389, s.getFStat(), 0.01 );
        assertEquals( 0.4418, s.getOverallPValue(), 0.001 );
        assertEquals( 3.77868, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.810, s.getContrastCoefficients().get( 1, 3 ), 0.001 ); // this ordering might change?
        GenericAnovaResult a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.2429, a.getMainEffectPValue( "Value" ), 0.0001 );

        s = sums.get( "232018_at" );
        assertEquals( 0.7167, s.getFStat(), 0.01 );
        assertEquals( 0.5259, s.getOverallPValue(), 0.001 );
        assertEquals( 6.5712, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.664, s.getContrastCoefficients().get( 1, 3 ), 0.001 );
        a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.2893, a.getMainEffectPValue( "Value" ), 0.0001 );

        // 232018_at // based on rstudent() in R.
        DoubleMatrix2D studentizedResiduals = fit.getStudentizedResiduals();
        // log.info( studentizedResiduals.viewRow( 10 ) );
        double[] expectedStudentizedResiduals = new double[] { -0.34655041, 1.46251738, -0.61403124, -0.34663812,
                -1.51245468, 0.06875469, 1.45818880, 1.02811044, -1.31696150 };

        for ( int i = 0; i < 9; i++ ) {
            assertEquals( expectedStudentizedResiduals[i], studentizedResiduals.viewRow( 10 ).get( i ), 0.001 );
        }

        // assertEquals( 1.1, DescriptiveWithMissing.variance( new DoubleArrayList( expectedStudentizedResiduals ) ),
        // 0.1 );

        assertEquals( 1.1,
                DescriptiveWithMissing.variance( new DoubleArrayList( studentizedResiduals.viewRow( 10 ).toArray() ) ),
                0.1 );

        // 1553129_at // based on rstudent() in R.
        studentizedResiduals = fit.getStudentizedResiduals();
        expectedStudentizedResiduals = new double[] { 0.46128657, -5.49429390, 0.84157385, 1.10053286, 1.10538546,
                -0.01706794, -0.05318259, -0.56926585, -0.35107932 };
        for ( int i = 0; i < 9; i++ ) {
            assertEquals( expectedStudentizedResiduals[i], studentizedResiduals.viewRow( 0 ).get( i ), 0.001 );
        }

    }

    /**
     * Weighted least squares test for 2D matrices
     */
    @Test
    public void testMatrixWeightedRegress() {
        DoubleMatrix2D dat = new DenseDoubleMatrix2D( new double[][] { { 1, 2, 3, 4, 5 }, { 1, 1, 6, 3, 2 } } );
        DoubleMatrix2D des = new DenseDoubleMatrix2D( new double[][] { { 1, 1, 1, 1, 1 }, { 1, 2, 2, 3, 3 },
                { 2, 1, 5, 3, 4 } } );

        DoubleMatrix2D w = dat.copy();
        w.assign( dat );
        Algebra solver = new Algebra();
        des = solver.transpose( des );
        LeastSquaresFit fit = new LeastSquaresFit( des, dat, w );

        /*
         * TODO R code please
         */

        // FIXME why is precision of these tests so low? It was 0.1! I changed it to 0.001

        // coefficients
        DoubleMatrix2D actuals = solver.transpose( fit.getCoefficients() );
        double[][] expected = new double[][] { { -1.7070, 1.7110, 0.3054 }, { 0.2092, -0.6642, 1.3640 } };
        for ( int i = 0; i < expected.length; i++ ) {
            assertArrayEquals( expected[i], actuals.viewRow( i ).toArray(), 0.001 );
        }

        // fitted
        actuals = fit.getFitted();
        expected = new double[][] { { 0.6151, 2.0210, 3.2430, 4.3430, 4.6490 }, { 2.273, 0.245, 5.701, 2.309, 3.673 } };
        for ( int i = 0; i < expected.length; i++ ) {
            assertArrayEquals( expected[i], actuals.viewRow( i ).toArray(), 0.001 );
        }

        // residuals
        actuals = fit.getResiduals();
        expected = new double[][] { { 0.38490, -0.02092, -0.24270, -0.34310, 0.35150 },
                { -1.2730, 0.7550, 0.2986, 0.6910, -1.6730 } };
        for ( int i = 0; i < expected.length; i++ ) {
            assertArrayEquals( expected[i], actuals.viewRow( i ).toArray(), 0.001 );
        }

    }

    /**
     * Has a lot of missing values.
     */
    @Test
    public void testOneWayAnova() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/anova-test-data.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<>( 8, 1 );
        for ( int i = 0; i < 8; i++ ) {
            design.set( i, 0, "A" + i % 3 );
        }
        design.addColumnName( "Factor1" );

        DesignMatrix d = new DesignMatrix( design, true );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix );
        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 100, sums.size() );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
            double interactionEffectP = a.getInteractionEffectPValue();
            assertTrue( Double.isNaN( interactionEffectP ) );
        }

        LinearModelSummary sum4 = sums.get( "probe_4" );
        assertNotNull( sum4.getContrastCoefficients() );
        assertEquals( 0.6531, sum4.getOverallPValue(), 0.0001 );
        assertEquals( 0.2735, sum4.getFStat(), 0.0001 );
        assertNotNull( sum4.getAnova() );
        assertEquals( 0.2735, sum4.getAnova().getMainEffectFStat( "Factor1" ), 0.0001 );
        assertEquals( 2, ( int ) sum4.getAnova().getResidualsDof() );
        assertEquals( 1, ( int ) sum4.getAnova().getMainEffectDof( "Factor1" ) );
        assertEquals( 0.6531, sum4.getAnova().getMainEffectPValue( "Factor1" ), 0.0001 );

        LinearModelSummary sum21 = sums.get( "probe_21" );
        assertNotNull( sum21.getContrastCoefficients() );
        assertEquals( 0.6492, sum21.getOverallPValue(), 0.0001 );
        assertEquals( 0.4821, sum21.getFStat(), 0.0001 );
        assertNotNull( sum21.getAnova() );
        assertEquals( 0.4821, sum21.getAnova().getMainEffectFStat( "Factor1" ), 0.0001 );
        assertEquals( 4, ( int ) sum21.getAnova().getResidualsDof() );
        assertEquals( 2, ( int ) sum21.getAnova().getMainEffectDof( "Factor1" ) );
        assertEquals( 0.6492, sum21.getAnova().getMainEffectPValue( "Factor1" ), 0.0001 );

        LinearModelSummary sum98 = sums.get( "probe_98" );
        assertNotNull( sum98.getContrastCoefficients() );
        assertEquals( 0.1604, sum98.getOverallPValue(), 0.0001 );
        assertEquals( 2.993, sum98.getFStat(), 0.0001 );
        assertNotNull( sum98.getAnova() );
        assertEquals( 4, ( int ) sum98.getAnova().getResidualsDof() );
        assertEquals( 2, ( int ) sum98.getAnova().getMainEffectDof( "Factor1" ) );
        assertEquals( 2.9931, sum98.getAnova().getMainEffectFStat( "Factor1" ), 0.0001 );
        assertEquals( 0.1604, sum98.getAnova().getMainEffectPValue( "Factor1" ), 1e-4 );

        LinearModelSummary sum10 = sums.get( "probe_10" );
        assertNotNull( sum10.getContrastCoefficients() );
        assertEquals( 0.8014, sum10.getOverallPValue(), 0.0001 );
        assertEquals( 0.2314, sum10.getFStat(), 0.0001 );
        assertNotNull( sum10.getAnova() );
        assertEquals( 5, ( int ) sum10.getAnova().getResidualsDof() );
        assertEquals( 2, ( int ) sum10.getAnova().getMainEffectDof( "Factor1" ) );
        assertEquals( 0.8014, sum10.getAnova().getMainEffectPValue( "Factor1" ), 1e-4 );

        /*
         * Painful case follows. Not full rank due to missing values:
         */
        LinearModelSummary sum60 = sums.get( "probe_60" );
        assertNotNull( sum60.getContrastCoefficients() );

        /*
         * See lmtests.R
         *
         * Note that using lmFit and lm give different results because (apparently) of the difference in strategy
         * dealing
         * with missing values. In lm, the factors are subset; in lmFit, the design matrix is subset. We do the latter.
         *
         * lmFit(dat, model.matrix(~ factor3))
         *
         * vs
         *
         * lm(t(dat["probe_60",]) ~ factor3)
         */
        assertEquals( 9.00145, sum60.getContrastCoefficients().get( 0, 0 ), 0.0001 ); // same as lmFit; R lm gives 8.9913; lm.fit gives tehse values.
        assertEquals( -0.01020, sum60.getContrastCoefficients().get( 1, 0 ), 0.0001 ); // same as lmFit; R lm gives +0.0102
        assertEquals( Double.NaN, sum60.getContrastCoefficients().get( 2, 0 ), 0.0001 ); // good ...

        /*
         * This is the value of R-squared from lm(). lmFit doesn't give us this, but...
         */
        //   assertEquals( -0.4996, sum60.getAdjRSquared(), 1e-5 );

        // using results of lm()
        // anova(object)
        //        Analysis of Variance Table
        //
        //        Response: t(dat["probe_60", ])
        //                  Df  Sum Sq  Mean Sq F value Pr(>F)
        //        factor3    1 0.00010 0.000104   5e-04 0.9846 <- not quite
        //        Residuals  2 0.44135 0.220675   <- we get this

        assertEquals( 2, ( int ) sum60.getResidualsDof() );
        assertEquals( 1, ( int ) sum60.getNumeratorDof() );

        assertNotNull( sum60.getAnova() );
        assertEquals( 2, ( int ) sum60.getAnova().getResidualsDof() );
        assertEquals( 1, ( int ) sum60.getAnova().getMainEffectDof( "Factor1" ) );

        // The value lm gives is 0.004715; for limma, topTableF gives NA and no pvalue.
        // However, for this case, this is actually the t-statistic (two groups) and the right value is 0.022 (from lm)
        assertEquals( 0.0004715, sum60.getFStat(), 1e-7 ); // on 1 and 2 dof,  p-value: 0.9846
        assertEquals( 0.9846482, sum60.getOverallPValue(), 1e-5 );

        //
        assertEquals( 0.9846482, sum60.getAnova().getMainEffectPValue( "Factor1" ), 1e-5 );

        // Result for topTable(eBayes(fit), coef=2)
        //                logFC    AveExpr            t      P.Value   adj.P.Val          B
        // probe_60  -0.01020000   8.996350  -0.02338077 9.831252e-01 0.983125229 -7.4711642

        // topTableF(eBayes(o2), number = 100)
        //          X.Intercept.     factor3v factor3w_base    AveExpr            F      P.Value    adj.P.Val
        // probe_60     9.001450  -0.01020000            NA   8.996350           NA           NA           NA

        // Using lm(formula = t(dat["probe_60", ]) ~ factor3)

        //        Call:
        //            lm(formula = t(dat["probe_60", ]) ~ factor3)
        //
        //            Residuals:
        //            X0b.bioassay.0b. X1a.bioassay.1a. X2a.bioassay.2a. X2b.bioassay.2b.
        //                     -0.1209          -0.4539           0.1209           0.4539
        //
        //            Coefficients:
        //                          Estimate Std. Error t value Pr(>|t|)
        //            (Intercept)     8.9913     0.3322  27.068  0.00136 **
        //            factor3w_base   0.0102     0.4698   0.022  0.98465
        //            ---
        //            Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1
        //
        //            Residual standard error: 0.4698 on 2 degrees of freedom
        //              (4 observations deleted due to missingness)
        //            Multiple R-squared:  0.0002357, Adjusted R-squared:  -0.4996
        //            F-statistic: 0.0004715 on 1 and 2 DF,  p-value: 0.9846

        // results of using      lm.fit(X,y)
        /// > X
        //        (Intercept) factor3v factor3w_base
        //        2           1        1             0
        //        3           1        0             1
        //        5           1        1             0
        //        6           1        0             1
        //        > y
        //        [1] 8.8704 8.5475 9.1121 9.4554
        //        > lm.fit(X,y)
        //        $coefficients
        //          (Intercept)      factor3v factor3w_base
        //              9.00145      -0.01020            NA
        //
        //        $residuals
        //        [1] -0.12085 -0.45395  0.12085  0.45395
        //
        //        $effects
        //        (Intercept)    factor3v
        //           -17.9927     -0.0102      0.0784      0.6597
        //
        //        $rank
        //        [1] 2
        //
        //        $fitted.values
        //        [1] 8.99125 9.00145 8.99125 9.00145
        //
        //        $assign
        //        NULL
        //
        //        $qr
        //        $qr
        //          (Intercept)   factor3v factor3w_base
        //        2        -2.0 -1.0000000 -1.000000e+00
        //        3         0.5  1.0000000 -1.000000e+00
        //        5         0.5 -0.3333333 -1.110223e-16
        //        6         0.5  0.6666667  0.000000e+00
        //
        //        $qraux
        //        [1] 1.500000 1.666667 2.000000
        //
        //        $pivot
        //        [1] 1 2 3
        //
        //        $tol
        //        [1] 1e-07
        //
        //        $rank
        //        [1] 2
        //
        //        attr(,"class")
        //        [1] "qr"
        //
        //        $df.residual
        //        [1] 2

    }

    @Test
    public void testSingular() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f
                .read( this.getClass().getResourceAsStream( "/data/lmtest2.dat.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass()
                .getResourceAsStream( "/data/lmtest2.des.txt" ) );
        DesignMatrix d = new DesignMatrix( sampleInfo, true );

        assertEquals( 9, d.getMatrix().columns() );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix );

        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 81, sums.size() );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
        }

        LinearModelSummary s = sums.get( "A01157cds_s_at" );

        assertNotNull( s.getContrastCoefficients() );

        assertEquals( 7.3740000, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        // assertEquals( 0.1147667, s.getCoefficients().get( 1, 3 ), 0.001 );
        assertEquals( 6, ( int ) s.getResidualsDof() );
        assertEquals( 7, ( int ) s.getNumeratorDof() );
        assertEquals( 0.8634, s.getFStat(), 0.01 );
        assertEquals( 0.5795, s.getOverallPValue(), 0.001 );

    }

    /**
     * Originally causes failures during summarization step. There are two pivoted columns.
     */
    @Test
    public void testSingular2() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/1027_GSE6189.data.test.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/1027_GSE6189_expdesign.data.txt" ) );
        DesignMatrix d = new DesignMatrix( sampleInfo, true );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix.getRowRange( 0, 0 ) );

        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 1, sums.size() );
        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
        }
        LinearModelSummary s = sums.get( "1367452_at" );
        assertNotNull( s );
        assertNotNull( s.getContrastCoefficients() );

        // log.info( s.getCoefficients() );

        // log.info( s.getAnova() );

        // our model matrix ends up with different coefficients than R which are
        // double[] rcoef = new double[] { 14.96355, 0.14421, -0.11525, 0.24257, Double.NaN, 0.04093, 0.06660,
        // Double.NaN };

        // here are the coefs we get in R if we use the exact model matrix we get drom our DesignMatrix
        double[] coef = new double[] { 15.10776244, -0.01689300, 0.09835841, -0.20163964, Double.NaN, -0.04092962,
                Double.NaN, 0.06660370 };

        for ( int i = 0; i < s.getContrastCoefficients().rows(); i++ ) {
            assertEquals( coef[i], s.getContrastCoefficients().get( i, 0 ), 0.0001 );
        }

    }

    @Test
    public void testThreeWaySingular() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/1064_GSE7863.data.test.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/1064_GSE7863_expdesign.data.test.txt" ) );
        DesignMatrix d = new DesignMatrix( sampleInfo, true );

        assertEquals( 5, d.getMatrix().columns() );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix );

        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 416, sums.size() );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
        }

        LinearModelSummary s = sums.get( "1415696_at" );

        assertNotNull( s.getContrastCoefficients() );
        assertEquals( 0.000794, s.getContrastCoefficients().get( 2, 3 ), 0.001 );
        assertEquals( 11, ( int ) s.getResidualsDof() );
        assertEquals( 4, ( int ) s.getNumeratorDof() );
        assertEquals( 24.38, s.getFStat(), 0.01 );
        assertEquals( 2.025e-05, s.getOverallPValue(), 0.001 );
        GenericAnovaResult anova = s.getAnova();
        assertNotNull( anova );
        assertEquals( 29.0386, anova.getMainEffectFStat( "Treatment" ), 0.0001 );

        s = sums.get( "1415837_at" );
        assertNotNull( s.getContrastCoefficients() );
        assertEquals( 11, ( int ) s.getResidualsDof() );
        assertEquals( 4, ( int ) s.getNumeratorDof() );
        assertEquals( 22.72, s.getFStat(), 0.01 );
        assertEquals( 2.847e-05, s.getOverallPValue(), 0.001 );
        anova = s.getAnova();
        assertNotNull( anova );
        assertEquals( 6.5977, anova.getMainEffectFStat( "Treatment" ), 0.0001 );

        s = sums.get( "1416179_a_at" );
        assertNotNull( s.getContrastCoefficients() );
        assertEquals( 11, ( int ) s.getResidualsDof() );
        assertEquals( 4, ( int ) s.getNumeratorDof() );
        assertEquals( 25.14, s.getFStat(), 0.01 );
        assertEquals( 1.743e-05, s.getOverallPValue(), 0.001 );
        anova = s.getAnova();
        assertNotNull( anova );
        assertEquals( 38.411, anova.getMainEffectFStat( "Treatment" ), 0.001 );

        s = sums.get( "1456759_at" );
        assertNotNull( s.getContrastCoefficients() );
        assertEquals( 11, ( int ) s.getResidualsDof() );
        assertEquals( 4, ( int ) s.getNumeratorDof() );
        assertEquals( 7.903, s.getFStat(), 0.01 );
        assertEquals( 0.002960, s.getOverallPValue(), 0.001 );
        anova = s.getAnova();
        assertNotNull( anova );
        assertEquals( 10.3792, anova.getMainEffectFStat( "Treatment" ), 0.001 );
        assertEquals( 2.6253, anova.getMainEffectFStat( "Genotype" ), 0.001 );
    }

    /**
     * Sanity check.
     */
    @Test
    public void testTwoWayAnovaUnfittable() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass()
                .getResourceAsStream( "/data/lmtest10.dat.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/lmtest10.des.txt" ) );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix );
        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 1, sums.size() );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
            assertEquals( Double.NaN, a.getMainEffectPValue( "CellType" ), 0.0001 );
            assertEquals( Double.NaN, a.getMainEffectPValue( "SamplingTimePoint" ), 0.0001 );
        }

        DoubleMatrix2D coefficients = fit.getCoefficients();
        DoubleMatrix2D residuals = fit.getResiduals();

        assertEquals( 2.238, coefficients.get( 0, 0 ), 0.0001 ); // mean.
        assertEquals( 0.0, coefficients.get( 1, 0 ), 0.0001 );

        for ( int i = 0; i < residuals.rows(); i++ ) {
            assertEquals( 0.0, residuals.get( 0, i ), 0.00001 );
        }
    }

    /**
     * Check for problem reported by TF -- Gemma gives slightly different result. Problem is not at this level.
     */
    @Test
    public void testTwoWayAnovaWithInteractions() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/GSE8441_expmat_8probes.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/606_GSE8441_expdesign.data.txt" ) );
        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        d.addInteraction();

        assertEquals( 4, d.getMatrix().columns() );

        assertEquals( 22, testMatrix.columns() );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix );

        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 8, sums.size() );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
        }

        LinearModelSummary s = sums.get( "217757_at" );
        GenericAnovaResult anova = s.getAnova();

        assertNotNull( s.getContrastCoefficients() );
        assertEquals( 0.763, s.getContrastCoefficients().get( 2, 3 ), 0.001 );
        assertEquals( 18, ( int ) s.getResidualsDof() );
        assertEquals( 3, ( int ) s.getNumeratorDof() );
        assertEquals( 0.299, s.getFStat(), 0.01 );
        assertEquals( 0.8257, s.getOverallPValue(), 0.001 );

        assertNotNull( anova );
        assertEquals( 0.5876, anova.getMainEffectFStat( "Treatment" ), 0.0001 );
        assertEquals( 0.5925, anova.getInteractionEffectPValue(), 0.001 );

        s = sums.get( "202851_at" );
        anova = s.getAnova();
        assertNotNull( s.getContrastCoefficients() );
        assertEquals( 0.787, s.getContrastCoefficients().get( 2, 3 ), 0.001 );
        assertEquals( 18, ( int ) s.getResidualsDof() );
        assertEquals( 3, ( int ) s.getNumeratorDof() );
        assertEquals( 0.1773, s.getFStat(), 0.01 );
        assertEquals( 0.9104, s.getOverallPValue(), 0.001 );

        assertNotNull( anova );
        assertEquals( 0.3777, anova.getMainEffectFStat( "Treatment" ), 0.0001 );
        assertEquals( 0.9956, anova.getInteractionEffectPValue(), 0.001 );
    }

    /**
     * No missing values; Two-way ANOVA with interaction, PLUS a continuous covariate.
     */
    @Test
    public void testTwoWayTwoLevelsOneContinousInteractionC() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<>( 9, 3 );

        design.set( 0, 0, "A" );
        design.set( 1, 0, "A" );
        design.set( 2, 0, "A" );
        design.set( 3, 0, "A" );
        design.set( 4, 0, "B" );
        design.set( 5, 0, "B" );
        design.set( 6, 0, "B" );
        design.set( 7, 0, "B" );
        design.set( 8, 0, "B" );
        design.set( 0, 1, 0.12 );
        design.set( 1, 1, 0.24 );
        design.set( 2, 1, 0.48 );
        design.set( 3, 1, 0.96 );
        design.set( 4, 1, 0.12 );
        design.set( 5, 1, 0.24 );
        design.set( 6, 1, 0.48 );
        design.set( 7, 1, 0.96 );
        design.set( 8, 1, 0.96 );
        design.set( 0, 2, "C" );
        design.set( 1, 2, "C" );
        design.set( 2, 2, "D" );
        design.set( 3, 2, "D" );
        design.set( 4, 2, "C" );
        design.set( 5, 2, "C" );
        design.set( 6, 2, "D" );
        design.set( 7, 2, "D" );
        design.set( 8, 2, "D" );
        design.addColumnName( "Treat" );
        design.addColumnName( "Value" );
        design.addColumnName( "Geno" );

        DesignMatrix designMatrix = new DesignMatrix( design, true );
        designMatrix.addInteraction( "Treat", "Geno" );
        LeastSquaresFit fit = new LeastSquaresFit( designMatrix, testMatrix );

        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );

        LinearModelSummary s = sums.get( "1553129_at" );
        assertEquals( 1.791, s.getFStat(), 0.01 );
        assertEquals( 0.2930, s.getOverallPValue(), 0.001 );
        assertEquals( 3.71542, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.184, s.getContrastCoefficients().get( 1, 3 ), 0.001 ); // this ordering might change?
        assertEquals( 0.137, s.getContrastCoefficients().get( 4, 3 ), 0.001 ); // this ordering might change?
        GenericAnovaResult a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.137, a.getInteractionEffectPValue(), 0.001 );

        s = sums.get( "232018_at" );
        assertEquals( 0.7167, s.getFStat(), 0.01 );
        assertEquals( 0.6235, s.getOverallPValue(), 0.001 );
        assertEquals( 6.8873, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.587932, s.getContrastCoefficients().get( 1, 3 ), 0.001 );
        a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.2904, a.getInteractionEffectPValue(), 0.001 );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult anova = lms.getAnova();
            assertNotNull( anova );
            double interactionEffectP = anova.getInteractionEffectPValue();
            assertFalse( Double.isNaN( interactionEffectP ) );

        }

    }

    @Test
    public void testVectorRegress() {

        DoubleMatrix1D vectorA = new DenseDoubleMatrix1D( new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 } );
        DoubleMatrix1D vectorB = new DenseDoubleMatrix1D( new double[] { 1, 2, 2, 3, 3, 4, 4, 5, 5, 6 } );

        LeastSquaresFit fit = new LeastSquaresFit( vectorA, vectorB );

        DoubleMatrix2D coefficients = fit.getCoefficients();
        DoubleMatrix2D residuals = fit.getResiduals();

        assertEquals( 0.666666, coefficients.get( 0, 0 ), 0.0001 );
        assertEquals( 0.5152, coefficients.get( 1, 0 ), 0.0001 );

        double[] expectedResiduals = new double[] { -0.1818182, 0.3030303, -0.2121212, 0.2727273, -0.2424242,
                0.2424242, -0.2727273, 0.2121212, -0.3030303, 0.1818182 };

        for ( int i = 0; i < expectedResiduals.length; i++ ) {
            assertEquals( expectedResiduals[i], residuals.get( 0, i ), 0.00001 );
        }

    }

    @Test
    public void testVectorWeightedRegress() {
        // a<-c( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 )
        //  b<-c(1, 2, 2, 3, 3, 4, 4, 5, 5, 6)
        //  w<-1/a
        DoubleMatrix1D vectorA = new DenseDoubleMatrix1D( new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 } );
        DoubleMatrix1D vectorB = new DenseDoubleMatrix1D( new double[] { 1, 2, 2, 3, 3, 4, 4, 5, 5, 6 } );
        DoubleMatrix1D w = vectorA.copy().assign( Functions.inv );

        LeastSquaresFit fit = new LeastSquaresFit( vectorA, vectorB, w );

        DoubleMatrix2D coefficients = fit.getCoefficients();
        DoubleMatrix2D residuals = fit.getResiduals();
        //lm(b~a, weights=w)
        //lm.wfit(model.matrix(~a, factor(a)), b, w)
        assertEquals( 0.60469, coefficients.get( 0, 0 ), 0.0001 );
        assertEquals( 0.52642, coefficients.get( 1, 0 ), 0.0001 );

        double[] expectedResiduals = new double[] { -0.1311097, 0.3424702, -0.1839499, 0.2896301, -0.2367900,
                0.2367900, -0.2896301, 0.1839499, -0.3424702, 0.1311097 };

        for ( int i = 0; i < expectedResiduals.length; i++ ) {
            assertEquals( expectedResiduals[i], residuals.get( 0, i ), 0.00001 );
        }

        double[] expectedFitted = new double[] { 1.13111, 1.65753, 2.18395, 2.71037, 3.23679, 3.76321, 4.28963,
                4.81605, 5.34247, 5.86889 };

        for ( int i = 0; i < expectedFitted.length; i++ ) {
            assertEquals( expectedFitted[i], fit.getFitted().get( 0, i ), 0.00001 );
        }
    }

    @Test
    public void testVectorWeightedRegressWithMissing() throws Exception {

        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt" ) );
        DoubleMatrix1D libSize = MatrixStats.colSums( testMatrix );
        testMatrix = MatrixStats.convertToLog2Cpm( testMatrix, libSize );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/example.metadata.small.txt" ) );
        DesignMatrix designMatrix = new DesignMatrix( sampleInfo );
        DoubleMatrix2D weights = new DenseDoubleMatrix2D( testMatrix.asArray() );
        weights.assign( Functions.inv );

        LeastSquaresFit fit = new LeastSquaresFit( designMatrix, testMatrix, weights );
        assertTrue( fit.isHasMissing() );

        DoubleMatrix2D coefficients = fit.getCoefficients();
        DoubleMatrix2D residuals = fit.getResiduals();

        assertEquals( 15.339801, coefficients.get( 0, 0 ), 0.0001 );
        assertEquals( -0.024058, coefficients.get( 1, 1 ), 0.0001 );
        assertEquals( -0.059586, coefficients.get( 2, 18 ), 0.0001 );

        assertEquals( -0.073732, residuals.get( 0, 0 ), 0.0001 );
        assertEquals( -0.064656, residuals.get( 1, 1 ), 0.0001 );
        assertEquals( -0.085214, residuals.get( 18, 8 ), 0.0001 );
        assertTrue( Double.isNaN( residuals.get( 4, 2 ) ) );

    }

    /**
     * Tests limma-like functionality
     * <p>
     * Multiple levels per factor, unbalanced design
     */
    @Test
    public void testNHBE() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream(
            "/data/NHBE_transcriptome_data.txt.gz" ) ) ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/NHBE_design.txt" ) );

        DesignMatrix designMatrix = new DesignMatrix( sampleInfo );
        designMatrix.addInteraction();
        designMatrix.setBaseline( "time", "1_h" );
        designMatrix.setBaseline( "Treatment", "control" );

        LeastSquaresFit fit = new LeastSquaresFit( designMatrix, testMatrix );

        //  System.err.println( designMatrix );
        //   List<LinearModelSummary> sums = fit.summarize( true );

        ModeratedTstat.ebayes( fit );

        /////////////
        //  System.err.println( "------- After ebayes ------" );
        List<LinearModelSummary> sums = fit.summarize( true );

        // fit3$sigma[1]
        assertEquals( 0.34927, sums.get( 0 ).getSigma(), 0.0001 );
        assertEquals( 1.3859, sums.get( 0 ).getPriorDof(), 0.01 );

        /*
         * TODO: add more tests here.
         */
    }

    /**
     * Tests limma-like functionality. Balanced 2x2 design
     */
    @Test
    public void testEstrogen() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( new GZIPInputStream( requireNonNull( this.getClass().getResourceAsStream(
            "/data/estrogen.data.txt.gz" ) ) ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/estrogen.meta.txt" ) );

        DesignMatrix designMatrix = new DesignMatrix( sampleInfo );
        designMatrix.addInteraction();
        LeastSquaresFit fit = new LeastSquaresFit( designMatrix, testMatrix );

        //  System.err.println( designMatrix );
        List<LinearModelSummary> sums = fit.summarize( true );
        //  System.err.println( fit.getCoefficients().viewColumn( 0 ) );

        LinearModelSummary s = sums.get( 0 );
        assertEquals( 3.8976, s.getFStat(), 0.01 );
        assertEquals( 0.11092, s.getOverallPValue(), 0.001 );
        assertEquals( 9.69220, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( -1.4517, s.getContrastCoefficients().get( 1, 2 ), 0.001 ); // tstat
        assertEquals( 0.220, s.getContrastCoefficients().get( 1, 3 ), 0.001 ); // pvalue
        assertEquals( 4, ( int ) s.getResidualsDof() );
        GenericAnovaResult a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.24143, a.getMainEffectFStat( "time" ), 0.0001 );
        assertEquals( 1, ( int ) a.getMainEffectDof( "time" ) );
        assertEquals( 4, ( int ) a.getResidualsDof() );
        assertEquals( 2.43873, a.getInteractionEffectFStat(), 0.001 );
        assertEquals( 0.19340, a.getInteractionEffectPValue(), 0.001 );

        /////////////
        ModeratedTstat.ebayes( fit );
        sums = fit.summarize( true );
        LinearModelSummary x = sums.get( 0 );

        assertEquals( 0.0765, x.getSigma(), 0.0001 );
        assertEquals( 4.48, x.getPriorDof(), 0.01 ); // we get 4.479999 or something.

        assertEquals( 3.8976, x.getFStat(), 0.01 );
        assertEquals( 0.11092, x.getOverallPValue(), 0.001 );
        // topTable(fit3, coef=2,n=dim(fit3)[1], sort.by="none" )["100_g_at",]
        assertEquals( 9.692196, x.getContrastCoefficients().get( 0, 0 ), 0.0001 );
        assertEquals( -0.92608, x.getContrastCoefficients().get( 1, 2 ), 0.001 ); // tstat
        assertEquals( 0.38, x.getContrastCoefficients().get( 1, 3 ), 0.001 ); // pvalue
        assertEquals( 4, ( int ) x.getResidualsDof() );
        // topTable(fit3, coef=4,n=dim(fit3)[1], sort.by="none" )["100_g_at",]
        assertEquals( 0.34671, x.getContrastCoefficients().get( 3, 3 ), 0.0001 ); // interaction pvalue

        GenericAnovaResult ax = x.getAnova();
        // classifyTestsF(fit3$t[,c(2)], df=fit3$df.residual, cor.matrix=cov2cor(fit3$cov.coefficients[2,2]), fstat.only = T)[1]
        assertNotNull( ax );
        assertEquals( 0.098252, ax.getMainEffectFStat( "time" ), 0.0001 );
        assertEquals( 1, ( int ) ax.getMainEffectDof( "time" ) );
        assertEquals( 8.48, ax.getResidualsDof(), 0.001 );

        assertEquals( 3.6678, ax.getMainEffectFStat( "dose" ), 0.0001 );
        assertEquals( 1, ( int ) ax.getMainEffectDof( "dose" ) );
        assertEquals( 8.48, ax.getResidualsDof(), 0.001 ); // 4 + 4.48

        // sum(f4^2)/1/sqig^2
        // pf(0.99247, 1, 8.48, lower.tail=F)
        assertEquals( 0.99247, ax.getInteractionEffectFStat(), 0.0001 );
        assertEquals( 0.34671, ax.getInteractionEffectPValue(), 0.0001 );

    }
}