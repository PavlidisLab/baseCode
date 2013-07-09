/*
 * The baseCode project
 * 
 * Copyright (c) 2007 University of British Columbia
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
 * @version $Id$
 */
public class HistogramSamplerTest {

    /**
     * Test method for {@link ubic.basecode.math.distribution.HistogramSampler#nextSample()}.
     */
    @Test
    public final void testNextSample() {
        int[] testHistogram = new int[] { 1, 10, 48, 20, 10, 10, 1 };

        HistogramSampler s = new HistogramSampler( testHistogram, -3, 3 );

        double numMax = 0.0;
        double numTwo = 0.0;
        double numOne = 0.0;
        double numZero = 0.0;
        double numNegOne = 0.0;
        int numiters = 100000;
        for ( int k = 0; k < numiters; k++ ) {
            double nextSample = s.nextSample();
            // log.warn( "" + nextSample );
            if ( Math.abs( nextSample - 3.0 ) < 0.0000001 ) {
                numMax++;
            } else if ( Math.abs( nextSample - 2.0 ) < 0.0000001 ) {
                numTwo++;
            } else if ( Math.abs( nextSample - 1.0 ) < 0.0000001 ) {
                numOne++;
            } else if ( Math.abs( nextSample ) < 0.0000001 ) {
                numZero++;
            } else if ( Math.abs( nextSample + 1.0 ) < 0.0000001 ) {
                numNegOne++;
            }
        }

        // Check that counts are within 10% of expectation. Note that this is not deterministic. It is theoretically
        // possible for it to fail. If this is a problem
        // increase numiters.
        assertEquals( numiters * 0.01, numMax, numiters * 0.01 * 0.1 );
        assertEquals( numiters * 0.1, numTwo, numiters * 0.1 * 0.1 );
        assertEquals( numiters * 0.1, numOne, numiters * 0.1 * 0.1 );
        assertEquals( numiters * 0.2, numZero, numiters * 0.2 * 0.1 );
        assertEquals( numiters * 0.48, numNegOne, numiters * 0.48 * 0.1 );

    }
}
