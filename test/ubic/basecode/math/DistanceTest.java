/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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

import cern.colt.list.DoubleArrayList;
import junit.framework.TestCase;

/**
 * @author paul
 * @version $Id$
 */
public class DistanceTest extends TestCase {

    DoubleArrayList x;
    DoubleArrayList y;

    public void setUp() throws Exception {
        x = new DoubleArrayList( new double[] { 1, 3, 4, 6 } );
        y = new DoubleArrayList( new double[] { -5, 2, 4, 6 } );
    }

    public void testManhattanDistance() {
        double actualValue = Distance.manhattanDistance( x, y );
        double expectedValue = 7.0;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testEuclDistance() {
        double actualValue = Distance.euclDistance( x, y );
        double expectedValue = 6.0827;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testSpearmanRankCorrelation() {
        double actualValue = Distance.spearmanRankCorrelation( x, y );
        double expectedValue = 1.0;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testCorrelationOfStandardizedDoubleArrayDoubleArray() {
        double actualValue = Distance.correlationOfStandardized( new double[] { -1.20096, -0.24019, 0.24019, 1.20096 },
                new double[] { -1.41, 0.0522233, 0.47, 0.8877 } );
        double expectedValue = 0.9533;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testCorrelationOfStandardizedDoubleArrayListDoubleArrayList() {
        double actualValue = Distance.correlationOfStandardized( new DoubleArrayList( new double[] { -1.20096,
                -0.24019, 0.24019, 1.20096 } ), new DoubleArrayList( new double[] { -1.41, 0.0522233, 0.47, 0.8877 } ) );
        double expectedValue = 0.9533;
        assertEquals( expectedValue, actualValue, 0.001 );
    }
}
