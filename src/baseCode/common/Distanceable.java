package baseCode.common;

/**
 * An object whose distance to other objects can be measured. It is up to the implementer
 * to determine that the two objects being compared are of the same type.
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public abstract class Distanceable extends Visitable {
   /**
    * @param a
    * @return
    */
   public abstract double distanceTo(Distanceable a);
}
