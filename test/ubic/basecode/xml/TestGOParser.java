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

import java.io.InputStream;

import junit.framework.TestCase;
import ubic.basecode.util.RegressionTesting;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestGOParser extends TestCase {

    private GOParser gOParser = null;

    /**
     * This sample is of a rather old version of the ontology
     * 
     * @throws Exception
     */
    public void testGOParser() throws Exception {

        InputStream i = GOParser.class.getResourceAsStream( "/data/go-termdb-sample.xml" );

        if ( i == null ) {
            throw new Exception( "Couldn't read the sample file" );
        }
        gOParser = new GOParser( i );

        String actualReturn = gOParser.getGraph().toString();

        String expectedReturn = RegressionTesting.readTestResult( "/data/goparsertestoutput.txt" );
        assertEquals( "return", expectedReturn, actualReturn );

        assertTrue( gOParser.getGraph().getRoot().toString().startsWith( "all" ) );
        /*
         * assertEquals( "Diffs: " + RegressionTesting.regress( expectedReturn, actualReturn ), expectedReturn,
         * actualReturn );
         */
    }

    /**
     * @throws Exception
     */
    public void testGOParserB() throws Exception {

        InputStream i = GOParser.class.getResourceAsStream( "/data/go_daily-termdb.rdf-sample2.xml" );

        if ( i == null ) {
            throw new Exception( "Couldn't read the sample file" );
        }

        gOParser = new GOParser( i );

        String actualReturn = gOParser.getGraph().toString();
        String expectedReturn = RegressionTesting.readTestResult( "/data/goparsertestoutput.2.txt" );
        assertEquals( "return", expectedReturn, actualReturn );

        assertTrue( gOParser.getGraph().getRoot().toString().startsWith( "all" ) );

    }
}