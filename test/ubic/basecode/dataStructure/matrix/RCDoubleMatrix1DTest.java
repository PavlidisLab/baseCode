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
package ubic.basecode.dataStructure.matrix;

import cern.colt.function.DoubleFunction;
import junit.framework.TestCase;

/**
 * @author Paul
 * @version $Id$
 */
public class RCDoubleMatrix1DTest extends TestCase {

    RCDoubleMatrix1D tester;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tester = new RCDoubleMatrix1D( new double[] { 1.0, 2.0, 3.0, Double.NaN, 5.0, 8.0 } );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.matrix.RCDoubleMatrix1D#zSum()}.
     */
    public void testZSum() {
        assertEquals( 19.0, tester.zSum(), 0.00001 );
    }

    public void testAssign() {
        tester.assign( new DoubleFunction() {
            public double apply( double argument ) {
                return argument * 2;
            }
        } );

        assertEquals( 2, tester.get( 0 ), 0.0001 );
        assertEquals( 4, tester.get( 1 ), 0.0001 );
        assertEquals( 6, tester.get( 2 ), 0.0001 );
        assertEquals( 0, tester.get( 3 ), 0.00001 );
        assertEquals( 10, tester.get( 4 ), 0.0001 );

    }

    public void testAssignAr() {
        tester.assign( new double[] { 1.0, 2.0, 3.0, Double.NaN, 5.0, 8.0 } );

        assertEquals( 1, tester.get( 0 ), 0.0001 );
        assertEquals( 2, tester.get( 1 ), 0.0001 );
        assertEquals( 3, tester.get( 2 ), 0.0001 );
        assertEquals( 0, tester.get( 3 ), 0.0001 );
        assertEquals( 5, tester.get( 4 ), 0.0001 );

    }

}
