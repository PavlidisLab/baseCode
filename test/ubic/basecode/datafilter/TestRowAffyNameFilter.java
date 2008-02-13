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

    public void testFilter() {
        AffymetrixProbeNameFilter<DoubleMatrixNamed<String, String>, String, String, Double> fi = new AffymetrixProbeNameFilter<DoubleMatrixNamed<String, String>, String, String, Double>(
                new int[] { AffymetrixProbeNameFilter.AFFX, AffymetrixProbeNameFilter.X, AffymetrixProbeNameFilter.ST,
                        AffymetrixProbeNameFilter.F } );
        DoubleMatrixNamed filtered = fi.filter( testdata );
        int expectedReturn = teststringdata.rows() - 5; // file contains on AFFX,
        // and two _f_ tags.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testStringFilter() {
        AffymetrixProbeNameFilter<StringMatrix2DNamed<String, String>, String, String, String> fi = new AffymetrixProbeNameFilter<StringMatrix2DNamed<String, String>, String, String, String>(
                new int[] { AffymetrixProbeNameFilter.AFFX, AffymetrixProbeNameFilter.X, AffymetrixProbeNameFilter.ST,
                        AffymetrixProbeNameFilter.F } );
        StringMatrix2DNamed filtered = fi.filter( teststringdata );
        int expectedReturn = teststringdata.rows() - 5; // file contains on AFFX,
        // and two _f_ tags.
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}