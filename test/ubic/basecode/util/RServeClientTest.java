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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
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
    DoubleMatrix<String, String> tester;
    double[] test1 = new double[] { -1.2241396, -0.6794486, -0.8475404, -0.4119554, -2.1980083 };
    double[] test2 = new double[] { 0.67676154, 0.20346679, 0.09289084, 0.68850551, 0.61120011 };

    @Override
    public void setUp() throws Exception {
        try {
            rc = new RServeClient( false );
            connected = rc.isConnected();
        } catch ( IOException e ) {
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
        DoubleMatrix<String, String> result = rc.retrieveMatrix( mat );
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
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
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
        DoubleMatrix<String, String> result = rc.retrieveMatrix( rc.assignMatrix( tester.asArray() ) );
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

    public void testTTest() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        List<String> rFactors = new ArrayList<String>();
        rFactors.add( "f" );
        rFactors.add( "f" );
        rFactors.add( "g" );
        rFactors.add( "g" );

        String facts = rc.assignStringList( rFactors );

        String tfacts = "t(" + facts + ")";

        String factor = "factor(" + tfacts + ")";

        DoubleMatrix<String, String> m = new DenseDoubleMatrix<String, String>( 1, 4 );
        m.set( 0, 0, 4.0 );
        m.set( 0, 1, 5.0 );
        m.set( 0, 2, 2.0 );
        m.set( 0, 3, 1.0 );
        String matrixName = rc.assignMatrix( m );

        /* handle the p-values */
        StringBuffer pvalueCommand = new StringBuffer();
        pvalueCommand.append( "apply(" );
        pvalueCommand.append( matrixName );
        pvalueCommand.append( ", 1, function(x) {t.test(x ~ " + factor + ")$p.value}" );
        pvalueCommand.append( ")" );

        double[] pvalues = rc.doubleArrayEval( pvalueCommand.toString() );
        assertEquals( 1, pvalues.length );
        assertEquals( 0.05, pvalues[0], 0.01 );
    }

    public void testTTestFail() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        List<String> rFactors = new ArrayList<String>();
        rFactors.add( "f" );
        rFactors.add( "f" );
        rFactors.add( "g" );
        rFactors.add( "g" );

        String facts = rc.assignStringList( rFactors );
        String tfacts = "t(" + facts + ")";
        String factor = "factor(" + tfacts + ")";

        DoubleMatrix<String, String> m = new DenseDoubleMatrix<String, String>( 1, 4 );
        // will fail with "data are essentially constant"
        m.set( 0, 0, 4.0 );
        m.set( 0, 1, 4.0 );
        m.set( 0, 2, 2.0 );
        m.set( 0, 3, 2.0 );
        String matrixName = rc.assignMatrix( m );

        /* handle the p-values - will fail internally, but we silently return 1.0 */
        StringBuffer pvalueCommand = new StringBuffer();
        pvalueCommand.append( "apply(" + matrixName + ", 1, function(x) {  tryCatch( t.test(x ~ " + factor
                + ")$p.value, error=function(e) { 1.0})})" );

        double[] r = rc.doubleArrayEval( pvalueCommand.toString() );

        assertEquals( 1, r.length );
        assertEquals( 1.0, r[0], 0.00001 );

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

    public void testExecError() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        try {
            rc.stringEval( "library(fooblydoobly)" );
            fail( "Should have gotten an exception" );
        } catch ( Exception e ) {
            assertTrue( e.getMessage().startsWith( "Error from R" ) );
        }
    }

    public void testFactorAssign() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }

        List<String> list = new ArrayList<String>();
        list.add( "a" );
        list.add( "b" );
        String factor = rc.assignFactor( list );
        assertNotNull( factor );
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