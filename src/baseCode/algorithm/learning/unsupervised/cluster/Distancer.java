package baseCode.algorithm.learning.unsupervised.cluster;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public interface Distancer {

   /**
    * @param a
    * @param b
    * @return
    */
   public double distance( Object a, Object b );

}