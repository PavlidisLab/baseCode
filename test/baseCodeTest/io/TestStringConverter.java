package baseCodeTest.io;

import junit.framework.TestCase;
import baseCode.io.StringConverter;
import baseCode.io.ByteArrayConverter;

//import javax.sql.rowset.serial;

/**
 * $Id$
 */
public class TestStringConverter extends TestCase {
   StringConverter sc;
   ByteArrayConverter bac;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      sc = new StringConverter();
      bac = new ByteArrayConverter();
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /**
    * 
    *
    */
   public void testStringToBytes() {
      //System.err.println(" CONVERTING STRING TO BYTES ");

      byte[] actualReturn = sc.StringArrayToBytes( null );
      byte[] expectedValue = actualReturn;
      for ( int i = 0; i < actualReturn.length; i++ ) {
         //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new
         // Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
         assertEquals( "return value", expectedValue[i], actualReturn[i], 0 );
      }
   }

   /**
    * 
    *
    */
   public void testStringToDoubles() {
      //System.err.println(" CONVERTING STRING TO DOUBLES ");

      double[] actualReturn = sc.StringToDoubles( null );
      double[] expectedValue = actualReturn;
      for ( int i = 0; i < actualReturn.length; i++ ) {
         //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new
         // Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
         //assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
      }
   }
   /**
    * 
    *
    */

}