package baseCode.dataStructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import cern.colt.matrix.impl.DenseObjectMatrix2D;

/**
* A NamedMatrix containing String objects.
 * <p> Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StringMatrix2DNamed
    extends DenseObjectMatrix2D
    implements NamedMatrix {

   private Vector rowNames;
   private Vector colNames;
   private Map rowMap; //contains a map of each row and elements in the row
   private Map colMap;

   public StringMatrix2DNamed( int x, int y ) {
      super( x, y );
      rowNames = new Vector();
      colNames = new Vector();
      rowMap = new HashMap();
      colMap = new HashMap();
   }

   /**
    *
    * @return java.lang.String
    */
   public String toString() {
      String result = "label";
      for ( int i = 0; i < columns(); i++ ) {
         result = result + "\t" + getColName( i );
      }
      result += "\n";

      for ( int i = 0; i < rows(); i++ ) {
         result += getRowName( i );
         for ( int j = 0; j < columns(); j++ ) {
            result = result + "\t" + get( i, j );
         }
         result += "\n";
      }
      return result;
   }

   public void addColumnName( String s ) {
      this.colNames.add( s );
      this.colMap.put( s, new Integer( columns() ) );
   }

   public void addRowName( String s ) {
      this.rowNames.add( s );
      this.rowMap.put( s, new Integer( rows() ) );
   }

   public int getRowIndexByName( String s ) {
      return ( ( Integer ) rowMap.get( s ) ).intValue();
   }

   public int getColIndexByName( String r ) {
      return ( ( Integer )this.colMap.get( r ) ).intValue();
   }

   public String getRowName( int i ) {
      return ( String ) rowNames.get( i );
   }

   public String getColName( int i ) {
      return ( String ) colNames.get( i );
   }

   public boolean hasRowNames() {
      return rowNames.size() == rows();
   }

   public boolean hasColNames() {
      return colNames.size() == columns();
   }

   public void setRowNames( Vector v ) {
      for ( int i = 0; i < v.size(); i++ ) {
         addRowName( ( String ) v.get( i ) );
      }
   }

   /**
    *
    * @return java.util.Vector
    */
   public Vector getColNames() {
      return colNames;
   }

   public Object[] getRow( int row ) {
      return viewRow( row ).toArray();
   }

   public Object[] getCol( int col ) {
      String[] result = new String[rows()];
      for ( int i = 0; i < rows(); i++ ) {
         result[i] = ( String ) get( i, col );
      }
      return result;
   }

   public Object[] getRowObj( int row ) {
      String[] result = new String[columns()];
      for ( int i = 0; i < columns(); i++ ) {
         result[i] = ( String ) get( row, i );
      }
      return result;
   }

   public Object[] getColObj( int col ) {
      String[] result = new String[rows()];
      for ( int i = 0; i < rows(); i++ ) {
         result[i] = ( String ) get( i, col );
      }
      return result;
   }

   /**
    *
    * @return java.util.Iterator
    */
   public Iterator getRowNameMapIterator() {
      return this.rowMap.keySet().iterator();
   }

   /**
    *
    * @param r String
    * @return boolean
    */
   public boolean hasRow( String r ) {
      return this.rowMap.get( r ) == null;
   }

   public void setColumnNames( Vector v ) {
      this.colNames = v;
   }

}
