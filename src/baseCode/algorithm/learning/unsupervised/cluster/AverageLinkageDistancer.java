package baseCode.algorithm.learning.unsupervised.cluster;

import java.util.Collection;
import java.util.Iterator;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class AverageLinkageDistancer implements Distancer {

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.algorithm.learning.unsupervised.cluster.Distance#distance(java.lang.Object, java.lang.Object)
    */
   public double distance( Object a, Object b ) {

      double mean = 0.0;
      int numComparisons = 0;
      
      for ( Iterator iter = ( ( Collection ) a ).iterator(); iter.hasNext(); ) {
         Distanceable elementA = ( Distanceable ) iter.next();

         for ( Iterator iterator = ( ( Collection ) b ).iterator(); iterator
               .hasNext(); ) {
            Distanceable elementB = ( Distanceable ) iterator.next();
            mean += elementA.distanceTo( elementB );
            numComparisons++;
         }

      }

      return mean / numComparisons;

   }
}