package baseCode.math;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

/**
 * Miscellaneous functions that probably should go somewhere else.
 * See cern.jet.math, cern.jet.stat for more functions.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Stats {

    /**
     * Convert an array into a cumulative array. Summing is from the left hand
     * side.
     *
     * @param x DoubleArrayList
     * @return cern.colt.list.DoubleArrayList
     */
    public static DoubleArrayList cumulate(DoubleArrayList x) {
      DoubleArrayList r = new DoubleArrayList(x.size());

      double sum = 0.0;
      for (int i = 0; i < x.size(); i++) {
         sum += x.get(i);
         r.add(sum);
      }
      return r;
   }

   /**
    * Convert an array into a CDF. This assumes that the input contains counts representing the distribution in question.
    * @param x The input of counts.
    * @return The CDF.
    */
   public static DoubleArrayList cdf(DoubleArrayList x) {
      return cumulate(normalize(x));
   }


    /**
     * Divide the elements of an array by a given factor.
     *
     * @param x Input array.
     * @param normfactor double
     * @return Normalized array.
     */
    public static DoubleArrayList normalize(DoubleArrayList x, double normfactor) {
      DoubleArrayList r = new DoubleArrayList(x.size());

      for (int i = 0; i < x.size(); i++) {
         r.add(x.get(i) / normfactor);
      }
      return r;

   }

   /**
    * Adjust the elements of an array so they total to 1.0.
    * @param x Input array.
    * @return Normalized array.
    */
   public static DoubleArrayList normalize(DoubleArrayList x) {
      return normalize(x, Descriptive.sum(x));
   }

   /**
    *
    * @param r Correlation coefficient.
    * @return Fisher transform of the Correlation.
    */
   public static double fisherTransformR(double r) {
      return 0.5 * Math.log( (1.0 + r) / (1.0 - r));
   }

    /**
     * Calculate the mean of the values above a particular quantile of an array.
     *
     * @param quantile A value from 0 to 100
     * @param array Array for which we want to get the quantile.
     * @return double
     */
    public static double meanAboveQuantile(int quantile,
                                          DoubleArrayList array) {

      if (quantile < 0 || quantile > 100) {
         throw new IllegalArgumentException("Quantile must be between 0 and 100");
      }

      double returnvalue = 0.0;
      int k = 0;

      double median = Descriptive.quantile(array, (double) quantile);

      for (int i = 0; i < array.size(); i++) {
         if (array.get(i) >= median) {
            returnvalue += array.get(i);
            k++;
         }
      }

      if (k == 0) {
         throw new ArithmeticException("No values found above quantile");
      }

      return (returnvalue / k);
   }

}
