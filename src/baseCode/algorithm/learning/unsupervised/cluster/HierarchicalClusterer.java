package baseCode.algorithm.learning.unsupervised.cluster;

import java.util.TreeSet;

import cern.colt.list.ObjectArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * Start with all items in separate clusters.
 * 
 * Compute the distances between all clusters
 * 
 * Find the clusters with the highest similarity. Combine them into one cluster
 * 
 * Iterate until there is only one cluster.
 * 
 * Output: for each cluster, print the clusters it contains.
 * 
 * 
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class HierarchicalClusterer extends ClusteringAlgorithm {

   /**
    * How many values we cache
    */
   int STORED = 10;

   /**
    * At the end of the run, this contains the cluster tree.
    */
   ClusterNode nodes;

   
   /**
    * Distance matrix
    */
   DoubleMatrix2D distances;

   /**
    * The objects that are getting clustered. 
    */
   ObjectArrayList objects;

   /**
    * @param c Objects to be clustered. These must be Distanceable
    */
   public HierarchicalClusterer( ObjectArrayList c ) {
      this.objects = c;
      distances = new DenseDoubleMatrix2D( c.size(), c.size() );
   }

   /**
    * Perform a hierarchical clustering on the objects. 
    * 
    * Initialize: compute the distances between all objects. 
    *    Find the closest pair to initialize the clustering.
    * 
    * 
    * 
    * @see baseCode.algorithm.learning.unsupervised.cluster.Algorithm#Run()
    */
   public void run() {

      // keep a running list of the best distances.
      TreeSet closestPairs = new TreeSet();
      AverageLinkageDistancer ald = new AverageLinkageDistancer();
      closestPairs.add( new ClusterNode( Double.MAX_VALUE, null, null ) );

      // compute all the distances. Keep track of the best distances.
      for ( int i = 0; i < objects.size(); i++ ) {
         Cluster elA = ( Cluster ) objects.elements()[i];
         for ( int j = i + 1; j < objects.size(); j++ ) {
            Cluster elB = ( Cluster ) objects.elements()[j];
            double d = elA.distanceTo( elB );
            distances.setQuick( i, j, d );
            distances.setQuick( j, i, d );
            if ( closestPairs.first() == null
                  || d < ( ( ClusterNode ) closestPairs.first() ).getDistance() ) {
               closestPairs.add( new ClusterNode( d, elA, elB ) );
               if ( closestPairs.size() > STORED ) {
                  closestPairs.remove( closestPairs.last() );
               }
            }
         }
      }
      
      // make a new cluster.
      
      

      // while there are still nodes left, keep finding the closest pairs.
      
      
      
      
     
   }

   /**
    * @return
    */
   public Object[] getResults() {
      // TODO Auto-generated method stub
      return null;
   }

}

/* just a helper to store the infor about a cluster node. */

class ClusterNode implements Comparable {
   private double distance;
   private Cluster firstThing;
   private Cluster secondThing;

   /**
    * @param distance double
    * @param firstThing Cluster
    * @param secondThing Cluster
    */
   public ClusterNode( double distance, Cluster firstThing, Cluster secondThing ) {
      super();
      this.distance = distance;
      this.firstThing = firstThing;
      this.secondThing = secondThing;
   }

   public double getDistance() {
      return distance;
   }

   public void setDistance( double distance ) {
      this.distance = distance;
   }

   public Cluster getFirstThing() {
      return firstThing;
   }

   public void setFirstThing( Cluster firstThing ) {
      this.firstThing = firstThing;
   }

   public Cluster getSecondThing() { // this could be another cluster node.
      return secondThing;
   }

   public void setSecondThing( Cluster secondThing ) {
      this.secondThing = secondThing;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo( Object o ) {
      // TODO Auto-generated method stub
      return 0;
   }
}

