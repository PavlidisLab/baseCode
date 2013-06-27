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

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestROC extends TestCase {

    List<Double> ranksOfPositives;

    public void testAroc() {
        double actualReturn = ROC.aroc( 10, ranksOfPositives );
        double expectedReturn = ( 21.0 - 5.0 ) / 21.0;
        assertEquals( "return value", expectedReturn, actualReturn, 0.00001 );
    }

    // wilcox.test(c(1,4,6), c(2,3,5,7,8,9,10), paired=F, alternative="l")
    public void testArocPvalue() {
        double actualReturn = ROC.rocpval( 10, ranksOfPositives );
        double expectedReturn = 0.13333333;
        assertEquals( "return value", expectedReturn, actualReturn, 0.00001 );
    }

    /**
     * Bug 3620
     */
    public void testBig() {
        int totalSize = 450000;
        List<Double> x = new LinkedList<Double>();
        for ( int i = 0; i < 200000; i++ ) {
            x.add( ( int ) ( Math.random() * 300000 ) + 1d );
        }
        assertTrue( 1.0 >= ROC.aroc( totalSize, x ) );

    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // set up the ranks of the positives
        ranksOfPositives = new Vector<Double>();
        ranksOfPositives.add( 1.0 );
        ranksOfPositives.add( 4.0 );
        ranksOfPositives.add( 6.0 );
    }

}