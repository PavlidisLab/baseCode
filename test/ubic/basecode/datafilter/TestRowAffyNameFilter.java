/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
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