package baseCode.dataStructure;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import corejava.Format;

/**
 *  A matrix that knows about row and column names.</p>
 * <p>Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
  @author Paul Pavlidis
  @version $Id$
 */
public class DenseDoubleMatrix2DNamed
    extends AbstractNamedMatrix implements NamedMatrix {

   private DenseDoubleMatrix2D matrix;


   /**
    *
    * @param T double[][]
    */
   public DenseDoubleMatrix2DNamed(double T[][] ) {
      super();
      matrix = new DenseDoubleMatrix2D(T );
 }

   /**
    *
    * @param rows int
    * @param cols int
    */
   public DenseDoubleMatrix2DNamed( int rows, int cols ) {
      super();
      matrix = new DenseDoubleMatrix2D(rows, cols);
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
    *
    * @return java.lang.String
    */
   public String toString() {
      Format nf = new Format( "%.4g" );
      StringBuffer result = new StringBuffer( this.rows() * this.columns() );
      if ( this.hasColNames() || this.hasRowNames() ) {
         result.append( "label" );
      }

      if ( this.hasColNames() ) {
         for ( int i = 0; i < columns(); i++ ) {
            result.append( "\t" + getColName( i ) );
         }
         result.append( "\n" );
      }

      for ( int i = 0; i < rows(); i++ ) {
         if ( this.hasRowNames() ) {
            result.append( getRowName( i ) );
         }
         for ( int j = 0; j < columns(); j++ ) {
            if ( Double.isNaN( get( i, j ) ) ) {
               result.append( "\t" );
            } else {
               result.append( "\t" + nf.form( get( i, j ) ) );
            }
         }
         result.append( "\n" );
      }
      return result.toString();
   }



   /**
    *
    * @param s String
    * @return double[]
    */
   public double[] getRowByName( String s ) {
      return getRow( getRowIndexByName( s ) );
   }


 
   public void set( int row, int col, Object value ) {
      set( row, col, ( ( Double ) value ).doubleValue() );
   }

   /**
    *
    * @todo make this use copy instead
    * @return baseCode.dataStructure.DenseDoubleMatrix2DNamed
    */
   public DenseDoubleMatrix2DNamed copyMatrix() {
      DenseDoubleMatrix2DNamed returnval = new DenseDoubleMatrix2DNamed( this.rows(), this.columns() );
      for ( int i = 0, n = this.rows(); i < n; i++ ) {
         returnval.addRowName( this.getRowName( i ), i );
         for ( int j = 0, m = this.columns(); j < m; j++ ) {
            if ( i == 0 ) {
               returnval.addColumnName( this.getColName( j ), j );
            }
            returnval.set( i, j, this.get( i, j ) );
         }
      }
      return returnval;
   }

   public boolean isMissing(int i, int j) {
       return Double.isNaN(get(i,j));
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
    * @param row
    * @return
    */
   public DoubleMatrix1D viewRow( int row ) {
      return matrix.viewRow( row );
   }
   /**
    * @return
    */
   public int columns() {
      return matrix.columns();
   }
   /**
    * @return
    */
   public int rows() {
      return matrix.rows();
   }
}
