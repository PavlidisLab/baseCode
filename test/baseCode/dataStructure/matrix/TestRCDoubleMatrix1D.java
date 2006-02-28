/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.dataStructure.matrix;

import junit.framework.TestCase;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestRCDoubleMatrix1D extends TestCase {

    RCDoubleMatrix1D a;
    RCDoubleMatrix1D b;
    DoubleMatrix1D c;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {

        /*
         * a: 0 1 2 0 5 b: 5 3 2 1 (0)
         */

        DoubleArrayList va = new DoubleArrayList( new double[] { 1, 2, 5 } );
        IntArrayList ina = new IntArrayList( new int[] { 1, 2, 4 } );

        DoubleArrayList vb = new DoubleArrayList( new double[] { 5, 3, 2, 1 } );
        IntArrayList inb = new IntArrayList( new int[] { 0, 1, 2, 3 } );

        // DoubleArrayList vc = new DoubleArrayList( new double[] {
        // 5, 3, 2, 1
        // } );
        // IntArrayList inc = new IntArrayList( new int[] {
        // 0, 1, 2, 3
        // } );

        a = new RCDoubleMatrix1D( ina, va );
        b = new RCDoubleMatrix1D( inb, vb );

        c = new DenseDoubleMatrix1D( new double[] { 5, 3, 2, 1 } );
        super.setUp();
    }

    // FIXME fails in maven 2 http://jira.codehaus.org/browse/MSUREFIRE-59

    // public void testForEachNonZero() {
    // DoubleMatrix1D actualReturn = a.forEachNonZero( new cern.colt.function.DoubleFunction() {
    // public double apply( double value ) {
    // return value / 2.0;
    // }
    // } );
    // DoubleMatrix1D expectedReturn = new RCDoubleMatrix1D( new double[] { 0, 0.5, 1, 0, 2.5 } );
    // assertEquals( "return value", new DoubleArrayList( expectedReturn.toArray() ), new DoubleArrayList(
    // actualReturn.toArray() ) );
    // }

    /*
     * Class under test for double zDotProduct(DoubleMatrix1D)
     */
    public void testZDotProductDoubleMatrix1D() throws Exception {
        double actualReturn = a.zDotProduct( b );
        double expectedReturn = 7;
        assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
    }

    /*
     * Class under test for double zDotProduct(DoubleMatrix1D)
     */
    public void testZDotProductDoubleMatrix1DReverse() throws Exception {
        double actualReturn = b.zDotProduct( a );
        double expectedReturn = 7;
        assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
    }

    /*
     * Class under test for double zDotProduct(DoubleMatrix1D)
     */
    public void testZDotProductDoubleMatrix1DHarder() throws Exception {
        double actualReturn = a.zDotProduct( c );
        double expectedReturn = 7;
        assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
    }

    /*
     * Class under test for double zDotProduct(DoubleMatrix1D)
     */
    public void testZDotProductDoubleMatrix1DHarderReverse() throws Exception {
        double actualReturn = c.zDotProduct( a );
        double expectedReturn = 7;
        assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
    }

    /*
     * Class under test for DoubleMatrix1D assign(DoubleFunction)
     */

    // FIXME test fails in maven 2.http://jira.codehaus.org/browse/MSUREFIRE-59
    // public void testAssignDoubleFunction() throws Exception {
    // DoubleMatrix1D actualReturn = a.assign( new cern.colt.function.DoubleFunction() {
    // public double apply( double value ) {
    // return 2;
    // }
    // } );
    // DoubleMatrix1D expectedReturn = new RCDoubleMatrix1D( new double[] { 0, 2, 2, 0, 2 } );
    // assertEquals( "return value", new DoubleArrayList( expectedReturn.toArray() ), new DoubleArrayList(
    // actualReturn.toArray() ) );
    // }
}