package baseCode.math;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public interface ProbabilityComputer {

   /**
    * Return the probability associated with a certain value. Unless otherwise specified, the upper tail of the
    * associated distribution is returned.
    * 
    * @param value
    * @return
    */
   public double probability( double value );

}