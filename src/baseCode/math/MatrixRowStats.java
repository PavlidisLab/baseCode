package baseCode.math;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import cern.colt.list.DoubleArrayList;

/**
 * Convenience functions for getting row statistics from matrices.
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 * @todo Have min() and max() throw an EmptyMatrixException -- this exception
 *       class does not yet exist and needs to be defined somewhere.
 */
public class MatrixRowStats {

   private MatrixRowStats() {}

   public static DoubleArrayList sumOfSquares( DenseDoubleMatrix2DNamed M ) {
      return sumOfSquares( M, means( M ) );
   }

   /**
    * Calculates the sum of squares for each row of a matrix
    *
    * @param M DenseDoubleMatrix2DNamed
    * @param means DoubleArrayList
    * @return DoubleArrayList
    */
   public static DoubleArrayList sumOfSquares( DenseDoubleMatrix2DNamed M, DoubleArrayList means ) {
      int i;
      DoubleArrayList r = new DoubleArrayList( M.rows() );

      for ( i = 0; i < M.rows(); i++ ) {
         DoubleArrayList row = new DoubleArrayList( M.getRow( i ) );
         r.set( i,
                DescriptiveWithMissing.sumOfSquaredDeviations( row ) );
      }

      return r;
   }

   /**
    * Calculates the means of a matrix's rows.
    *
    * @param M DenseDoubleMatrix2DNamed
    * @return DoubleArrayList
    */
   public static DoubleArrayList means( DenseDoubleMatrix2DNamed M ) {
      int i;
      DoubleArrayList r = new DoubleArrayList( M.rows() );
      for ( i = 0; i < M.rows(); i++ ) {
         r.set( i, DescriptiveWithMissing.mean( new DoubleArrayList( M.getRow( i ) ) ) );
      }
      return r;
   }

   /**
    * Calculate the sums of a matrix's rows.
    *
    * @param M DenseDoubleMatrix2DNamed
    * @return DoubleArrayList
    */
   public static DoubleArrayList sums( DenseDoubleMatrix2DNamed M ) {
      int i;
      DoubleArrayList r = new DoubleArrayList( M.rows() );
      for ( i = 0; i < M.rows(); i++ ) {
         r.set( i, DescriptiveWithMissing.sum( new DoubleArrayList( M.getRow( i ) ) ) );
      }
      return r;
   }

   /**
    * Calculates the standard deviation of each row of a matrix
    *
    * @param M DenseDoubleMatrix2DNamed
    * @return DoubleArrayList
    */
   public static DoubleArrayList standardDeviations( DenseDoubleMatrix2DNamed M ) {
      int i;
      DoubleArrayList r = new DoubleArrayList( M.rows() );
      for ( i = 0; i < M.rows(); i++ ) {
         r.set( i,
                DescriptiveWithMissing.standardDeviation( DescriptiveWithMissing.variance(
             DescriptiveWithMissing.
             sumOfSquares( new DoubleArrayList( M.getRow( i ) ) ) ) ) );
      }
      return r;
   }

   /**
    * @param matrix DenseDoubleMatrix2DNamed
    * @return the smallest value in the matrix
    */
   public static double min( DenseDoubleMatrix2DNamed matrix ) {

      int totalRows = matrix.rows();
      int totalColumns = matrix.columns();

      // initial min candidate
      double min = matrix.get( 0, 0 );

      // the first N values might all be NaN (missing),
      // to try to find the first non-NaN value.
      int r = 0;
      int c = 0;
      for ( ; Double.isNaN( min ) && r < totalRows; r++ ) {
         for ( ; Double.isNaN( min ) && c < totalColumns; c++ ) {
            min = matrix.get( r, c ); // maybe this next value is not missing

            // unless all values are missing (the matrix is empty),
            // see if there are any values smaller than the one we already found
         }
      }
      if ( !Double.isNaN( min ) && r < totalRows && c < totalColumns ) {

         // see if there are any smaller values
         for ( r = 0; r < totalRows; r++ ) {
            for ( c = 0; c < totalColumns; c++ ) {
               double value = matrix.get( r, c ); // next min candidate
               if ( !Double.isNaN( value ) ) {
                  min = ( min > value ? value : min );
               }
            }
         } // end looking for smaller values

      } // end if matrix is not empty

      return min; // might be NaN if all values are missing

   } // end min

   /**
    * @param matrix DenseDoubleMatrix2DNamed
    * @return the largest value in the matrix
    */
   public static double max( DenseDoubleMatrix2DNamed matrix ) {

      int totalRows = matrix.rows();
      int totalColumns = matrix.columns();

      // initial max candidate
      double max = matrix.get( 0, 0 );

      // the first N values might all be NaN (missing),
      // to try to find the first non-NaN value.
      int r = 0;
      int c = 0;
      for ( ; Double.isNaN( max ) && r < totalRows; r++ ) {
         for ( ; Double.isNaN( max ) && c < totalColumns; c++ ) {
            max = matrix.get( r, c ); // maybe this next value is not missing

            // unless all values are missing (the matrix is empty),
            // see if there are any values smaller than the one we already found
         }
      }
      if ( !Double.isNaN( max ) && r < totalRows && c < totalColumns ) {

         // see if there are any smaller values
         for ( r = 0; r < totalRows; r++ ) {
            for ( c = 0; c < totalColumns; c++ ) {
               double value = matrix.get( r, c ); // next min candidate
               if ( !Double.isNaN( value ) ) {
                  max = ( max < value ? value : max );
               }
            }
         } // end looking for smaller values

      } // end if matrix is not empty

      return max; // might be NaN if all values are missing

   } // end max

}
