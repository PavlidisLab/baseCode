package baseCode.dataStructure;

import java.text.DecimalFormat;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class Link extends Point {

   private double weight;

   /**
    *
    * @param i
    * @param j
    * @param weight
    */
   public Link(int i, int j, double weight) {
     super(i,j);
     this.weight = weight;
  }

  /**
   *
   * @return
   */
   public double getWeight() { return weight; }

   /**
    *
    * @return
    */
   public String toString() {
      return super.toString() + "\t" + DecimalFormat.getInstance().format(this.weight);
   }

}