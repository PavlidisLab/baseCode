package baseCodeTest.math;

import java.util.HashSet;
import java.util.Set;

import baseCode.math.ROC;
import baseCode.math.Rank;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import junit.framework.TestCase;

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

}