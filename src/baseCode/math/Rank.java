package baseCode.math;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.ObjectArrayList;

/**
 * Calculate rank statistics for arrays.
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Rank {

   /**
    * Rank transform an array. Ties are not handled specially.
    * 
    * @param array
    *           DoubleArrayList
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
    * Rank transform a map, where the values are numerical (java.lang.Double)
    * values we wish to rank. Ties are not handled specially.
    * 
    * @param m
    *           java.util.Map with keys Objects, values Doubles.
    * @return A java.util.Map keys=old keys, values=java.lang.Integer rank of
    *         the key.
    * @throws IllegalArgumentException if the input Map does not have Double values.
    */
   public static Map rankTransform( Map m ) throws IllegalArgumentException {
      int counter = 0;
      
      keyAndValueData[] values = new keyAndValueData[m.size()];

      /*
       * put the pvalues into an array of objects which contain the pvalue and
       * the gene id
       */
      for (Iterator itr = m.keySet().iterator(); itr.hasNext(); ) {
  
         Object key = itr.next();

         if ( !( m.get(key) instanceof Double ) ) {
            throw new IllegalArgumentException(
                  "Attempt to rank a map with non-Double values" );
         }

         double val = ( double ) ( ( ( Double ) m.get(key) )
               .doubleValue() );
         values[counter] = new keyAndValueData( key, val );
         counter++;
      }

      /* sort it */
      Arrays.sort( values );
      Map result = new LinkedHashMap();
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

/*
 * Helper class for rankTransform map.
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

      if ( this.value > other.value ) {
         return 1;
      } else if ( this.value < other.value ) {
         return -1;
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