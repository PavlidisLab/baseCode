package baseCode.algorithm.learning.unsupervised.cluster;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public interface Visitable {

   public boolean isVisited();
   
   public void mark();
   
   public void unMark();
   
}
