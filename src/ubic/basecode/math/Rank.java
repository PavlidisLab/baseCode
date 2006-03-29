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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
     * positions of the sorted values in the current array (the lowest value has the lowest rank, but it could be
     * located anywhere in the array).
     * 
     * @param array
     * @return
     */
    public static IntArrayList order( DoubleArrayList array ) {

        // this is easily done, albeit very inefficiently, if we 1) get the ranks and then 2) find the indexes of the
        // ranks.
        IntArrayList ranks = rankTransform( array );

        IntArrayList order = new IntArrayList( ranks.size() );

        for ( int i = 0; i < ranks.size(); i++ ) {

            // find the ith item.
            for ( int j = 0; j < ranks.size(); j++ ) {
                if ( i == ranks.getQuick( j ) ) {
                    order.add( j );
                    break;
                }
            }

        }
        return order;
    }

    /**
     * @param ranks
     * @return
     */
    public static int rankSum( Collection ranks ) {
        int sum = 0;
        for ( Iterator iter = ranks.iterator(); iter.hasNext(); ) {
            Integer rank = ( Integer ) iter.next();
            sum += rank.intValue();
        }
        return sum;
    }

    /**
     * Rank transform an array. Ties are not handled specially. The ranks are constructed based on the sort order of the
     * elements. That is, low values get low numbered ranks.
     * 
     * @param array DoubleArrayList
     * @return cern.colt.list.DoubleArrayList
     */
    public static IntArrayList rankTransform( DoubleArrayList array ) {
        if ( array == null ) {
            throw new IllegalArgumentException( "Null array" );
        }

        int size = array.size();
        if ( size == 0 ) {
            return null;
        }

        ObjectArrayList ranks = new ObjectArrayList( size );
        IntArrayList result = new IntArrayList( new int[size] );

        // store the ranks in the array.
        for ( int i = 0; i < size; i++ ) {
            rankData rd = new rankData( i, array.get( i ) );
            ranks.add( rd );
        }

        ranks.sort();

        // fill in the results.
        for ( int i = 0; i < size; i++ ) {
            result.set( ( ( rankData ) ranks.get( i ) ).getIndex(), i );
        }

        return result;
    }

    /**
     * Rank transform a map, where the values are numerical (java.lang.Double) values we wish to rank. Ties are not
     * handled specially. Ranks are zero-based.
     * 
     * @param m java.util.Map with keys Objects, values Doubles.
     * @return A java.util.Map keys=old keys, values=java.lang.Integer rank of the key.
     * @throws IllegalArgumentException if the input Map does not have Double values.
     */
    public static Map rankTransform( Map m ) throws IllegalArgumentException {
        int counter = 0;

        keyAndValueData[] values = new keyAndValueData[m.size()];

        /*
         * put the pvalues into an array of objects which contain the pvalue and the gene id
         */
        for ( Iterator itr = m.keySet().iterator(); itr.hasNext(); ) {

            Object key = itr.next();

            if ( !( m.get( key ) instanceof Double ) ) {
                throw new IllegalArgumentException( "Attempt to rank a map with non-Double values" );
            }

            double val = ( ( Double ) m.get( key ) ).doubleValue();
            values[counter] = new keyAndValueData( key, val );
            counter++;
        }

        /* sort it */
        Arrays.sort( values );
        Map result = new HashMap();
        /* put the sorted items back into a hashmap with the rank */
        for ( int i = 0; i < m.size(); i++ ) {
            result.put( values[i].getKey(), new Integer( i ) );
        }
        return result;
    }
}

/*
 * Helper class for rankTransform.
 */

class keyAndValueData implements Comparable {
    private Object key;

    private double value;

    public keyAndValueData( Object id, double v ) {
        this.key = id;
        this.value = v;
    }

    public int compareTo( Object ob ) {
        keyAndValueData other = ( keyAndValueData ) ob;

        if ( this.value < other.value ) {
            return -1;
        } else if ( this.value > other.value ) {
            return 1;
        } else {
            return 0;
        }
    }

    public Object getKey() {
        return key;
    }

    public double getValue() {
        return value;
    }
}

/*
 * Helper class for rankTransform map.
 */

class rankData implements Comparable {

    private int index;
    private double value;

    public rankData( int tindex, double tvalue ) {
        index = tindex;
        value = tvalue;
    }

    public int compareTo( Object a ) {
        rankData other = ( rankData ) ( a );
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
}