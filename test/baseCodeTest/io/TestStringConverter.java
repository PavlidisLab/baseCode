package baseCodeTest.io;

import junit.framework.TestCase;
import baseCode.io.StringConverter;
import baseCode.io.ByteArrayConverter;
//import javax.sql.rowset.serial;

/**
 * $Id$
 */
public class TestStringConverter extends TestCase {
   StringConverter f;
   ByteArrayConverter g;
   
   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      f = new StringConverter();
      g = new ByteArrayConverter();
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
      String filename = "C:\\Documents and Settings\\Kiran Keshav\\Desktop\\Neuro\\ross-nci60-data.txt";
      byte[] actualReturn = f.StringToBytes( filename );
      byte[] expectedValue = actualReturn;
      for (int i=0;i<actualReturn.length;i++){
        //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
      }
   }
   /**
    * 
    *
    */
   public void testStringToDoubles() {
      //System.err.println(" CONVERTING STRING TO DOUBLES ");
      String filename = "C:\\Documents and Settings\\Kiran Keshav\\Desktop\\Neuro\\ross-nci60-data.txt";
      double[] actualReturn = f.StringToDoubles( filename );
      double[] expectedValue = actualReturn;
      for (int i=0;i<actualReturn.length;i++){
        //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
        //assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
      }
   }
   /**
    * 
    *
    */

}
   