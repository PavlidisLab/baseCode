package baseCode.math;

import java.util.Set;

import cern.jet.stat.Probability;

/**
 * Functions for calculating Receiver operator characteristics.
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class ROC {

   /**
    * Calculate area under ROC. The input is the total number of items in the data, and the ranks of the positives in
    * the current ranking. LOW ranks are considered better. (e.g., rank 0 is the 'best')
    * 
    * @param totalSize int
    * @param ranks Map
    * @return AROC
    */
   public static double aroc( int totalSize, Set ranks ) {
      int numPosSeen = 0;
      int targetSize = ranks.size();
      if ( targetSize == 0 ) {
         return 0.0;
      }
      double result = 0.0;
      for ( int i = 0; i < totalSize; i++ ) {
         if ( ranks.contains( new Integer( i ) ) ) {
            numPosSeen++;
         //   System.err.print( "+" );
         } else {
         //   System.err.print( "-" );
            result += numPosSeen;
         }

         if ( numPosSeen == targetSize ) { // we've seen all the positives, we can stop.
            result += numPosSeen * ( totalSize - i - 1 );
            break;
         }
      }
      return result / ( numPosSeen * ( totalSize - targetSize ) );
   }

   /**
    * For an AROC value, calculates a p value based on approximation for calculating the stanadard deviation. Highly
    * approximate!
    * 
    * @param numpos How many positives are in the data.
    * @param aroc The AROC
    * @return The p value.
    */
   public static double rocpval( int numpos, double aroc ) {
      double stdev = Math.exp( -0.5 * ( Math.log( numpos ) + 1 ) );
      double z = ( aroc - 0.5 ) / stdev;

      /* We are only interested in the upper tails. */
      if ( z < 0.0 ) {
         z = 0.0;
      }
      return 1.0 - Probability.normal( z );
   }

}