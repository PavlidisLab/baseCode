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
 * Represents a two-way ANOVA table
 * 
 * @author paul
 * @version $Id$
 */
public class TwoWayAnovaResult extends AnovaResult {

    private String factorAName = null;

    private String factorBName = null;

    private boolean hasInteractionEstimate = false;

    private Integer interactionDf = null;

    private Double interactionfVal = Double.NaN;

    private Double interactionPval = Double.NaN;

    private Integer mainEffectADf = null;

    private Double mainEffectAfVal = Double.NaN;

    private Double mainEffectAPval = Double.NaN;

    private Integer mainEffectBDf = null;

    private Double mainEffectBfVal = Double.NaN;

    private Double mainEffectBPval = Double.NaN;

    /**
     * A null result.
     */
    public TwoWayAnovaResult() {
    };

    /**
     * @param rAnovaTable from R
     */
    public TwoWayAnovaResult( REXP rAnovaTable ) {

        try {

            String[] names = rAnovaTable.getAttribute( "row.names" ).asStrings();
            this.factorAName = names[0];
            this.factorBName = names[1];

            double[] pvs = rAnovaTable.asList().at( "Pr(>F)" ).asDoubles();

            this.mainEffectAPval = pvs[0];
            this.mainEffectBPval = pvs[1];

            if ( pvs.length > 2 ) {
                this.hasInteractionEstimate = true;
                this.interactionPval = pvs[2];
            }

            int[] dfs = rAnovaTable.asList().at( "Df" ).asIntegers();

            this.mainEffectADf = dfs[0];
            this.mainEffectBDf = dfs[1];
            if ( dfs.length == 4 ) {
                this.interactionDf = dfs[2];
                this.residualDf = dfs[3];
            } else {
                this.residualDf = dfs[2];
            }

            double[] fs = rAnovaTable.asList().at( "F value" ).asDoubles();

            this.mainEffectAfVal = fs[0];
            this.mainEffectBfVal = fs[1];
            if ( hasInteractionEstimate ) this.interactionfVal = fs[2];

        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    /**
     * @return the factorAName
     */
    public String getFactorAName() {
        return factorAName;
    }

    /**
     * @return the factorBName
     */
    public String getFactorBName() {
        return factorBName;
    }

    /**
     * @return the interactionDf
     */
    public Integer getInteractionDf() {
        return interactionDf;
    }

    /**
     * @return the interactionfVal
     */
    public Double getInteractionfVal() {
        return interactionfVal;
    }

    /**
     * @return the interactionPval
     */
    public Double getInteractionPval() {
        return interactionPval;
    }

    /**
     * @return the mainEffectADf
     */
    public Integer getMainEffectADf() {
        return mainEffectADf;
    }

    /**
     * @return the mainEffectAfVal
     */
    public Double getMainEffectAfVal() {
        return mainEffectAfVal;
    }

    /**
     * @return the mainEffectAPval
     */
    public Double getMainEffectAPval() {
        return mainEffectAPval;
    }

    /**
     * @return the mainEffectBDf
     */
    public Integer getMainEffectBDf() {
        return mainEffectBDf;
    }

    /**
     * @return the mainEffectBfVal
     */
    public Double getMainEffectBfVal() {
        return mainEffectBfVal;
    }

    /**
     * @return the mainEffectBPval
     */
    public Double getMainEffectBPval() {
        return mainEffectBPval;
    }

    /**
     * @return the hasInteractionEstimate
     */
    public boolean isHasInteractionEstimate() {
        return hasInteractionEstimate;
    }

    @Override
    public String toString() {

        return String.format(
                "Factor\tDf\tF\tP\n%s\t%d\t%.2f\t%.3g\n%s\t%d\t%.2f\t%.3g\nInteraction\t%d\t%.2f\t%.3g\nResidual\t%d",
                factorAName, mainEffectADf, mainEffectAfVal, mainEffectAPval, factorBName, mainEffectBDf,
                mainEffectBfVal, mainEffectBPval, interactionDf, interactionfVal, interactionPval, residualDf );
    }

}
