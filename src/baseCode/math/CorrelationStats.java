package baseCode.math;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.jet.stat.Probability;

/**
 *
 * Statistical evaluation and transformation tools for correlations.
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class CorrelationStats {

   private static DoubleMatrix2D correlationPvalLookup;
   private static final double BINSIZE = 0.005; // resolution of correlation. Differences smaller than this are considered meaningless.
   private static final double STEPSIZE = BINSIZE * 2; // this MUST be more than the binsize.
   private static final int MAXCOUNT = 1000; // maximum number of things.
   private static final double LOG10 = Math.log( 10.0 );
   private static final double PVALCHOP = 8.0; // value by which log(pvalues) are scaled before storing as bytes. Values less than 10^e-256/PVALCHOP are 'clipped'.

   static {
      int numbins = ( int ) Math.ceil( 1.0 / BINSIZE );
      correlationPvalLookup = new SparseDoubleMatrix2D( numbins, MAXCOUNT + 1 );
   }

   /**
    *
    * @param correl Pearson correlation.
    * @param count Number of items used to calculate the correlation. NOT the
    *   degrees of freedom.
    * @return double
    */
   public static double pvalue( double correl, int count ) {

      double acorrel = Math.abs( correl );

      if ( acorrel == 1.0 ) {
         return 0.0;
      }

      if ( acorrel == 0.0 ) {
         return 1.0;
      }

      int dof = count - 2;

      if ( dof <= 0 ) {
         return 1.0;
      }

      int bin = ( int ) Math.ceil( acorrel / BINSIZE );
      if ( count <= MAXCOUNT && correlationPvalLookup.getQuick( bin, dof ) != 0.0 ) {
         return correlationPvalLookup.getQuick( bin, dof );
      } else {
         double t = correlationTstat( acorrel, dof );
         double p = Probability.studentT( dof, -t );
         if ( count < MAXCOUNT ) {
            correlationPvalLookup.setQuick( bin, dof, p );
         }
         return p;
      }
   }

   /**
    *
    * @param correl double
    * @return int
    */
   public static int correlAsByte( double correl ) {
      if ( correl == -1.0 ) {
         return 0;
      }

      return ( int ) ( Math.ceil( ( correl + 1.0 ) * 128 ) - 1 );
   }

   /**
    * Compute the Fisher transform of the Pearson correlation.
    *
    * @param r Correlation coefficient.
    * @return Fisher transform of the Correlation.
    */
   public static double fisherTransform( double r ) {
      return 0.5 * Math.log( ( 1.0 + r ) / ( 1.0 - r ) );
   }

   /**
    * Conver a correlation p value into a value between 0 and 255 inclusive.
    * This is done by taking the log, multiplying it by a fixed value (currently
    * 8). This means that pvalues less than 10^-32 are rounded to 10^-32.
    *
    * @param correl double
    * @param count int
    * @return int
    */
   public static int pvalueAsByte( double correl, int count ) {
      int p = - ( int ) Math.floor( PVALCHOP * ( Math.log( pvalue( correl, count ) ) / LOG10 ) );

      if ( p < 0 ) {
         return 0;
      } else if ( p > 255 ) {
         return 255;
      }
      return p;
   }

   /**
    *
    * @param pvalByte int
    * @return double
    */
   public static double byteToPvalue( int pvalByte ) {
      return Math.pow( 10.0, - ( double ) pvalByte / PVALCHOP );
   }

   /**
    *
    * @param correlByte int
    * @return double
    */
   public static double byteToCorrel( int correlByte ) {
      return ( double ) correlByte / 128.0 - 1.0;
   }

   /**
    * Compute the t-statistic associated with a Pearson correlation.
    * @param correl Pearson correlation
    * @param dof Degrees of freedom (n - 2)
    * @return double
    */
   public static double correlationTstat( double correl, int dof ) {
      return correl / Math.sqrt( ( 1.0 - correl * correl ) / ( double ) ( dof ) );
   }

   /**
    * Statistical comparison of two correlations. Assumes data are bivariate
    * normal. Null hypothesis is that the two correlations are equal. See Zar (Biostatistics)
    *
    * @param correl1 First correlation
    * @param n1 Number of values used to compute correl1
    * @param correl2 Second correlation
    * @param n2 Number of values used to compute correl2
    * @return double p value.
    */
   public static double compare( double correl1, int n1, double correl2, int n2 ) {

      double Z;
      double sigma;
      double p;

      sigma = Math.sqrt( ( 1 / ( ( double ) n1 - 3 ) ) + ( 1 / ( ( double ) n2 - 3 ) ) );

      Z = Math.abs( correl1 - correl2 ) / sigma;

      p = Probability.normal( -Z ); // upper tail.

      if ( p > 0.5 ) {
         return 1.0 - p;
      } else {
         return p;
      }

   }

   /**
    * Find the approximate correlation required to meet a particular pvalue.
    * This works by simple gradient descent.
    *
    * @param pval double
    * @param count int
    * @return double
    */
   public static double correlationForPvalue( double pval, int count ) {
      double stop = pval / 100.0;
      double err = 1.0;
      double corrguess = 1.0;
      double step = STEPSIZE;
      double preverr = 0.0;
      int maxiter = 1000;
      int iter = 0;
      while ( Math.abs( err ) > stop && step >= BINSIZE ) {
         double guess = pvalue( corrguess, count );
         if ( guess > pval ) {
            corrguess += step;
         } else {
            corrguess -= step;
         }

         if ( preverr * err < 0 ) { // opposite signs. Means we missed. Make step smaller and keep going.
            step /= 2;
         }

         preverr = err;
         err = pval - guess;
         iter++;

         if ( iter > maxiter ) {
            throw new IllegalStateException( "Too many iterations" );
         }
      }
      return ( corrguess );
   }

}
