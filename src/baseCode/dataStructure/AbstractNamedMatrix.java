package baseCode.dataStructure;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public abstract class AbstractNamedMatrix implements NamedMatrix {

   private Vector rowNames;
   private Vector colNames;
   private Map rowMap; //contains a map of each row and elements in the row
   private Map colMap;

   /**
    * 
    *  
    */
   public AbstractNamedMatrix() {
      rowMap = new LinkedHashMap(); //contains a map of each row name to index
      // of the row.
      colMap = new LinkedHashMap();
      rowNames = new Vector();
      colNames = new Vector();
   }

   public final void addColumnName( String s, int i ) {
      this.colNames.add( s );
      this.colMap.put( s, new Integer( i ) );
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.NamedMatrix#addRowName(java.lang.String, int)
    */
   public final void addRowName( String s, int i ) {
      this.rowNames.add( s );
      this.rowMap.put( s, new Integer( i ) );
   }

   /**
    * 
    * @param s String
    * @return int
    */
   public final int getRowIndexByName( String s ) {
      return ( ( Integer ) rowMap.get( s ) ).intValue();
   }

   /**
    * 
    * @param r String
    * @return int
    */
   public final int getColIndexByName( String r ) {
      return ( ( Integer ) this.colMap.get( r ) ).intValue();
   }

   /**
    * 
    * @param i int
    * @return java.lang.String
    */
   public final String getRowName( int i ) {
      return ( String ) rowNames.get( i );
   }

   /**
    * 
    * @param i int
    * @return java.lang.String
    */
   public final String getColName( int i ) {
      return ( String ) colNames.get( i );
   }

   public final boolean hasRowNames() {
      return rowNames.size() == rows();
   }

   public final boolean hasColNames() {
      return colNames.size() == columns();
   }

   public final void setRowNames( Vector v ) {
      for ( int i = 0; i < v.size(); i++ ) {
         addRowName( ( String ) v.get( i ), i );
      }
   }

   public final void setColumnNames( Vector v ) {
      for ( int i = 0; i < v.size(); i++ ) {
         addColumnName( ( String ) v.get( i ), i );
      }
   }

   public final Vector getColNames() {
      return colNames;
   }

   public final Vector getRowNames() {
      return rowNames;
   }

   public final boolean hasRow( String r ) {
      return this.rowMap.containsKey( r );
   }

   public final Iterator getRowNameMapIterator() {
      return this.rowMap.keySet().iterator();
   }

   public abstract int rows();

   public abstract int columns();

   public abstract void set( int i, int j, Object val );

   public abstract Object[] getRowObj( int i );

   public abstract Object[] getColObj( int i );

   public abstract boolean isMissing( int i, int j );

   public final boolean containsRowName( String rowName ) {
      return rowNames.contains( rowName );
   }

   public final boolean containsColumnName( String columnName ) {
      return colNames.contains( columnName );
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.NamedMatrix#numMissing()
    */
   public int numMissing() {
      int count = 0;
      int n = this.rows();
      int m = this.columns();
      for ( int i = 0; i < n; i++ ) {
         for ( int j = 0; j < m; j++ ) {
            if ( isMissing( i, j ) ) {
               count++;
            }
         }
      }
      return count;
   }

}