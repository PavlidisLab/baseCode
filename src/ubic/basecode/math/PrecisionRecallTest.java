/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
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

package ubic.basecode.math;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author paul
 * @version $Id$
 */
public class PrecisionRecallTest {

    @Test
    public void testApr() {

        Double[] ranks = new Double[] { 0.0, 2.0, 5.0, 9.0, 14.0 };

        double aprc = PrecisionRecall.meanAveragePrecision( 11, Arrays.asList( ranks ) );

        assertEquals( 0.7883, aprc, 0.001 );
    }

    @Test
    public void testAveragePrecision() {
        Double[] ranks = new Double[] { 0.0, 2.0, 5.0, 9.0, 14.0 };

        double ap = PrecisionRecall.averagePrecision( 15, Arrays.asList( ranks ) );

        assertEquals( 0.580, ap, 0.001 );
    }
}
