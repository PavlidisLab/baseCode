/*
 * The baseCode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.math;

import junit.framework.TestCase;

/**
 * Gold standard (?) is the exactRankTests (http://cran.r-project.org/src/contrib/Descriptions/exactRankTests.html)
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestWilcoxon extends TestCase {
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
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

    public final void testWilxcoxonPGauss() throws Exception {

        /*
         * x<-c(1:100, 201:300, 401:500, 601:700, 801:900); y<-c(101:200, 301:400, 501:600, 701:800);
         * wilcox.exact(x,y, alternative="less")
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
        double expectedValue = 0.001677;
        assertEquals( expectedValue, actualValue, 0.001 );
    }

    public final void testWilxcoxonPExactD() throws Exception {

        /*
         * y<-c(1:10, 69:90); x<-c(11:68); wilcox.exact(x,y, alternative="less")
         */
        double expectedValue = 0.001677;
        double actualValue = Wilcoxon.exactWilcoxonP( 90, 58, 2291 );
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

}
