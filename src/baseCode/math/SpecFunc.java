package baseCode.math;

import cern.jet.math.Arithmetic;

/**
  See cern.jet.stat.Probability and cern.jet.stat.Gamma and cern.jet.stat.math.Arithmetic for what you don't see here.
  @author Paul Pavlidis
  @version $Id$
 */
public class SpecFunc {

  /**
     Calculate hypergeometric distribution.  Gives same answer as
     dhyper in R.
     @param positives Number of positives in the data
     @param successes Number of 'successes'
     @param negatives Number of negatives in the data
     @param failures Number of 'failures'
     @return The hypergeometric probability for the parameters.
   */
  public static double hypergeometric(int positives, int successes,
                                      int negatives, int failures) {
    if (successes > positives || failures > negatives || successes < 0 ||
        failures < 0 || positives <= 0 || negatives <= 0) {
      throw new IllegalArgumentException("Illegal values for hyperPval");
    }
    return Arithmetic.binomial(positives, successes) *
        Arithmetic.binomial(negatives, failures) /
       Arithmetic.binomial(positives + negatives, successes + failures);
  }

  /**
    Cumulative hypergeometric probability ( for over-represented
    and uner-reprsented categories ). Gives same pvalues as phyper
    in R.
    @param positives Number of positives in the data
    @param successes Number of 'successes'
    @param negatives Number of negatives in the data
    @param failures Number of 'failures'
    @return The cumulative hypergeometric distribution.
  */
 public static double cumHyperGeometric(int positives, int successes,
                                        int negatives, int failures) {
   double pval = 0.0;
   int i;

   for (i = 0; i <= successes; i++) {
	// starts at failures, goes down to zero while i goes up to positives.
     pval += hypergeometric(positives, i, negatives, successes + failures - i);
   }

   if (pval > 0.5) {
     return 1.0 - pval;
   }
   return pval;
 }



}
