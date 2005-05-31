package baseCode.dataFilter;

import baseCode.dataStructure.matrix.DenseDoubleMatrix2DNamed;

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
public class TestItemLevelFilter extends AbstractTestFilter {

   ItemLevelFilter f = null;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      f = new ItemLevelFilter();
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();

   }

   public final void testFilter() {
      f.setLowCut( 0.0 );
      DenseDoubleMatrix2DNamed result = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int expectedReturn = 283;
      int actualReturn = result.size() - result.numMissing();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

}