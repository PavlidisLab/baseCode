package baseCode.math;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Probability;

/**
 * Statistics for meta-analysis
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

public class MetaAnalysis {

   /**
    * @param pvals DoubleArrayList
    * @return double
    */
   public static double fisherCombinePvalues( DoubleArrayList pvals ) {
      double r = 0.0;
      for ( int i = 0, n = pvals.size(); i < n; i++ ) {
         r += Math.log( pvals.getQuick( i ) );
      }
      r *= -2.0;
      return Probability.chiSquare( r, 2.0 * ( double ) pvals.size() );
   }

   /**
    * Use for p values that have already been log transformed.
    * 
    * @param pvals DoubleArrayList
    * @return double
    */
   public static double fisherCombineLogPvalues( DoubleArrayList pvals ) {
      double r = 0.0;
      for ( int i = 0, n = pvals.size(); i < n; i++ ) {
         r += pvals.getQuick( i );
      }
      r *= -2.0;
      return Probability.chiSquare( r, 2.0 * ( double ) pvals.size() );
   }

   /**
    * 
    * @param r double
    * @param n int
    * @return double
    */
   public static double correlationVariance( double r, int n ) {
      if ( n < 2 ) {
         return Double.NaN;
      }
      double k = 1.0 - r * r;
      return k * k / ( n - 1 );
   }

   /**
    * 
    * @param effectSizes DoubleArrayList
    * @param variances DoubleArrayList
    * @param globalMean double
    * @return double
    */
   public static double qTest( DoubleArrayList effectSizes,
         DoubleArrayList variances, double globalMean ) {
      double r = 0.0;
      for ( int i = 0, n = effectSizes.size(); i < n; i++ ) {
         r += Math.pow( effectSizes.getQuick( i ) - globalMean, 2.0 )
               / variances.getQuick( i );
      }
      return r;
   }

}