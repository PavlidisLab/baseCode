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
import org.rosuda.REngine.RList;
import ubic.basecode.math.linearmodels.OneWayAnovaResult;

/**
 * @author paul
 *
 */
class OneWayAnovaResultImpl extends AbstractAnovaResult implements OneWayAnovaResult {

    private final String factorName;
    private final double mainEffectDof;
    private final double mainEffectFStat;
    private final double mainEffectPValue;
    private final double residualDof;
    private final double residualFStat;
    private final double residualPValue;

    /**
     * A null result.
     */
    public OneWayAnovaResultImpl( String key ) {
        super( key );
        factorName = null;
        mainEffectDof = Double.NaN;
        mainEffectFStat = Double.NaN;
        mainEffectPValue = Double.NaN;
        residualDof = Double.NaN;
        residualFStat = Double.NaN;
        residualPValue = Double.NaN;
    }

    /**
     * @param rAnovaTable from R
     */
    public OneWayAnovaResultImpl( String key, REXP rAnovaTable ) throws REXPMismatchException {
        super( key );
        this.factorName = rAnovaTable.getAttribute( "row.names" ).asStrings()[0];
        RList list = rAnovaTable.asList();
        this.mainEffectDof = list.at( "Df" ).asDoubles()[0];
        this.mainEffectPValue = list.at( "Pr(>F)" ).asDoubles()[0];
        this.mainEffectFStat = list.at( "F value" ).asDoubles()[0];
        this.residualDof = list.at( "Df" ).asDoubles()[1];
        this.residualFStat = list.at( "F value" ).asDoubles()[1];
        this.residualPValue = list.at( "Pr(>F)" ).asDoubles()[1];
    }

    @Override
    public String getFactorName() {
        return factorName;
    }

    @Override
    public double getMainEffectDof() {
        return mainEffectDof;
    }

    @Override
    public double getMainEffectFStat() {
        return mainEffectFStat;
    }

    @Override
    public double getMainEffectPValue() {
        return mainEffectPValue;
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
    public String toString() {
        return String.format( "Factor\tDf\tF\tP\n%s\t%.2f\t%.2f\t%.3g\nResidual\t%.2f\t%.2f\t%.3g", factorName,
            mainEffectDof, mainEffectFStat, mainEffectPValue,
            residualDof, residualFStat, residualPValue );
    }
}
