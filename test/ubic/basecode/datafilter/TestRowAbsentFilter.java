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

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed2D;
import ubic.basecode.dataStructure.matrix.StringMatrix2DNamed;
import ubic.basecode.io.reader.StringMatrixReader;

/**
 * See the file test/data/matrix-testing.xls for the validation.
 * 
 * @author paul
 * @version $Id$
 */
public class TestRowAbsentFilter extends AbstractTestFilter {

    private static Log log = LogFactory.getLog( TestRowAbsentFilter.class.getName() );

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
        DoubleMatrixNamed2D filtered = ( DoubleMatrixNamed2D ) f.filter( testdata );
        int expectedReturn = testdata.rows() - 7; // 7 rows have some absent or
        // marginal.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissing() {
        f.setFlagMatrix( testpdata );
        f.setMinPresentCount( 12 );
        DoubleMatrixNamed2D filtered = ( DoubleMatrixNamed2D ) f.filter( testmissingdata );
        int expectedReturn = 17;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterWithMissingLessStringent() {
        f.setFlagMatrix( testpdata );
        f.setMinPresentCount( 8 );
        DoubleMatrixNamed2D filtered = ( DoubleMatrixNamed2D ) f.filter( testmissingdata );
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
        DoubleMatrixNamed2D filtered = ( DoubleMatrixNamed2D ) f.filter( testdata );
        int expectedReturn = testdata.rows() - 6; // 6 rows have some absent
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterFraction() {
        f.setFlagMatrix( testpdata );
        f.setMinPresentFraction( 1.0 );
        DoubleMatrixNamed2D filtered = ( DoubleMatrixNamed2D ) f.filter( testdata );
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
            log.debug( "As expected, got " + e );
        }

    }

    public void testFilterPresentCountInvalid() {
        try {
            f.setFlagMatrix( testpdata );
            f.setMinPresentCount( 129 );
            f.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalStateException e ) {
            log.debug( "As expected, got " + e );
        }

    }

    public void testFilterNullFlags() {
        try {
            f.setFlagMatrix( null );
            f.setMinPresentCount( 10 );
            f.filter( testdata );
            fail( "Should have gotten an exception" );
        } catch ( IllegalArgumentException success ) {
            log.debug( "As expected, got " + success );
        }
    }

}