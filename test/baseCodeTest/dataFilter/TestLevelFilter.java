package baseCodeTest.dataFilter;

import baseCode.dataFilter.LevelFilter;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;

/**
 * 
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestLevelFilter extends AbstractTestFilter {
   LevelFilter f = null;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      f = new LevelFilter();
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /*
    * Class under test for NamedMatrix filter(NamedMatrix)
    */
   public final void testFilterMax() {
      f.setLowCut( 100.0, false );
      f.setRemoveAllNegative( true ); // irrelevant.
      f.setMethod( LevelFilter.MAX ); // this is the default.
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 26;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMaxTooHigh() {
      f.setLowCut( 100000000.0, false );
      f.setRemoveAllNegative( true ); // irrelevant.
      f.setMethod( LevelFilter.MAX ); // this is the default.
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 0;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMin() {
      f.setLowCut( 100.0, false );
      f.setRemoveAllNegative( true );
      f.setMethod( LevelFilter.MIN );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 16;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMean() {
      f.setLowCut( 100.0, false );
      f.setRemoveAllNegative( true );
      f.setMethod( LevelFilter.MEAN );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 21;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMedian() {
      f.setLowCut( 100.0, false );
      f.setRemoveAllNegative( true ); // irrelevant
      f.setMethod( LevelFilter.MEDIAN );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 20;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMaxFraction() {
      f.setLowCut( 0.3, true );
      f.setRemoveAllNegative( false );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 21;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMaxFractionAll() {
      f.setLowCut( 1.0, true );
      f.setRemoveAllNegative( false );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 1; // we don't actually ever remove all items.
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMaxFractionNone() {
      f.setLowCut( 0.0, true );
      f.setRemoveAllNegative( false );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 30;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMaxFractionNoneRemoveNeg() {
      f.setLowCut( 0.0, true );
      f.setRemoveAllNegative( true );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 27;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterFractionMaxRemoveNeg() {
      f.setLowCut( 0.6, true );
      f.setRemoveAllNegative( true ); // removes 3 of 30 rows. 27 * 0.6 =16.2,
                                      // ceil 17, + 3 = 20
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 11;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterBadFraction() {
      try {
         f.setLowCut( 110.3, true );
         DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
               .filter( testdata );
         fail( "Should have gotten an exception" );
      } catch ( RuntimeException success ) {
      }
   }

   public final void testFilterNoFilter() {
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      assertEquals( "return value", testdata, filtered );
   }

   public final void testFilterMaxHigh() {
      f.setHighCut( 1000.0, false );
      f.setRemoveAllNegative( true ); // irrelevant.
      f.setMethod( LevelFilter.MAX ); // this is the default.
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 16;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMinHigh() {
      f.setHighCut( 1000.0, false );
      f.setRemoveAllNegative( true );
      f.setMethod( LevelFilter.MIN );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 24;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMeanHigh() {
      f.setHighCut( 1000.0, false );
      f.setRemoveAllNegative( true );
      f.setMethod( LevelFilter.MEAN );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 21;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMedianHigh() {
      f.setHighCut( 1000.0, false );
      f.setRemoveAllNegative( true ); // irrelevant
      f.setMethod( LevelFilter.MEDIAN );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 22;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterMaxFractionHigh() {
      f.setHighCut( 0.3, true );
      f.setRemoveAllNegative( false );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 21;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterFractionMaxRemoveNegHigh() {
      f.setHighCut( 0.3, true );
      f.setRemoveAllNegative( true );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 19;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterFractionMaxRemoveHighAndLow() {
      f.setHighCut( 0.3, true );
      f.setLowCut( 100, false );
      f.setRemoveAllNegative( true );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 15;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterFractionMaxRemoveHighAndLowBothFraction() {
      f.setHighCut( 0.3, true );
      f.setLowCut( 0.1, true );
      f.setRemoveAllNegative( true );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 14; // 3 neg, 0.1*27 = 3, 0.3*27 = 9, inclusive at
                               // top means 10 + 3 + 3 = 16, from 30 is 14.
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterFractionMaxRemoveHighAndLowBothLevels() {
      f.setHighCut( 1000, false );
      f.setLowCut( 100, false );
      f.setRemoveAllNegative( true ); // irrelevant
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 12;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterFractionMaxRemoveHighAndLowBothLevelsNoNegRemove() {
      f.setHighCut( 1000, false );
      f.setLowCut( 100, false );
      f.setRemoveAllNegative( false );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 12;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterFractionMaxRemoveHighAndLowBothLevelsNegLow() {
      f.setHighCut( 1000, false );
      f.setLowCut( -100, false );
      f.setRemoveAllNegative( true );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int actualReturn = filtered.rows();
      int expectedReturn = 14;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

}