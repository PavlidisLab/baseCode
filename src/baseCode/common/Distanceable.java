package baseCode.common;

import java.util.Collection;

/**
 * An object whose distance to other objects can be measured. It is up to the implementer to determine that the two
 * objects being compared are of the same type.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public abstract class Distanceable extends Visitable {

   /**
    * @param a
    * @return
    */
   public abstract double distanceTo( Distanceable a );

   /**
    * Return a collections view of the object. The implementer has to make sure the Collection is of Distanceables.
    * 
    * @return @todo not very elegant...
    */
   public abstract Collection toCollection();

}