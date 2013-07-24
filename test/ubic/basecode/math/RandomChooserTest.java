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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author paul
 * @version $Id$
 */
public class RandomChooserTest {

    @Before
    public void setUp() throws Exception {

        // Note that this does not make algorithm 100% reproducible across all
        // java/platform versions.
        RandomChooser.init( 0 );
    }

    /**
     * Test method for {@link ubic.basecode.math.RandomChooser#chooserandom(int[], boolean[], int, int)} .
     */
    @Test
    public void testChooserandomIntArrayBooleanArrayIntInt() {
        int[] result = RandomChooser.chooserandom( 100, 10 );
        int[] expected = { 60, 48, 29, 47, 15, 53, 91, 61, 19, 54 };
        for ( int i = 0; i < result.length; i++ ) {
            assertEquals( expected[i], result[i] );
        }
    }

    @Test
    public void testChooseRandomDeck() {
        int[] v = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Double[] e = new Double[] { 0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d };
        double[] result = RandomChooser.chooserandom( e, v, 4 );
        double[] expected = new double[] { 5.0, 3.0, 1.0, 0.0 };
        assertEquals( expected.length, result.length );
        for ( int i = 0; i < result.length; i++ ) {
            assertEquals( expected[i], result[i], 0.0001 );
        }
    }

    /**
     * Test method for {@link ubic.basecode.math.RandomChooser#chooserandomWrep(int[], int, int)} .
     */
    @Test
    public void testChooserandomWrep() {
        int[] result = RandomChooser.chooserandomWrep( 100, 500 );
        assertEquals( 500, result.length );
        assertEquals( 76, result[0] );
        assertEquals( 55, result[10] );
        assertEquals( 55, result[11] );
    }

    @Test
    public void testRandomSubset() {
        Collection<String> vals = new HashSet<String>();
        vals.add( "a" );
        vals.add( "b" );
        vals.add( "c" );
        vals.add( "d" );
        vals.add( "e" );
        vals.add( "f" );
        Set<String> result = RandomChooser.chooseRandomSubset( 2, vals );

        /*
         * Note: this used to test for specific values but for some reason this doesn't work on latest java+winxp?
         */
        assertEquals( 2, result.size() );
    }

    /**
     * Check correctness of the sampling algorithm for choosing subsets of size 2. The average value should come out
     * close to k/2.
     * 
     * @throws Exception
     */
    @Test
    public void testRepeat() throws Exception {
        int k = 1000;

        double total = 0.0;
        int reps = 10000;
        for ( int j = 0; j < reps; j++ ) {
            int[] r = RandomChooser.chooserandom( k, 2 );
            int m = r[0] + r[1];
            total += m / 2.0;
        }

        assertEquals( 500, total / reps, 5 );
    }

    /**
     * Check correctness of the sampling algorithm for choosing subsets of size 2. The average value should come out
     * very close to k/2.
     * 
     * @throws Exception
     */
    @Test
    public void testRepeatSubset() throws Exception {
        List<Integer> k = new ArrayList<Integer>();
        int max = 1000;
        for ( int i = 0; i < max; i++ ) {
            k.add( i );
        }
        double total = 0.0;
        int reps = 10000;
        for ( int j = 0; j < reps; j++ ) {
            List<Integer> r = new ArrayList<Integer>( RandomChooser.chooseRandomSubset( 2, k ) );
            int m = r.get( 0 ) + r.get( 1 );
            total += m / 2.0;
        }
        assertEquals( 500, total / reps, 5 );
    }

}
