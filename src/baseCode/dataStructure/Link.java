package baseCode.dataStructure;

import java.text.NumberFormat;

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

public class Link extends Point {

   private double weight;

   /**
    * @param i int
    * @param j int
    * @param weight double
    */
   public Link( int i, int j, double weight ) {
      super( i, j );
      this.weight = weight;
   }

   /**
    * @return double
    */
   public double getWeight() {
      return weight;
   }

   /**
    * @return java.lang.String
    */
   public String toString() {
      return super.toString() + "\t"
            + NumberFormat.getInstance().format( this.weight );
   }

}