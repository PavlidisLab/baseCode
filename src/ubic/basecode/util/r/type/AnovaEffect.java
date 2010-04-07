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

/**
 * Represents one row of an ANOVA table
 * 
 * @author paul
 * @version $Id$
 */
public class AnovaEffect {

    private Integer degreesOfFreedom = null;

    private String effectName = null;

    private Double fStatistic = Double.NaN;

    private boolean isInteraction = false;

    private Double meanSq = Double.NaN;

    private Double pValue = Double.NaN;

    private Double ssQ = Double.NaN;

    public AnovaEffect( String effectName, Double pValue, Double fStatistic, Integer degresOfFreedom, Double meanSq,
            Double ssQ, boolean isInteraction ) {
        super();
        this.effectName = effectName;
        this.pValue = pValue;
        this.fStatistic = fStatistic;
        this.degreesOfFreedom = degresOfFreedom;
        this.ssQ = ssQ;
        this.meanSq = meanSq;
        this.isInteraction = isInteraction;
    }

    /**
     * @return the degreesOfFreedom
     */
    public Integer getDegreesOfFreedom() {
        return degreesOfFreedom;
    }

    /**
     * @return the effectName
     */
    public String getEffectName() {
        return effectName;
    }

    /**
     * @return the fStatistic
     */
    public Double getFStatistic() {
        return fStatistic;
    }

    /**
     * @return the meanSq
     */
    public Double getMeanSq() {
        return meanSq;
    }

    /**
     * @return the pValue
     */
    public Double getPValue() {
        return pValue;
    }

    /**
     * @return the ssQ
     */
    public Double getSsQ() {
        return ssQ;
    }

    /**
     * @return the isInteraction
     */
    public boolean isInteraction() {
        return isInteraction;
    }

}
