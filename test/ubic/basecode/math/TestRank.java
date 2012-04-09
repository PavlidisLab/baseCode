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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestRank extends TestCase {

    DoubleArrayList testdata = null;
    Map<String, Double> testmap = null;

    public void testOrder() {
        double[] a = new double[] { 49.0, 43.0, 310.0, 20.0, 20.0, 688.0, 498.0, 533.0, 723.0, 1409.0, 279.0 };
        DoubleArrayList al = new DoubleArrayList( a );
        IntArrayList actual = Rank.order( al );
        int[] expected = new int[] { 5, 4, 2, 1, 11, 3, 7, 8, 6, 9, 10 };
        // note, the ties are the 5,4, so it could
        // be 4,5. So we start checking at 2.
        for ( int i = 2; i < al.size(); i++ ) {
            assertEquals( "at position " + i, expected[i] - 1, actual.get( i ) );
        }
    }

    public void testOrderB() {
        double[] a = new double[] { 2, 3, 1, 5, 4 };
        DoubleArrayList al = new DoubleArrayList( a );
        IntArrayList actual = Rank.order( al );
        int[] expected = new int[] { 3, 1, 2, 5, 4 };
        for ( int i = 0; i < al.size(); i++ ) {
            assertEquals( "at position " + i, expected[i] - 1, actual.get( i ) );
        }
    }

    /*
     * Class under test for DoubleArrayList rankTransform(DoubleArrayList)
     */
    public void testRankTransformDoubleArrayList() {
        DoubleArrayList actualReturn = Rank.rankTransform( testdata );
        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 2, 3, 4, 5, 6, 1 } );
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testRankTransformDoubleArrayListDesc() {
        DoubleArrayList actualReturn = Rank.rankTransform( testdata, true );
        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 5, 4, 3, 2, 1, 6 } );
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testRankTransformMap() {

        Map<String, Double> actualReturn = Rank.rankTransform( testmap );
        Map<String, Double> expectedReturn = new HashMap<String, Double>();
        expectedReturn.put( "Ten", 1.0 );
        expectedReturn.put( "Eleven", 2.0 );
        expectedReturn.put( "Twelve", 3.0 );
        expectedReturn.put( "Thirteen", 4.0 );
        expectedReturn.put( "HundredFourteen", 5.0 );
        expectedReturn.put( "Five", 0.0 );
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testRankTransformMapDesc() {
        Map<String, Double> actualReturn = Rank.rankTransform( testmap, true );
        Map<String, Double> expectedReturn = new HashMap<String, Double>();
        expectedReturn.put( "Ten", 4.0 );
        expectedReturn.put( "Eleven", 3.0 );
        expectedReturn.put( "Twelve", 2.0 );
        expectedReturn.put( "Thirteen", 1.0 );
        expectedReturn.put( "HundredFourteen", 0.0 );
        expectedReturn.put( "Five", 5.0 );
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testRankWithTies() {
        double[] a = new double[] { 49.0, 43.0, 310.0, 20.0, 20.0, 688.0, 498.0, 533.0, 723.0, 1409.0, 279.0 };
        DoubleArrayList al = new DoubleArrayList( a );
        DoubleArrayList actual = Rank.rankTransform( al );
        double[] expected = new double[] { 4.0, 3.0, 6.0, 1.5, 1.5, 9.0, 7.0, 8.0, 10.0, 11.0, 5.0 };
        for ( int i = 0; i < al.size(); i++ ) {
            assertEquals( "at position " + i, expected[i], actual.get( i ), 0.0001 );
        }
    }

    public void testRankWithTiesB() {
        double[] a = new double[] { 49.0, 43.0, 310.0, 20.0, 20.0, 688.0, 498.0, 533.0, 723.0, 1409.0, 279.0, 279.0 };
        DoubleArrayList al = new DoubleArrayList( a );
        DoubleArrayList actual = Rank.rankTransform( al );
        double[] expected = new double[] { 4.0, 3.0, 7.0, 1.5, 1.5, 10.0, 8.0, 9.0, 11.0, 12.0, 5.5, 5.5 };
        for ( int i = 0; i < al.size(); i++ ) {
            assertEquals( "at position " + i, expected[i], actual.get( i ), 0.0001 );
        }
    }

    public void testRankWithTiesC() {
        double[] a = new double[] { 49.0, 43.0, 310.0, 20.0, 279.0, 20.0, 688.0, 498.0, 533.0, 723.0, 1409.0, 279.0 };
        DoubleArrayList al = new DoubleArrayList( a );
        DoubleArrayList actual = Rank.rankTransform( al );
        double[] expected = new double[] { 4.0, 3.0, 7.0, 1.5, 5.5, 1.5, 10.0, 8.0, 9.0, 11.0, 12.0, 5.5 };
        for ( int i = 0; i < al.size(); i++ ) {
            assertEquals( "at position " + i, expected[i], actual.get( i ), 0.0001 );
        }
    }

    public void testRankWithTiesD() {
        double[] a = new double[] { 49.0, 49.0 };
        DoubleArrayList al = new DoubleArrayList( a );
        DoubleArrayList actual = Rank.rankTransform( al );
        double[] expected = new double[] { 1.5, 1.5 };
        for ( int i = 0; i < al.size(); i++ ) {
            assertEquals( "at position " + i, expected[i], actual.get( i ), 0.0001 );
        }
    }

    public void testRankWithTiesE() {
        double[] a = new double[] { 49.0, 1.0, 49.0, 49.0 };
        DoubleArrayList al = new DoubleArrayList( a );
        DoubleArrayList actual = Rank.rankTransform( al );
        double[] expected = new double[] { 3, 1, 3, 3 };
        for ( int i = 0; i < al.size(); i++ ) {
            assertEquals( "at position " + i, expected[i], actual.get( i ), 0.0001 );
        }
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testdata = new DoubleArrayList( new double[] { 10.0, 11.0, 12.0, 13.0, 114.0, 5.0 } );
        testmap = new HashMap<String, Double>();
        testmap.put( "Ten", new Double( 10.0 ) );
        testmap.put( "Eleven", new Double( 11.0 ) );
        testmap.put( "Twelve", new Double( 12.0 ) );
        testmap.put( "Thirteen", new Double( 13.0 ) );
        testmap.put( "HundredFourteen", new Double( 114.0 ) );
        testmap.put( "Five", new Double( 5.0 ) );

    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testdata = null;
        testmap = null;
    }
}