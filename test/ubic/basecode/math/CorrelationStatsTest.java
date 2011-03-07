/*
 * The baseCode project
 * 
 * Copyright (c) 2007 University of British Columbia
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

import junit.framework.TestCase;

/**
 * Most benchmarks come from R, using things like cor.test(rnorm(50), rnorm(50), method="spearman",
 * alternative="two.sided"),
 * <p>
 * OR (where the first argument is n, second is S (rounded off)
 * <p>
 * 
 * <pre>
 * 1-.C(&quot;prho&quot;,as.integer(10),as.double(4),double(1),integer(1),as.logical(FALSE),PACKAGE=&quot;stats&quot;)[[3]]
 * </pre>
 * 
 * @author paul
 * @version $Id$
 */
public class CorrelationStatsTest extends TestCase {

    public void testCorrel() throws Exception {
        double[] x = new double[] { 1, 3, 4, 6 };
        double[] y = new double[] { -5, 2, 4, 6 };
        double actual = CorrelationStats.correl( x, y );
        double expected = 0.9533159;
        assertEquals( expected, actual, 0.0001 );
    }

    public void testCorrelAsByte() throws Exception {
        assertEquals( 198, CorrelationStats.correlAsByte( 0.55 ) );
        assertEquals( 57, CorrelationStats.correlAsByte( -0.55 ) );
        assertEquals( 127, CorrelationStats.correlAsByte( 0 ) );
        assertEquals( 255, CorrelationStats.correlAsByte( 1 ) );
        assertEquals( 0, CorrelationStats.correlAsByte( -1 ) );
    }

    public void testCorrelationForPvalue() throws Exception {
        assertEquals( 0.9533, CorrelationStats.correlationForPvalue( 0.02334, 4 ), 0.01 );
        assertEquals( 0.03487332, CorrelationStats.correlationForPvalue( 0.4619, 10 ), 0.01 );
        assertEquals( 0.31008, CorrelationStats.correlationForPvalue( 0.1916, 10 ), 0.01 );
        assertEquals( 0.180, CorrelationStats.correlationForPvalue( 1.0 - 0.691, 10 ), 0.01 );
        assertEquals( 0.127, CorrelationStats.correlationForPvalue( CorrelationStats.pvalue( 0.127, 20 ), 20 ), 0.01 );
        assertEquals( 0.227, CorrelationStats.correlationForPvalue( CorrelationStats.pvalue( 0.227, 20 ), 20 ), 0.01 );
        assertEquals( 0.0127, CorrelationStats.correlationForPvalue( CorrelationStats.pvalue( 0.0127, 20 ), 20 ), 0.01 );
        assertEquals( 0.00127, CorrelationStats.correlationForPvalue( CorrelationStats.pvalue( 0.00127, 20 ), 20 ),
                0.01 );
    }

    public void testInverseFisherTransform() throws Exception {
        assertEquals( 0.5, CorrelationStats.unFisherTransform( CorrelationStats.fisherTransform( 0.5 ) ), 0.001 );
        assertEquals( -0.5, CorrelationStats.unFisherTransform( CorrelationStats.fisherTransform( -0.5 ) ), 0.001 );
        assertEquals( 0.05, CorrelationStats.unFisherTransform( CorrelationStats.fisherTransform( 0.05 ) ), 0.001 );
        assertEquals( 1.0, CorrelationStats.unFisherTransform( CorrelationStats.fisherTransform( 1.0 ) ), 0.001 );
        assertEquals( 0.0, CorrelationStats.unFisherTransform( CorrelationStats.fisherTransform( 0.0 ) ), 0.001 );
    }

    public void testCorrelTooSmall() throws Exception {
        double[] x = new double[] { 1, };
        double[] y = new double[] { -5 };
        double actual = CorrelationStats.correl( x, y );
        assertEquals( Double.NaN, actual );
    }

    public void testPvalue() throws Exception {
        assertEquals( 0.02334, CorrelationStats.pvalue( 0.9533, 4 ), 0.001 );
        assertEquals( 0.4619, CorrelationStats.pvalue( 0.03487, 10 ), 0.001 );
        assertEquals( 0.1916, CorrelationStats.pvalue( 0.31008, 10 ), 0.001 );
        assertEquals( 1.0 - 0.691, CorrelationStats.pvalue( 0.180, 10 ), 0.001 );
    }

    public void testSpearmanPvalueA() {
        double actual = CorrelationStats.spearmanPvalue( -0.8, 5 );
        double expected = 0.08333;
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueA2() {
        double actual = CorrelationStats.spearmanPvalue( 0.8, 5 );
        double expected = 0.08333;
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueB() {
        double actual = CorrelationStats.spearmanPvalue( -0.6484848, 10 );
        // double expected = 0.04904; // value from R
        double expected = 0.0440265; // our value
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueD() {
        double actual = CorrelationStats.spearmanPvalue( -0.03788715, 50 );
        // double expected = 0.7934; // value from R
        double expected = 0.7929; // our value
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueD2() {
        double actual = CorrelationStats.spearmanPvalue( -0.602401, 50 );
        // double expected = 5.889e-06; // value from R
        double expected = 5.889e-06; // our value
        assertEquals( expected, actual, 0.0000001 );
    }

    public void testSpearmanPvalueE() {
        double actual = CorrelationStats.spearmanPvalue( 0.983333, 9 );
        double expected = 5.511464e-6; // value from R for N=9,S=2
        assertEquals( expected, actual, 0.0000001 );
    }

    public void testSpearmanPvalueEh() {
        double actual = CorrelationStats.spearmanPvalue( 1, 9 );
        double expected = 0; // value from R
        assertEquals( expected, actual, 0.0000001 );
    }

    public void testSpearmanPvalueEm() {
        double actual = CorrelationStats.spearmanPvalue( -0.966667, 9 );
        double expected = 4.960317e-5; // value from R for N=9,S=4
        assertEquals( expected, actual, 0.0000001 );
    }

    public void testSpearmanPvalueF() throws Exception {
        double actual = CorrelationStats.spearmanPvalue( 0.9759, 10 );
        double expected = 0.0; // value from R is basically 0
        assertEquals( expected, actual, 0.00001 );
    }

    public void testSpearmanPvalueG() throws Exception {
        double actual = CorrelationStats.spearmanPvalue( 0.9166667, 9 );
        double expected = 0.0007495591; // R result for S=10, N=9
        assertEquals( expected, actual, 0.00001 );
    }

    public void testSpearmanPvalueG2() throws Exception {
        double actual = CorrelationStats.spearmanPvalue( 0.9166667, 6 );
        double expected = 0.016667; // R result.
        assertEquals( expected, actual, 0.00001 );
    }

    public void testSpearmanPvalueTDist() {
        double actual = CorrelationStats.spearmanPvalue( 0.001644122, 2110 );
        double expected = 0.9398;
        assertEquals( expected, actual, 0.0001 );
    }

}
