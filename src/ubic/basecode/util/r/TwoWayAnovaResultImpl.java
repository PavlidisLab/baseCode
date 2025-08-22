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
package ubic.basecode.util.r;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import ubic.basecode.math.linearmodels.TwoWayAnovaResult;

/**
 * Represents a two-way ANOVA table
 *
 * @author paul
 */
class TwoWayAnovaResultImpl extends AbstractAnovaResult implements TwoWayAnovaResult {

    private final String factorAName;
    private final String factorBName;
    private final double mainEffectADof;
    private final double mainEffectAFStat;
    private final double mainEffectAPValue;
    private final double mainEffectBDof;
    private final double mainEffectBFStat;
    private final double mainEffectBPValue;
    private final boolean hasInteraction;
    private final double interactionDof;
    private final double interactionFStat;
    private final double interactionPValue;
    private final double residualDof;
    private final double residualFStat;
    private final double residualPValue;

    /**
     * A null result.
     */
    public TwoWayAnovaResultImpl( String key ) {
        super( key );
        this.factorAName = "null";
        this.factorBName = "null";
        this.hasInteraction = false;
        this.interactionDof = Double.NaN;
        this.interactionFStat = Double.NaN;
        this.interactionPValue = Double.NaN;
        this.mainEffectADof = Double.NaN;
        this.mainEffectAFStat = Double.NaN;
        this.mainEffectAPValue = Double.NaN;
        this.mainEffectBDof = Double.NaN;
        this.mainEffectBFStat = Double.NaN;
        this.mainEffectBPValue = Double.NaN;
        this.residualDof = Double.NaN;
        this.residualPValue = Double.NaN;
        this.residualFStat = Double.NaN;
    }

    /**
     * @param rAnovaTable from R
     */
    public TwoWayAnovaResultImpl( String key, REXP rAnovaTable ) throws REXPMismatchException {
        super( key );
        String[] names = rAnovaTable.getAttribute( "row.names" ).asStrings();
        this.factorAName = names[0];
        this.factorBName = names[1];

        double[] pvs = rAnovaTable.asList().at( "Pr(>F)" ).asDoubles();

        this.hasInteraction = pvs.length == 4; // interaction is present if there are 4 p-values, f-stats, dof, etc.

        this.mainEffectAPValue = pvs[0];
        this.mainEffectBPValue = pvs[1];
        if ( hasInteraction ) {
            this.interactionPValue = pvs[2];
            this.residualPValue = pvs[3];
        } else {
            this.interactionPValue = Double.NaN;
            this.residualPValue = pvs[2];
        }

        double[] dfs = rAnovaTable.asList().at( "Df" ).asDoubles();
        this.mainEffectADof = dfs[0];
        this.mainEffectBDof = dfs[1];
        if ( dfs.length == 4 ) {
            this.interactionDof = dfs[2];
            this.residualDof = dfs[3];
        } else {
            this.interactionDof = Double.NaN;
            this.residualDof = dfs[2];
        }

        double[] fs = rAnovaTable.asList().at( "F value" ).asDoubles();

        this.mainEffectAFStat = fs[0];
        this.mainEffectBFStat = fs[1];
        if ( hasInteraction ) {
            this.interactionFStat = fs[2];
            this.residualFStat = fs[3];
        } else {
            this.interactionFStat = Double.NaN;
            this.residualFStat = fs[2];
        }
    }

    /**
     * @return the factorAName
     */
    @Override
    public String getFactorAName() {
        return factorAName;
    }

    /**
     * @return the factorBName
     */
    @Override
    public String getFactorBName() {
        return factorBName;
    }

    /**
     * @return the interactionDof
     */
    @Override
    public double getInteractionDof() {
        return interactionDof;
    }

    @Override
    public double getInteractionFStat() {
        return interactionFStat;
    }

    @Override
    public double getInteractionPValue() {
        return interactionPValue;
    }

    @Override
    public double getMainEffectADof() {
        return mainEffectADof;
    }

    @Override
    public double getMainEffectAFStat() {
        return mainEffectAFStat;
    }

    @Override
    public double getMainEffectAPValue() {
        return mainEffectAPValue;
    }

    @Override
    public double getMainEffectBDof() {
        return mainEffectBDof;
    }

    @Override
    public double getMainEffectBFStat() {
        return mainEffectBFStat;
    }

    @Override
    public double getMainEffectBPValue() {
        return mainEffectBPValue;
    }

    @Override
    public boolean hasResiduals() {
        return true;
    }

    @Override
    public double getResidualsDof() {
        return residualDof;
    }

    @Override
    public double getResidualsFStat() {
        return residualFStat;
    }

    @Override
    public double getResidualsPValue() {
        return residualPValue;
    }

    @Override
    public boolean hasInteraction() {
        return hasInteraction;
    }

    @Override
    public String toString() {

        return String.format(
            "Factor\tDof\tF\tP\n%s\t%.2f\t%.2f\t%.3g\n%s\t%.2f\t%.2f\t%.3g\nInteraction\t%.2f\t%.2f\t%.3g\nResidual\t%.2f",
            factorAName, mainEffectADof, mainEffectAFStat, mainEffectAPValue, factorBName, mainEffectBDof,
            mainEffectBFStat, mainEffectBPValue, interactionDof, interactionFStat, interactionPValue, residualDof );
    }

}
