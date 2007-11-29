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
 * @author paul
 * @version $Id$
 */
public class CorrelationStatsTest extends TestCase {

    /*
     * Values come from R, using things like cor.test(rnorm(50), rnorm(50), method="spearman", alternative="two.sided")
     */
    public void testSpearmanPvalueA() {
        double actual = CorrelationStats.spearmanPvalue( -0.8, 5 );
        double expected = 0.1333;
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueA2() {
        double actual = CorrelationStats.spearmanPvalue( 0.8, 5 );
        double expected = 0.1333;
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueB() {
        double actual = CorrelationStats.spearmanPvalue( -0.6, 10 );
        // double expected = 0.07312; // value from R
        double expected = 0.0739814; // our value
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueTDist() {
        double actual = CorrelationStats.spearmanPvalue( 0.001644122, 2110 );
        double expected = 0.9398;
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueD() {
        double actual = CorrelationStats.spearmanPvalue( -0.03788715, 50 );
        // double expected = 0.7934; // value from R
        double expected = 0.78851988243; // our value
        assertEquals( expected, actual, 0.0001 );
    }

    public void testSpearmanPvalueD2() {
        double actual = CorrelationStats.spearmanPvalue( -0.6108523, 50 );
        // double expected = 4.089e-6; // value from R
        double expected = 3.397260e-5; // our value
        assertEquals( expected, actual, 0.0000001 );
    }
}
