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
      return ROC.aroc( totalSize, ranks, -1 );
   }

   /**
    * Calculate area under ROC, up to a given number of False positives. The input is the total number of items in the
    * data, and the ranks of the positives in the current ranking. LOW ranks are considered better. (e.g., rank 0 is the
    * 'best')
    * 
    * @param totalSize int
    * @param ranks Map
    * @param maxFP - the maximum number of false positives to see before stopping. Set to 50 to get the Gribskov roc50.
    *        If maxFP <= 0, it is ignored.
    * @return AROC
    */
   public static double aroc( int totalSize, Set ranks, int maxFP ) {
      int numPosSeen = 0;
      int numNegSeen = 0;
      int targetSize = ranks.size();
      
      if ( targetSize == 0 ) {
         return 0.0;
      }
      
      if (totalSize <= 0) {
         throw new IllegalArgumentException("Total size must be positive. ( received " + totalSize + ")");
      }
      
      double result = 0.0;
      for ( int i = 0; i < totalSize; i++ ) {
         if ( ranks.contains( new Integer( i ) ) ) { // if the ith item in the ranked list is a positive.
            numPosSeen++;
         //   System.err.print(i + "+ " );
         } else { 
         //   System.err.print(i +  "- " );
            result += numPosSeen;
            numNegSeen++;
            if ( maxFP > 0 && numNegSeen >= maxFP ) {
               break;
            }
         }

         //         if ( numPosSeen == targetSize ) { // we've seen all the positives, we can stop.
         //            result += numPosSeen * ( totalSize - i - 1 );
         //            break;
         //        }
      }
//      System.err.println( numNegSeen + " negs, " + numPosSeen
//            + " pos seen out of " + targetSize + " positives" );

      if (numPosSeen == 0 ) return 0.0;
      
      if ( maxFP > 0 ) {
         return result / ( targetSize * numNegSeen );
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