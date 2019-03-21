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
 * An interface that describes objects that can produce probabilities according to some distribution.
 * 
 * @author pavlidis
 * 
 */
public interface ProbabilityComputer {

    /**
     * Return the probability associated with a certain value.The upper tail of the associated distribution is returned.
     * 
     * @param value
     * @return
     */
    public double probability( double value );

    /**
     * Return the probability associated with a certain value, with choice of tail.
     * 
     * @param value
     * @param upperTail
     * @return
     */
    public double probability( double value, boolean upperTail );

}