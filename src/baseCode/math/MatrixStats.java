package baseCode.math;

import cern.colt.list.DoubleArrayList;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.SparseDoubleMatrix2DNamed;

/**
 * 
 * 
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class MatrixStats {

   /**
    * @todo this is pretty inefficient - calls new
    * @param data
    * @return @throws OutOfMemoryError
    */
   public static DenseDoubleMatrix2DNamed correlationMatrix(
         DenseDoubleMatrix2DNamed data ) throws OutOfMemoryError {
      DenseDoubleMatrix2DNamed result = new DenseDoubleMatrix2DNamed( data
            .rows(), data.rows() );

      for ( int i = 0; i < data.rows(); i++ ) {
         DoubleArrayList irow = new DoubleArrayList( data.getRow( i ) );
         for ( int j = i + 1; j < data.rows(); j++ ) {
            DoubleArrayList jrow = new DoubleArrayList( data.getRow( j ) );
            double c = DescriptiveWithMissing.correlation( irow, jrow );
            result.setQuick( i, j, c );
            result.setQuick( j, i, c );
         }
      }
      result.setRowNames( data.getRowNames() );
      result.setColumnNames( data.getRowNames() );

      return result;
   }

   /**
    * 
    * @param data
    * @param threshold only correlations with absolute values above this level
    *        are stored.
    * @return SparseDoubleMatrix2DNamed
    * @throws OutOfMemoryError
    */
   public static SparseDoubleMatrix2DNamed correlationMatrix(
         DenseDoubleMatrix2DNamed data, double threshold )
         throws OutOfMemoryError {
      SparseDoubleMatrix2DNamed result = new SparseDoubleMatrix2DNamed( data
            .rows(), data.rows() );

      for ( int i = 0; i < data.rows(); i++ ) {
         DoubleArrayList irow = new DoubleArrayList( data.getRow( i ) );
         for ( int j = i + 1; j < data.rows(); j++ ) {
            DoubleArrayList jrow = new DoubleArrayList( data.getRow( j ) );
            double c = DescriptiveWithMissing.correlation( irow, jrow );
            if ( Math.abs( c ) > threshold ) {
               result.setQuick( i, j, c );
               result.setQuick( j, i, c );
            }
         }
      }
      result.setRowNames( data.getRowNames() );
      result.setColumnNames( data.getRowNames() );

      return result;
   }

   /**
    * Find the minimum of the entire matrix.
    * 
    * @param matrix DenseDoubleMatrix2DNamed
    * @return the smallest value in the matrix
    */
   public static double min( DenseDoubleMatrix2DNamed matrix ) {

      int totalRows = matrix.rows();
      int totalColumns = matrix.columns();

      double min = Double.MAX_VALUE;

      for ( int i = 0; i < totalRows; i++ ) {
         for ( int j = 0; j < totalColumns; j++ ) {
            double val = matrix.getQuick( i, j );
            if ( Double.isNaN( val ) ) {
               continue;
            }

            if ( val < min ) {
               min = val;
            }

         }
      }
      if ( min == Double.MAX_VALUE ) {
         return Double.NaN;
      }
      return min; // might be NaN if all values are missing

   } // end min

   /**
    * Compute the maximum value in the matrix.
    * 
    * @param matrix DenseDoubleMatrix2DNamed
    * @return the largest value in the matrix
    */
   public static double max( DenseDoubleMatrix2DNamed matrix ) {

      int totalRows = matrix.rows();
      int totalColumns = matrix.columns();

      double max = -Double.MAX_VALUE;

      for ( int i = 0; i < totalRows; i++ ) {
         for ( int j = 0; j < totalColumns; j++ ) {
            double val = matrix.getQuick( i, j );
            if ( Double.isNaN( val ) ) {
               continue;
            }

            if ( val > max ) {
               max = val;
            }

         }
      }

      if ( max == -Double.MAX_VALUE ) {
         return Double.NaN;
      }

      return max; // might be NaN if all values are missing

   }
}