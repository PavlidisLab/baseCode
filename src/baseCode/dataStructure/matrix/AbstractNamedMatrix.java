package baseCode.dataStructure.matrix;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
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

   private int lastColumnIndex = 0;
   private int lastRowIndex = 0;

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

   /**
    * Add a column name when we don't care what the index will be. The index will be set by the method. This is useful
    * for when we need to set up a matrix before we know how many column or rows there are.
    * 
    * @param s
    */
   public final void addColumnName( String s ) {

      if ( colMap.containsKey( s ) ) {
         throw new IllegalArgumentException( "Duplicate column name " + s );
      }

      this.colNames.add( s );
      this.colMap.put( s, new Integer( lastColumnIndex ) );
      lastColumnIndex++;

   }

   public final void addColumnName( String s, int i ) {

      if ( colMap.containsKey( s ) ) {
         throw new IllegalArgumentException( "Duplicate column name " + s );
      }

      this.colNames.add( s );
      this.colMap.put( s, new Integer( i ) );
   }

   /**
    * Add a row name when we don't care what the index will be. The index will be set by the method. This is useful for
    * when we need to set up a matrix before we know how many column or rows there are.
    * 
    * @param s
    */
   public final void addRowName( String s ) {

      if ( rowMap.containsKey( s ) ) {
         //     throw new IllegalArgumentException("Duplicate row name " + s);
         return;
      }

      this.rowNames.add( s );
      this.rowMap.put( s, new Integer( lastRowIndex ) );
      lastRowIndex++;
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.NamedMatrix#addRowName(java.lang.String, int)
    */
   public final void addRowName( String s, int i ) {

      if ( rowMap.containsKey( s ) ) {
         //      throw new IllegalArgumentException("Duplicate row name " + s);
         return;
      }

      this.rowNames.add( s );
      this.rowMap.put( s, new Integer( i ) );
   }

   /**
    * @param s String
    * @return int
    */
   public final int getRowIndexByName( String s ) {
      if ( !rowNames.contains( s ) ) {
         throw new IllegalArgumentException( s + " not found" );
      }

      return ( ( Integer ) rowMap.get( s ) ).intValue();
   }

   /**
    * @param r String
    * @return int
    */
   public final int getColIndexByName( String r ) {
      if ( !colNames.contains( r ) ) {
         throw new IllegalArgumentException( r + " not found" );
      }

      return ( ( Integer ) this.colMap.get( r ) ).intValue();
   }

   /**
    * @param i int
    * @return java.lang.String
    */
   public final String getRowName( int i ) {
      return ( String ) rowNames.get( i );
   }

   /**
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

   public final void setRowNames( List v ) {
      for ( int i = 0; i < v.size(); i++ ) {
         addRowName( ( String ) v.get( i ), i );
      }
   }

   public final void setColumnNames( List v ) {
      for ( int i = 0; i < v.size(); i++ ) {
         addColumnName( ( String ) v.get( i ), i );
      }
   }

   public final List getColNames() {
      return colNames;
   }

   public final List getRowNames() {
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