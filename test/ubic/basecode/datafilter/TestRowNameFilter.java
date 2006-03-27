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

import java.util.HashSet;
import java.util.Set;

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.dataStructure.matrix.StringMatrix2DNamed;

/**
 * @author Pavlidis
 * @version $Id$
 */

public class TestRowNameFilter extends AbstractTestFilter {

    protected Set testfilterlist;

    protected void setUp() throws Exception {
        super.setUp();

        testfilterlist = new HashSet();
        testfilterlist.add( "gene1_at" );
        testfilterlist.add( "gene4_at" );
        testfilterlist.add( "gene13_s_at" );
        testfilterlist.add( "AFFXgene30_at" );
        testfilterlist.add( "fooblydoobly" ); // shouldn't care.
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        testfilterlist = null;
    }

    public void testFilter() {
        RowNameFilter fi = new RowNameFilter( testfilterlist );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) fi.filter( testdata );
        int expectedReturn = 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterExclude() {
        RowNameFilter fi = new RowNameFilter( testfilterlist, true );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) fi.filter( testdata );
        int expectedReturn = testdata.rows() - 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterString() {
        RowNameFilter fi = new RowNameFilter( testfilterlist );
        StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) fi.filter( teststringdata );
        int expectedReturn = 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterStringExclude() {
        RowNameFilter fi = new RowNameFilter( testfilterlist, true );
        StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) fi.filter( teststringdata );
        int expectedReturn = teststringdata.rows() - 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}