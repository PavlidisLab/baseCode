package baseCode.algorithm.learning.unsupervised;

import baseCode.algorithm.Algorithm;
import baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix;
import baseCode.dataStructure.matrix.DenseDoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix1D;

import com.braju.beta.format.Format;
import com.braju.beta.format.Parameters;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis (port from Jason's code)
 * @version $Id$
 */
public class RankProp extends Algorithm {

   double alpha = 0.95; // alpha parameter, controls amount of "clustering"
   int maxIter = 20;// number of iterations of algorithm

   /**
    * @param matrix
    * @param query
    * @param k
    * @return
    */
   public DoubleMatrix1D computeRanking( AbstractNamedDoubleMatrix matrix,
         AbstractNamedDoubleMatrix query, int k ) {

      int dim = query.columns();
      DoubleMatrix1D y = new DenseDoubleMatrix1D( dim ); // we use own implementation for performance.s
      DoubleMatrix1D yold = new DenseDoubleMatrix1D( dim );
      DoubleMatrix1D yorig = new DenseDoubleMatrix1D( dim );

      y.assign( 0.0 ); // set all to zero.
      y.set( k, 1.0 ); // query point
      yorig.assign(query.viewRow(0).toArray()); // the query always has just one row.

   //   System.err.println( yorig );

      if ( alpha == 0.0 ) {
         return yorig;
      }

      int numActualRows = matrix.rows();
      for ( int loops = 0; loops < maxIter; loops++ ) { // iterations of propagation

         yold.assign( y ); // initially all zero except for 1 at the query point.

         for ( int j = 0; j < numActualRows; j++ ) {
            if ( j == k ) continue; // don't update query
          
            double dotProduct = matrix.viewRow( j ).zDotProduct( yold );

            // new y is old y +
            // new weighted linear combination of neighbors
            y.set( j, ( alpha * dotProduct ) + yorig.getQuick( j ) );
         }

         log.info( " iteration " + loops + " :  y[0]="
               + Format.sprintf( "%g", new Parameters( y.getQuick( 0 ) ) ) );

      }

      return y;

   }

   /**
    * @return Returns the alpha.
    */
   public double getAlpha() {
      return alpha;
   }

   /**
    * @param alpha The alpha to set.
    */
   public void setAlpha( double alpha ) {
      this.alpha = alpha;
   }

   /**
    * @return Returns the max_loops.
    */
   public int getMaxIter() {
      return maxIter;
   }

   /**
    * @param max_loops The max_loops to set.
    */
   public void setMaxIter( int maxIter ) {
      this.maxIter = maxIter;
   }
}

