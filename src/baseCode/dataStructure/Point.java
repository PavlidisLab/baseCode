package baseCode.dataStructure;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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

public class Point {

   private int x, y;

   /**
    * 
    * @param i
    * @param j
    */
   public Point( int i, int j ) {
      set( i, j );
   }

   /**
    * 
    * @param i
    * @param j
    */
   public void set( int i, int j ) {
      x = i;
      y = j;
   }

   /**
    * 
    * @return array containing the coordinates x,y.
    */
   public int[] get() {
      return new int[] { x, y };
   }

   /**
    * 
    * @return x the x value.
    */
   public int getx() {
      return x;
   }

   /**
    * 
    * @return y the y value.
    */
   public int gety() {
      return y;
   }

   /**
    * 
    * @return string representation of the point.
    */
   public String toString() {
      return new String( x + "\t" + y );
   }

}