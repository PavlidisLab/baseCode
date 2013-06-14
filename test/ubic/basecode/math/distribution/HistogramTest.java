/*
 * The baseCode project
 * 
 * Copyright (c) 2012 University of British Columbia
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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author paul
 * @version $Id$
 */
public class HistogramTest {

    /**
     * Test method for {@link ubic.basecode.math.distribution.Histogram#getApproximateQuantile(int)}.
     */
    @Test
    public void testGetApproximateQuantile() {
        Histogram h = new Histogram( "foo", 100, 0, 100 );
        for ( int i = 0; i < 100; i++ ) {
            for ( int j = 0; j < 10; j++ ) {
                h.fill( i );
            }
            // if ( i == 52 ) {
            // for ( int j = 0; j < 1000; j++ ) {
            // h.fill( i );
            // }
            // }
        }

        assertEquals( 0, h.getApproximateQuantile( 0 ), 0.001 );
        assertEquals( 100, h.getApproximateQuantile( 100 ), 0.001 );
        assertEquals( 19, h.getApproximateQuantile( 20 ), 0.001 );

    }

    /**
     * Test method for {@link ubic.basecode.math.distribution.Histogram#getBiggestBinSize()}.
     */
    @Test
    public void testGetBiggestBinSize() {
        Histogram h = new Histogram( "foo", 100, 0, 100 );
        for ( int i = 0; i < 100; i++ ) {
            for ( int j = 0; j < 10; j++ ) {
                h.fill( i );
            }
            if ( i == 52 ) {
                for ( int j = 0; j < 10; j++ ) {
                    h.fill( i );
                }
                h.fill( 1000 );
            }

        }

        assertEquals( 20, h.getBiggestBinSize() );

        assertEquals( 1, h.overflow(), 0.001 );
    }

}
