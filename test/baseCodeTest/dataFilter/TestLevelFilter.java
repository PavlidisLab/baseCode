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
   public final void testFilterNamedMatrix() {
      f.setLowCut(100.0, false);
      f.setRemoveAllNegative(true);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 26;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   public final void testFilterNamedMatrixFraction() {
      f.setLowCut(0.3, true);
      f.setRemoveAllNegative(false);
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 20;
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   
   public final void testFilterNamedMatrixFractionRemoveNeg() {
      f.setLowCut(0.6, true);
      f.setRemoveAllNegative(true); // removes 3 of 30 rows. 27 * 0.4 = 10.8. 
      DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)f.filter(testdata);
      int actualReturn = filtered.rows();
      int expectedReturn = 11;
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


}
