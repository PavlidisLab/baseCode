package baseCode.math.distribution;

/**
 * An interface that describes a class that can produce values from a particular probability density.
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public interface DensityGenerator {

   /**
    * 
    * @param value
    * @return The value of the density at x
    */
   public double density(double x);
 
}
