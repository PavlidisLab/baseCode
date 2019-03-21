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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.Well19937c;

/**
 * Fill arrays with random values
 * 
 * @author Paul Pavlidis
 * 
 */
public class RandomChooser {

    private static Random generator = new Random();
    private static RandomDataGenerator dataGenerator = new RandomDataGenerator();

    /**
     * Random numbers from sourcedata, without replacement.
     * 
     * @param sourcedata Data to be randomly selected
     * @param deck an array pre-filled with integers from 0 to max, but they don't have to be in order (just an
     *        optimization so we don't have to generate this multiple times)
     * @param k how many values we need.
     */
    public static double[] chooserandom( double[] sourcedata, int k ) {
        if ( k <= 0 ) throw new IllegalArgumentException( "numNeeded must be greater than zero" );

        int len = sourcedata.length;
        int[] index = dataGenerator.nextPermutation( len, k );
        double[] result = new double[k];
        for ( int i = 0; i < k; i++ ) {
            result[i] = sourcedata[index[i]];
        }
        return result;
        //
        // int rand, i, temp;
        // int sourceSize = sourcedata.length;
        // double[] result = new double[numNeeded];
        // for ( i = 0; i < numNeeded; i++ ) {
        // rand = generator.nextInt( sourceSize - i ) + i; // a value between i and max.
        // temp = deck[rand];
        // deck[rand] = deck[i];
        // deck[i] = temp;
        // result[i] = sourcedata[temp];
        // }
        // return result;
    }

    /**
     * choose n random integers from 0 (inclusive) to max (exclusive) without repeating
     * 
     * @param max largest value to choose
     * @param n how many to choose
     */
    public static int[] chooserandom( int max, int n ) {
        return dataGenerator.nextPermutation( max, n );
    }

    /**
     * choose k random integers from 0 to max (exclusive) without repeating
     * 
     * @param deck an array pre-filled with integers from 0 to max, but they don't have to be in order. Provided to
     *        avoid recomputing in iterative computations.
     * @param k how many to choose
     */
    public static int[] chooserandom( int[] deck, int k ) {
        int len = deck.length;
        int[] index = dataGenerator.nextPermutation( len, k );
        int[] result = new int[k];
        for ( int i = 0; i < k; i++ ) {
            result[i] = deck[index[i]];
        }
        return result;
    }

    /**
     * @param source
     * @param n
     * @return sample without replacement
     * @see RandomDataGenerator.nextSample
     */
    public static List<? extends Object> chooserandom( List<? extends Object> source, int n ) {
        return Arrays.asList( dataGenerator.nextSample( source, n ) );
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
     * Initialized the random number generator with a given seed.
     * 
     * @param seed
     */
    public static void init( long seed ) {
        generator = new Random( seed );
        dataGenerator = new RandomDataGenerator( new Well19937c( seed ) );
    }

    private RandomChooser() { /* block instantiation */
    }

}