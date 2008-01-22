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
package ubic.basecode.datafilter;

import ubic.basecode.math.Stats;

/**
 * Abstract class representing a filter that removes things from matrices based on the values themselves.
 * 
 * @author pavlidis
 * @version $Id$
 */
public abstract class AbstractLevelFilter<R, C> extends AbstractFilter<R, C> {

    protected double lowCut = -Double.MAX_VALUE;
    protected double highCut = Double.MAX_VALUE;
    protected boolean useLowAsFraction = false;
    protected boolean useHighAsFraction = false;

    /**
     * Set the low threshold for removal.
     * 
     * @param lowCut the threshold
     */
    public void setLowCut( double lowCut ) {
        this.lowCut = lowCut;
    }

    /**
     * @param lowCut
     * @param isFraction
     */
    public void setLowCut( double lowCut, boolean isFraction ) {
        setLowCut( lowCut );
        setUseLowCutAsFraction( isFraction );
        useLowAsFraction = isFraction;
    }

    /**
     * Set the high threshold for removal. If not set, no filtering will occur.
     * 
     * @param h the threshold
     */
    public void setHighCut( double h ) {
        highCut = h;
    }

    /**
     * @param highCut
     * @param isFraction
     */
    public void setHighCut( double highCut, boolean isFraction ) {
        setHighCut( highCut );
        setUseHighCutAsFraction( isFraction );
        useHighAsFraction = isFraction;
    }

    /**
     * @param setting
     */
    public void setUseHighCutAsFraction( boolean setting ) {
        if ( setting == true && !Stats.isValidFraction( highCut ) ) {
            throw new IllegalArgumentException( "Value for cut(s) are invalid for using "
                    + "as fractions, must be >0.0 and <1.0," );
        }
        useHighAsFraction = setting;
    }

    /**
     * @param setting
     */
    public void setUseLowCutAsFraction( boolean setting ) {
        if ( setting == true && !Stats.isValidFraction( lowCut ) ) {
            throw new IllegalArgumentException( "Value for cut(s) are invalid for using "
                    + "as fractions, must be >0.0 and <1.0," );
        }
        useLowAsFraction = setting;
    }

    /**
     * Set the filter to interpret the low and high cuts as fractions; that is, if true, lowcut 0.1 means remove 0.1 of
     * the rows with the lowest values. Otherwise the cuts are interpeted as actual values. Default = false.
     * 
     * @param setting boolean
     */
    public void setUseAsFraction( boolean setting ) {
        setUseHighCutAsFraction( setting );
        setUseLowCutAsFraction( setting );
    }

}