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
package ubic.basecode.xml;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import ubic.basecode.util.RegressionTesting;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestGOParser extends TestCase {
    private GOParser gOParser = null;

    public void testGOParser() throws IOException {
        String actualReturn = gOParser.getGraph().toString();
        String expectedReturn = RegressionTesting.readTestResult( "/data/goparsertestoutput.txt" );
        assertEquals( "return", expectedReturn, actualReturn );
        /*
         * assertEquals( "Diffs: " + RegressionTesting.regress( expectedReturn, actualReturn ), expectedReturn,
         * actualReturn );
         */
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        InputStream i = GOParser.class.getResourceAsStream( "/data/go-termdb-sample.xml" );
        // GOParser.class.getResourceAsStream( "/data/go_200406-termdb.xml" );
        if ( i == null ) {
            throw new Exception( "Couldn't read the sample file" );
        }
        gOParser = new GOParser( i );

    }

    @Override
    protected void tearDown() throws Exception {
        gOParser = null;
        super.tearDown();
    }

}