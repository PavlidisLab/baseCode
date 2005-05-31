package baseCode.math;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;

/**
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestRank extends TestCase {

   DoubleArrayList testdata = null;
   Map testmap = null;
   Map testbadmap = null;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      testdata = new DoubleArrayList( new double[] { 10.0, 11.0, 12.0, 13.0,
            114.0, 5.0 } );
      testmap = new HashMap();
      testmap.put( "Ten", new Double( 10.0 ) );
      testmap.put( "Eleven", new Double( 11.0 ) );
      testmap.put( "Twelve", new Double( 12.0 ) );
      testmap.put( "Thirteen", new Double( 13.0 ) );
      testmap.put( "HundredFourteen", new Double( 114.0 ) );
      testmap.put( "Five", new Double( 5.0 ) );
      testbadmap = new HashMap();
      testbadmap.put( "Ten", "I am not a Double" );
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
      testdata = null;
      testmap = null;
      testbadmap = null;
   }

   /*
    * Class under test for DoubleArrayList rankTransform(DoubleArrayList)
    */
   public void testRankTransformDoubleArrayList() {
      IntArrayList actualReturn = Rank.rankTransform( testdata );
      IntArrayList expectedReturn = new IntArrayList( new int[] { 1 ,
            2 , 3 , 4 , 5 , 0 } );
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testRankTransformMap() {

      Map actualReturn = Rank.rankTransform( testmap );
      Map expectedReturn = new HashMap();
      expectedReturn.put( "Ten", new Integer( 1 ) );
      expectedReturn.put( "Eleven", new Integer( 2 ) );
      expectedReturn.put( "Twelve", new Integer( 3 ) );
      expectedReturn.put( "Thirteen", new Integer( 4 ) );
      expectedReturn.put( "HundredFourteen", new Integer( 5 ) );
      expectedReturn.put( "Five", new Integer( 0 ) );
      assertEquals( "return value", expectedReturn, actualReturn );

   } /*
      * Class under test for Map rankTransform(Map)
      */

   public void testRankTransformBadMap() {
      try {
         Rank.rankTransform( testbadmap );
         fail( "Should have generated an exception" );
      } catch ( IllegalArgumentException success ) {
      }
   }

}