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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Fill arrays with random values
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class RandomChooser {

    private static Random generator = new Random();

    /**
     * Fill randomvals with random things from sourcedata, without replacement.
     * 
     * @param randomvals answers go here.
     * @param sourcedata Data to be randomly selected
     * @param deck an array pre-filled with integers from 0 to max, but they don't have to be in order.
     * @param numNeeded how many values we need.
     * @param n int
     */
    public static double[] chooserandom( Double[] sourcedata, int[] deck, int numNeeded ) {
        if ( numNeeded <= 0 ) throw new IllegalArgumentException( "numNeeded must be greater than zero" );
        int rand, i, temp;
        int sourceSize = sourcedata.length;
        double[] result = new double[numNeeded];
        for ( i = 0; i < numNeeded; i++ ) {
            rand = generator.nextInt( sourceSize - i ) + i; // a value between i and max.
            temp = deck[rand];
            deck[rand] = deck[i];
            deck[i] = temp;
            result[i] = sourcedata[temp];
        }
        return result;
    }

    /**
     * choose n random integers from 0 (inclusive) to max (exclusive) without repeating
     * 
     * @param max largest value to choose
     * @param n how many to choose
     */
    public static int[] chooserandom( int max, int n ) {
        if ( n > max ) {
            throw new IllegalArgumentException(
                    "n must be less than or equal to max (and should be much smaller, actually)" );
        }
        // Initialize a boolean vector to keep track of which values have been used already
        int[] result = new int[n];
        boolean[] recLog = new boolean[max];
        for ( int i = 0; i < max; i++ ) {
            recLog[i] = false;
        }

        int numgot = 0;
        while ( numgot < n ) { /* numgot is the index of the last gotten item */
            int newnum = generator.nextInt( max );
            if ( !recLog[newnum] ) {
                result[numgot] = newnum;
                recLog[newnum] = true;
                numgot++;
            }
        }

        return result;

    }

    /**
     * choose n random integers from 0 to max without repeating
     * 
     * @param randomnums answers go here.
     * @param deck an array pre-filled with integers from 0 to max, but they don't have to be in order.
     * @param max how many values we need.
     * @param n int
     */
    public static void chooserandom( int[] randomnums, int[] deck, int max, int n ) {
        for ( int i = 0; i < n; i++ ) {
            int rand = generator.nextInt( max - i ) + i; // a value between i and max.
            randomnums[i] = deck[rand];
            deck[rand] = deck[i];
            deck[i] = randomnums[i];
        }
    }

    /**
     * Select a random subset of size n from collection superSet
     * 
     * @param n
     * @param superSet
     * @return subset.
     */
    public static <T> Set<T> chooseRandomSubset( int n, Collection<? extends T> superSet ) {
        return chooseRandomSubset( n, new ArrayList<T>( superSet ) );
    }

    /**
     * Select a random subset of size n from List superSet
     * 
     * @param n
     * @param superSet
     * @return subset
     */
    public static <T> Set<T> chooseRandomSubset( int n, List<? extends T> superSet ) {
        int max = superSet.size();
        int[] indices = chooserandom( max, n );
        Set<T> result = new HashSet<T>();
        for ( int i = 0; i < n; ++i ) {
            result.add( superSet.get( indices[i] ) );
        }
        return result;
    }

    /**
     * Same as chooserandom, but with replacement -- that is, repeats are allowed.
     * 
     * @param randomnums int[]
     * @param max int
     * @param n int
     */
    public static int[] chooserandomWrep( int max, int n ) {
        int[] randomnums = new int[n];
        for ( int i = 0; i < n; i++ ) {
            int newnum = Math.abs( generator.nextInt() ) % max;
            randomnums[i] = newnum;
        }
        return randomnums;
    }

    /**
     * Initialized the random number generator witha given seed.
     * 
     * @param seed
     */
    public static void init( long seed ) {
        generator = new Random( seed );
    }

    private RandomChooser() { /* block instantiation */
    }

}