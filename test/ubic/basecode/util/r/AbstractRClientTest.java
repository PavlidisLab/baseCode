/*
 * The baseCode project
 *
 * Copyright (c) 2013 University of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.util.r;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrixImpl;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.math.Constants;
import ubic.basecode.math.linearmodels.LinearModelSummary;
import ubic.basecode.math.linearmodels.OneWayAnovaResult;
import ubic.basecode.math.linearmodels.TwoWayAnovaResult;
import ubic.basecode.util.RegressionTesting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.*;

/**
 * @author Paul
 *
 */
public abstract class AbstractRClientTest {

    protected static Logger log = LoggerFactory.getLogger( AbstractRClientTest.class );

    private AbstractRClient rc = null;
    private final double[] test1 = new double[] { -1.2241396, -0.6794486, -0.8475404, -0.4119554, -2.1980083 };
    private final double[] test2 = new double[] { 0.67676154, 0.20346679, 0.09289084, 0.68850551, 0.61120011 };
    private DoubleMatrix<String, String> tester;

    @Before
    public void setUp() throws IOException {
        rc = createRClient();
        DoubleMatrixReader reader = new DoubleMatrixReader();
        tester = reader.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
    }

    @After
    public void tearDown() {
        if ( rc != null && rc.isConnected() ) {
            rc.disconnect();
        }
    }

    protected abstract AbstractRClient createRClient();

    @Test
    public void testAnovaA() {
        /*
         * foo<-c(212.1979, 8.8645, 8.4814, 11.915); a<-factor(c( "A", "B", "B", "B" )); b<-factor(c( "D", "C", "D", "D"
         * )); anova(aov(foo ~ a+b));
         */

        double[] data = new double[] { 212.1979, 8.8645, 8.4814, 11.915 };
        String[] f1 = new String[] { "A", "B", "B", "B" };
        String[] f2 = new String[] { "D", "C", "D", "D" };

        TwoWayAnovaResult r = rc.twoWayAnova( data, Arrays.asList( f1 ), Arrays.asList( f2 ), false );
        assertEquals( 0.008816, r.getMainEffectAPValue(), 0.0001 );
        assertEquals( 0.731589, r.getMainEffectBPValue(), 0.0001 );
        assertEquals( 5214.3817, r.getMainEffectAFStat(), 0.0001 );
        assertEquals( 0.2012, r.getMainEffectBFStat(), 0.0001 );
    }

    @Test
    public void testAnovaB() {
        /*
         * foo<-c( 3.2969, 3.1856, 3.1638, NA, 3.2342, 3.3533, 3.4347, 3.3074); a<-factor(c( "A", "A", "A", "A", "B",
         * "B", "B", "B" )); b<-factor(c( "C", "C", "D", "D", "C", "C", "D", "D" )); anova(aov(foo ~ a+b));
         */

        double[] data = new double[] { 3.2969, 3.1856, 3.1638, Double.NaN, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };
        String[] f2 = new String[] { "C", "C", "D", "D", "C", "C", "D", "D" };

        TwoWayAnovaResult r = rc.twoWayAnova( data, Arrays.asList( f1 ), Arrays.asList( f2 ), false );
        assertEquals( 0.1567, r.getMainEffectAPValue(), 0.0001 );
        assertEquals( 0.8323, r.getMainEffectBPValue(), 0.0001 );
        assertEquals( 3.0294, r.getMainEffectAFStat(), 0.0001 );
        assertEquals( 0.0511, r.getMainEffectBFStat(), 0.0001 );
    }

    @Test
    public void testAnovaC() {
        /*
         * foo<-c( 213.7725, 211.6383, NA, 212.589, 10.3011, 10.1182, 13.486, 12.916); a<-factor(c( "A", "A", "A", "A",
         * "B", "B", "B", "B" )); b<-factor(c( "C", "C", "D", "D", "C", "C", "D", "D" )); anova(aov(foo ~ a+b));
         */

        double[] data = new double[] { 213.7725, 211.6383, Double.NaN, 212.589, 10.3011, 10.1182, 13.486, 12.916 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };
        String[] f2 = new String[] { "C", "C", "D", "D", "C", "C", "D", "D" };

        TwoWayAnovaResult r = rc.twoWayAnova( data, Arrays.asList( f1 ), Arrays.asList( f2 ), true );
        assertEquals( 8.97e-08, r.getMainEffectAPValue(), 0.0000001 );
        assertEquals( 0.08816, r.getMainEffectBPValue(), 0.0001 );
        assertEquals( 0.11823, r.getInteractionPValue(), 0.0001 );
        assertEquals( 84546.9846, r.getMainEffectAFStat(), 0.0001 );
        assertEquals( 6.2208, r.getMainEffectBFStat(), 0.0001 );
        assertEquals( 4.7178, r.getInteractionFStat(), 0.0001 );
    }

    @Test
    public void testAnovaD() {
        /*
         * foo<-c( 206.2209 , NA , 205.6038 , 203.0751 , NA , NA , 4.6569 , NA ); a<-factor(c( "A", "A", "A", "A", "B",
         * "B", "B", "B" )); b<-factor(c( "C", "C", "D", "D", "C", "C", "D", "D" )); anova(aov(foo ~ a+b));
         */

        double[] data = new double[] { 206.2209, Double.NaN, 205.6038, 203.0751, Double.NaN, Double.NaN, 4.6569,
                Double.NaN };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };
        String[] f2 = new String[] { "C", "C", "D", "D", "C", "C", "D", "D" };

        TwoWayAnovaResult r = rc.twoWayAnova( data, Arrays.asList( f1 ), Arrays.asList( f2 ), true );
        assertEquals( 0.006562, r.getMainEffectAPValue(), 0.00001 );
        assertEquals( 0.548142, r.getMainEffectBPValue(), 0.0001 );
        assertEquals( Double.NaN, r.getInteractionPValue(), 0.0001 );
        assertEquals( 9412.4049, r.getMainEffectAFStat(), 0.0001 );
        assertEquals( 0.7381, r.getMainEffectBFStat(), 0.0001 );
        assertEquals( Double.NaN, r.getInteractionFStat(), 0.0001 );
    }

    @Test
    public void testAnovaE() {
        /*
         * foo<-c(212.1979, 8.8645, 8.4814, 11.915); a<-factor(c( "A", "B", "B", "B" )); anova(aov(foo ~ a));
         */

        double[] data = new double[] { 212.1979, 8.8645, 8.4814, 11.915 };
        String[] f1 = new String[] { "A", "B", "B", "B" };

        OneWayAnovaResult r = rc.oneWayAnova( data, Arrays.asList( f1 ) );
        assertEquals( 0.0001152, r.getMainEffectPValue(), 0.0001 );
        assertEquals( 8682.2, r.getMainEffectFStat(), 0.01 );
    }

    /**
     * One way @
     */
    @Test
    public void testAnovaF() {
        /*
         * foo<-c( 3.2969, 3.1856, 3.1638, NA, 3.2342, 3.3533, 3.4347, 3.3074); a<-factor(c( "A", "A", "A", "A", "B",
         * "B", "B", "B" )); anova(aov(foo ~ a));
         */

        double[] data = new double[] { 3.2969, 3.1856, 3.1638, Double.NaN, 3.2342, 3.3533, 3.4347, 3.3074 };
        String[] f1 = new String[] { "A", "A", "A", "A", "B", "B", "B", "B" };

        OneWayAnovaResult r = rc.oneWayAnova( data, Arrays.asList( f1 ) );
        assertEquals( 0.1110, r.getMainEffectPValue(), 0.0001 );
        assertEquals( 3.739, r.getMainEffectFStat(), 0.001 );
    }

    @Test
    public void testAssignAndRetrieveMatrix() {
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

    @Test
    public void testAssignAndRetrieveMatrixB() {
        DoubleMatrix<String, String> result = rc.retrieveMatrix( rc.assignMatrix( tester ) );
        assertEquals( "gene1_at", result.getRowName( 0 ) );
        assertEquals( "sample1", result.getColName( 0 ) );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );
    }

    @Test
    public void testAssignAndRetrieveMatrixC() {
        DoubleMatrix<String, String> result = rc.retrieveMatrix( rc.assignMatrix( tester.asArray() ) );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );

    }

    @Test
    public void testAssignAndRetrieveMatrixD() {
        String m = rc.assignMatrix( tester.asArray() );
        DoubleMatrix<String, String> result = rc.retrieveMatrix( m );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );
    }

    @Test
    public void testAssignStringList() {
        List<String> l = new ArrayList<String>();
        l.add( "foo" );
        l.add( "bar" );

        String varname = rc.assignStringList( l );
        String actualValue = rc.stringEval( varname + "[1]" );
        assertEquals( "foo", actualValue );
        actualValue = rc.stringEval( varname + "[2]" );
        assertEquals( "bar", actualValue );
    }

    @Test
    public void testDataFrameA() {
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
    public void testDoubleArrayTwoDoubleArrayEval() {
        double[] actual = rc.doubleArrayTwoDoubleArrayEval( "a-b", "a", test1, "b", test2 );
        double[] expected = new double[] { -1.9009011, -0.8829154, -0.9404312, -1.1004609, -2.8092084 };
        RegressionTesting.closeEnough( expected, actual, Constants.SMALLISH );
    }

    @Test
    public void testDoubleTwoDoubleArrayEval() {
        double actual = rc.doubleTwoDoubleArrayEval( "cor(a,b)", "a", test1, "b", test2 );
        double expected = -0.29843518070456654;
        assertEquals( expected, actual, Constants.SMALLISH );
    }

    @Test
    /*
     * Test method for ' RCommand.exec(String)'
     */
    public void testExec() {
        String actualValue = rc.stringEval( "R.version.string" );
        String expectedValue = "R version";

        assertTrue( "rc.eval() return version " + actualValue + ", expected something starting with R version",
                actualValue.startsWith( expectedValue ) );
    }

    /*
     * Test method for 'exec(String)'
     */
    @Test
    public void testExecDoubleArray() {
        double[] dd = rc.doubleArrayEval( "rep(1, 10)" );
        assertNotNull( dd );
        assertTrue( RegressionTesting.closeEnough( new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, dd, 0.001 ) );
    }

    @Test
    public void testExecError() {
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
        List<String> list = new ArrayList<String>();
        list.add( "a" );
        list.add( "b" );
        String factor = rc.assignFactor( list );
        assertNotNull( factor );
    }

    @Test
    public void testFindExecutable() throws Exception {
        String cmd = RServeClient.findRserveCommand();
        assertNotNull( cmd ); // should always come up with something.
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
        assertEquals( "gene8_at", dataFrameEval.getRowName( 1 ) );
        assertEquals( 0.684, ( double ) dataFrameEval.get( 6, 4 ), 0.001 );

    }

    /**
     * Like a two-sample t-test where the intercept is also of interest. @
     */
    @Test
    public void testLinearModelA() throws REXPMismatchException {
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
        LinearModelSummaryImpl lms = new LinearModelSummaryImpl( facN, result, anova, new String[] { "f1" } );

        assertNotNull( lms.getAnova() );
        double p = lms.getAnova().getMainEffectPValue( facN );
        double t = lms.getContrastTStats( facN ).get( "f1B" );
        double ip = lms.getInterceptPValue();
        double it = lms.getInterceptTStat();
        double dof = lms.getNumeratorDof();
        double rdof = lms.getResidualsDof();

        assertEquals( 0.11096, p, 0.0001 );
        assertEquals( 1.933653, t, 0.0001 );
        assertEquals( 1.10e-08, ip, 1e-10 );
        assertEquals( 70.319382, it, 0.0001 );
        assertEquals( 1.0, dof, 0.0 );
        assertEquals( 5.0, rdof, 0.0 );

    }

    /**
     * With a continuous covariate as well a categorical one. @
     */
    @Test
    public void testLinearModelB() {
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

        double p = lms.getAnova().getMainEffectPValue( "foo" );
        double t = lms.getContrastTStats( "foo" ).get( "fooB" );

        double pp = lms.getAnova().getMainEffectPValue( "bar" );
        double tt = lms.getContrastTStats( "bar" ).get( "bar" );

        double ip = lms.getInterceptPValue();
        double it = lms.getInterceptTStat();
        double dof = lms.getNumeratorDof();
        double rdof = lms.getResidualsDof();

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

        double p = lms.getAnova().getMainEffectPValue( "foo" );
        double t = lms.getContrastTStats( "foo" ).get( "fooB" );

        double pp = lms.getAnova().getMainEffectPValue( "bar" );
        double tt = lms.getContrastTStats( "bar" ).get( "bar" );

        double ip = lms.getInterceptPValue();
        double it = lms.getInterceptTStat();

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

        double p = lms.getAnova().getMainEffectPValue( "foo" );
        double t = lms.getContrastTStats( "foo" ).get( "foox" );

        double ip = lms.getInterceptPValue();
        double it = lms.getInterceptTStat();

        assertEquals( 0.006669, p, 0.0001 );
        assertEquals( -0.462, t, 0.001 );
        assertEquals( 2.821526e-06, ip, 0.0001 );
        assertEquals( 28.464, it, 0.001 );
        assertEquals( 3, ( int ) lms.getNumeratorDof() );
        assertEquals( 4, ( int ) lms.getResidualsDof() );
        assertEquals( 20.8, lms.getFStat(), 0.01 );
        assertEquals( 0.00667, lms.getOverallPValue(), 0.0001 );
    }

    @Test
    public void testListEvalA() throws Exception {
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
        DoubleMatrixReader r = new DoubleMatrixReader();
        DoubleMatrix<String, String> read = r.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
        String matrixName = rc.assignMatrix( read );
        List<?> results;

        results = rc.listEval( HTest.class, "apply(" + matrixName + ", 1, function(x) {cor.test(x, " + matrixName
                + "[1,])})" );
        assertNotNull( results );

        for ( Object o : results ) {
            assertFalse( Double.isNaN( ( ( HTest ) o ).getPvalue() ) );
        }

    }

    @Test
    public void testLoadLibrary() {
        assertFalse( rc.loadLibrary( "foooobly" ) );
        assertTrue( rc.loadLibrary( "graphics" ) );
    }

    @Test
    public void testLoadScript() throws Exception {
        BufferedReader reader = new BufferedReader( new InputStreamReader( requireNonNull( this.getClass().getResourceAsStream(
            "/ubic/basecode/util/r/testScript.R" ) ) ) );
        String line;
        StringBuilder buf = new StringBuilder();
        while ( ( line = reader.readLine() ) != null ) {
            if ( line.startsWith( "#" ) || StringUtils.isBlank( line ) ) {
                continue;
            }
            buf.append( StringUtils.trim( line ) + "\n" );
        }
        String sc = buf.toString();

        rc.loadScript( this.getClass().getResourceAsStream( "/ubic/basecode/util/r/linearModels.R" ) );
        String rawscript = "rowlm<-function(formula,data)" + "{\nmf<-lm(formula,data,method=\"model.frame\")\n"
                + "mt <- attr(mf, \"terms\")\n" + "x <- model.matrix(mt, mf)\n"
                + "design<-model.matrix(formula)\ncl <- match.call()\n" + "r<-nrow(data)\nres<-vector(\"list\",r)\n"
                + "lev<-.getXlevels(mt, mf)\nclz<-c(\"lm\")\n" + "D<-as.matrix(data)\n" + "ids<-row.names(data)\n"
                + "for(i in 1:r) {\n" + "y<-as.vector(D[i,])\n" + "id<-ids[i]\n" + "m<-is.finite(y)\n"
                + "if (sum(m) > 0) {\n" + "X<-design[m,,drop=FALSE]\n"
                + "attr(X,\"assign\")<-attr(design,\"assign\")\n" + "y<-y[m]\n" + "z<-lm.fit(X,y)\n"
                + "class(z) <- clz\n" + "z$na.action <- na.exclude\n" + "z$contrasts <- attr(x, \"contrasts\")\n"
                + "z$xlevels <- lev\n" + "z$call <- cl\n" + "z$terms <- mt\n" + "z$model <- mf\n" + "res[[i]]<-z"
                + "\n}\n" + "}\n" + "names(res)<-row.names(data)\n" + "return(res)\n" + "}\n";

        rc.voidEval( rawscript );
        rc.voidEval( sc );

        rc.loadScript( this.getClass().getResourceAsStream( "/ubic/basecode/util/r/linearModels.R" ) );
    }

    @Test
    public void testStringListEval() {
        List<String> actual = rc.stringListEval( "c('a','b')" );
        assertEquals( 2, actual.size() );
        assertEquals( "a", actual.get( 0 ) );
        assertEquals( "b", actual.get( 1 ) );
    }

    @Test
    public void testStringListEvalB() {
        List<String> actual = rc.stringListEval( "c('a','b')" );
        assertEquals( 2, actual.size() );
        assertEquals( "a", actual.get( 0 ) );
        assertEquals( "b", actual.get( 1 ) );
    }

    @Test
    public void testTTest() {
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

    @Test
    public void testTTestFail() {
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
}
