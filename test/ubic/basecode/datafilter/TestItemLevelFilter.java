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

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;

/**
 * @author pavlidis
 * 
 */
public class TestItemLevelFilter extends AbstractTestFilter {

    ItemLevelFilter<String, String> f = null;

    /*
     * @see TestCase#setUp()
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        f = new ItemLevelFilter<String, String>();
    }

    @Test
    public final void testFilter() {
        f.setLowCut( 0.0 );
        DoubleMatrix<String, String> result = f.filter( testdata );
        int expectedReturn = 283;
        int actualReturn = result.rows() * result.columns() - result.numMissing();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}