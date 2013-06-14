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

/**
 * @author pavlidis
 * @version $Id$
 */
public class UniformDensityComputer implements DensityGenerator {

    double max = 1;
    double min = 0;

    /**
     * Create a UniformDensityComputer where the density is defined over the unit inteval [0,1].
     */
    public UniformDensityComputer() {
        this( 0, 1 );
    }

    /**
     * Create a UniformDensityComputer where the density is defined over the interval given
     * 
     * @param min
     * @param max
     */
    public UniformDensityComputer( double min, double max ) {
        if ( max <= min ) {
            throw new IllegalArgumentException( "Max must be higher than min" );
        }
        this.min = min;
        this.max = max;
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.math.DensityGenerator#density(double)
     */
    @Override
    public double density( double x ) {

        if ( x > max || x < min ) return 0;

        return 1 / ( max - min );
    }

}