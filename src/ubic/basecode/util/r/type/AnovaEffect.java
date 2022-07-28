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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents one row of an ANOVA table
 * 
 * @author paul
 * 
 */
public class AnovaEffect implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double degreesOfFreedom = null;

    private String effectName = null;

    private Double fStatistic = Double.NaN;

    private boolean isInteraction = false;

    private Double meanSq = Double.NaN;

    private Double pValue = Double.NaN;

    private Double ssQ = Double.NaN;

    /**
     * @param effectName
     * @param pValue
     * @param fStatistic
     * @param degreesOfFreedom
     * @param ssQ
     * @param isInteraction
     */
    public AnovaEffect( String effectName, Double pValue, Double fStatistic, Double degreesOfFreedom, Double ssQ,
            boolean isInteraction ) {
        super();
        this.effectName = effectName;
        this.pValue = pValue;
        this.fStatistic = fStatistic;
        this.degreesOfFreedom = degreesOfFreedom;
        this.ssQ = ssQ;
        this.meanSq = ssQ / degreesOfFreedom;
        this.isInteraction = isInteraction;
    }

    /**
     * @return the degreesOfFreedom
     */
    public Double getDegreesOfFreedom() {
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

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append( StringUtils.rightPad( StringUtils.abbreviate( getEffectName(), 10 ), 10 ) + "\t" );
        buf.append( String.format( "%.2f", getDegreesOfFreedom() ) + "\t" );
        buf.append( String.format( "%.4f", getSsQ() ) + "\t" );
        buf.append( String.format( "%.4f", getMeanSq() ) + "\t" );

        if ( fStatistic != null ) {
            buf.append( StringUtils.rightPad( String.format( "%.3f", getFStatistic() ), 6 ) + "\t" );
            buf.append( String.format( "%.3g", getPValue() ) );
        }
        return buf.toString();
    }

}
