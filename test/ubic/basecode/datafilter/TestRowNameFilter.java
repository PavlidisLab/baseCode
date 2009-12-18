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

import java.util.HashSet;
import java.util.Set;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;

/**
 * @author Pavlidis
 * @version $Id$
 */

public class TestRowNameFilter extends AbstractTestFilter {

    protected Set<String> testfilterlist;

    public void testFilter() {
        RowNameFilter<DoubleMatrix<String, String>, String, String, Double> fi = new RowNameFilter<DoubleMatrix<String, String>, String, String, Double>(
                testfilterlist );
        DoubleMatrix<String, String> filtered = fi.filter( testdata );
        int expectedReturn = 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterExclude() {
        RowNameFilter<DoubleMatrix<String, String>, String, String, Double> fi = new RowNameFilter<DoubleMatrix<String, String>, String, String, Double>(
                testfilterlist, true );
        DoubleMatrix<String, String> filtered = fi.filter( testdata );
        int expectedReturn = testdata.rows() - 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterString() {
        RowNameFilter<StringMatrix<String, String>, String, String, String> fi = new RowNameFilter<StringMatrix<String, String>, String, String, String>(
                testfilterlist );
        StringMatrix<String, String> filtered = fi.filter( teststringdata );
        int expectedReturn = 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterStringExclude() {
        RowNameFilter<StringMatrix<String, String>, String, String, String> fi = new RowNameFilter<StringMatrix<String, String>, String, String, String>(
                testfilterlist, true );
        StringMatrix<String, String> filtered = fi.filter( teststringdata );
        int expectedReturn = teststringdata.rows() - 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testfilterlist = new HashSet<String>();
        testfilterlist.add( "gene1_at" );
        testfilterlist.add( "gene4_at" );
        testfilterlist.add( "gene13_s_at" );
        testfilterlist.add( "AFFXgene30_at" );
        testfilterlist.add( "fooblydoobly" ); // shouldn't care.
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testfilterlist = null;
    }

}