package baseCode.algorithm.learning.unsupervised.cluster;

import baseCode.common.Distanceable;

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
   public double distance( Distanceable a, Distanceable b );

}