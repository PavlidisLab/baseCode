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

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.dataStructure.matrix.StringMatrix2DNamed;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;

/**
 * Fixture for testing filtering of matrices.
 * 
 * @author Pavlidis
 * @version $Id$
 */
public abstract class AbstractTestFilter extends TestCase {

    protected DoubleMatrixNamed testdata = null;
    protected StringMatrix2DNamed teststringdata = null;
    protected DoubleMatrixNamed testmissingdata = null;
    protected StringMatrix2DNamed teststringmissingdata = null;

    public AbstractTestFilter() {
        super();
    }

    protected void setUp() throws Exception {
        super.setUp();
        DoubleMatrixReader f = new DoubleMatrixReader();
        StringMatrixReader s = new StringMatrixReader();

        testdata = ( DoubleMatrixNamed ) f.read( AbstractTestFilter.class.getResourceAsStream( "/data/testdata.txt" ) );

        testmissingdata = ( DoubleMatrixNamed ) f.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdatamissing.txt" ) );

        teststringdata = ( StringMatrix2DNamed ) s.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdata.txt" ) );

        teststringmissingdata = ( StringMatrix2DNamed ) s.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdatamissing.txt" ) );

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        testdata = null;
        testmissingdata = null;
        teststringdata = null;
        teststringmissingdata = null;
    }

}