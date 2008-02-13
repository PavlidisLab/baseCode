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
package ubic.basecode.math.distribution;

import cern.jet.stat.Probability;

/**
 * @author pavlidis
 * @version $Id$
 */
public class NormalProbabilityComputer implements ProbabilityComputer {

    double variance;
    double mean;

    /**
     * @param mean
     * @param variance
     */
    public NormalProbabilityComputer( double mean, double variance ) {
        super();
        this.variance = variance;

        if ( variance < 0 ) {
            throw new IllegalArgumentException( "Variance must be non-negative" );
        }

        this.mean = mean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.math.ProbabilityComputer#probability(double)
     */
    public double probability( double value ) {
        return 1.0 - Probability.normal( mean, variance, value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.math.distribution.ProbabilityComputer#probability(double, boolean)
     */
    public double probability( double value, boolean upperTail ) {
        if ( upperTail ) {
            return probability( value );
        }

        return Probability.normal( mean, variance, value );

    }

}