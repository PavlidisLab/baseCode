package baseCode.math;

/**
 * Generate probabilities from the uniform distribution.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class UniformProbabilityComputer implements ProbabilityComputer {

   double min = 0;
   double max = 1;

   /**
    * Create a UniformProbabilityComputer where the density is defined over the unit inteval [0,1].
    */
   public UniformProbabilityComputer() {
      this( 0, 1 );
   }

   /**
    * Create a UniformProbabilityComputer where the density is defined over the interval given
    * 
    * @param min
    * @param max
    */
   public UniformProbabilityComputer( double min, double max ) {
      if ( max <= min ) {
         throw new IllegalArgumentException( "Max must be higher than min" );
      }
      this.min = min;
      this.max = max;
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.math.ProbabilityComputer#probability(double)
    */
   public double probability( double x ) {
      if ( x < min ) return 0;
      if ( x > max ) return 1;
      return ( x - min ) / ( max - min );
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.math.ProbabilityComputer#probability(double, boolean)
    */
   public double probability( double x, boolean upperTail ) {

      if ( !upperTail ) {
         return probability( x );
      }

      if ( x < min ) return 0;

      if ( x > max ) return 1.0;

      return ( max - x ) / ( max - min );
   }

}