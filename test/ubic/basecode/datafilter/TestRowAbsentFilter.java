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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.StringMatrixReader;

/**
 * See the file test/data/matrix-testing.xls for the validation.
 * 
 * @author paul
 * @version $Id$
 */
public class TestRowAbsentFilter extends AbstractTestFilter {

    private static Log log = LogFactory.getLog( TestRowAbsentFilter.class.getName() );

    StringMatrix<String, String> testpdata = null;

    RowAbsentFilter<DoubleMatrix<String, String>, String, String, Double> fd = new RowAbsentFilter<DoubleMatrix<String, String>, String, String, Double>();
    RowAbsentFilter<StringMatrix<String, String>, String, String, String> fs = new RowAbsentFilter<StringMatrix<String, String>, String, String, String>();

    /*
     * Class under test for DoubleMatrixNamed filter(DoubleMatrixNamed)
     */
    public void testFilter() {
        fd.setFlagMatrix( testpdata );
        fd.setMinPresentCount( 12 );
        DoubleMatrix filtered = fd.filter( testdata );
        int expectedReturn = testdata.rows() - 7; // 7 rows have some absent or
        // marginal.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterFraction() {
        fd.setFlagMatrix( testpdata );
        fd.setMinPresentFraction( 1.0 );
        DoubleMatrix filtered = fd.filter( testdata );
        int expectedReturn = testdata.rows() - 7; // 7 rows have some missing or
        // marginal.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterFractionInvalid() {
        try {
            fd.setFlagMatrix( testpdata );
            fd.setMinPresentFraction( 934109821 );
            fd.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException e ) {
            log.debug( "As expected, got " + e );
        }

    }

    public void testFilterKeepMarginal() {
        fd.setFlagMatrix( testpdata );
        fd.setKeepMarginal( true );
        fd.setMinPresentCount( 12 );
        DoubleMatrix filtered = fd.filter( testdata );
        int expectedReturn = testdata.rows() - 6; // 6 rows have some absent
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterNullFlags() {
        try {
            fd.setFlagMatrix( null );
            fd.setMinPresentCount( 10 );
            fd.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException success ) {
            log.debug( "As expected, got " + success );
        }
    }

    public void testFilterPresentCountInvalid() {
        try {
            fd.setFlagMatrix( testpdata );
            fd.setMinPresentCount( 129 );
            fd.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalStateException e ) {
            log.debug( "As expected, got " + e );
        }

    }

    public void testFilterStringMatrix() {
        fs.setFlagMatrix( testpdata );
        fs.setMinPresentCount( 12 );
        StringMatrix filtered = fs.filter( teststringdata );
        int expectedReturn = testdata.rows() - 7; // 7 rows have some missing or
        // marginal OR absent.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissing() {
        fd.setFlagMatrix( testpdata );
        fd.setMinPresentCount( 12 );
        DoubleMatrix filtered = fd.filter( testmissingdata );
        int expectedReturn = 17;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissingLessStringent() {
        fd.setFlagMatrix( testpdata );
        fd.setMinPresentCount( 8 );
        DoubleMatrix filtered = fd.filter( testmissingdata );
        int expectedReturn = 24;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        StringMatrixReader s = new StringMatrixReader();
        testpdata = s.read( AbstractTestFilter.class.getResourceAsStream( "/data/test-presence-data.txt" ) );
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        testpdata = null;
        testdata = null;
        super.tearDown();
    }

}