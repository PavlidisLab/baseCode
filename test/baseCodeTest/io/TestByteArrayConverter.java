package baseCodeTest.io;
/*
 * Created on Feb 23, 2005 TODO To change the template for this generated file go to Window - Preferences - Java - Code
 * Style - Code Templates
 */


import baseCode.io.ByteArrayConverter;
import junit.framework.TestCase;
import java.lang.Double;
import javax.sql.*;
//import javax.sql.rowset.serial;

/**
 * $Id$
 */
public class TestByteArrayConverter extends TestCase {
   ByteArrayConverter f;
   
   public double x = 424542.345;
   public double y = 25425.5652;
   public double z = 24524523.254;   
   
   
   double[] testD = new double[]{x, y, z}; 
           
   byte[] expectedValue = new byte[]{65,25,-23,121,97,71,-82,20,64,-40,
         -44,100,44,60,-98,-19,65,119,99,110,-76,16,98,78};

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      f = new ByteArrayConverter();
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testDoubleArrayToBytes() {
      System.err.println(" CONVERTING DOUBLES TO BYTES ");
      byte[] actualReturn = f.DoubleArrayToBytes( testD );
      //byte[] expectedValue = f.DoubleArrayToBytes( testD );
      for(int i=0;i<expectedValue.length;i++){
         //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i]));   
       assertEquals( "return value",expectedValue[i],actualReturn[i]);
      }
   }
   
   public void testByteArrayToDoubles() {
      System.err.println(" CONVERTING BYTES TO DOUBLES ");
      double[] actualReturn = f.ByteArrayToDoubles( f.DoubleArrayToBytes(testD) );
      double[] expectedValue = testD;
      for (int i=0;i<actualReturn.length;i++){
        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
      }
   }
   
//   public void testIntArrayToBytes() {
//      System.err.println("converting ints to bytes ");
//      byte[] actualReturn = f.IntArrayToBytes( testI );
//      byte[] expectedValue = new byte[]{a,b,c}; 
//      for(int i=0;i<expectedValue.length;i++){
//       assertEquals( "return value",expectedValue[i],actualReturn[i]);
//      }
//   }
//   
//   public void testByteArrayToInts() {
//      System.err.println("converting bytes to ints ");
//      int[] actualReturn = f.ByteArrayToInts( testB );
//      int[] expectedValue = new int[]{u,v,w};
//      for (int i=0;i<expectedValue.length;i++){
//        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
//      }
//   }  
//   
//   public void testCharArrayToBytes() {
//      System.err.println("converting chars to bytes ");
//      byte[] actualReturn = f.CharArrayToBytes( testC );
//      byte[] expectedValue = new String(testC).getBytes(); 
//      for(int i=0;i<expectedValue.length;i++){
//       assertEquals( "return value",expectedValue[i],actualReturn[i]);
//      }
//   }
//   
//   public void testByteArrayToChars() {
//      System.err.println("converting bytes to chars ");
//      double[] actualReturn = f.ByteArrayToDoubles( testB );
//      double[] expectedValue = new double[]{x,y,z};
//      for (int i=0;i<expectedValue.length;i++){
//        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
//      }
//   }

}
   