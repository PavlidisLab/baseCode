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
 * Fill arrays with random values given a source of values.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class RandomChooser {

    private RandomChooser() { /* block instantiation */
    }

    private static Random generator = new Random( System.currentTimeMillis() );

    /**
     * Initialized the random number generator witha given seed.
     * 
     * @param seed
     */
    public static void init( long seed ) {
        generator = new Random( seed );
    }

    /**
     * Fill randomvals with random things from sourcedata, without replacement.
     * 
     * @param randomvals answers go here.
     * @param sourcedata Data to be randomly selected
     * @param deck an array pre-filled with integers from 0 to max, but they don't have to be in order.
     * @param numNeeded how many values we need.
     * @param n int
     */
    public static void chooserandom( double[] randomvals, double[] sourcedata, int[] deck, int numNeeded, int n ) {
        if (numNeeded <= 0) throw new IllegalArgumentException("numNeeded must be greater than zero");
        int rand;
        int i;
        int temp;
        for ( i = 0; i < n; i++ ) {
            rand = generator.nextInt( numNeeded - i ) + i; // a value between i and max.
            temp = deck[rand];
            deck[rand] = deck[i];
            deck[i] = temp;
            randomvals[i] = sourcedata[temp];
        }
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
        int rand;
        int i;
        for ( i = 0; i < n; i++ ) {
            rand = generator.nextInt( max - i ) + i; // a value between i and max.
            randomnums[i] = deck[rand];
            deck[rand] = deck[i];
            deck[i] = randomnums[i];
        }
    }

    /**
     * choose n random integers from 0 to max without repeating
     * 
     * @param randomnums int[]
     * @param recLog record of what values are already chosen.
     * @param max int
     * @param n int
     */
    public static void chooserandom( int[] randomnums, boolean[] recLog, int max, int n ) {
        int numgot;
        int i;
        int newnum;

        numgot = 0;

        while ( numgot < n ) { /* numgot is the index of the last gotten item */
            newnum = generator.nextInt( max );
            if ( !recLog[newnum] ) {
                randomnums[numgot] = newnum;
                recLog[newnum] = true;
                numgot++;
            }
        }

        // reset all elements in recLog to false
        for ( i = 0; i < n; i++ ) {
            recLog[randomnums[i]] = false;
        }

    }

    /**
     * Same as chooserandom, but with replacement -- that is, repeats are allowed.
     * 
     * @param randomnums int[]
     * @param max int
     * @param n int
     */
    public static void chooserandomWrep( int[] randomnums, int max, int n ) {
        for ( int i = 0; i < n; i++ ) {
            int newnum = ( char ) ( generator.nextInt() % max );
            randomnums[i] = newnum;
        }
    }

    public static <T> Set<T> chooseRandomSubset( int n, Collection<? extends T> superSet ) {
        return chooseRandomSubset( n, new ArrayList( superSet) );
    }
    
    public static <T> Set<T> chooseRandomSubset( int n, List<? extends T> superSet ) {
        int max = superSet.size();
        int[] indices = new int[ n ];
        boolean[] chosen = new boolean[ max ];
        chooserandom( indices, chosen, max, n );
        
        Set<T> result = new HashSet<T>();
        for ( int i=0; i<n; ++i ) {
            result.add( superSet.get( indices[i] ) );
        }
        return result;
    }
    
}