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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;

/**
 * See the file test/data/matrix-testing.xls for the validation.
 * 
 * @author pavlidis
 * 
 */
public class TestRowMissingFilter extends AbstractTestFilter {

    RowMissingFilter<DoubleMatrix<String, String>, String, String, Double> f = null;
    RowMissingFilter<StringMatrix<String, String>, String, String, String> fss = null;

    /*
     * @see TestCase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        f = new RowMissingFilter<DoubleMatrix<String, String>, String, String, Double>();
        fss = new RowMissingFilter<StringMatrix<String, String>, String, String, String>();
    }

    @Test
    public void testFilter() {
        f.setMinPresentCount( 12 );
        DoubleMatrix<String, String> filtered = f.filter( testdata );
        int expectedReturn = testdata.rows();
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testFilterFraction() {
        f.setMinPresentFraction( 1.0 );
        DoubleMatrix<String, String> filtered = f.filter( testmissingdata );
        int expectedReturn = 21;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testFilterFractionInvalid() {
        try {
            f.setMinPresentFraction( 934109821 );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

    }

    @Test
    public void testFilterFractionInvalid2() {
        try {
            f.setMinPresentCount( -1093 );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

    }

    @Test
    public void testFilterNoFiltering() {
        DoubleMatrix<String, String> filtered = f.filter( testdata );
        int expectedReturn = testdata.rows();
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testFilterPresentCountInvalid() {
        try {
            f.setMinPresentCount( 129 );
            f.filter( testmissingdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalStateException e ) {
            // ok
        }

    }

    @Test
    public void testFilterStringMatrix() {
        fss.setMinPresentCount( 12 );
        StringMatrix<String, String> filtered = fss.filter( teststringmissingdata );
        int expectedReturn = 21;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testFilterWithMissing() {
        f.setMinPresentCount( 12 );
        DoubleMatrix<String, String> filtered = f.filter( testmissingdata );
        int expectedReturn = 21;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testFilterWithMissingLessStringent() {

        f.setMinPresentCount( 10 );
        DoubleMatrix<String, String> filtered = f.filter( testmissingdata );
        int expectedReturn = 29;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testFilterWithMissingLowMaxFraction() {
        f.setMaxFractionRemoved( 0.1 );
        f.setMinPresentCount( 12 );
        DoubleMatrix<String, String> filtered = f.filter( testmissingdata );
        int expectedReturn = 21;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testMaxFractionRemovedInvalid() {
        try {
            f.setMaxFractionRemoved( 934109821 );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

    }

}