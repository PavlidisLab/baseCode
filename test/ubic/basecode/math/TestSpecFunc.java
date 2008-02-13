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

import junit.framework.TestCase;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestSpecFunc extends TestCase {

    public final void testCumHyperGeometric() {
        // phyper(2, 20, 100, 50);
        // [1] 0.001077697
        double expectedReturn = 0.001077697;
        double actualReturn = SpecFunc.phyper( 2, 20, 100, 50, true );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    public final void testCumHyperGeometricUT() {
        // phyper(18, 20, 100, 50, lower.tail=F);
        // [1] 7.384185e-08

        double expectedReturn = 7.384185e-08;
        double actualReturn = SpecFunc.phyper( 18, 20, 100, 50, false );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    public final void testdBinom() {
        // dbinom(2, 100, 0.1) == 0.001623197
        double expectedReturn = 0.001623197;
        double actualReturn = SpecFunc.dbinom( 2, 100, 0.1 );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

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

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}