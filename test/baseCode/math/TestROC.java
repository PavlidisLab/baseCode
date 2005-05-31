package baseCode.math;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestROC extends TestCase {

   Set ranksOfPositives;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      DoubleArrayList m = new DoubleArrayList( new double[] {} );

      IntArrayList ranks = Rank.rankTransform( m );

      // set up the ranks of the opsitives
      ranksOfPositives = new HashSet();
      ranksOfPositives.add( new Integer( 0 ) );
      ranksOfPositives.add( new Integer( 3 ) );
      ranksOfPositives.add( new Integer( 5 ) );
   }

   public void testAroc() {
      double actualReturn = ROC.aroc( 10, ranksOfPositives );
      double expectedReturn = ( 21.0 - 5.0 ) / 21.0;
      assertEquals( "return value", expectedReturn, actualReturn, 0.00001 );
   }
   
   public void testArocN() {
      double actualReturn = ROC.aroc( 10, ranksOfPositives, 2 );
      double expectedReturn = 2.0/6.0;
      assertEquals( "return value", expectedReturn, actualReturn, 0.00001 );
   }

}