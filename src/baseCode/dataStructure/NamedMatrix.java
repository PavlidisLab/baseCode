package baseCode.dataStructure;

import java.util.Iterator;
import java.util.Vector;

/**
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public interface NamedMatrix {

   /**
    *
    * @param s String
    */
   public void addColumnName( String s );

   /**
    *
    * @param s String
    */
   public void addRowName( String s );

   /**
    *
    * @param s String
    * @return int
    */
   public int getRowIndexByName( String s );

   /**
    *
    * @param s String
    * @return int
    */
   public int getColIndexByName( String s );

   /**
    *
    * @param i int
    * @return java.lang.String
    */
   public String getRowName( int i );

   /**
    *
    * @param i int
    * @return java.lang.String
    */
   public String getColName( int i );

   /**
    *
    * @return boolean
    */
   public boolean hasRowNames();

   /**
    *
    * @return boolean
    */
   public boolean hasColNames();

   /**
    *
    * @param v Vector
    */
   public void setRowNames( Vector v );

   /**
    *
    * @param v Vector
    */
   public void setColumnNames( Vector v );

   /**
    *
    * @return Vector
    */
   public Vector getColNames();

   /**
    *
    * @param r String
    * @return boolean
    */
   public boolean hasRow( String r );

   /**
    *
    * @return java.util.Iterator
    */
   public Iterator getRowNameMapIterator();

   /**
    *
    * @return int
    */
   public int rows();

   /**
    *
    * @return int
    */
   public int columns();

   /**
    *
    * @param i int
    * @param j int
    * @param val Object
    */
   public void set( int i, int j, Object val );

   /**
    *
    * @param i int row
    * @return Object[]
    */
   public Object[] getRowObj( int i );

   /**
    *
    * @param i int column
    * @return Object[]
    */
   public Object[] getColObj( int i );

   /**
    * Check if the value at a given index is missing.
    * @param i row
    * @param j column
    * @return
    */
   public boolean isMissing(int i, int j);
   
}
