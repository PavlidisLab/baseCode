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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.math.Constants;

/**
 * @author pavlidis
 * @version $Id$
 */
public class RServeClientTest extends TestCase {
    private static Log log = LogFactory.getLog( RServeClientTest.class.getName() );

    RServeClient rc = null;
    boolean connected = false;
    DoubleMatrixNamed<String, String> tester;
    double[] test1 = new double[] { -1.2241396, -0.6794486, -0.8475404, -0.4119554, -2.1980083 };
    double[] test2 = new double[] { 0.67676154, 0.20346679, 0.09289084, 0.68850551, 0.61120011 };

    @Override
    public void setUp() throws Exception {
        try {
            rc = new RServeClient( false );
            connected = rc.isConnected();
        } catch ( RuntimeException e ) {
            connected = false;
        }

        DoubleMatrixReader reader = new DoubleMatrixReader();

        tester = reader.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
    }

    @Override
    public void tearDown() throws Exception {
        tester = null;
    }

    public void testAssignAndRetrieveMatrix() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        String mat = rc.assignMatrix( tester );
        DoubleMatrixNamed result = rc.retrieveMatrix( mat );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );

        for ( int i = 0; i < tester.rows(); i++ ) {
            assertEquals( tester.getRowName( i ), result.getRowName( i ) );
        }

        for ( int i = 0; i < tester.columns(); i++ ) {
            assertEquals( tester.getColName( i ), result.getColName( i ) );
        }

    }

    public void testDoubleArrayTwoDoubleArrayEval() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        double[] actual = rc.doubleArrayTwoDoubleArrayEval( "a-b", "a", test1, "b", test2 );
        double[] expected = new double[] { -1.9009011, -0.8829154, -0.9404312, -1.1004609, -2.8092084 };
        RegressionTesting.closeEnough( expected, actual, Constants.SMALLISH );
    }

    public void testLoadLibrary() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        assertFalse( rc.loadLibrary( "foooobly" ) );
        assertTrue( rc.loadLibrary( "graphics" ) );
    }

    public void testDoubleTwoDoubleArrayEval() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        double actual = rc.doubleTwoDoubleArrayEval( "cor(a,b)", "a", test1, "b", test2 );
        double expected = -0.29843518070456654;
        assertEquals( expected, actual, Constants.SMALLISH );
    }

    public void testFindExecutable() throws Exception {
        String cmd = RServeClient.findRserveCommand();
        assertNotNull( cmd ); // should always come up with something.
    }

    public void testStringListEval() throws Exception {
        List<String> actual = rc.stringListEval( "c('a','b')" );
        assertEquals( 2, actual.size() );
        assertEquals( "a", actual.get( 0 ) );
        assertEquals( "b", actual.get( 1 ) );
    }

    public void testAssignAndRetrieveMatrixB() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        DoubleMatrixNamed result = rc.retrieveMatrix( rc.assignMatrix( tester.asArray() ) );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );

    }

    public void testAssignStringList() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }

        List<String> l = new ArrayList<String>();
        l.add( "foo" );
        l.add( "bar" );

        String varname = rc.assignStringList( l );
        String actualValue = rc.stringEval( varname + "[1]" );
        assertEquals( "foo", actualValue );
        actualValue = rc.stringEval( varname + "[2]" );
        assertEquals( "bar", actualValue );
    }

    /*
     * Test method for ' RCommand.exec(String)'
     */
    public void testExec() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        String actualValue = rc.stringEval( "R.version.string" );
        String expectedValue = "R version 2";

        assertTrue( "rc.eval() return version " + actualValue + ", expected something starting with R version 2",
                actualValue.startsWith( expectedValue ) );
    }

    /*
     * Test method for ' RCommand.exec(String)'
     */
    public void testExecDoubleArray() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        assertTrue( RegressionTesting.closeEnough( new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, rc
                .doubleArrayEval( "rep(1, 10)" ), 0.001 ) );
    }
}