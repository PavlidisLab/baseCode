/*
 * The basecode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package ubic.basecode.math;

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.io.reader.DoubleMatrixReader;
import cern.colt.list.DoubleArrayList;

/**
 * @author pavlidis
 * @version $Id$
 */
public class MultipleTestCorrectionTest extends TestCase {

    private DoubleArrayList values;

    /*
     * Test method for 'basecode.math.MultipleTestCorrection.BenjaminiHochbergCut(DoubleArrayList, double)'
     */
    public void testBenjaminiHochbergCut() throws Exception {
        double actualResult = MultipleTestCorrection.BenjaminiHochbergCut( values, 0.01 );
        double expectedResult = 0.0018;
        assertEquals( expectedResult, actualResult, 0.00001 );
    }

    /*
     * Test method for 'basecode.math.MultipleTestCorrection.BenjaminiYekuteliCut(DoubleArrayList, double)'
     */
    public void testBenjaminiYekuteliCut() throws Exception {
        double actualResult = MultipleTestCorrection.BenjaminiYekuteliCut( values, 0.01 );
        double expectedResult = 0.00013;
        assertEquals( expectedResult, actualResult, 0.00001 );
    }

    /*
     * Test method for 'basecode.math.MultipleTestCorrection.BonferroniCut(DoubleArrayList, double)'
     */
    public void testBonferroniCut() throws Exception {
        double actualResult = MultipleTestCorrection.BonferroniCut( values, 0.01 );
        double expectedResult = 3.30E-06;
        assertEquals( expectedResult, actualResult, 0.00001 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DoubleMatrixReader dmr = new DoubleMatrixReader();
        DoubleMatrixNamed<String, String> mat = ( DoubleMatrixNamed<String, String> ) dmr.read( this.getClass()
                .getResourceAsStream( "/data/multtest.test.txt" ) );

        values = new DoubleArrayList( mat.getColumnByName( "rawp" ) );
    }

}
