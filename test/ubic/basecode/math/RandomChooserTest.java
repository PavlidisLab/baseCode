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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author paul
 * @version $Id$
 */
public class RandomChooserTest extends TestCase {

    double[] sourceData = new double[] { 0, 1, 2, 3, 4, 5 };

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Note that this does not make algorithm 100% reproducible across all
        // java/platform versions.
        RandomChooser.init( 0 );
    }

    /**
     * Test method for {@link ubic.basecode.math.RandomChooser#chooserandom(int[], boolean[], int, int)} .
     */
    public void testChooserandomIntArrayBooleanArrayIntInt() throws Exception {
        int[] result = RandomChooser.chooserandom( 100, 10 );
        int[] expected = { 60, 48, 29, 47, 15, 53, 91, 61, 19, 54 };
        for ( int i = 0; i < result.length; i++ ) {
            assertEquals( expected[i], result[i] );
        }
    }

    /**
     * Check correctness of the sampling algorithm for choosing subsets of size 2. The average value should come out
     * very close to k/2.
     * 
     * @throws Exception
     */
    public void testRepeat() throws Exception {
        int max = 1000;

        double total = 0.0;
        int reps = 500;
        for ( int j = 0; j < reps; j++ ) {

            for ( int i = 0; i < reps; i++ ) {
                int[] r = RandomChooser.chooserandom( max, 200 );
                int m = r[0] + r[1];

                total += m / 2.0;

            }
        }

        assertEquals( 500, total / ( reps * reps ), 0.5 );
    }

    /**
     * Check correctness of the sampling algorithm for choosing subsets of size 2. The average value should come out
     * very close to k/2.
     * 
     * @throws Exception
     */
    public void testRepeatSubset() throws Exception {
        List<Integer> k = new ArrayList<Integer>();
        int max = 1000;
        for ( int i = 0; i < max; i++ ) {
            k.add( i );
        }
        double total = 0.0;
        int reps = 500;
        for ( int j = 0; j < reps; j++ ) {
            for ( int i = 0; i < reps; i++ ) {
                List<Integer> r = new ArrayList<Integer>( RandomChooser.chooseRandomSubset( 2, k ) );
                int m = r.get( 0 ) + r.get( 1 );
                total += m / 2.0;

            }
        }
        assertEquals( 500, total / ( reps * reps ), 0.5 );
    }

    public void testRandomSubset() throws Exception {
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
     * Test method for {@link ubic.basecode.math.RandomChooser#chooserandomWrep(int[], int, int)} .
     */
    public void testChooserandomWrep() {
        int[] result = RandomChooser.chooserandomWrep( 100, 500 );
        assertEquals( 500, result.length );
        assertEquals( 76, result[0] );
        assertEquals( 55, result[10] );
        assertEquals( 55, result[11] );
    }

}
