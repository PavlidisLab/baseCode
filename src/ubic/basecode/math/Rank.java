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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private static Log log = LogFactory.getLog( Rank.class );

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
    public static int rankSum( List<Double> ranks ) {
        double sum = 0.0;
        for ( Double rank : ranks ) {
            sum += rank;
        }
        return ( int ) sum;
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
     * @param array, or null if the ranks could not be computed.
     * @return
     */
    public static DoubleArrayList rankTransform( DoubleArrayList array ) {
        return rankTransform( array, false );
    }

    /**
     * Rank transform an array. The ranks are constructed based on the sort order of the elements. That is, low values
     * get low numbered ranks starting from 1, unless you set descending = true
     * <p>
     * Ties are resolved by assigning the average rank for tied values. For example, instead of arbitrarily assigning
     * ties ranks 3,4,5, all three values would get a rank of 4 and no value would get a rank of 3 or 5.
     * <p>
     * Missing values are not handled particularly gracefully: missing values (Double.NaN) are treated as per their
     * natural sort order.
     * 
     * @param array DoubleArrayList
     * @param descending - reverse the usual ordering so larger values are the the front.
     * @return cern.colt.list.DoubleArrayList, or null if the input is empty or null.
     */
    public static DoubleArrayList rankTransform( DoubleArrayList array, boolean descending ) {
        if ( array == null ) {
            log.error( "Array was null" );
            return null;
        }

        int size = array.size();
        if ( size == 0 ) {
            return null;
        }

        List<RankData> ranks = new ArrayList<RankData>( size );
        DoubleArrayList result = new DoubleArrayList( new double[size] );

        // store the values with their indices - not sorted yet.
        for ( int i = 0; i < size; i++ ) {
            double v = array.get( i );
            if ( descending ) {
                v = -v;
            }
            RankData rd = new RankData( i, v );
            ranks.add( rd );
        }

        Collections.sort( ranks );

        // fill in the results. We iterate over the ranks by order.
        Double prevVal = null;
        int rank = 1;
        int nominalRank = 1; // rank we'd have if no ties.
        for ( int i = 0; i < size; i++ ) {
            RankData rankData = ranks.get( i );
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

            prevVal = val;
            nominalRank++;
        }

        // At this point we may have repeated ranks.

        fixTies( result, ranks );

        return result;
    }

    /**
     * Rank transform a map, where the values are numerical (java.lang.Double) values we wish to rank. Ties are broken
     * as for the other methods. Ranks are zero-based.
     * 
     * @param m java.util.Map with keys Objects, values Doubles.
     * @return A java.util.Map keys=old keys, values=java.lang.Integer rank of the key.
     */
    public static <K> Map<K, Double> rankTransform( Map<K, Double> m ) {
        return rankTransform( m, false );
    }

    // /**
    // * Replace the values in one list with values from another, based on ranks. Thus the n'th highest value in the
    // first
    // * array is replaced with the n'th highest value in the second.
    // *
    // * @param toReplace
    // * @param sourceOfValues
    // * @return values in sourceOfValues, in the order of the ranks of to
    // */
    // public static List<Double> replaceByRanks( List<Double> toReplace, List<Double> sourceOfValues ) {
    // if ( toReplace.size() != sourceOfValues.size() ) {
    // throw new IllegalArgumentException( "Lists must be of same size" );
    // }
    // }

    /**
     * Ties are broken as for the other methods. CAUTION - ranks start at 0.
     * 
     * @param <K>
     * @param m
     * @param desc if true, the lowest (first) rank will be for the highest value.
     * @return
     */
    public static <K> Map<K, Double> rankTransform( Map<K, Double> m, boolean desc ) {

        List<KeyAndValueData<K>> values = new ArrayList<KeyAndValueData<K>>();

        for ( Iterator<K> itr = m.keySet().iterator(); itr.hasNext(); ) {

            K key = itr.next();

            double val = m.get( key ).doubleValue();
            values.add( new KeyAndValueData<K>( 0, key, val ) );
        }

        /* sort it */
        Collections.sort( values );
        Map<K, Double> result = new HashMap<K, Double>();

        Double rank = 0.0;
        Double prevVal = null;
        Double nominalRank = 0.0; // rank we'd have if no ties.
        for ( int i = 0; i < m.size(); i++ ) {
            result.put( values.get( i ).getKey(), ( double ) nominalRank ); // might not keep.

            double val = values.get( i ).getValue();
            // only bump up ranks if we're not tied with the last one.
            if ( prevVal != null && val != prevVal.doubleValue() ) {
                rank = nominalRank;
            } else {
                // tied. Do not advance the rank.
                result.put( values.get( i ).getKey(), rank );
            }

            prevVal = val;
            nominalRank++;

        }

        fixTies( result, values );

        if ( desc ) {
            /*
             * Reverse all the values.
             */
            Map<K, Double> finalResult = new HashMap<K, Double>();
            double mr = result.size();
            for ( K k : result.keySet() ) {
                double d = result.get( k );
                finalResult.put( k, mr - d - 1.0 );
            }
            return finalResult;
        }

        return result;
    }

    /**
     * @param ranksWithTies
     */
    private static void fixTies( DoubleArrayList ranksWithTies, List<? extends RankData> ranks ) {

        int numties = 1;
        Double prev = null;
        int i = 0;
        // iterate over in order of the values.
        for ( int j = ranksWithTies.size(); i < j; i++ ) {
            RankData rankData = ranks.get( i );
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
                        int indexOfTied = ranks.get( i - k ).getIndex(); // original rank?
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
     * @param <K>
     * @param ranksWithTies
     * @param ranks
     */
    private static <K> void fixTies( Map<K, Double> ranksWithTies, List<KeyAndValueData<K>> ranks ) {
        int numties = 1;
        Double prev = null;
        int i = 0;
        // iterate over in order of the values.
        for ( int j = ranksWithTies.size(); i < j; i++ ) {
            KeyAndValueData<K> rankData = ranks.get( i );
            double rank = ranksWithTies.get( rankData.getKey() );
            if ( prev != null ) {
                if ( rank == prev.doubleValue() ) {
                    // record how many ties we have seen
                    numties++;
                    // System.err.println( "Tied rank: " + rank );
                } else if ( numties > 1 ) {
                    // we're past the batch of ties and need to adjust them
                    for ( int k = 1; k <= numties; k++ ) {
                        K indexOfTied = ranks.get( i - k ).getKey(); // original key
                        double rawRankInTie = ranksWithTies.get( indexOfTied );
                        double meanRank = meanRank( rawRankInTie, numties );
                        ranksWithTies.put( indexOfTied, meanRank );
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
                K indexOfTied = ranks.get( i - k ).getKey();
                double rawRankInTie = ranksWithTies.get( indexOfTied );
                double meanRank = meanRank( rawRankInTie, numties );
                ranksWithTies.put( indexOfTied, meanRank );
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
class KeyAndValueData<K> extends RankData {
    private K key;

    public KeyAndValueData( int index, K id, double v ) {
        super( index, v );
        this.key = id;
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
}

/*
 * Helper class for rankTransform .
 */
class RankData implements Comparable<RankData> {

    int index = 0;

    double value = 0;

    public RankData( int tindex, double tvalue ) {
        index = tindex;
        value = tvalue;
    }

    @Override
    public int compareTo( RankData other ) {
        if ( this.getValue() < other.getValue() ) {
            return -1;
        } else if ( this.getValue() > other.getValue() ) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        RankData other = ( RankData ) obj;
        if ( Double.doubleToLongBits( value ) != Double.doubleToLongBits( other.value ) ) return false;
        return true;
    }

    public int getIndex() {
        return index;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits( value );
        result = prime * result + ( int ) ( temp ^ ( temp >>> 32 ) );
        return result;
    }

    @Override
    public String toString() {
        return "Index=" + index + " value=" + value;
    }
}