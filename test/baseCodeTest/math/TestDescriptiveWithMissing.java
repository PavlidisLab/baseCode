package baseCodeTest.math;

import baseCode.math.DescriptiveWithMissing;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import junit.framework.TestCase;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class TestDescriptiveWithMissing
    extends TestCase {

   private DoubleArrayList data1missing;
   private DoubleArrayList data1Nomissing;
   private DoubleArrayList data2missing;
   private DoubleArrayList data2Nomissing;
   private DoubleArrayList data3shortmissing;
   private DoubleArrayList data3shortNomissing;

   private DoubleArrayList datacortest1Nomissing;
   private DoubleArrayList datacortest2Nomissing;
   private DoubleArrayList dataAcortest1Nomissing;

   protected void setUp() throws Exception {
      super.setUp();
      data1missing = new DoubleArrayList( new double[] {1.0, Double.NaN, 3.0,
                                          4.0, 5.0, 6.0} );
      data2missing = new DoubleArrayList( new double[] {Double.NaN, Double.NaN,
                                          3.0,
                                          Double.NaN, 3.5, 4.0} );
      data3shortmissing = new DoubleArrayList( new double[] {Double.NaN,
                                               Double.NaN, 3.0} );

      /* versions of the above, but without the NaNs */
      data1Nomissing = new DoubleArrayList( new double[] {1.0, 3.0,
                                            4.0, 5.0, 6.0} );
      data2Nomissing = new DoubleArrayList( new double[] {3.0, 3.5, 4.0} );
      data3shortNomissing = new DoubleArrayList( new double[] {3.0} );

      datacortest1Nomissing = new DoubleArrayList( new double[] {3.0, 5.0, 6.0} );
      dataAcortest1Nomissing = new DoubleArrayList( new double[] {1.0, 3.0, 5.0, 6.0} );
      datacortest2Nomissing = new DoubleArrayList( new double[] {3.0, 3.5, 4.0} );

   }

   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testCorrelationA() {
      double s1 = Descriptive.standardDeviation( Descriptive.variance( datacortest1Nomissing.size(),
          Descriptive.sum( datacortest1Nomissing ), Descriptive.sumOfSquares( datacortest1Nomissing ) ) );
      double s2 = Descriptive.standardDeviation( Descriptive.variance( datacortest2Nomissing.size(),
          Descriptive.sum( datacortest2Nomissing ), Descriptive.sumOfSquares( datacortest2Nomissing ) ) );

      double expectedReturn = Descriptive.correlation( datacortest1Nomissing, s1,
          datacortest2Nomissing, s2 );
      double actualReturn = DescriptiveWithMissing.correlation( data1missing, data2missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testCorrelationB() {
      double s1 = Descriptive.standardDeviation( Descriptive.variance( datacortest1Nomissing.size(),
          Descriptive.sum( datacortest1Nomissing ), Descriptive.sumOfSquares( datacortest1Nomissing ) ) );
      double s2 = Descriptive.standardDeviation( Descriptive.variance( datacortest2Nomissing.size(),
          Descriptive.sum( datacortest2Nomissing ), Descriptive.sumOfSquares( datacortest2Nomissing ) ) );

      double s1m = DescriptiveWithMissing.standardDeviation( DescriptiveWithMissing.variance(
          data1missing ) );
      double s2m = DescriptiveWithMissing.standardDeviation( DescriptiveWithMissing.variance(
          data2missing ) );

      double expectedReturn = Descriptive.correlation( datacortest1Nomissing, s1,
          datacortest2Nomissing, s2 );
      double actualReturn = DescriptiveWithMissing.correlation( data1missing, s1m, data2missing,
          s2m );

      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testCovariance() {
      double expectedReturn = Descriptive.covariance( datacortest1Nomissing, datacortest2Nomissing );
      double actualReturn = DescriptiveWithMissing.covariance( data1missing, data2missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testDurbinWatson() {
      double expectedReturn = Descriptive.durbinWatson( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.durbinWatson( data1missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testDurbinWatsonTwo() {
      double expectedReturn = Descriptive.durbinWatson( data2Nomissing );
      double actualReturn = DescriptiveWithMissing.durbinWatson( data2missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testDurbinWatsonShort() {

      try {
         double expectedReturn = Descriptive.durbinWatson(
             data3shortNomissing );
         double actualReturn = DescriptiveWithMissing.durbinWatson(
             data3shortmissing );
         assertEquals( "Short array failure.", expectedReturn, actualReturn,
                       Double.MIN_VALUE );
         fail( "Should have thrown an IllegalArgumentException" );
      }
      catch ( IllegalArgumentException e ) {
         return;
      }
      catch ( Exception e ) {
         fail( "Threw wrong exception: " + e );
      }
   }

   public void testGeometricMean() {
      double expectedReturn = Descriptive.geometricMean( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.geometricMean( data1missing );
      assertEquals( "Excercises sumOfLogarithms too; return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );

   }

   public void testMean() {
      double expectedReturn = Descriptive.mean( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.mean( data1missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );

   }

   public void testMin() {
      double expectedReturn = Descriptive.min( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.min( data1missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testMax() {
      double expectedReturn = Descriptive.max( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.max( data1missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testMedian() {
      data1missing.sort();
      data1Nomissing.sort();
      double expectedReturn = Descriptive.median( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.median( data1missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testProduct() {
      double expectedReturn = Descriptive.product( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.product( data1missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testSampleKurtosis() {
      double expectedReturn = Descriptive.sampleKurtosis( data1Nomissing,
          Descriptive.mean( data1Nomissing ),
          Descriptive.sampleVariance( data1Nomissing, Descriptive.mean( data1Nomissing ) ) );
      double actualReturn = DescriptiveWithMissing.sampleKurtosis( data1missing,
          DescriptiveWithMissing.mean( data1missing ),
          DescriptiveWithMissing.sampleVariance( data1missing,
                                                 DescriptiveWithMissing.mean( data1missing ) ) );
      assertEquals( "Exercises sampleVariance, mean as well; return value", expectedReturn,
                    actualReturn,
                    Double.MIN_VALUE );
   }

   public void testQuantile() {
      data1missing.sort();
      data1Nomissing.sort();
      double expectedReturn = Descriptive.quantile( data1Nomissing, 0.10 );
      double actualReturn = DescriptiveWithMissing.quantile( data1missing, 0.10 );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );

   }

   public void testSum() {
      double expectedReturn = Descriptive.sum( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.sum( data1missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testSumOfSquares() {
      double expectedReturn = Descriptive.sumOfSquares( data1Nomissing );
      double actualReturn = DescriptiveWithMissing.sumOfSquares( data1missing );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );
   }

   public void testSampleVariance() {
      double expectedReturn = Descriptive.sampleVariance( data1Nomissing,
          Descriptive.mean( data1Nomissing ) );
      double actualReturn = DescriptiveWithMissing.sampleVariance(
          data1missing, DescriptiveWithMissing.mean( data1missing ) );
      assertEquals( "return value", expectedReturn,
                    actualReturn,
                    Double.MIN_VALUE );

   }

   public void testTrimmedMean() {
      data1Nomissing.sort();
      data1missing.sort();
      double expectedReturn = Descriptive.trimmedMean( data1Nomissing,
          Descriptive.mean( data1Nomissing ), 1, 1 );
      double actualReturn = DescriptiveWithMissing.trimmedMean( data1missing,
          DescriptiveWithMissing.mean( data1missing ), 1, 1 );
      assertEquals( "return value", expectedReturn, actualReturn,
                    Double.MIN_VALUE );

   }

   public void testVariance() {
      double expectedReturn = Descriptive.variance( data1Nomissing.size(),
          Descriptive.sum( data1Nomissing ),
          Descriptive.sumOfSquares( data1Nomissing ) );
      double actualReturn = DescriptiveWithMissing.variance(
          DescriptiveWithMissing.sizeWithoutMissingValues( data1missing ),
          DescriptiveWithMissing.sum( data1missing ),
          DescriptiveWithMissing.sumOfSquares( data1missing ) );
      assertEquals( "return value", expectedReturn,
                    actualReturn,
                    Double.MIN_VALUE );
   }

}
