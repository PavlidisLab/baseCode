/*
 * The baseCode project
 * 
 * Copyright (c) 2005 Columbia University
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
package baseCode.dataFilter;

import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import baseCode.dataStructure.matrix.StringMatrix2DNamed;
import baseCode.io.reader.StringMatrixReader;

/**
 * See the file test/data/matrix-testing.xls for the validation. Copyright (c) 2004 Columbia University
 * 
 * @author Owner
 * @version $Id$
 */
public class TestRowAbsentFilter extends AbstractTestFilter {

    StringMatrix2DNamed testpdata = null;

    RowAbsentFilter f = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        f = new RowAbsentFilter();
        StringMatrixReader s = new StringMatrixReader();
        testpdata = ( StringMatrix2DNamed ) s.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/test-presence-data.txt" ) );
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        testpdata = null;
        testdata = null;
        super.tearDown();
    }

    /*
     * Class under test for DoubleMatrixNamed filter(DoubleMatrixNamed)
     */
    public void testFilter() {
        f.setFlagMatrix( testpdata );
        f.setMinPresentCount( 12 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int expectedReturn = testdata.rows() - 7; // 7 rows have some absent or
        // marginal.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissing() {
        f.setFlagMatrix( testpdata );
        f.setMinPresentCount( 12 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testmissingdata );
        int expectedReturn = 17;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissingLessStringent() {
        f.setFlagMatrix( testpdata );
        f.setMinPresentCount( 8 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testmissingdata );
        int expectedReturn = 24;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterStringMatrix() {
        f.setFlagMatrix( testpdata );
        f.setMinPresentCount( 12 );
        StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) f.filter( teststringdata );
        int expectedReturn = testdata.rows() - 7; // 7 rows have some missing or
        // marginal OR absent.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterKeepMarginal() {
        f.setFlagMatrix( testpdata );
        f.setKeepMarginal( true );
        f.setMinPresentCount( 12 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int expectedReturn = testdata.rows() - 6; // 6 rows have some absent
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterFraction() {
        f.setFlagMatrix( testpdata );
        f.setMinPresentFraction( 1.0 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int expectedReturn = testdata.rows() - 7; // 7 rows have some missing or
        // marginal.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterFractionInvalid() {
        try {
            f.setFlagMatrix( testpdata );
            f.setMinPresentFraction( 934109821 );
            f.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {
            System.err.println( e );
        }

    }

    public void testFilterPresentCountInvalid() {
        try {
            f.setFlagMatrix( testpdata );
            f.setMinPresentCount( 129 );
            f.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalStateException e ) {
            System.err.println( e );
        }

    }

    public void testFilterNullFlags() {
        try {
            f.setFlagMatrix( null );
            f.setMinPresentCount( 10 );
            f.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalStateException success ) {
            System.err.println( success );
        } catch ( IllegalArgumentException success ) {
            System.err.println( success );
        }
    }

}