package baseCode.dataStructure.matrix;

import java.text.NumberFormat;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * <p>
 * Title: SparseDoubleMatrix2DNamed
 * </p>
 * <p>
 * Description: A sparse matrix that knows about row and column names.
 * </p>
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution:: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class SparseDoubleMatrix2DNamed extends AbstractNamedMatrix implements
      NamedMatrix {

   private SparseDoubleMatrix2D matrix;

   /**
    * @param T double[][]
    */
   public SparseDoubleMatrix2DNamed( double T[][] ) {
      super();
      matrix = new SparseDoubleMatrix2D( T );
   }

   /**
    * @param rows int
    * @param cols int
    */
   public SparseDoubleMatrix2DNamed( int rows, int cols ) {
      super();
      matrix = new SparseDoubleMatrix2D( rows, cols );
   }

   public void set( int row, int col, Object value ) {
      set( row, col, ( ( Double ) value ).doubleValue() );
   }

   /**
    * Return a reference to a specific row.
    * 
    * @param row int
    * @return double[]
    */
   public double[] getRow( int row ) {
      return viewRow( row ).toArray();
   }

   /**
    * Return a copy of a given column.
    * 
    * @param col int
    * @return double[]
    */
   public double[] getCol( int col ) {
      double[] result = new double[rows()];
      for ( int i = 0; i < rows(); i++ ) {
         result[i] = get( i, col );
      }
      return result;
   }

   public Object[] getRowObj( int row ) {
      Double[] result = new Double[columns()];
      for ( int i = 0; i < columns(); i++ ) {
         result[i] = new Double( get( row, i ) );
      }
      return result;
   }

   public Object[] getColObj( int col ) {
      Double[] result = new Double[rows()];
      for ( int i = 0; i < rows(); i++ ) {
         result[i] = new Double( get( i, col ) );
      }
      return result;
   }

   /**
    * @return java.lang.String
    */
   public String toString() {
      NumberFormat nf = NumberFormat.getInstance();
      String result = "";
      if ( this.hasColNames() || this.hasRowNames() ) {
         result = "label";
      }

      if ( this.hasColNames() ) {
         for ( int i = 0; i < columns(); i++ ) {
            result = result + "\t" + getColName( i );
         }
         result += "\n";
      }

      for ( int i = 0; i < rows(); i++ ) {
         if ( this.hasRowNames() ) {
            result += getRowName( i );
         }
         for ( int j = 0; j < columns(); j++ ) {
            result = result + "\t" + nf.format( get( i, j ) );
         }
         result += "\n";
      }
      return result;
   }

   /**
    * @param s String
    * @return double[]
    */
   public double[] getRowByName( String s ) {
      return getRow( getRowIndexByName( s ) );
   }

   public boolean isMissing( int i, int j ) {
      return Double.isNaN( get( i, j ) );
   }

   /**
    * @return
    */
   public int columns() {
      return matrix.columns();
   }

   /**
    * @param row
    * @param column
    * @return
    */
   public double get( int row, int column ) {
      return matrix.get( row, column );
   }

   /**
    * @param row
    * @param column
    * @return
    */
   public double getQuick( int row, int column ) {
      return matrix.getQuick( row, column );
   }

   /**
    * @return
    */
   public int rows() {
      return matrix.rows();
   }

   /**
    * @param row
    * @param column
    * @param value
    */
   public void set( int row, int column, double value ) {
      matrix.set( row, column, value );
   }

   /**
    * @param row
    * @param column
    * @param value
    */
   public void setQuick( int row, int column, double value ) {
      matrix.setQuick( row, column, value );
   }

   /**
    * @param column
    * @return
    */
   public DoubleMatrix1D viewColumn( int column ) {
      return matrix.viewColumn( column );
   }

   /**
    * @param row
    * @return
    */
   public DoubleMatrix1D viewRow( int row ) {
      return matrix.viewRow( row );
   }

}