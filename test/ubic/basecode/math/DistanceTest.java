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

import junit.framework.TestCase;

import org.junit.Test;

import cern.colt.list.DoubleArrayList;

/**
 * @author paul
 * @version $Id$
 */
public class DistanceTest extends TestCase {

    DoubleArrayList x;
    DoubleArrayList y;

    @Override
    public void setUp() throws Exception {
        x = new DoubleArrayList( new double[] { 1, 3, 4, 6 } );
        y = new DoubleArrayList( new double[] { -5, 2, 4, 6 } );
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

    public void testEuclDistance() {
        double actualValue = Distance.euclDistance( x, y );
        double expectedValue = 6.0827;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testManhattanDistance() {
        double actualValue = Distance.manhattanDistance( x, y );
        double expectedValue = 7.0;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testSpearmanRankCorrelation() {
        double actualValue = Distance.spearmanRankCorrelation( x, y );
        double expectedValue = 1.0;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testSpearmanRankCorrelation2() {
        // Example - R returns 0.3356643,
        double[] a = new double[] { 98.0, 91.0, 23.0, 58.0, 106.0, 31.0, 30.0, 69.0, 27.0, 9.0, 22.0, 40.0 };
        double[] b = new double[] { 40.0, 744.0, 13.0, 5282.0, 2031.0, 27.0, 965.0, 170.0, 874.0, 191.0, 32.0, 90.0 };

        double actualValue = Distance.spearmanRankCorrelation( new DoubleArrayList( a ), new DoubleArrayList( b ) );
        double expectedValue = 0.3356643;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testSpearmanRankCorrelation3() {
        // Example - R returns -0.8193465
        double[] a = new double[] { 0.3444789380683436, 0.25730556300327256, -0.3537694496071584, 0.30107097096672647,
                0.2616453086877814, -0.3443256334665076, 0.07761487750036018, 0.30774542507040675, -0.3531845397619931,
                -0.19525720140467262, 0.07495906681671448, -0.37828332587330704 };
        double[] b = new double[] { 64006.0, 64006.0, 64007.0, 64006.0, 64006.0, 64007.0, 64006.0, 64006.0, 64007.0,
                64006.0, 64006.0, 64007.0 };

        double actualValue = Distance.spearmanRankCorrelation( new DoubleArrayList( a ), new DoubleArrayList( b ) );
        double expectedValue = -0.8193465;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public void testSpearmanRankCorrelationNaN() {
        // Example - R returns 0.8597647 using use = "complete.obs" parameter // not for me PP

        // a<-c(98.0, 23.0, 58.0, 106.0, 31.0 );
        // b<-c(NA, -8.950001233616126, -3.5698243390049167, -3.062729280960539, NA);
        // cor(a,b,method='s', use='complete.obs');

        double[] a = new double[] { 98.0, 23.0, 58.0, 106.0, 31.0 };
        double[] b = new double[] { Double.NaN, -8.950001233616126, -3.5698243390049167, -3.062729280960539, Double.NaN };

        double actualValue = Distance.spearmanRankCorrelation( new DoubleArrayList( a ), new DoubleArrayList( b ) );
        // double expectedValue = 0.8597647;
        double expectedValue = 1.0;

        assertEquals( expectedValue, actualValue, 0.001 );
    }

    @Test
    public void testSpearmanOneList1() throws Exception {

        DoubleArrayList f = new DoubleArrayList( new double[] { 0, 1, 2, 3, 4, 5, 6 } );
        double actual = Distance.spearmanRankCorrelation( f );
        assertEquals( 1.0, actual, 0.0001 );
    }

    @Test
    public void testSpearmanOneList2() throws Exception {

        DoubleArrayList f = new DoubleArrayList( new double[] { 6, 5, 4, 3, 2, 1, 0 } );
        double actual = Distance.spearmanRankCorrelation( f );
        assertEquals( -1.0, actual, 0.0001 );
    }

    @Test
    public void testSpearmanOneList3() throws Exception {

        DoubleArrayList f = new DoubleArrayList( new double[] { 10, 6, 5, 4, 3, 2, 1, 0, -100 } );
        double actual = Distance.spearmanRankCorrelation( f );
        assertEquals( -1.0, actual, 0.0001 );
    }

    @Test
    public void testSpearmanOneList4() throws Exception {
        DoubleArrayList f = new DoubleArrayList( new double[] { 100, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 100 } );
        double actual = Distance.spearmanRankCorrelation( f );
        assertEquals( 0.0, actual, 0.01 );
    }

}
