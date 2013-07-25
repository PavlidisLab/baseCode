/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package ubic.basecode.dataStructure.matrix;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * @author pavlidis
 * @version $Id$
 */
public class RCDoubleMatrix1DTest2 {

    RCDoubleMatrix1D a;
    RCDoubleMatrix1D b;
    DoubleMatrix1D c;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() {

        /*
         * a: 0 1 2 0 5 b: 5 3 2 1 (0)
         */

        DoubleArrayList va = new DoubleArrayList( new double[] { 1, 2, 5 } );
        IntArrayList ina = new IntArrayList( new int[] { 1, 2, 4 } );

        DoubleArrayList vb = new DoubleArrayList( new double[] { 5, 3, 2, 1 } );
        IntArrayList inb = new IntArrayList( new int[] { 0, 1, 2, 3 } );

        a = new RCDoubleMatrix1D( ina, va );
        b = new RCDoubleMatrix1D( inb, vb );

        c = new DenseDoubleMatrix1D( new double[] { 5, 3, 2, 1 } );
    }

    /*
     * Class under test for DoubleMatrix1D assign(DoubleFunction)
     */
    @Test
    public void testAssignDoubleFunction() {
        DoubleMatrix1D actualReturn = a.assign( new cern.colt.function.DoubleFunction() {
            @Override
            public double apply( double value ) {
                return 2;
            }
        } );
        DoubleMatrix1D expectedReturn = new RCDoubleMatrix1D( new double[] { 0, 2, 2, 0, 2 } );
        assertEquals( "return value", new DoubleArrayList( expectedReturn.toArray() ), new DoubleArrayList(
                actualReturn.toArray() ) );
    }

    /*
     * Class under test for double zDotProduct(DoubleMatrix1D)
     */
    @Test
    public void testZDotProductDoubleMatrix1D() {
        double actualReturn = a.zDotProduct( b );
        double expectedReturn = 7;
        assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
    }

    /*
     * Class under test for double zDotProduct(DoubleMatrix1D)
     */
    @Test
    public void testZDotProductDoubleMatrix1DHarder() {
        double actualReturn = a.zDotProduct( c );
        double expectedReturn = 7;
        assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
    }

    /*
     * Class under test for double zDotProduct(DoubleMatrix1D)
     */
    @Test
    public void testZDotProductDoubleMatrix1DHarderReverse() {
        double actualReturn = c.zDotProduct( a );
        double expectedReturn = 7;
        assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
    }

    /*
     * Class under test for double zDotProduct(DoubleMatrix1D)
     */
    @Test
    public void testZDotProductDoubleMatrix1DReverse() {
        double actualReturn = b.zDotProduct( a );
        double expectedReturn = 7;
        assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
    }
}