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
public abstract class Ranker {

   public abstract Object best();
   
   public abstract Object worst();
   
   public abstract DoubleArrayList getScores();
   
   
   
}
