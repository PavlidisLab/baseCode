/*
 * The baseCode project
 * 
 * Copyright (c) 2010 University of British Columbia
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
package ubic.basecode.util.r.type;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

/**
 * @author paul
 * 
 */
public class OneWayAnovaResult extends AnovaResult {

    private Integer df = null;

    private String factorName = null;

    private Double fVal = Double.NaN;

    private Double pval = Double.NaN;

    /**
     * A null result.
     */
    public OneWayAnovaResult() {
    }

    /**
     * @param rAnovaTable from R
     */
    public OneWayAnovaResult( REXP rAnovaTable ) {

        try {

            String[] names = rAnovaTable.getAttribute( "row.names" ).asStrings();
            this.factorName = names[0];

            double[] pvs = rAnovaTable.asList().at( "Pr(>F)" ).asDoubles();

            this.pval = pvs[0];

            int[] dfs = rAnovaTable.asList().at( "Df" ).asIntegers();

            this.df = dfs[0];

            this.residualDf = new Integer( dfs[1] ).doubleValue();

            double[] fs = rAnovaTable.asList().at( "F value" ).asDoubles();

            this.fVal = fs[0];

        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    /* *//**
          * @param linearModelSummary
          */
    /*
     * public OneWayAnovaResult( LinearModelSummary linearModelSummary ) { this.factorName =
     * linearModelSummary.getFactorNames().iterator().next(); this.pval = linearModelSummary.getP( this.factorName );
     * this.tstat = linearModel }
     */

    /**
     * @return the df
     */
    public Integer getDf() {
        return df;
    }

    /**
     * @return the factorName
     */
    public String getFactorName() {
        return factorName;
    }

    /**
     * @return the fVal
     */
    public Double getFVal() {
        return fVal;
    }

    /**
     * @return the pval
     */
    public Double getPval() {
        return pval;
    }

    @Override
    public String toString() {
        return String.format( "Factor\tDf\tF\tP\n%s\t%d\t%.2f\t%.3g\nResidual\t%d", factorName, df, fVal, pval,
                residualDf );
    }
}
