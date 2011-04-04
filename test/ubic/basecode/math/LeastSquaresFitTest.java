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

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrixImpl;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import ubic.basecode.util.r.type.GenericAnovaResult;
import ubic.basecode.util.r.type.LinearModelSummary;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * @author paul
 * @version $Id$
 */
public class LeastSquaresFitTest {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog( LeastSquaresFitTest.class );

    /**
     * @throws Exception
     */
    @Test
    public void testLSFOneContinuousWithMissing3() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 9, 1 );

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
        assertEquals( 0.1495, s.getF(), 0.01 );
        assertEquals( 0.7123, s.getP(), 0.001 );
        assertEquals( 10.9180, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.712, s.getCoefficients().get( 1, 3 ), 0.001 );
        assertEquals( 6, s.getResidualDof().intValue() );
        GenericAnovaResult a = s.getAnova();
        assertEquals( 0.1495, a.getMainEffectF( "Value" ), 0.0001 );
        assertEquals( 1, a.getMainEffectDof( "Value" ).intValue() );
        assertEquals( 6, a.getResidualDf().intValue() );

        FDistribution fd = new FDistributionImpl( 1, 6 );
        double p = 1.0 - fd.cumulativeProbability( 0.1495 );
        assertEquals( 0.7123, p, 0.0001 );
        assertEquals( 0.7123, a.getMainEffectP( "Value" ), 0.0001 );

        s = sums.get( "1553129_at" );
        assertNotNull( s );
        assertEquals( 2.095, s.getF(), 0.01 );
        assertEquals( 0.1911, s.getP(), 0.001 );
        assertEquals( 3.78719, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.191, s.getCoefficients().get( 1, 3 ), 0.001 ); // this ordering might change?
        a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.1911, a.getMainEffectP( "Value" ), 0.0001 );

        s = fit.summarize( 14 );
        assertNotNull( s );
        assertEquals( "214502_at", s.getKey() );// has missing
        assertEquals( 1.992, s.getF(), 0.01 );
        assertEquals( 0.2172, s.getP(), 0.001 );
        assertEquals( 4.2871, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.217, s.getCoefficients().get( 1, 3 ), 0.001 );

        s = sums.get( "232018_at" );
        assertNotNull( s );
        assertEquals( 1.381, s.getF(), 0.01 );
        assertEquals( 0.2783, s.getP(), 0.001 );
        assertEquals( 6.6537, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.278, s.getCoefficients().get( 1, 3 ), 0.001 );
        a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.2783, a.getMainEffectP( "Value" ), 0.0001 );
        s = sums.get( "228980_at" ); // has missing
        assertNotNull( s );
        assertEquals( 0.1495, s.getF(), 0.01 );
        assertEquals( 0.7123, s.getP(), 0.001 );
        assertEquals( 10.9180, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.712, s.getCoefficients().get( 1, 3 ), 0.001 );
        a = s.getAnova();
        assertNotNull( a );
        assertEquals( 0.7123, a.getMainEffectP( "Value" ), 0.0001 );
    }

    /**
     * @throws Exception
     */
    @Test
    public void testLSFThreeLevelsOnecontinous2() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 9, 2 );

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

    /**
     * @throws Exception
     */
    @Test
    public void testLSFThreeLevelsOneContinuousWithMissing3() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.withmissing.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 9, 2 );

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

    /**
     * @throws Exception
     */
    @Test
    public void testLSFTwoLevels() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 9, 1 );

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
        assertEquals( 0.183, s.getF(), 0.01 );
        assertEquals( 0.6817, s.getP(), 0.001 );
        assertEquals( 3.84250, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.682, s.getCoefficients().get( 1, 3 ), 0.001 );

        s = sums.get( "232018_at" );
        assertEquals( 0.07879, s.getF(), 0.01 );
        assertEquals( 0.787, s.getP(), 0.001 );
        assertEquals( 6.2650, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.787, s.getCoefficients().get( 1, 3 ), 0.001 );
    }

    /**
     * Many missing values; Two factors, two levels + interaction.
     * 
     * @throws Exception
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

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
            Double interactionEffectP = a.getInteractionEffectP();
            assertNotNull( interactionEffectP );
        }

    }

    /**
     * @throws Exception
     */
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
            Double interactionEffectP = a.getInteractionEffectP();
            assertNotNull( interactionEffectP );
        }

        assertEquals( 0.0048, sums.get( "probe_4" ).getMainEffectP( "factor1" ), 0.0001 );
        assertEquals( 5.158e-10, sums.get( "probe_10" ).getMainEffectP( "factor1" ), 1e-12 );
        assertEquals( 0.6888, sums.get( "probe_98" ).getMainEffectP( "factor2" ), 1e-4 );
        assertEquals( 0.07970, sums.get( "probe_10" ).getMainEffectP( "factor2" ), 1e-4 );

    }

    /**
     * @throws Exception
     */
    @Test
    public void testLSFTwoLevelsOneContinuous() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 9, 2 );

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
        assertEquals( 0.9389, s.getF(), 0.01 );
        assertEquals( 0.4418, s.getP(), 0.001 );
        assertEquals( 3.77868, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.810, s.getCoefficients().get( 1, 3 ), 0.001 ); // this ordering might change?
        GenericAnovaResult a = s.getAnova();
        assertEquals( 0.2429, a.getMainEffectP( "Value" ), 0.0001 );

        s = sums.get( "232018_at" );
        assertEquals( 0.7167, s.getF(), 0.01 );
        assertEquals( 0.5259, s.getP(), 0.001 );
        assertEquals( 6.5712, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.664, s.getCoefficients().get( 1, 3 ), 0.001 );
        a = s.getAnova();
        assertEquals( 0.2893, a.getMainEffectP( "Value" ), 0.0001 );

    }

    /**
     * Has a lot of missing values.
     * 
     * @throws Exception
     */
    @Test
    public void testOneWayAnova() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/anova-test-data.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 8, 1 );
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
            Double interactionEffectP = a.getInteractionEffectP();
            assertNull( interactionEffectP );
        }

        LinearModelSummary sum4 = sums.get( "probe_4" );
        assertNotNull( sum4.getCoefficients() );
        assertEquals( 0.6531, sum4.getP(), 0.0001 );
        assertEquals( 0.2735, sum4.getF(), 0.0001 );
        assertEquals( 0.2735, sum4.getAnova().getMainEffectF( "Factor1" ), 0.0001 );
        assertEquals( 2, sum4.getAnova().getResidualDf().intValue() );
        assertEquals( 1, sum4.getAnova().getMainEffectDof( "Factor1" ).intValue() );
        assertEquals( 0.6531, sum4.getMainEffectP( "Factor1" ), 0.0001 );

        LinearModelSummary sum21 = sums.get( "probe_21" );
        assertNotNull( sum21.getCoefficients() );
        assertEquals( 0.6492, sum21.getP(), 0.0001 );
        assertEquals( 0.4821, sum21.getF(), 0.0001 );
        assertEquals( 0.4821, sum21.getAnova().getMainEffectF( "Factor1" ), 0.0001 );
        assertEquals( 4, sum21.getAnova().getResidualDf().intValue() );
        assertEquals( 2, sum21.getAnova().getMainEffectDof( "Factor1" ).intValue() );
        assertEquals( 0.6492, sum21.getMainEffectP( "Factor1" ), 0.0001 );

        LinearModelSummary sum98 = sums.get( "probe_98" );
        assertNotNull( sum98.getCoefficients() );
        assertEquals( 0.1604, sum98.getP(), 0.0001 );
        assertEquals( 2.993, sum98.getF(), 0.0001 );
        assertEquals( 4, sum98.getAnova().getResidualDf().intValue() );
        assertEquals( 2, sum98.getAnova().getMainEffectDof( "Factor1" ).intValue() );
        assertEquals( 2.9931, sum98.getAnova().getMainEffectF( "Factor1" ).doubleValue(), 0.0001 );
        assertEquals( 0.1604, sum98.getMainEffectP( "Factor1" ), 1e-4 );

        LinearModelSummary sum10 = sums.get( "probe_10" );
        assertNotNull( sum10.getCoefficients() );
        assertEquals( 0.8014, sum10.getP(), 0.0001 );
        assertEquals( 0.2314, sum10.getF(), 0.0001 );
        assertEquals( 5, sum10.getAnova().getResidualDf().intValue() );
        assertEquals( 2, sum10.getAnova().getMainEffectDof( "Factor1" ).intValue() );
        assertEquals( 0.8014, sum10.getMainEffectP( "Factor1" ), 1e-4 );

        LinearModelSummary sum60 = sums.get( "probe_60" );
        assertNotNull( sum60.getCoefficients() );
        assertEquals( 9.001, sum60.getCoefficients().get( 0, 0 ), 0.01 ); // R gives 8.9913
        assertEquals( -0.0102, sum60.getCoefficients().get( 1, 0 ), 0.0001 ); // R gives +0.0102
        assertEquals( 2, sum60.getAnova().getResidualDf().intValue() );
        assertEquals( 1, sum60.getAnova().getMainEffectDof( "Factor1" ).intValue() );
        assertEquals( 0.0004715, sum60.getF(), 1e-7 );
        assertEquals( 0.98465, sum60.getP(), 1e-5 );
        assertEquals( 0.98465, sum60.getMainEffectP( "Factor1" ), 1e-5 );

    }

    /**
     * @throws Exception
     */
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

        assertNotNull( s.getCoefficients() );

        assertEquals( 7.3740000, s.getCoefficients().get( 0, 0 ), 0.001 );
        // assertEquals( 0.1147667, s.getCoefficients().get( 1, 3 ), 0.001 );
        assertEquals( 6, s.getResidualDof().intValue() );
        assertEquals( 7, s.getNumeratorDof().intValue() );
        assertEquals( 0.8634, s.getF(), 0.01 );
        assertEquals( 0.5795, s.getP(), 0.001 );

    }

    /**
     * Originally causes failures during summarization step. There are two pivoted columns.
     * 
     * @throws Exception
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

        log.info( d );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix.getRowRange( 0, 0 ) );

        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 1, sums.size() );
        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
        }
        LinearModelSummary s = sums.get( "1367452_at" );
        assertNotNull( s );
        assertNotNull( s.getCoefficients() );

        // log.info( s.getCoefficients() );

        // log.info( s.getAnova() );

        // our model matrix ends up with different coefficients than R which are:
        double[] rcoef = new double[] { 14.96355, 0.14421, -0.11525, 0.24257, Double.NaN, 0.04093, 0.06660, Double.NaN };

        // here are the coefs we get in R if we use the exact model matrix we get drom our DesignMatrix
        double[] coef = new double[] { 15.10776244, -0.01689300, 0.09835841, -0.20163964, Double.NaN, -0.04092962,
                Double.NaN, 0.06660370 };

        for ( int i = 0; i < s.getCoefficients().rows(); i++ ) {
            assertEquals( coef[i], s.getCoefficients().get( i, 0 ), 0.0001 );
        }

    }

    /**
     * No missing values; Two-way ANOVA with interaction, PLUS a continuous covariate.
     * 
     * @throws Exception
     */
    @Test
    public void testTwoWayTwoLevelsOneContinousInteractionC() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/example.madata.small.txt" ) );

        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 9, 3 );

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
        assertEquals( 1.791, s.getF(), 0.01 );
        assertEquals( 0.2930, s.getP(), 0.001 );
        assertEquals( 3.71542, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.184, s.getCoefficients().get( 1, 3 ), 0.001 ); // this ordering might change?
        assertEquals( 0.137, s.getCoefficients().get( 4, 3 ), 0.001 ); // this ordering might change?
        GenericAnovaResult a = s.getAnova();
        assertEquals( 0.137, a.getInteractionEffectP(), 0.001 );

        s = sums.get( "232018_at" );
        assertEquals( 0.7167, s.getF(), 0.01 );
        assertEquals( 0.6235, s.getP(), 0.001 );
        assertEquals( 6.8873, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.587932, s.getCoefficients().get( 1, 3 ), 0.001 );
        a = s.getAnova();
        assertEquals( 0.2904, a.getInteractionEffectP(), 0.001 );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult anova = lms.getAnova();
            assertNotNull( anova );
            Double interactionEffectP = anova.getInteractionEffectP();
            assertNotNull( interactionEffectP );
            assertTrue( !Double.isNaN( interactionEffectP ) );

        }

    }
}
