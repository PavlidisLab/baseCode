/*
 * The baseCode project
 * 
 * Copyright (c) 2008-2019 University of British Columbia
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Paul
 * 
 */
public class UniformDensityComputerTest {

    /**
     * Test method for {@link ubic.basecode.math.distribution.UniformDensityComputer#density(double)}.
     */
    @Test
    public void testDensity() {
        UniformDensityComputer u = new UniformDensityComputer( 0, 10 );
        double actual = u.density( 0.4 );
        assertEquals( 0.1, actual, 0.000001 );
    }

}
