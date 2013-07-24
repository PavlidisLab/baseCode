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
package ubic.basecode.util.r;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.REngine.REXP;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrixImpl;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.math.Constants;
import ubic.basecode.util.RegressionTesting;
import ubic.basecode.util.r.type.HTest;
import ubic.basecode.util.r.type.LinearModelSummary;
import ubic.basecode.util.r.type.OneWayAnovaResult;
import ubic.basecode.util.r.type.TwoWayAnovaResult;

/**
 * @author pavlidis
 * @version $Id$
 */
public class JRIClientTest {
    private static Log log = LogFactory.getLog( JRIClientTest.class.getName() );

    boolean connected = true;
    JRIClient rc = null;
    double[] test1 = new double[] { -1.2241396, -0.6794486, -0.8475404, -0.4119554, -2.1980083 };
    double[] test2 = new double[] { 0.67676154, 0.20346679, 0.09289084, 0.68850551, 0.61120011 };
    DoubleMatrix<String, String> tester;

    @Before
    public void setUp() throws Exception {

        connected = JRIClient.ready();

        if ( !connected ) {
            return;
        }
        try {
            log.debug( "java.library.path=" + System.getProperty( "java.library.path" ) );
            rc = new JRIClient();
            if ( rc == null || !rc.isConnected() ) {
                connected = false;
                return;
            }
        } catch ( UnsatisfiedLinkError e ) {
            log.error( e, e );
            connected = false;
            return;
        }

        DoubleMatrixReader reader = new DoubleMatrixReader();

        tester = reader.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
        assert rc.isConnected();
    }

    @After
    public void tearDown() {
        tester = null;
        rc = null;
    }

    @Test
    public void testAnovaA() {
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

    @Test
    public void testAnovaB() {
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

    @Test
    public void testAnovaC() {
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

    @Test
    public void testAnovaD() {
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
        assertEquals( Double.NaN, r.getInteractionPval(), 0.0001 );
        assertEquals( 9412.4049, r.getMainEffectAfVal(), 0.0001 );
        assertEquals( 0.7381, r.getMainEffectBfVal(), 0.0001 );
        assertEquals( Double.NaN, r.getInteractionfVal(), 0.0001 );
    }

    @Test
    public void testAnovaE() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /*
         * foo<-c(212.1979, 8.8645, 8.4814, 11.915); a<-factor(c( "A", "B", "B", "B" )); anova(aov(foo ~ a));
         */

        double[] data = new double[] { 212.1979, 8.8645, 8.4814, 11.915 };
        String[] f1 = new String[] { "A", "B", "B", "B" };

        OneWayAnovaResult r = rc.oneWayAnova( data, Arrays.asList( f1 ) );
        assertEquals( 0.0001152, r.getPval(), 0.0001 );
        assertEquals( 8682.2, r.getFVal(), 0.01 );
    }

    /**
     * One way @
     */
    @Test
    public void testAnovaF() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /*
         * foo<-c( 3.2969, 3.1856, 3.1638, NA, 3.2342, 3.3533, 3.4347, 3.3074); a<-factor(c( "A", "A", "A", "A", "B",
         * "B", "B", "B" )); anova(aov(foo ~ a));
         */

        double[] data = new double[] { 3.2969, 3.1856, 3.1638, Double.NaN, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };

        OneWayAnovaResult r = rc.oneWayAnova( data, Arrays.asList( f1 ) );
        assertEquals( 0.1110, r.getPval(), 0.0001 );
        assertEquals( 3.739, r.getFVal(), 0.001 );
    }

    @Test
    public void testAssignAndRetrieveMatrix() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        DoubleMatrix<String, String> result = rc.retrieveMatrix( rc.assignMatrix( tester ) );
        assertEquals( "gene1_at", result.getRowName( 0 ) );
        assertEquals( "sample1", result.getColName( 0 ) );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );
    }

    @Test
    public void testAssignAndRetrieveMatrixB() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        String m = rc.assignMatrix( tester.asArray() );
        DoubleMatrix<String, String> result = rc.retrieveMatrix( m );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );
    }

    @Test
    public void testDataFrameA() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        double[] data = new double[] { 3.2969, 3.1856, 3.1638, Double.NaN, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };

        double[] f2 = new double[] { 1, 2, 3, 4, 5, 6, 7, 8 };

        ObjectMatrix<String, String, Object> d = new ObjectMatrixImpl<String, String, Object>( 8, 3 );

        d.setColumnNames( Arrays.asList( new String[] { "foo", "bar", "ack" } ) );
        d.setRowNames( Arrays.asList( new String[] { "a", "b", "c", "d", "e", "f", "g", "i" } ) );

        for ( int i = 0; i < 8; i++ ) {
            for ( int j = 0; j < 3; j++ ) {
                if ( j == 0 ) {
                    d.set( i, j, f1[i] );
                } else if ( j == 1 ) {
                    d.set( i, j, f2[i] );
                } else {
                    d.set( i, j, data[i] );
                }
            }
        }

        String dataFrame = rc.dataFrame( d );

        assertNotNull( dataFrame );

    }

    @Test
    public void testDoubleTwoDoubleArrayEval() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        double actual = rc.doubleTwoDoubleArrayEval( "cor(a,b)", "a", test1, "b", test2 );
        double expected = -0.29843518070456654;
        assertEquals( expected, actual, Constants.SMALLISH );
    }

    /*
     * Test method for 'RCommand.exec(String)'
     */
    @Test
    public void testExec() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        String actualValue = rc.stringEval( "R.version.string" );
        String expectedValue = "R version 2";

        assertTrue( "rc.eval() returned version '" + actualValue + "', expected something starting with R version 2",
                actualValue.startsWith( expectedValue ) );
    }

    /*
     * Test method for 'exec(String)'
     */
    @Test
    public void testExecDoubleArray() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        double[] dd = rc.doubleArrayEval( "rep(1, 10)" );
        assertNotNull( dd );
        assertTrue( RegressionTesting.closeEnough( new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, dd, 0.001 ) );
    }

    @Test
    public void testExecError() {
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

    @Test
    public void testFactorAssign() {
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

    /**
     * Also exercises dataFrameEval
     * 
     * <pre>
     * library(limma)
     * 
     * dat&lt;-read.table(&quot;data/testdata.txt&quot;, header=T, row.names=1)
     * 
     * f1&lt;-factor(c(&quot;A&quot;, &quot;A&quot;, &quot;A&quot;, &quot;A&quot;, &quot;A&quot;, &quot;A&quot;, &quot;B&quot;, &quot;B&quot;, &quot;B&quot;, &quot;B&quot;, &quot;B&quot;, &quot;B&quot;));
     * 
     * f2&lt;-factor(c(&quot;X&quot;, &quot;X&quot;, &quot;Y&quot;, &quot;Y&quot;, &quot;Z&quot;, &quot;Z&quot;, &quot;X&quot;, &quot;X&quot;, &quot;Y&quot;, &quot;Y&quot;, &quot;Z&quot;, &quot;Z&quot;));
     * 
     * cov1&lt;-c( -0.230 , 1.400, -0.210, 0.570, -0.064, 0.980 ,-0.082, -0.094, 0.630, -2.000, 0.640, -0.870);
     * 
     * mo&lt;-model.matrix(&tilde; f1 + f2 + cov1 - 1);
     * 
     * contr&lt;-makeContrasts(A-B, levels=mo);
     * 
     * fit&lt;-lmFit(dat, mo);
     * 
     * fit&lt;-contrasts.fit(fit, contr);
     * 
     * fit&lt;-eBayes(fit)
     * 
     * res&lt;-topTable(fit)
     * </pre>
     * 
     * @
     */
    @Test
    public void testLimmaA() throws Exception {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        boolean haveLimma = rc.loadLibrary( "limma" );
        if ( !haveLimma ) {
            log.warn( "Cannot load limma, skipping test" );
            return;
        }

        DoubleMatrixReader r = new DoubleMatrixReader();
        DoubleMatrix<String, String> read = r.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
        String matrixName = rc.assignMatrix( read );

        String[] f1 = new String[] { "A", "A", "A", "A", "A", "A", "B", "B", "B", "B", "B", "B" };

        String facN = rc.assignFactor( Arrays.asList( f1 ) );

        rc.voidEval( "mo<-model.matrix(~ " + facN + " - 1)" );
        rc.voidEval( "colnames(mo)<-levels(" + facN + ")" );
        rc.voidEval( "fit<-lmFit(" + matrixName + ", mo)" );
        rc.voidEval( "contr<-makeContrasts(A-B, levels=mo)" );
        rc.voidEval( "fit<-contrasts.fit(fit,contr)" );
        rc.voidEval( "fit<-eBayes(fit)" );
        ObjectMatrix<String, String, Object> dataFrameEval = rc.dataFrameEval( "topTable(fit, number=Inf)" );

        // log.info( dataFrameEval );

        /*
         * The 'ID' column has our original probe ids.
         */
        assertEquals( "gene8_at", dataFrameEval.get( 1, 0 ) );
        assertEquals( 0.684, ( Double ) dataFrameEval.get( 6, 4 ), 0.001 );

    }

    /**
     * Like a two-sample t-test where the intercept is also of interest. @
     */
    @Test
    public void testLinearModelA() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /**
         * foo<-c( 3.2969, 3.1856, 3.1638, NA, 3.2342, 3.3533, 3.4347, 3.3074);
         * <p>
         * a<-factor(c( "A", "A", "A", "A", "B", "B", "B", "B" )); summary(lm(foo ~ a)); anova(lm(foo ~ a));
         */

        double[] data = new double[] { 3.2969, 3.1856, 3.1638, Double.NaN, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };

        rc.assign( "foo", data );
        String facN = rc.assignFactor( "f1", Arrays.asList( f1 ) );
        REXP result = rc.eval( "summary(lm(foo ~ " + facN + ") )" );
        REXP anova = rc.eval( "anova(lm(foo ~ " + facN + ") )" );
        LinearModelSummary lms = new LinearModelSummary( result, anova, new String[] { "f1" } );

        Double p = lms.getMainEffectP( facN );
        Double t = lms.getContrastTStats( facN ).get( "f1B" );
        Double ip = lms.getInterceptP();
        Double it = lms.getInterceptT();
        Integer dof = lms.getNumeratorDof();
        Integer rdof = lms.getResidualDof();

        assertEquals( 0.11096, p, 0.0001 );
        assertEquals( 1.933653, t, 0.0001 );
        assertEquals( 1.10e-08, ip, 1e-10 );
        assertEquals( 70.319382, it, 0.0001 );
        assertEquals( 1, ( int ) dof );
        assertEquals( 5, ( int ) rdof );

    }

    /**
     * With a continuous covariate as well a categorical one. @
     */
    @Test
    public void testLinearModelB() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /**
         * dat<-c( 3.2969, 3.1856, 3.1638, NA, 3.2342, 3.3533, 3.4347, 3.3074);
         * <p>
         * a<-factor(c( "A", "A", "A", "A", "B", "B", "B", "B" )); b<-c(1, 2, 3, 4, 5, 6, 7, 8); bar<-lm(dat ~ a + b);
         * <p>
         * summary(bar) ;anova(bar); summary(bar)$df
         */

        double[] data = new double[] { 3.2969, 3.1856, 3.1638, Double.NaN, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };

        List<String> fac1 = Arrays.asList( f1 );

        double[] fac2 = new double[] { 1, 2, 3, 4, 5, 6, 7, 8 };

        Map<String, List<?>> factors = new LinkedHashMap<String, List<?>>();
        factors.put( "foo", fac1 );
        factors.put( "bar", Arrays.asList( ArrayUtils.toObject( fac2 ) ) );

        LinearModelSummary lms = rc.linearModel( data, factors );

        Double p = lms.getMainEffectP( "foo" );
        Double t = lms.getContrastTStats( "foo" ).get( "fooB" );

        Double pp = lms.getMainEffectP( "bar" );
        Double tt = lms.getContrastTStats( "bar" ).get( "bar" );

        Double ip = lms.getInterceptP();
        Double it = lms.getInterceptT();
        Integer dof = lms.getNumeratorDof();
        Integer rdof = lms.getResidualDof();

        assertEquals( 0.1586, p, 0.0001 );
        assertEquals( 0.64117305, t, 0.0001 );
        assertEquals( 0.9443, pp, 0.0001 );
        assertEquals( 0.07432241, tt, 0.0001 );
        assertEquals( 2.821526e-06, ip, 0.0001 );
        assertEquals( 38.14344686, it, 0.0001 );
        assertEquals( 2, ( int ) dof );
        assertEquals( 4, ( int ) rdof );

    }

    /**
     * @
     */
    @Test
    public void testLinearModelC() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /*
         * Another way of running a linear model.
         */

        double[] data = new double[] { 3.2969, 3.1856, 3.1638, Double.NaN, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };

        double[] f2 = new double[] { 1, 2, 3, 4, 5, 6, 7, 8 };

        ObjectMatrix<String, String, Object> d = new ObjectMatrixImpl<String, String, Object>( 8, 2 );

        for ( int i = 0; i < 8; i++ ) {
            for ( int j = 0; j < 2; j++ ) {
                if ( j == 0 ) {
                    d.set( i, j, f1[i] );
                } else {
                    d.set( i, j, f2[i] );
                }
            }
        }

        d.setColumnNames( Arrays.asList( new String[] { "foo", "bar" } ) );
        d.setRowNames( Arrays.asList( new String[] { "k", "m", "n", "o", "p", "q", "r", "s" } ) );

        LinearModelSummary lms = rc.linearModel( data, d );

        Double p = lms.getMainEffectP( "foo" );
        Double t = lms.getContrastTStats( "foo" ).get( "fooB" );

        Double pp = lms.getMainEffectP( "bar" );
        Double tt = lms.getContrastTStats( "bar" ).get( "bar" );

        Double ip = lms.getInterceptP();
        Double it = lms.getInterceptT();

        assertEquals( 0.1586, p, 0.0001 );
        assertEquals( 0.64117305, t, 0.0001 );
        assertEquals( 9.443222e-01, pp, 0.0001 );
        assertEquals( 0.07432241, tt, 0.0001 );
        assertEquals( 2.821526e-06, ip, 0.0001 );
        assertEquals( 38.14344686, it, 0.0001 );

    }

    /**
     * Basically a one-way anova with 4 levels in the factor. @
     */
    @Test
    public void testLinearModelD() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }

        /**
         * foo<-c( 3.2969, 3.1856, 4.1638, 4.59, 3.2342, 3.3533, 3.4347, 3.3074);
         * <p>
         * d<-factor(c( "y", "y", "z", "z", "x", "x", "w", "w" ));
         * <p>
         * summary(lm(foo ~ d));summary(lm(foo ~ d))$df; anova(lm(foo ~ d));
         */

        double[] data = new double[] { 3.2969, 3.1856, 4.1638, 4.59, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "y", "y", "z", "z", "x", "x", "w", "w" };

        List<String> fac1 = Arrays.asList( f1 );

        Map<String, List<?>> factors = new LinkedHashMap<String, List<?>>();
        factors.put( "foo", fac1 );

        LinearModelSummary lms = rc.linearModel( data, factors );

        Double p = lms.getMainEffectP( "foo" );
        Double t = lms.getContrastTStats( "foo" ).get( "foox" );

        Double ip = lms.getInterceptP();
        Double it = lms.getInterceptT();

        assertEquals( 0.006669, p, 0.0001 );
        assertEquals( -0.462, t, 0.001 );
        assertEquals( 2.821526e-06, ip, 0.0001 );
        assertEquals( 28.464, it, 0.001 );
        assertEquals( 3, ( int ) lms.getNumeratorDof() );
        assertEquals( 4, ( int ) lms.getResidualDof() );
        assertEquals( 20.8, lms.getF(), 0.01 );
        assertEquals( 0.00667, lms.getP(), 0.0001 );
    }

    @Test
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

    @Test
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

    @Test
    public void testStringListEval() {
        if ( !connected ) {
            log.warn( "Cannot load JRI, skipping test" );
            return;
        }
        List<String> actual = rc.stringListEval( "c('a','b')" );
        assertEquals( 2, actual.size() );
        assertEquals( "a", actual.get( 0 ) );
        assertEquals( "b", actual.get( 1 ) );
    }
}