package baseCodeTest.math;

import cern.colt.list.DoubleArrayList;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.reader.DoubleMatrixReader;
import baseCode.math.MatrixRowStats;
import baseCode.util.RegressionTesting;
import baseCodeTest.dataFilter.AbstractTestFilter;
import junit.framework.TestCase;

/**
 * 
 * 
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestMatrixRowStats extends TestCase {

   protected DenseDoubleMatrix2DNamed testdata = null;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      DoubleMatrixReader f = new DoubleMatrixReader();

      testdata = ( DenseDoubleMatrix2DNamed ) f.read( AbstractTestFilter.class
            .getResourceAsStream( "/data/testdata.txt" ) );
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /*
    * Class under test for DoubleArrayList
    * sumOfSquares(DenseDoubleMatrix2DNamed)
    */
   public final void testSumOfSquaresDenseDoubleMatrix2DNamed() {
      DoubleArrayList actualReturn = MatrixRowStats.sumOfSquares( testdata );
      DoubleArrayList expectedReturn = new DoubleArrayList( new double[] {
            293847.67, 500060.93, 11146426060.47, 17712065.42, 18082011688.08,
            723701.66, 286139.3, 11612833039.53, 2918638887.99, 20682978339.68,
            34465.26, 5331217.87, 1160638.14, 11110284.05, 466405.43,
            1238996.89, 8761486.94, 10293417.38, 8606490.67, 84399.81,
            25917.75, 10492284.77, 2110962.48, 195657.72, 14831407.56,
            2248759.62, 66003411.25, 159539.01, 19204218029.07, 1413519.59 } );
      assertEquals( true, RegressionTesting.closeEnough( expectedReturn,
            actualReturn, 0.0001 ) );
   }

   /*
    * Class under test for DoubleArrayList
    * sumOfSquares(DenseDoubleMatrix2DNamed, DoubleArrayList)
    */
   public final void testSumOfSquaresDenseDoubleMatrix2DNamedDoubleArrayList() {
      DoubleArrayList actualReturn = MatrixRowStats.sumOfSquares( testdata,
            MatrixRowStats.means( testdata ) );
      DoubleArrayList expectedReturn = new DoubleArrayList( new double[] {
            293847.67, 500060.93, 11146426060.47, 17712065.42, 18082011688.08,
            723701.66, 286139.3, 11612833039.53, 2918638887.99, 20682978339.68,
            34465.26, 5331217.87, 1160638.14, 11110284.05, 466405.43,
            1238996.89, 8761486.94, 10293417.38, 8606490.67, 84399.81,
            25917.75, 10492284.77, 2110962.48, 195657.72, 14831407.56,
            2248759.62, 66003411.25, 159539.01, 19204218029.07, 1413519.59 } );

      assertEquals( true, RegressionTesting.closeEnough( expectedReturn,
            actualReturn, 0.0001 ) );
   }

   public final void testMeans() {
      DoubleArrayList actualReturn = MatrixRowStats.means( testdata );
      DoubleArrayList expectedReturn = new DoubleArrayList( new double[] {
            134.708333333333, 168.641666666667, 30408.875, 1042.28333333333,
            38778.8833333333, 199.35, -44.7666666666667, 31072.8416666667,
            15550.7083333333, 41462.5166666667, 9.33333333333333,
            -644.091666666667, 258.466666666667, 934.708333333333,
            -166.791666666667, -172.158333333333, 811.633333333333,
            920.366666666667, 825.375, -11.1916666666667, 21.7916666666667,
            927.058333333333, 396.6, 107.933333333333, 1080.13333333333,
            416.183333333333, 2265.75833333333, 12.3916666666667,
            39963.4916666667, -333.291666666667 } );
      assertEquals( true, RegressionTesting.closeEnough( expectedReturn,
            actualReturn, 0.0001 ) );
   }

   public final void testSums() {

      DoubleArrayList actualReturn = MatrixRowStats.sums( testdata );
      DoubleArrayList expectedReturn = new DoubleArrayList(
            new double[] { 1616.5, 2023.7, 364906.5, 12507.4, 465346.6, 2392.2,
                  -537.2, 372874.1, 186608.5, 497550.2, 112, -7729.1, 3101.6,
                  11216.5, -2001.5, -2065.9, 9739.6, 11044.4, 9904.5, -134.3,
                  261.5, 11124.7, 4759.2, 1295.2, 12961.6, 4994.2, 27189.1,
                  148.7, 479561.9, -3999.5 } );
      assertEquals( true, RegressionTesting.closeEnough( expectedReturn,
            actualReturn, 0.0001 ) );
   }

   public final void testStandardDeviations() {

      DoubleArrayList actualReturn = MatrixRowStats
            .sampleStandardDeviations( testdata );
      DoubleArrayList expectedReturn = new DoubleArrayList( new double[] {
            83.1710445498621, 120.144151738525, 2132.64486415128,
            651.97677431211, 1818.84521598115, 149.792762903225,
            154.358098019645, 1554.32442586264, 1233.78692609993,
            2201.13358556793, 55.11964397123, 179.131482732826,
            180.64981862625, 238.580657062224, 109.781563763795,
            283.378158709053, 279.041040752507, 108.09077273993,
            198.073444252838, 86.8105403982859, 42.8732054134542,
            127.578471098688, 142.53030299809, 71.2629360930776,
            274.880361854632, 124.410178137653, 632.418255527338,
            119.733176524329, 1888.96399621998, 85.5567696522802, } );
      assertEquals( true, RegressionTesting.closeEnough( expectedReturn,
            actualReturn, 0.00001 ) );
   }



}