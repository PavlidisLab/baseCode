/*
 * The basecode project
 * 
 * Copyright (c) 2005 Columbia University
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
package ubic.basecode.datafilter;

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;

/**
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestRowLevelFilter extends AbstractTestFilter {
    RowLevelFilter f = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        f = new RowLevelFilter();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Class under test for NamedMatrix filter(NamedMatrix)
     */
    public final void testFilterMax() {
        f.setLowCut( 100.0, false );
        f.setRemoveAllNegative( true ); // irrelevant.
        f.setMethod( RowLevelFilter.MAX ); // this is the default.
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 26;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMaxTooHigh() {
        f.setLowCut( 100000000.0, false );
        f.setRemoveAllNegative( true ); // irrelevant.
        f.setMethod( RowLevelFilter.MAX ); // this is the default.
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 0;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMin() {
        f.setLowCut( 100.0, false );
        f.setRemoveAllNegative( true );
        f.setMethod( RowLevelFilter.MIN );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 16;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMean() {
        f.setLowCut( 100.0, false );
        f.setRemoveAllNegative( true );
        f.setMethod( RowLevelFilter.MEAN );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 21;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMedian() {
        f.setLowCut( 100.0, false );
        f.setRemoveAllNegative( true ); // irrelevant
        f.setMethod( RowLevelFilter.MEDIAN );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 20;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMaxFraction() {
        f.setLowCut( 0.3, true );
        f.setRemoveAllNegative( false );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 21;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMaxFractionAll() {
        f.setLowCut( 1.0, true );
        f.setRemoveAllNegative( false );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 1; // we don't actually ever remove all items.
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMaxFractionNone() {
        f.setLowCut( 0.0, true );
        f.setRemoveAllNegative( false );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 30;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMaxFractionNoneRemoveNeg() {
        f.setLowCut( 0.0, true );
        f.setRemoveAllNegative( true );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 27;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterFractionMaxRemoveNeg() {
        f.setLowCut( 0.6, true );
        f.setRemoveAllNegative( true ); // removes 3 of 30 rows. 27 * 0.6 =16.2,
        // ceil 17, + 3 = 20
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 11;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterBadFraction() {
        try {
            f.setLowCut( 110.3, true );
            f.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( RuntimeException success ) {

        }
    }

    public final void testFilterNoFilter() {
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        assertEquals( "return value", testdata, filtered );
    }

    public final void testFilterMaxHigh() {
        f.setHighCut( 1000.0, false );
        f.setRemoveAllNegative( true ); // irrelevant.
        f.setMethod( RowLevelFilter.MAX ); // this is the default.
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 16;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMinHigh() {
        f.setHighCut( 1000.0, false );
        f.setRemoveAllNegative( true );
        f.setMethod( RowLevelFilter.MIN );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 24;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMeanHigh() {
        f.setHighCut( 1000.0, false );
        f.setRemoveAllNegative( true );
        f.setMethod( RowLevelFilter.MEAN );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 21;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMedianHigh() {
        f.setHighCut( 1000.0, false );
        f.setRemoveAllNegative( true ); // irrelevant
        f.setMethod( RowLevelFilter.MEDIAN );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 22;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterMaxFractionHigh() {
        f.setHighCut( 0.3, true );
        f.setRemoveAllNegative( false );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 21;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterFractionMaxRemoveNegHigh() {
        f.setHighCut( 0.3, true );
        f.setRemoveAllNegative( true );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 19;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterFractionMaxRemoveHighAndLow() {
        f.setHighCut( 0.3, true );
        f.setLowCut( 100, false );
        f.setRemoveAllNegative( true );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 15;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterFractionMaxRemoveHighAndLowBothFraction() {
        f.setHighCut( 0.3, true );
        f.setLowCut( 0.1, true );
        f.setRemoveAllNegative( true );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 14; // 3 neg, 0.1*27 = 3, 0.3*27 = 9, inclusive at
        // top means 10 + 3 + 3 = 16, from 30 is 14.
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterFractionMaxRemoveHighAndLowBothLevels() {
        f.setHighCut( 1000, false );
        f.setLowCut( 100, false );
        f.setRemoveAllNegative( true ); // irrelevant
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 12;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterFractionMaxRemoveHighAndLowBothLevelsNoNegRemove() {
        f.setHighCut( 1000, false );
        f.setLowCut( 100, false );
        f.setRemoveAllNegative( false );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 12;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public final void testFilterFractionMaxRemoveHighAndLowBothLevelsNegLow() {
        f.setHighCut( 1000, false );
        f.setLowCut( -100, false );
        f.setRemoveAllNegative( true );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int actualReturn = filtered.rows();
        int expectedReturn = 14;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}