package baseCode.math.distribution;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.random.Beta;
import cern.jet.random.engine.RandomEngine;
import cern.jet.stat.Descriptive;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class Dirichlet {

   private Algebra a = new Algebra();
   private RandomEngine r;

   private DoubleArrayList p;
   private Beta rbeta;

   public Dirichlet( DoubleArrayList p, RandomEngine randomGenerator ) {
      if ( randomGenerator == null ) throw new IllegalArgumentException( "Null random number generator" );
      this.r = randomGenerator;
      this.p = p;
      rbeta = new Beta( 1, 1, r );
   }

   public double nextDouble() {

      return 0;
   }

   public DoubleArrayList draws( int n ) {

      DoubleMatrix2D mat = new DenseDoubleMatrix2D( n, p.size() );
      double psum = Descriptive.sum( (DoubleArrayList)p.partFromTo( 1, p.size() - 1 ) );

      for ( int i = 0; i < mat.rows(); i++ ) {
         mat.setQuick( 0, i, rbeta.nextDouble( p.getQuick( 0 ), psum ) );
      }

      for (int i = 1; i < p.size(); i++) {
         for ( int j = 0; j < mat.rows(); j++ ) {
            mat.setQuick( i, j , rbeta.nextDouble( p.getQuick( 0 ), psum ) );
         }
      }
      
      return null;
   }
}
//rdirichlet <- function ( n, p ) {
//   # return n random samples from a Dirichlet distribution with parameter p
//     if ( !is.vector(n, "numeric")
//          | length(n) != 1
//          | !is.vector(p, "numeric")
//        ) { stop("error in call to rdirichlet") }
//     mat <- matrix ( NA, n, length(p) )
//     mat[,1] <- rbeta ( n, p[1], sum(p[-1]) )
//     
//   for ( i in 2:(length(p)-1) ) {
//    
//     mat[,i] <- ( rbeta ( n, p[i], sum(p[(i+1):length(p)]) )
//                     * ( 1 - apply ((mat[,1:(i-1),drop=F]), 1, sum) )
//                  )
//     }
//     mat[,length(p)] <- 1 - apply ( (mat[,-length(p),drop=F]), 1, sum )
//
//     return ( mat )
//   }
