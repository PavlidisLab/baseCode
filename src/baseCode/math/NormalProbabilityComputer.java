package baseCode.math;

import cern.jet.stat.Probability;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public class NormalProbabilityComputer implements ProbabilityComputer {

   double variance;
   double mean;
   
   /**
    * @param variance
    * @param mean
    */
   public NormalProbabilityComputer( double variance, double mean ) {
      super();
      this.variance = variance;
      this.mean = mean;
   }
    
   
   /* (non-Javadoc)
    * @see baseCode.math.ProbabilityComputer#probability(double)
    */
   public double probability( double value ) {
      return 1.0 - Probability.normal(mean, variance, value) ;
   }

}
