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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;

/**
 * Calculate rank statistics for arrays.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Rank {

    /**
     * Return a permutation which puts the array in sorted order. In other words, the values returned indicate the
     * positions of the sorted values in the <em>original</em> array (the lowest value has the lowest rank, but it could
     * be located anywhere in the array). Indexes start from 0. Tied values are put in an arbitrary ordering.
     * 
     * @param array
     * @return
     */
    public static IntArrayList order( DoubleArrayList array ) {
        int size = array.size();
        IntArrayList result = new IntArrayList( new int[size] );

        ObjectArrayList ranks = new ObjectArrayList( size );
        for ( int i = 0; i < size; i++ ) {
            RankData rd = new RankData( i, array.get( i ) );
            ranks.add( rd );
        }
        ranks.sort();

        for ( int i = 0; i < size; i++ ) {
            RankData rd = ( RankData ) ranks.getQuick( i );
            result.setQuick( i, rd.getIndex() );
        }
        return result;
    }

    /**
     * @param ranks
     * @return
     */
    public static int rankSum( Collection<Integer> ranks ) {
        int sum = 0;
        for ( Integer rank : ranks ) {
            sum += rank.intValue();
        }
        return sum;
    }

    /**
     * Rank transform an array. The ranks are constructed based on the sort order of the elements. That is, low values
     * get low numbered ranks starting from 1.
     * <p>
     * Ties are resolved by assigning the average rank for tied values. For example, instead of arbitrarily assigning
     * ties ranks 3,4,5, all three values would get a rank of 4 and no value would get a rank of 3 or 5.
     * <p>
     * Missing values are not handled particularly gracefully: missing values (Double.NaN) are treated as per their
     * natural sort order.
     * 
     * @param array DoubleArrayList
     * @return cern.colt.list.DoubleArrayList
     */
    public static DoubleArrayList rankTransform( DoubleArrayList array ) {
        if ( array == null ) {
            throw new IllegalArgumentException( "Null array" );
        }

        int size = array.size();
        if ( size == 0 ) {
            return null;
        }

        ObjectArrayList ranks = new ObjectArrayList( size );
        DoubleArrayList result = new DoubleArrayList( new double[size] );

        // store the values with their indices.
        for ( int i = 0; i < size; i++ ) {
            RankData rd = new RankData( i, array.get( i ) );
            ranks.add( rd );
        }

        ranks.sort();

        // fill in the results. We iterate over the ranks by order.
        Double prevVal = null;
        int rank = 1;
        int nominalRank = 1; // rank we'd have if no ties.
        for ( int i = 0; i < size; i++ ) {
            RankData rankData = ( RankData ) ranks.get( i );
            int index = rankData.getIndex();
            double val = rankData.getValue();

            result.set( index, nominalRank ); // might not keep.

            // only bump up ranks if we're not tied with the last one.
            if ( prevVal != null && val != prevVal.doubleValue() ) {
                rank = nominalRank;
            } else {
                // tied. Do not advance the rank.
                result.set( index, rank );
            }

            // System.err.println( rankData + " assigned rank=" + result.get( index ) + " rank for ties=" + rank
            // + " nominal rank =" + nominalRank );

            prevVal = val;
            nominalRank++;
        }

        // At this point we may have repeated ranks.

        fixTies( result, ranks );

        return result;
    }

    /**
     * @param <K>
     * @param m
     * @param desc if true, the lowest (first) rank will be for the highest value.
     * @return
     */
    public static <K> Map<K, Integer> rankTransform( Map<K, Double> m, boolean desc ) {
        int counter = 0;

        List<KeyAndValueData<K>> values = new ArrayList<KeyAndValueData<K>>();

        for ( Iterator<K> itr = m.keySet().iterator(); itr.hasNext(); ) {

            K key = itr.next();

            double val = m.get( key ).doubleValue();
            values.add( new KeyAndValueData<K>( key, val ) );
            counter++;
        }

        /* sort it */
        Collections.sort( values );
        Map<K, Integer> result = new HashMap<K, Integer>();
        /* put the sorted items back into a hashmap with the rank */
        for ( int i = 0; i < m.size(); i++ ) {
            int rank = i;
            if ( desc ) {
                rank = values.size() - i - 1;
            }
            result.put( values.get( i ).getKey(), rank );
        }
        return result;
    }

    /**
     * Rank transform a map, where the values are numerical (java.lang.Double) values we wish to rank. Ties are not
     * handled specially. Ranks are zero-based.
     * 
     * @param m java.util.Map with keys Objects, values Doubles.
     * @return A java.util.Map keys=old keys, values=java.lang.Integer rank of the key.
     */
    public static <K> Map<K, Integer> rankTransform( Map<K, Double> m ) {
        return rankTransform( m, false );
    }

    /**
     * @param ranksWithTies
     */
    private static void fixTies( DoubleArrayList ranksWithTies, ObjectArrayList ranks ) {

        int numties = 1;
        Double prev = null;
        int i = 0;
        // iterate over in order of the values.
        for ( int j = ranksWithTies.size(); i < j; i++ ) {
            RankData rankData = ( RankData ) ranks.get( i );
            int index = rankData.getIndex();
            double rank = ranksWithTies.getQuick( index );
            if ( prev != null ) {
                if ( rank == prev.doubleValue() ) {
                    // record how many ties we have seen
                    numties++;
                    // System.err.println( "Tied rank: " + rank );
                } else if ( numties > 1 ) {
                    // we're past the batch of ties and need to adjust them
                    for ( int k = 1; k <= numties; k++ ) {
                        int indexOfTied = ( ( RankData ) ranks.get( i - k ) ).getIndex();
                        double rawRankInTie = ranksWithTies.getQuick( indexOfTied );
                        double meanRank = meanRank( rawRankInTie, numties );
                        ranksWithTies.setQuick( indexOfTied, meanRank );
                    }
                    numties = 1; // reset
                }
                // no tie, nothing needs to be changed.
            }
            prev = rank;
        }

        // cleanup the end of the array if there were ties there.
        if ( numties > 1 ) {
            for ( int k = 1; k <= numties; k++ ) {
                int indexOfTied = ( ( RankData ) ranks.get( i - k ) ).getIndex();
                double rawRankInTie = ranksWithTies.getQuick( indexOfTied );
                double meanRank = meanRank( rawRankInTie, numties );
                ranksWithTies.setQuick( indexOfTied, meanRank );
            }
        }

    }

    /**
     * @param rawRank
     * @param numties
     * @return
     */
    private static double meanRank( double rawRank, int numties ) {
        double total = 0;
        for ( int i = 0; i < numties; i++ ) {
            total = total + rawRank + i;
        }
        return total / numties;
    }
}

/*
 * Helper class for rankTransform map.
 */
class KeyAndValueData<K> implements Comparable<KeyAndValueData<K>> {
    private K key;

    private double value;

    public KeyAndValueData( K id, double v ) {
        this.key = id;
        this.value = v;
    }

    public int compareTo( KeyAndValueData<K> other ) {

        if ( this.value < other.value ) {
            return -1;
        } else if ( this.value > other.value ) {
            return 1;
        } else {
            return 0;
        }
    }

    public K getKey() {
        return key;
    }

    public double getValue() {
        return value;
    }
}

/*
 * Helper class for rankTransform .
 */
class RankData implements Comparable<RankData> {

    private int index;
    private double value;

    public RankData( int tindex, double tvalue ) {
        index = tindex;
        value = tvalue;
    }

    public int compareTo( RankData other ) {
        if ( this.value < other.getValue() ) {
            return -1;
        } else if ( this.value > other.getValue() ) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getIndex() {
        return index;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Index=" + index + " value=" + value;
    }
}