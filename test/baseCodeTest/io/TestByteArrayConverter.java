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
 * @author Kiran Keshav TODO To change the template for this generated type comment go to Window - Preferences - Java -
 *         Code Style - Code Templates
 */
public class TestByteArrayConverter extends TestCase {
   ByteArrayConverter f;
   public int u = 1;
   public int v = 2;
   public int w = 3;  
   public double x = 1.0;
   public double y = 2.0;
   public double z = 3.0;   
   public byte a = 1;
   public byte b = 2;
   public byte c = 3;
   
   double[] testD = new double[]{x, y, z}; 
         
   byte[] testB = new byte[] {a, b, c};
   
   int[] testI = new int[]{u, v, w};
   
   char[] testC = new char[] {'a','b','c'};

   

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
      System.err.println("converting doubles to bytes ");
      byte[] actualReturn = f.DoubleArrayToBytes( testD );
      byte[] expectedValue = new byte[]{a,b,c}; 
      for(int i=0;i<expectedValue.length;i++){
       assertEquals( "return value",expectedValue[i],actualReturn[i]);
      }
   }
   
   public void testByteArrayToDoubles() {
      System.err.println("converting bytes to doubles");
      //double[] actualReturn = f.ByteArrayToDoubles( f.DoubleArrayToBytes (testD) );
      double[] actualReturn = f.ByteArrayToDoubles( testB );
      double[] expectedValue = new double[]{x,y,z};
      for (int i=0;i<expectedValue.length;i++){
        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
      }
   }
   
   public void testIntArrayToBytes() {
      System.err.println("converting ints to bytes ");
      byte[] actualReturn = f.IntArrayToBytes( testI );
      byte[] expectedValue = new byte[]{a,b,c}; 
      for(int i=0;i<expectedValue.length;i++){
       assertEquals( "return value",expectedValue[i],actualReturn[i]);
      }
   }
   
   public void testByteArrayToInts() {
      System.err.println("converting bytes to ints ");
      int[] actualReturn = f.ByteArrayToInts( testB );
      int[] expectedValue = new int[]{u,v,w};
      for (int i=0;i<expectedValue.length;i++){
        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
      }
   }  
   
   public void testCharArrayToBytes() {
      System.err.println("converting chars to bytes ");
      byte[] actualReturn = f.CharArrayToBytes( testC );
      byte[] expectedValue = new String(testC).getBytes(); 
      for(int i=0;i<expectedValue.length;i++){
       assertEquals( "return value",expectedValue[i],actualReturn[i]);
      }
   }
   
   public void testByteArrayToChars() {
      System.err.println("converting bytes to chars ");
      double[] actualReturn = f.ByteArrayToDoubles( testB );
      double[] expectedValue = new double[]{x,y,z};
      for (int i=0;i<expectedValue.length;i++){
        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
      }
   }

}
   