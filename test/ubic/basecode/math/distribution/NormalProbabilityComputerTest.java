/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Paul
 * @version $Id$
 */
public class NormalProbabilityComputerTest {

    @SuppressWarnings("unused")
    @Test
    public void testInvalidVariance() {
        try {
            new NormalProbabilityComputer( 0.0, -1.0 );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {
            // OK
        }
    }

    /**
     * Test method for {@link ubic.basecode.math.distribution.NormalProbabilityComputer#probability(double)}.
     */
    @Test
    public void testProbabilityDouble() {
        NormalProbabilityComputer c = new NormalProbabilityComputer( 0.0, 1.0 );
        assertEquals( 0.02275013, c.probability( 2.0 ), 0.00001 );
    }

    /**
     * Test method for {@link ubic.basecode.math.distribution.NormalProbabilityComputer#probability(double, boolean)}.
     */
    @Test
    public void testProbabilityDoubleBoolean() {
        NormalProbabilityComputer c = new NormalProbabilityComputer( 0.0, 1.0 );
        assertEquals( 0.02275013, c.probability( 2.0, true ), 0.00001 );
    }

    @Test
    public void testProbabilityDoubleBooleanLower() {
        NormalProbabilityComputer c = new NormalProbabilityComputer( 0.0, 1.0 );
        assertEquals( 1.0 - 0.02275013, c.probability( 2.0, false ), 0.00001 );
    }

}
