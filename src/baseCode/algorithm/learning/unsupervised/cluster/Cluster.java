package baseCode.algorithm.learning.unsupervised.cluster;

import java.util.Collection;
import java.util.Vector;

import baseCode.common.Distanceable;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class Cluster extends Distanceable {

   protected Distancer distAlg;
   protected Collection items;

   public Cluster() {}
   
   public Cluster(Object item, Distancer distAlg) {
      this.distAlg = distAlg;
      Vector v = new Vector();
      v.add(item);
      this.items = v;
   }
   
   public Cluster( Collection items, Distancer distAlg ) {
      this.distAlg = distAlg;
      this.items = items;
   }
   
   public void addItem(Cluster item ) {
      
   }
   
   /**
    * 
    * @return true if this cluster is made up of multiple distanceables, or multiple ones.
    */
   public boolean isCompound() {
      return items != null && items.size() > 1;
   }
   
   
   /*
    * (non-Javadoc)
    * 
    * @see baseCode.common.Distanceable#distanceTo(baseCode.common.Distanceable)
    */
   public double distanceTo( Distanceable a ) {
      return this.distAlg.distance( this, a );
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo( Object o ) {
      // TODO Auto-generated method stub
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.common.Distanceable#toCollection()
    */
   public Collection toCollection() {
      return items;
   }

}