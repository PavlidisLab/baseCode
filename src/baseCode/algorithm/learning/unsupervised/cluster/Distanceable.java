package baseCode.algorithm.learning.unsupervised.cluster;

/**
 * An object whose distance to other objects can be measured. It is up to the implementer
 * to determine that the two objects being compared are of the same type.
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public interface Distanceable extends Visitable {
   /**
    * @param a
    * @return
    */
   public double distanceTo(Distanceable a);
   
}
