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
 * <p> Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Rank {

   /**
    * Rank transform an array. Ties are not handled specially.
    *
    * @param array DoubleArrayList
    * @return cern.colt.list.DoubleArrayList
    */
   public static DoubleArrayList rankTransform( DoubleArrayList array ) {
      ObjectArrayList ranks = new ObjectArrayList( array.size() );
      DoubleArrayList result = new DoubleArrayList( array.size() );

      for ( int i = 0; i < array.size(); i++ ) {
         ranks.set( i, new rankData( i, array.getQuick( i ) ) );
      }

      ranks.sort();

      for ( int i = 0; i < array.size(); i++ ) {
         result.set( ( ( rankData ) ranks.get( i ) ).getIndex(), i );
      }

      return result;
   }

   /**
    * Rank transform a map, where the keys are strings and the values are the
    * numerical values we wish to rank. Ties are not handled specially.
    *
    * @param m java.util.Map with keys strings, values doubles.
    * @return A java.util.Map keys=old keys, values=integer rank of the key.
    */
   public static Map rankTransform( Map m ) {
      int counter = 0;
      LinkedHashMap result = new LinkedHashMap();
      keyAndValueData[] values = new keyAndValueData[m.size()];
      Collection entries = m.entrySet();
      Iterator itr = entries.iterator();

      /* put the pvalues into an array of objects which contain the
       * pvalue and the gene id */
      while ( itr.hasNext() ) {
         Map.Entry tuple = ( Map.Entry ) itr.next();
         String key = ( String ) tuple.getKey();
         double val = ( double ) ( ( ( Double ) tuple.getValue() ).doubleValue() );
         values[counter] = new keyAndValueData( key, val );
         counter++;
      }

      /* sort it */
      Arrays.sort( values );

      /* put the sorted items back into a hashmap with the rank */
      for ( int i = 0; i < m.size(); i++ ) {
         result.put( values[i].getId(), new Integer( m.size() - i ) );
      }
      return result;
   }

}

/*
 * Helper class for rankTransform.
 */
class rankData
    implements Comparable {
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
    Helper class for rankTransform map.
 */
class keyAndValueData
    implements Comparable {
   private String key;
   private double value;

   public keyAndValueData( String id, double v ) {
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

   public String getId() {
      return key;
   }

   public double getValue() {
      return value;
   }
}
