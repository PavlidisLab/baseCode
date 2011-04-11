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
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * @author pavlidis
 * @version $Id$
 */
public class MultipleTestCorrectionTest extends TestCase {

    private DoubleArrayList values;

    DoubleMatrix<String, String> mat;

    /**
     * @throws Exception
     */
    public void testBenjaminiHochberg() throws Exception {
        DoubleArrayList actualResult = MultipleTestCorrection.benjaminiHochberg( values );
        DoubleArrayList expected = new DoubleArrayList( mat.getColumnByName( "BH" ) );
        for ( int i = 0; i < actualResult.size(); i++ ) {
            assertEquals( "At " + i, expected.get( i ), actualResult.get( i ), expected.get( i ) / 10.0 );
        }
    }

    public void testBenjaminiHochbergM() throws Exception {
        DoubleMatrix1D actualResult = MultipleTestCorrection.benjaminiHochberg( new DenseDoubleMatrix1D( values
                .elements() ) );
        DoubleArrayList expected = new DoubleArrayList( mat.getColumnByName( "BH" ) );
        for ( int i = 0; i < actualResult.size(); i++ ) {
            assertEquals( "At " + i, expected.get( i ), actualResult.get( i ), expected.get( i ) / 10.0 );
        }
    }

    /*
     * Test method for 'basecode.math.MultipleTestCorrection.BenjaminiHochbergCut(DoubleArrayList, double)'
     */
    public void testBenjaminiHochbergCut() throws Exception {
        double actualResult = MultipleTestCorrection.benjaminiHochbergCut( values, 0.01 );
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

    /**
     * @throws Exception
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
        mat = dmr.read( this.getClass().getResourceAsStream( "/data/multtest.test.randord.txt" ) );
        values = new DoubleArrayList( mat.getColumnByName( "rawp" ) );
        values.trimToSize();
    }

}
