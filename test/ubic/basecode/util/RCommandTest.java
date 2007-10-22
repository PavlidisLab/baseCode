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
package ubic.basecode.util;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.JRclient.REXP;

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed;
import ubic.basecode.io.reader.DoubleMatrixReader;

/**
 * @author pavlidis
 * @version $Id$
 */
public class RCommandTest extends TestCase {
    private static Log log = LogFactory.getLog( RCommandTest.class.getName() );

    RCommand rc = null;
    boolean connected = false;
    DoubleMatrixNamed tester;

    public void setUp() throws Exception {
        try {
            rc = RCommand.newInstance( 20000 );
            connected = rc == null;
        } catch ( RuntimeException e ) {
            connected = false;
        }

        DoubleMatrixReader reader = new DoubleMatrixReader();

        tester = ( DoubleMatrixNamed ) reader.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
    }

    public void tearDown() throws Exception {
        if ( rc != null ) {
            rc.disconnect();
            rc.stopServer();
        }
        tester = null;
    }

    /*
     * Test method for 'edu.columbia.gemma.tools.RCommand.exec(String)'
     */
    public void testExec() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        String actualValue = rc.eval( "R.version.string" ).asString();
        String expectedValue = "Version 2";

        assertTrue( "rc.eval() return version " + actualValue + ", expected something starting with R version 2",
                actualValue.startsWith( expectedValue ) );
    }

    /*
     * Test method for 'edu.columbia.gemma.tools.RCommand.exec(String)'
     */
    public void testExecDoubleArray() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        assertTrue( RegressionTesting.closeEnough( new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, rc
                .doubleArrayEval( "rep(1, 10)" ), 0.001 ) );
    }

    public void testAssignAndRetrieveMatrix() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        DoubleMatrixNamed result = rc.retrieveMatrix( rc.assignMatrix( tester ) );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );
    }
}