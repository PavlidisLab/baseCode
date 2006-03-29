/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.math;

import cern.colt.list.DoubleArrayList;

/**
 * Methods for p-value correction of sets of hypothesis tests.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class MultipleTestCorrection {

    /**
     * Determine the Bonferroni pvalue threshold to maintain the family wise error rate (assuming pvalues are
     * independent).
     * 
     * @param pvalues The pvalues
     * @param fwe The family wise error rate
     * @return The minimum pvalue that maintains the FWE
     */
    public static double BonferroniCut( DoubleArrayList pvalues, double fwe ) {
        int numpvals = pvalues.size();
        return fwe / numpvals;
    }

    /**
     * Benjamini-Hochberg method. Determines the maximum p value to maintain the false discovery rate. (Assuming pvalues
     * are independent);
     * 
     * @param pvalues list of pvalues. Need not be sorted.
     * @param fdr false discovery rate (value q in B-H).
     * @return The maximum pvalue that maintains the false discovery rate *
     * @throws IllegalArgumentException if invalid pvalues are encountered.
     */
    public static double BenjaminiHochbergCut( DoubleArrayList pvalues, double fdr ) {
        int numpvals = pvalues.size();
        DoubleArrayList pvalcop = pvalues.copy();
        pvalcop.sort();

        for ( int i = numpvals - 1; i >= 0; i-- ) {

            double p = pvalcop.get( i );
            if ( p < 0.0 || p > 1.0 ) throw new IllegalArgumentException( "p-value must be in range [0,1]" );

            double thresh = fdr * i / numpvals;
            if ( p < thresh ) {
                return p;
            }
        }
        return 0.0;
    }

    /**
     * Benjamini-Yekuteli method. Determines the maximum p value to maintain the false discovery rate. Valid under
     * dependence of the pvalues. This method is more conservative than Benjamini-Hochberg.
     * 
     * @param pvalues list of pvalues. Need not be sorted.
     * @param fdr false discovery rate (value q in B-H).
     * @return The maximum pvalue that maintains the false discovery rate
     * @throws IllegalArgumentException if invalid pvalues are encountered.
     */
    public static double BenjaminiYekuteliCut( DoubleArrayList pvalues, double fdr ) {

        int numpvals = pvalues.size();
        DoubleArrayList pvalcop = pvalues.copy();
        pvalcop.sort();

        // as per Theorem 1.3 in paper.
        // qmod replaces q (fdr) in the method
        double qmod = 0.0;
        for ( int i = 1; i <= numpvals; i++ ) {
            qmod += 1.0 / i;
        }
        qmod = fdr / qmod;

        for ( int i = numpvals - 1; i >= 0; i-- ) {

            double p = pvalcop.get( i );
            if ( p < 0.0 || p > 1.0 ) throw new IllegalArgumentException( "p-value must be in range [0,1]" );

            double thresh = qmod * i / numpvals;
            if ( p < thresh ) {
                return p;
            }
        }
        return 0.0;
    }

}