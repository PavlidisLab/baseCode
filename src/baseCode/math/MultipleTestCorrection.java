package baseCode.Math;

import cern.colt.list.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class MultipleTestCorrection {


   /**
    * Determine the Bonferroni pvalue threshold to maintain the family wise error rate (assuming pvalues are independent).
    * @param pvalues The pvalues
    * @param fwe The family wise error rate
    * @return The minimum pvalue that maintains the FWE
    */
   public static double BonferroniCut(DoubleArrayList pvalues, double fwe) {
      int numpvals = pvalues.size();
      return fwe / (double)numpvals;
   }

   /**
    *
    * Benjamini-Hochberg method. Determines the maximum p value to maintain the false discovery rate.
    * (Assuming pvalues are independent);
    * @param pvalues list of pvalues. Need not be sorted.
    * @param fdr false discovery rate
    * @return The maximum pvalue that maintains the false discovery rate
    */
   public static double BenjaminiHochbergCut(DoubleArrayList pvalues, double fdr) {
     int numpvals = pvalues.size();
     DoubleArrayList pvalcop = pvalues.copy();
     pvalcop.sort();
     pvalcop.reverse();

     int n = pvalcop.size();
     double thresh = fdr * n / numpvals;


     double corrected_p;
     for (int i = 0; i < n; i++) {
       double p = pvalcop.get(i);

       if (p < thresh) {
          return p;
       }
     }
     return 0.0;
   }

}