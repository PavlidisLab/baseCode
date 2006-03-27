package ubic.basecode.math.distribution;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class UniformDensityComputer implements DensityGenerator {

   double min = 0;
   double max = 1;

   /**
    * Create a UniformDensityComputer where the density is defined over the unit inteval [0,1].
    */
   public UniformDensityComputer() {
      this( 0, 1 );
   }

   /**
    * Create a UniformDensityComputer where the density is defined over the interval given
    * 
    * @param min
    * @param max
    */
   public UniformDensityComputer( double min, double max ) {
      if ( max <= min ) {
         throw new IllegalArgumentException( "Max must be higher than min" );
      }
      this.min = min;
      this.max = max;
   }

   /*
    * (non-Javadoc)
    * 
    * @see basecode.math.DensityGenerator#density(double)
    */
   public double density( double x ) {

      if ( x > max || x < min ) return 0;

      return 1 / ( max - min );
   }

}