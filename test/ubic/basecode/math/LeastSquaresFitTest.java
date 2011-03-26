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

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrixImpl;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.util.r.type.GenericAnovaResult;
import ubic.basecode.util.r.type.LinearModelSummary;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * @author paul
 * @version $Id$
 */
public class LeastSquaresFitTest extends TestCase {

    /**
     * @throws Exception
     */
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

        List<GenericAnovaResult> anova = fit.anova();
        assertEquals( 19, anova.size() );

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
     * @throws Exception
     */
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

        s = sums.get( "232018_at" );
        assertEquals( 0.7167, s.getF(), 0.01 );
        assertEquals( 0.5259, s.getP(), 0.001 );
        assertEquals( 6.5712, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.664, s.getCoefficients().get( 1, 3 ), 0.001 );
    }

    /**
     * @throws Exception
     */
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

        s = sums.get( "232018_at" );
        assertEquals( 0.7167, s.getF(), 0.01 );
        assertEquals( 0.6235, s.getP(), 0.001 );
        assertEquals( 6.8873, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.587932, s.getCoefficients().get( 1, 3 ), 0.001 );

    }

    /**
     * @throws Exception
     */
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

        s = sums.get( "1553129_at" );
        assertNotNull( s );
        assertEquals( 2.095, s.getF(), 0.01 );
        assertEquals( 0.1911, s.getP(), 0.001 );
        assertEquals( 3.78719, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.191, s.getCoefficients().get( 1, 3 ), 0.001 ); // this ordering might change?

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

        s = sums.get( "228980_at" ); // has missing
        assertNotNull( s );
        assertEquals( 0.1495, s.getF(), 0.01 );
        assertEquals( 0.7123, s.getP(), 0.001 );
        assertEquals( 10.9180, s.getCoefficients().get( 0, 0 ), 0.001 );
        assertEquals( 0.712, s.getCoefficients().get( 1, 3 ), 0.001 );

    }

    /**
     * @throws Exception
     */
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
}
