package baseCodeTest.math;

import baseCode.math.SpecFunc;
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
public class TestSpecFunc extends TestCase {

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

   public final void testHypergeometric() {
      //hypergeometric( int positives, int successes,
      //     int negatives, int failures )

      // dhyper takes : successes, positives, negatives, trials
      //  dhyper(2, 20, 100, 50);
      //  [1] 0.0009644643
      double expectedReturn = 0.0009644643;
      double actualReturn = SpecFunc.hypergeometric( 20, 2, 100, 48 );
      assertEquals( expectedReturn, actualReturn, 1e-5 );
   }

   public final void testCumHyperGeometric() {
      //      phyper(2, 20, 100, 50);
      //      [1] 0.001077697
      double expectedReturn = 0.001077697;
      double actualReturn = SpecFunc.cumHyperGeometric( 20, 2, 100, 48 );
      assertEquals( expectedReturn, actualReturn, 1e-5 );
   }

}