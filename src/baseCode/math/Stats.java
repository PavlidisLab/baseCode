package baseCode.math;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

/**
 * Miscellaneous functions used for statistical analysis. Some are optimized or specialized versions of methods that can be found elsewhere.
 * @see <a href="http://hoschek.home.cern.ch/hoschek/colt/V1.0.3/doc/cern/jet/math/package-summary.html">cern.jet.math</a>
 * @see <a href="http://hoschek.home.cern.ch/hoschek/colt/V1.0.3/doc/cern/jet/stat/package-summary.html">cern.jet.stat</a>
 * <p>Copyright (c) 2004</p>
 * <p>Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Stats {

   private Stats() {};

   /**
    * Convert an array into a cumulative array. Summing is from the left hand
    * side.
    *
    * @param x DoubleArrayList
    * @return cern.colt.list.DoubleArrayList
    */
   public static DoubleArrayList cumulate( DoubleArrayList x ) {
      DoubleArrayList r = new DoubleArrayList( x.size() );

      double sum = 0.0;
      for ( int i = 0; i < x.size(); i++ ) {
         sum += x.get( i );
         r.add( sum );
      }
      return r;
   }

   /**
    * Convert an array into a CDF. This assumes that the input contains counts representing the distribution in question.
    * @param x The input of counts.
    * @return The CDF.
    */
   public static DoubleArrayList cdf( DoubleArrayList x ) {
      return cumulate( normalize( x ) );
   }

   /**
    * Divide the elements of an array by a given factor.
    *
    * @param x Input array.
    * @param normfactor double
    * @return Normalized array.
    */
   public static DoubleArrayList normalize( DoubleArrayList x, double normfactor ) {
      DoubleArrayList r = new DoubleArrayList( x.size() );

      for ( int i = 0; i < x.size(); i++ ) {
         r.add( x.get( i ) / normfactor );
      }
      return r;

   }

   /**
    * Adjust the elements of an array so they total to 1.0.
    * @param x Input array.
    * @return Normalized array.
    */
   public static DoubleArrayList normalize( DoubleArrayList x ) {
      return normalize( x, Descriptive.sum( x ) );
   }

   /**
    * calculate the mean of the values above a particular quantile of an array.
    * Quantile must be a value from 0 to 100.
    *
    * @see DescriptiveWithMissing#meanAboveQuantile
    * @param quantile A value from 0 to 100
    * @param array Array for which we want to get the quantile.
    * @param effectiveSize The size of the array.
    * @return double
    */
   public static double meanAboveQuantile(int quantile,
       double[] array, int effectiveSize) {
     double[] temp = new double[effectiveSize];
     double median;
     double returnvalue = 0.0;
     int k = 0;

     temp = array;
     median = quantile(quantile, array, effectiveSize);

     for (int i = 0; i < effectiveSize; i++) {
       if (temp[i] >= median) {
         returnvalue += temp[i];
         k++;
       }
     }
     return (returnvalue / k);
   }

   /**
    * Given an array, calculate the quantile requested.
    *
    * @see DescriptiveWithMissing#quantile
    * @param index int
    * @param values double[]
    * @param effectiveSize int
    * @return double
    */
   public static double quantile(int index, double[] values,
                                            int effectiveSize)

    {
      double pivot = -1.0;
      if (index == 0) {
        double ans = values[0];
        for (int i = 1; i < effectiveSize; i++) {
          if (ans > values[i]) {
            ans = values[i];
            //System.out.println("indx0");
          }
        }
        return ans;
      }
      else {
        double[] temp = new double[effectiveSize];

        for (int i = 0; i < effectiveSize; i++) {
          temp[i] = values[i];
          //System.out.println("temp["+i+"]= "+temp[i]);
        }
        try {
          pivot = temp[0];

          double[] smaller = new double[effectiveSize];
          double[] bigger = new double[effectiveSize];
          int itrSm = 0;
          int itrBg = 0;
          for (int i = 1; i < effectiveSize; i++) {
            if (temp[i] <= pivot) {
              smaller[itrSm] = temp[i];
              itrSm++;
            }
            else if (temp[i] > pivot) {
              bigger[itrBg] = temp[i];
              itrBg++;
            }
          }
          if (itrSm > index) {
            //System.out.println("Small  pivot="+pivot+ "  sm= " + itrSm + "  bg= " + itrBg +"  index="+index);
            return quantile(index, smaller, itrSm);
          }
          else if (itrSm < index) {
            //System.out.println("Big    pivot="+pivot+ "  sm= " + itrSm + "  bg= " + itrBg +"  index="+index);
            return quantile(index - itrSm - 1, bigger, itrBg);
          }
          else {
            //System.out.println("DONE   pivot="+pivot+ "  sm= " + itrSm + "  bg= " + itrBg +"  index="+index);
            return pivot;
          }
        }
        catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("\n\nERROR:" + "  index=" + index + "  size=" + effectiveSize +
                             "  pivot=" + pivot);
          for (int i = 0; i < values.length; i++) {
            System.out.print("random_class[" + i + "]= " + values[i]);
            if (i <= effectiveSize) {
              System.out.println("   temp[" + i + "]= " + temp[i]);
            }
          }
          return -1.0;
        }
      }
    }



}
