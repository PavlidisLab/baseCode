package baseCode.dataStructure;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * <p>Title: SparseDoubleMatrix2DNamed</p>
 * <p>Description: A sparse matrix that knows about row and column names.</p>
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>

  @author Paul Pavlidis
  @version $Id$
 */
public class SparseDoubleMatrix2DNamed
    extends SparseDoubleMatrix2D
    implements NamedMatrix {

   private Vector rowNames;
   private Vector colNames;
   private Map rowMap; //contains a map of each row and elements in the row
   private Map colMap;
   /**
    *
    * @param rows int
    * @param cols int
    * @param T double[][]
    */
   public SparseDoubleMatrix2DNamed( int rows, int cols, double T[][] ) {
      super( T );
      rowMap = new HashMap(); //contains a map of each row name to index of the row.
      colMap = new HashMap();
      rowNames = new Vector();
      colNames = new Vector();
   }

   /**
    *
    * @param rows int
    * @param cols int
    */
   public SparseDoubleMatrix2DNamed( int rows, int cols ) {
      super( rows, cols );
      rowMap = new LinkedHashMap(); //contains a map of each row name to index of the row.
      rowNames = new Vector();
      colNames = new Vector();

   }

   public void set( int row, int col, Object value ) {
      super.set( row, col, ( ( Double ) value ).doubleValue() );
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
    *
    * @return java.util.Iterator
    */
   public Iterator getRowNameMapIterator() {
      return this.rowMap.keySet().iterator();
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
    * @param r String
    * @return boolean
    */
   public boolean hasRow( String r ) {
      return this.rowMap.containsKey( r );
   }

   /**
    *
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
    *
    * @param s String
    */
   public void addColumnName( String s ) {
      this.colNames.add( s );
      this.colMap.put( s, new Integer( columns() ) );
   }

   /**
    *
    * @param s String
    */
   public void addRowName( String s ) {
      this.rowNames.add( s );
      this.rowMap.put( s, new Integer( rows() ) );
   }

   /**
    *
    * @param s String
    * @return int
    */
   public int getRowIndexByName( String s ) {
      return ( ( Integer ) rowMap.get( s ) ).intValue();
   }

   /**
    *
    * @param r String
    * @return int
    */
   public int getColIndexByName( String r ) {
      return ( ( Integer )this.colMap.get( r ) ).intValue();
   }

   /**
    *
    * @param s String
    * @return double[]
    */
   public double[] getRowByName( String s ) {
      return getRow( getRowIndexByName( s ) );
   }

   /**
    *
    * @param i int
    * @return java.lang.String
    */
   public String getRowName( int i ) {
      return ( String ) rowNames.get( i );
   }

   /**
    *
    * @param i int
    * @return java.lang.String
    */
   public String getColName( int i ) {
      return ( String ) colNames.get( i );
   }

   /**
    *
    * @return boolean
    */
   public boolean hasRowNames() {
      return rowNames.size() == rows();
   }

   /**
    *
    * @return boolean
    */
   public boolean hasColNames() {
      return colNames.size() == columns();
   }

   /**
    *
    * @param v Vector
    */
   public void setRowNames( Vector v ) {
      for ( int i = 0; i < v.size(); i++ ) {
         addRowName( ( String ) v.get( i ) );
      }
   }

   /**
    *
    * @param v Vector
    */
   public void setColumnNames( Vector v ) {
      this.colNames = v;
   }

   /**
    *
    * @return java.util.Vector
    */
   public Vector getColNames() {
      return colNames;
   }
   
   
   public boolean isMissing(int i, int j) {
       return Double.isNaN(get(i,j));
   }
}
