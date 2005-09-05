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

/**
 * See the file test/data/matrix-testing.xls for the validation.
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestRowMissingFilter extends AbstractTestFilter {

    RowMissingFilter f = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        f = new RowMissingFilter();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        f = null;
        super.tearDown();
    }

    public void testFilter() {
        f.setMinPresentCount( 12 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int expectedReturn = testdata.rows();
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterNoFiltering() {
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testdata );
        int expectedReturn = testdata.rows();
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissing() {
        f.setMinPresentCount( 12 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testmissingdata );
        int expectedReturn = 21;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissingLowMaxFraction() {
        f.setMaxFractionRemoved( 0.1 );
        f.setMinPresentCount( 12 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testmissingdata );
        int expectedReturn = 21;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissingLessStringent() {

        f.setMinPresentCount( 10 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testmissingdata );
        int expectedReturn = 29;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterStringMatrix() {
        f.setMinPresentCount( 12 );
        StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) f.filter( teststringmissingdata );
        int expectedReturn = 21;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterFraction() {
        f.setMinPresentFraction( 1.0 );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) f.filter( testmissingdata );
        int expectedReturn = 21;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterFractionInvalid() {
        try {
            f.setMinPresentFraction( 934109821 );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {

        }

    }

    public void testFilterFractionInvalid2() {
        try {
            f.setMinPresentCount( -1093 );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {

        }

    }

    public void testMaxFractionRemovedInvalid() {
        try {
            f.setMaxFractionRemoved( 934109821 );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {

        }

    }

    public void testFilterPresentCountInvalid() {
        try {
            f.setMinPresentCount( 129 );
            f.filter( testmissingdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalStateException e ) {

        }

    }

}