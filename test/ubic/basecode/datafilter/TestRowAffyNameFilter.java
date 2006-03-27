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
import ubic.basecode.dataStructure.matrix.StringMatrix2DNamed;

/**
 * @author Pavlidis
 * @version $Id$
 */
public class TestRowAffyNameFilter extends AbstractTestFilter {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFilter() {
        RowAffyNameFilter fi = new RowAffyNameFilter( new int[] { RowAffyNameFilter.AFFX, RowAffyNameFilter.X,
                RowAffyNameFilter.ST, RowAffyNameFilter.F } );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) fi.filter( testdata );
        int expectedReturn = teststringdata.rows() - 5; // file contains on AFFX,
        // and two _f_ tags.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testStringFilter() {
        RowAffyNameFilter fi = new RowAffyNameFilter( new int[] { RowAffyNameFilter.AFFX, RowAffyNameFilter.X,
                RowAffyNameFilter.ST, RowAffyNameFilter.F } );
        StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) fi.filter( teststringdata );
        int expectedReturn = teststringdata.rows() - 5; // file contains on AFFX,
        // and two _f_ tags.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}