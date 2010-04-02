/*
 * The baseCode project
 * 
 * Copyright (c) 2006-2010 University of British Columbia
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
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.math.Constants;
import ubic.basecode.util.r.type.HTest;
import ubic.basecode.util.r.type.TwoWayAnovaResult;

/**
 * @author pavlidis
 * @version $Id$
 */
public class JRIClientTest extends TestCase {
    private static Log log = LogFactory.getLog( JRIClientTest.class.getName() );

    JRIClient rc = null;
    boolean connected = true;
    DoubleMatrix<String, String> tester;
    double[] test1 = new double[] { -1.2241396, -0.6794486, -0.8475404, -0.4119554, -2.1980083 };
    double[] test2 = new double[] { 0.67676154, 0.20346679, 0.09289084, 0.68850551, 0.61120011 };

    @Override
    public void setUp() throws Exception {

        if ( rc == null ) {

            try {
                rc = new JRIClient();
                if ( rc == null || !rc.isConnected() ) {
                    connected = false;
                    return;
                }
            } catch ( RuntimeException e ) {
                log.error( e, e );
                connected = false;
                return;
            }
        }

        DoubleMatrixReader reader = new DoubleMatrixReader();

        tester = reader.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
        assert rc.isConnected();
    }

    @Override
    public void tearDown() throws Exception {
        tester = null;
        // rc.disconnect();
        rc = null;
    }

    public void testAssignAndRetrieveMatrix() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        DoubleMatrix<String, String> result = rc.retrieveMatrix( rc.assignMatrix( tester ) );
        assertEquals( "gene1_at", result.getRowName( 0 ) );
        assertEquals( "sample1", result.getColName( 0 ) );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );
    }

    public void testAssignAndRetrieveMatrixB() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        String m = rc.assignMatrix( tester.asArray() );
        DoubleMatrix<String, String> result = rc.retrieveMatrix( m );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );
    }

    public void testStringListEval() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        List<String> actual = rc.stringListEval( "c('a','b')" );
        assertEquals( 2, actual.size() );
        assertEquals( "a", actual.get( 0 ) );
        assertEquals( "b", actual.get( 1 ) );
    }

    public void testFactorAssign() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        List<String> list = new ArrayList<String>();
        list.add( "a" );
        list.add( "b" );
        String factor = rc.assignFactor( list );
        assertNotNull( factor );
    }

    /*
     * Test method for 'RCommand.exec(String)'
     */
    public void testExec() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        String actualValue = rc.stringEval( "R.version.string" );
        String expectedValue = "R version 2";

        assertTrue( "rc.eval() returned version '" + actualValue + "', expected something starting with R version 2",
                actualValue.startsWith( expectedValue ) );
    }

    public void testExecError() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test." );
            return;
        }
        try {
            rc.stringEval( "library(fooblydoobly)" );
            fail( "Should have gotten an exception" );
        } catch ( Exception e ) {
            assertTrue( e.getMessage().startsWith( "Error from R" ) );
        }
        try {
            rc.stringEval( "t.test(dadx,ymom)" );
            fail( "Should have gotten an exception" );
        } catch ( Exception e ) {
            assertTrue( e.getMessage().startsWith( "Error from R" ) );
        }
        try {
            rc.stringEval( "wwfollck(1)" );
            fail( "Should have gotten an exception" );
        } catch ( Exception e ) {
            assertTrue( e.getMessage().startsWith( "Error from R" ) );
        }
        try {
            rc.stringEval( "sqrt(\"A\")" );
            fail( "Should have gotten an exception" );
        } catch ( Exception e ) {
            assertTrue( e.getMessage().startsWith( "Error from R" ) );
        }
    }

    /*
     * Test method for 'exec(String)'
     */
    public void testExecDoubleArray() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        double[] dd = rc.doubleArrayEval( "rep(1, 10)" );
        assertNotNull( dd );
        assertTrue( RegressionTesting.closeEnough( new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, dd, 0.001 ) );
    }

    public void testDoubleTwoDoubleArrayEval() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        double actual = rc.doubleTwoDoubleArrayEval( "cor(a,b)", "a", test1, "b", test2 );
        double expected = -0.29843518070456654;
        assertEquals( expected, actual, Constants.SMALLISH );
    }

    public void testListEvalA() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        DoubleMatrixReader r = new DoubleMatrixReader();
        DoubleMatrix<String, String> read = r.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
        String matrixName = rc.assignMatrix( read );
        List<?> results;

        results = rc.listEval( double[].class, "apply(" + matrixName + ", 1, function(x) {summary(x)})" );
        assertNotNull( results );

        for ( Object o : results ) {
            assertTrue( o instanceof double[] );
        }

    }

    public void testListEvalB() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        DoubleMatrixReader r = new DoubleMatrixReader();
        DoubleMatrix<String, String> read = r.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
        String matrixName = rc.assignMatrix( read );
        List<?> results;

        results = rc.listEval( HTest.class, "apply(" + matrixName + ", 1, function(x) {cor.test(x, " + matrixName
                + "[1,])})" );
        assertNotNull( results );

        for ( Object o : results ) {
            assertNotNull( ( ( HTest ) o ).getPvalue() );
        }

    }

    public void testAnovaA() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /*
         * foo<-c(212.1979, 8.8645, 8.4814, 11.915); a<-factor(c( "A", "B", "B", "B" )); b<-factor(c( "D", "C", "D", "D"
         * )); anova(aov(foo ~ a+b));
         */

        double[] data = new double[] { 212.1979, 8.8645, 8.4814, 11.915 };
        String[] f1 = new String[] { "A", "B", "B", "B" };
        String[] f2 = new String[] { "D", "C", "D", "D" };

        TwoWayAnovaResult r = rc.twoWayAnova( data, Arrays.asList( f1 ), Arrays.asList( f2 ), false );
        assertEquals( 0.008816, r.getMainEffectAPval(), 0.0001 );
        assertEquals( 0.731589, r.getMainEffectBPval(), 0.0001 );
        assertEquals( 5214.3817, r.getMainEffectAfVal(), 0.0001 );
        assertEquals( 0.2012, r.getMainEffectBfVal(), 0.0001 );
    }

    public void testAnovaB() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /*
         * foo<-c( 3.2969, 3.1856, 3.1638, NA, 3.2342, 3.3533, 3.4347, 3.3074); a<-factor(c( "A", "A", "A", "A", "B",
         * "B", "B", "B" )); b<-factor(c( "C", "C", "D", "D", "C", "C", "D", "D" )); anova(aov(foo ~ a+b));
         */

        double[] data = new double[] { 3.2969, 3.1856, 3.1638, Double.NaN, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };
        String[] f2 = new String[] { "C", "C", "D", "D", "C", "C", "D", "D" };

        TwoWayAnovaResult r = rc.twoWayAnova( data, Arrays.asList( f1 ), Arrays.asList( f2 ), false );
        assertEquals( 0.1567, r.getMainEffectAPval(), 0.0001 );
        assertEquals( 0.8323, r.getMainEffectBPval(), 0.0001 );
        assertEquals( 3.0294, r.getMainEffectAfVal(), 0.0001 );
        assertEquals( 0.0511, r.getMainEffectBfVal(), 0.0001 );
    }

    public void testAnovaC() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /*
         * foo<-c( 213.7725, 211.6383, NA, 212.589, 10.3011, 10.1182, 13.486, 12.916); a<-factor(c( "A", "A", "A", "A",
         * "B", "B", "B", "B" )); b<-factor(c( "C", "C", "D", "D", "C", "C", "D", "D" )); anova(aov(foo ~ a+b));
         */

        double[] data = new double[] { 213.7725, 211.6383, Double.NaN, 212.589, 10.3011, 10.1182, 13.486, 12.916 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };
        String[] f2 = new String[] { "C", "C", "D", "D", "C", "C", "D", "D" };

        TwoWayAnovaResult r = rc.twoWayAnova( data, Arrays.asList( f1 ), Arrays.asList( f2 ), true );
        assertEquals( 8.97e-08, r.getMainEffectAPval(), 0.0000001 );
        assertEquals( 0.08816, r.getMainEffectBPval(), 0.0001 );
        assertEquals( 0.11823, r.getInteractionPval(), 0.0001 );
        assertEquals( 84546.9846, r.getMainEffectAfVal(), 0.0001 );
        assertEquals( 6.2208, r.getMainEffectBfVal(), 0.0001 );
        assertEquals( 4.7178, r.getInteractionfVal(), 0.0001 );
    }

    public void testAnovaD() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /*
         * foo<-c( 206.2209 , NA , 205.6038 , 203.0751 , NA , NA , 4.6569 , NA ); a<-factor(c( "A", "A", "A", "A", "B",
         * "B", "B", "B" )); b<-factor(c( "C", "C", "D", "D", "C", "C", "D", "D" )); anova(aov(foo ~ a+b));
         */

        double[] data = new double[] { 206.2209, Double.NaN, 205.6038, 203.0751, Double.NaN, Double.NaN, 4.6569,
                Double.NaN };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };
        String[] f2 = new String[] { "C", "C", "D", "D", "C", "C", "D", "D" };

        TwoWayAnovaResult r = rc.twoWayAnova( data, Arrays.asList( f1 ), Arrays.asList( f2 ), true );
        assertEquals( 0.006562, r.getMainEffectAPval(), 0.00001 );
        assertEquals( 0.548142, r.getMainEffectBPval(), 0.0001 );
        assertEquals( Double.NaN, r.getInteractionPval() );
        assertEquals( 9412.4049, r.getMainEffectAfVal(), 0.0001 );
        assertEquals( 0.7381, r.getMainEffectBfVal(), 0.0001 );
        assertEquals( Double.NaN, r.getInteractionfVal() );
    }
}