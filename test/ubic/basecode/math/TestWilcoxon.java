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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Gold standard (?) is the exactRankTests (http://cran.r-project.org/src/contrib/Descriptions/exactRankTests.html) or
 * on Catmap.
 *
 * @author pavlidis
 * 
 */
public class TestWilcoxon {
    /* tests based on catmap */
    @Test
    public void testAExact() {
        /* kinetochore */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 2, 184 );
        double expectedValue = 0.000613671594516244;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    @Test
    public void testBGaussian() {
        /* mitotic cell cycle */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 97, 176069 );
        double expectedValue = 7.36188216415985e-08;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    @Test
    public void testBig() {
        int N0 = 473395;
        int n0 = 36081;
        long R0 = 224102826025L;
        double r = Wilcoxon.wilcoxonP( N0, n0, R0 );
        assertEquals( 1.0, r, 1e-5 );
    }

    @Test
    public void testCGaussian() {
        /* nucleus */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 618, 1499756 );
        double expectedValue = 0.000557086593133454;
        assertEquals( expectedValue, actualValue, 1e-6 );
    }

    @Test
    public void testDVolume() {
        /* nuclear division */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 41, 59721 );
        double expectedValue = 2.23181930573246e-07;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    @Test
    public void testEVolume() {
        /* DNA replicatoin */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 48, 89794 );
        double expectedValue = 0.000293433699059724;
        assertEquals( expectedValue, actualValue, 1e-6 );
    }

    @Test
    public void testFVolume() {
        /* Mphase */
        double actualValue = Wilcoxon.wilcoxonP( 5224, 42, 60490 );
        double expectedValue = 1.06593257987108e-07;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    @Test
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

    @Test
    public final void testWilxcoxonPExactB() {

        /*
         * x<-c(1,3,5,7,9); y<-c(2,4,6,8,10,11,12,13,14); wilcox.exact(x,y, alternative="less")
         */

        double[] x = new double[] { 1, 3, 5, 7, 9 };
        double[] y = new double[] { 2, 4, 6, 8, 10, 11, 12, 13, 14 };

        double actualValue = Wilcoxon.wilcoxonP( x.length + y.length, x.length, 25 );
        double expectedValue = 0.05594;

        assertEquals( expectedValue, actualValue, 0.001 );
    }

    @Test
    public final void testWilxcoxonPExactC() {
        /*
         * x<-c(1,3 ); y<-c(2 ); wilcox.exact(x,y, alternative="less")
         */

        double[] x = new double[] { 1, 3 };
        double[] y = new double[] { 2 };

        double actualValue = Wilcoxon.wilcoxonP( x.length + y.length, x.length, 4 );
        double expectedValue = 0.66667;

        assertEquals( expectedValue, actualValue, 0.001 );
    }

    @Test
    public final void testWilxcoxonPExactD() {

        /*
         * y<-c(1:10, 69:90); x<-c(11:68); wilcox.exact(x,y, alternative="less")
         */
        double[] y = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81,
                82, 83, 84, 85, 86, 87, 88, 89, 90 };
        double[] x = new double[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
                32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
                58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68 };

        double actualValuea = Wilcoxon.exactWilcoxonP( x, y );
        double expectedValue = 0.001529; // I now get 0.001676763 with wilcox.test.

        assertEquals( expectedValue, actualValuea, 0.00001 );

        double actualValue = Wilcoxon.exactWilcoxonP( 90, 58, 2291 );
        assertEquals( expectedValue, actualValue, 0.00001 );
    }

    @Test
    public final void testWilxcoxonPGauss() {

        /*
         * x<-c(1:100, 201:300, 401:500, 601:700, 801:900); y<-c(101:200, 301:400, 501:600, 701:800); wilcox.exact(x,y,
         * alternative="less")
         */

        double actualValue = Wilcoxon.wilcoxonP( 900, 500, 225250 );
        double expectedValue = 0.5;

        assertEquals( expectedValue, actualValue, 0.001 );
    }

    @Test
    public final void testWilxcoxonPGaussB() {

        /*
         * y<-c(1:300, 401:900); x<-c(301:400); wilcox.exact(x,y, alternative="less") > 2.250e-05
         */

        double actualValue = Wilcoxon.wilcoxonP( 900, 100, 35050 );
        double expectedValue = 2.250e-05;

        assertEquals( expectedValue, actualValue, 0.00001 );
    }

    @Test
    public final void testWilxcoxonPVolume() {

        /*
         * y<-c(1:10, 69:90); x<-c(11:68); wilcox.exact(x,y, alternative="less")
         */

        double actualValue = Wilcoxon.wilcoxonP( 90, 58, 2291 );
        double expectedValue = 0.0535181722243048;
        assertEquals( expectedValue, actualValue, 1e-10 );
    }

    // bug 3133
    @Test
    public final void testWilxcoxonPWithTies() {

        // assertEquals( 0.110, Wilcoxon.wilcoxonP( 3, 2, 3, true ), 0.001 );

        /*
         * x<-c(1,1 ); y<-c(2 ); wilcox.exact(x,y, alternative="less") 0.33
         */

        double[] x = new double[] { 1, 1 };
        double[] y = new double[] { 2 };
        try {
            Wilcoxon.exactWilcoxonP( x, y );
            fail( "Should have gotten an exception because of ties" );
        } catch ( Exception e ) {
            // ok
        }

    }

}
