package baseCode.dataStructure;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
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
   public Point(int i, int j) {
      set(i,j);
   }

   /**
    *
    * @param i
    * @param j
    */
   public void set(int i, int j) {
      x = i;
      y = j;
   }

   /**
    *
    * @return
    */
   public int[] get() {
      return new int[] {x,y};
   }

   /**
    *
    * @return
    */
   public int getx() {
      return x;
   }

   /**
    *
    * @return
    */
   public int gety() {
      return y;
   }

   /**
    *
    * @return
    */
   public String toString() {
      return new String(x + "\t" + y);
   }

}