package baseCodeTest.dataFilter;

import baseCode.dataFilter.LevelFilter;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;

/**
 *  
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 * @todo add more tests - median, min, mean
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
   public final void testFilterNamedMatrixMax() {
      f.setLowCut(100.0, false);
      f.setRemoveAllNegative(true); // irrelevant.
      f.setMethod(LevelFilter.MAX); // this is the default.
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 16;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   public final void testFilterNamedMatrixMin() {
      f.setLowCut(100.0, false);
      f.setRemoveAllNegative(true);
      f.setMethod(LevelFilter.MIN);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 26;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   public final void testFilterNamedMatrixMean() {
      f.setLowCut(100.0, false);
      f.setRemoveAllNegative(true);
      f.setMethod(LevelFilter.MEAN);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 21;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   public final void testFilterNamedMatrixMedian() {
      f.setLowCut(100.0, false);
      f.setRemoveAllNegative(true); // irrelevant
      f.setMethod(LevelFilter.MEDIAN);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 20;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   
   public final void testFilterNamedMatrixMaxFraction() {
      f.setLowCut(0.3, true);
      f.setRemoveAllNegative(false);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 20;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   
   public final void testFilterNamedMatrixFractionMaxRemoveNeg() {
      f.setLowCut(0.6, true);
      f.setRemoveAllNegative(true); // removes 3 of 30 rows. 27 * 0.6 =16.2, ceil 17, + 3 = 20.
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 10;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   public final void testFilterNamedMatrixBadFraction() {
      try {
         f.setLowCut(110.3, true);
         DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
         fail("Should have gotten an exception");
      } catch ( RuntimeException success ) {
      }
   }
   
   public final void testFilterNamedMatrixNoFilter() {
         DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
         assertEquals( "return value", testdata, filtered );
   }
   
   
   public final void testFilterNamedMatrixMaxHigh() {
      f.setHighCut(1000.0, false);
      f.setRemoveAllNegative(true); // irrelevant.
      f.setMethod(LevelFilter.MAX); // this is the default.
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 16;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   public final void testFilterNamedMatrixMinHigh() {
      f.setHighCut(1000.0, false);
      f.setRemoveAllNegative(true);
      f.setMethod(LevelFilter.MIN);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 26;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   public final void testFilterNamedMatrixMeanHigh() {
      f.setHighCut(1000.0, false);
      f.setRemoveAllNegative(true);
      f.setMethod(LevelFilter.MEAN);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 21;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   public final void testFilterNamedMatrixMedianHigh() {
      f.setHighCut(1000.0, false);
      f.setRemoveAllNegative(true); // irrelevant
      f.setMethod(LevelFilter.MEDIAN);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 20;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   
   public final void testFilterNamedMatrixMaxFractionHigh() {
      f.setHighCut(0.3, true);
      f.setRemoveAllNegative(false);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 20;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   
   public final void testFilterNamedMatrixFractionMaxRemoveNegHigh() {
      f.setHighCut(0.3, true);
      f.setRemoveAllNegative(true); 
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 10;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public final void testFilterNamedMatrixFractionMaxRemoveHighAndLow() {
      f.setHighCut(0.3, true);
      f.setLowCut(100, false);
      f.setRemoveAllNegative(true); 
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 10;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   


}
