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
package ubic.basecode.math;

import junit.framework.TestCase;

/**
 * Gold standard (?) is the exactRankTests (http://cran.r-project.org/src/contrib/Descriptions/exactRankTests.html) or
 * on Catmap.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestWilcoxon extends TestCase {
    /* tests based on catmap */
    public void testAExact() throws Exception {
        /* kinetochore */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 2, 184 );
        double expectedValue = 0.000613671594516244;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    public void testBGaussian() throws Exception {
        /* mitotic cell cycle */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 97, 176069 );
        double expectedValue = 7.36188216415985e-08;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    public void testCGaussian() throws Exception {
        /* nucleus */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 618, 1499756 );
        double expectedValue = 0.000557086593133454;
        assertEquals( expectedValue, actualValue, 1e-6 );
    }

    public void testDVolume() throws Exception {
        /* nuclear division */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 41, 59721 );
        double expectedValue = 2.23181930573246e-07;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    public void testEVolume() throws Exception {
        /* DNA replicatoin */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 48, 89794 );
        double expectedValue = 0.000293433699059724;
        assertEquals( expectedValue, actualValue, 1e-6 );
    }

    public void testFVolume() throws Exception {
        /* Mphase */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 42, 60490 );
        double expectedValue = 1.06593257987108e-07;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    public final void testWilcoxonPExactA() {

        int N = 10;
        int n = 3;
        int R = 6; // all at the top;

        double actualReturn = Wilcoxon.wilcoxonP( N, n, R );
        double expectedReturn = 0.008333; // validated with R: ;
        // library(exactRankTests)
        // wilcox.exact(c(1,2,3), c(4,5,6,7,8,9,10), alternative="less")
        assertEquals( expectedReturn, actualReturn, 0.001 );

    }

    public final void testWilxcoxonPExactB() throws Exception {

        /*
         * x<-c(1,3,5,7,9); y<-c(2,4,6,8,10,11,12,13,14); wilcox.exact(x,y, alternative="less")
         */

        double[] x = new double[] { 1, 3, 5, 7, 9 };
        double[] y = new double[] { 2, 4, 6, 8, 10, 11, 12, 13, 14 };

        double actualValue = Wilcoxon.wilcoxonP( x.length + y.length, x.length, 25 );
        double expectedValue = 0.05594;

        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public final void testWilxcoxonPExactC() throws Exception {
        /*
         * x<-c(1,3 ); y<-c(2 ); wilcox.exact(x,y, alternative="less")
         */

        double[] x = new double[] { 1, 3 };
        double[] y = new double[] { 2 };

        double actualValue = Wilcoxon.wilcoxonP( x.length + y.length, x.length, 4 );
        double expectedValue = 0.66667;

        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public final void testWilxcoxonPExactD() throws Exception {

        /*
         * y<-c(1:10, 69:90); x<-c(11:68); wilcox.exact(x,y, alternative="less")
         */
        double expectedValue = 0.00153; // I now get 0.001676763 with wilcox.test.
        double actualValue = Wilcoxon.exactWilcoxonP( 90, 58, 2291 );
        assertEquals( expectedValue, actualValue, 0.00001 );
    }

    public final void testWilxcoxonPGauss() throws Exception {

        /*
         * x<-c(1:100, 201:300, 401:500, 601:700, 801:900); y<-c(101:200, 301:400, 501:600, 701:800); wilcox.exact(x,y,
         * alternative="less")
         */

        double actualValue = Wilcoxon.wilcoxonP( 900, 500, 225250 );
        double expectedValue = 0.5;

        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public final void testWilxcoxonPGaussB() throws Exception {

        /*
         * y<-c(1:300, 401:900); x<-c(301:400); wilcox.exact(x,y, alternative="less") > 2.250e-05
         */

        double actualValue = Wilcoxon.wilcoxonP( 900, 100, 35050 );
        double expectedValue = 2.250e-05;

        assertEquals( expectedValue, actualValue, 0.00001 );
    }

    public final void testWilxcoxonPVolume() throws Exception {

        /*
         * y<-c(1:10, 69:90); x<-c(11:68); wilcox.exact(x,y, alternative="less")
         */

        double actualValue = Wilcoxon.wilcoxonP( 90, 58, 2291 );
        double expectedValue = 0.0535181722243048;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
