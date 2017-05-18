/*
 * The baseCode project
 *
 * Copyright (c) 2006 University of British Columbia
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ubic.basecode.util.RegressionTesting;

/**
 * @author pavlidis
 */
public class TestSpecFunc {

    @Test
    public final void testCumHyperGeometric() {
        // phyper(2, 20, 100, 50);
        // [1] 0.001077697
        double expectedReturn = 0.001077697;
        double actualReturn = SpecFunc.phyper( 2, 20, 100, 50, true );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    @Test
    public final void testCumHyperGeometricB() {
        // GO:0043371 ingroupoverthresh=0 setsize=5 totalinputsize=15799 totaloverthresh=24 oraP=0.007573332171226688
        double expectedReturn = 0.00757333;
        // phyper(0, 5, 15794, 24, lower.tail=F);

        double actualReturn = SpecFunc.phyper( 0, 5, 15799 - 5, 24, false );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    @Test
    public final void testCumHyperGeometricUT() {
        // phyper(18, 20, 100, 50, lower.tail=F);
        // [1] 7.384185e-08

        double expectedReturn = 7.384185e-08;
        double actualReturn = SpecFunc.phyper( 18, 20, 100, 50, false );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    @Test
    public final void testdBinom() {
        // dbinom(2, 100, 0.1) == 0.001623197
        double expectedReturn = 0.001623197;
        double actualReturn = SpecFunc.dbinom( 2, 100, 0.1 );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    @Test
    public final void testHypergeometric() {
        // hypergeometric( int positives, int successes,
        // int negatives, int failures )

        // dhyper takes : successes, positives, negatives, trials
        // dhyper(2, 20, 100, 50);
        // [1] 0.0009644643
        double expectedReturn = 0.0009644643;
        double actualReturn = SpecFunc.dhyper( 2, 20, 100, 50 );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    @Test
    public void testTrigammaInverse() {

        cern.colt.matrix.impl.DenseDoubleMatrix1D x = new cern.colt.matrix.impl.DenseDoubleMatrix1D(
                new double[] { 1.0, 2.0, 3.0 } );

        // options(digits = 20);limma::trigammaInverse(c(1,2,3))
        double[] expected = new double[] { 1.42625512021507883098, 0.87666407746426022740, 0.67547810528137008923 };

        double[] actual = SpecFunc.trigammaInverse( x ).toArray();
        assertTrue( RegressionTesting.closeEnough( actual, expected, 1e-10 ) );
    }

}