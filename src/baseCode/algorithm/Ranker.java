package baseCode.algorithm;

import cern.colt.list.DoubleArrayList;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public interface Ranker {

   public Object best();
   
   public Object worst();
   
   public DoubleArrayList getScores();
   
   
}
