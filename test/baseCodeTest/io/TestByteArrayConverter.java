package baseCodeTest.io;

import junit.framework.TestCase;
import baseCode.io.ByteArrayConverter;
import baseCode.io.StringConverter;
//import javax.sql.rowset.serial;

/**
 * $Id$
 */
public class TestByteArrayConverter extends TestCase {
   ByteArrayConverter g;
   StringConverter f;
   
   public double x = 424542.345;
   public double y = 25425.5652;
   public double z = 24524523.254; 
   
   public int a = 424542;
   public int b = 25425;
   public int c = 24524523;   
   
   public char u = 'k';
   public char v = 'i';
   public char w = 'r';
 
   double[] testD = new double[]{x, y, z}; 
   
   int [] testI = new int[]{a,b,c};
   
   char[] testC = new char[]{u, v, w}; 
           
   byte[] expectedBfD = new byte[]{65,25,-23,121,97,71,-82,20,64,-40,
         -44,100,44,60,-98,-19,65,119,99,110,-76,16,98,78};
   
   byte[] expectedBfI = new byte[]{0,6,122,94,0,0,99,81,1,118};
   
   byte[] expectedBfC = new byte[]{0,107,0,105,0,114};
   
   String filename = "C:\\Documents and Settings\\Kiran Keshav\\Desktop\\Neuro\\ross-nci60-data.txt";
   

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      g = new ByteArrayConverter();
      f = new StringConverter();
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
//   public void testDoubleArrayToBytes() {
//      //System.err.println(" CONVERTING DOUBLES TO BYTES ");
//      byte[] actualReturn = f.DoubleArrayToBytes( testD );
//      byte[] expectedValue = expectedBfD;
//      for(int i=0;i<expectedValue.length;i++){
//         //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i]));   
//       assertEquals( "return value",expectedValue[i],actualReturn[i]);
//      }
//   }
     /**
      * 
      *
      */
//   public void testByteArrayToDoubles() {
//      System.err.println(" CONVERTING BYTES TO DOUBLES ");
//      double[] actualReturn = f.ByteArrayToDoubles( f.DoubleArrayToBytes(testD) );
//      double[] expectedValue = testD;
//      for (int i=0;i<actualReturn.length;i++){
//        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
//      }
//   }
   /**
    * Second Method to test the ByteArrayToDoubles.  This is used 
    * for Benchmarking purposes.
    */
    
//   public void testByteArrayToDoubles() {
////    System.err.println(" CONVERTING BYTES TO DOUBLES ");
//      double[] actualReturn = f.ByteArrayToDoubles( f.DoubleArrayToBytes(g.StringToDoubles(filename)));
//      double[] expectedValue = actualReturn;
//      for (int i=0;i<actualReturn.length;i++){
//        //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i])); 
//        //assertEquals( "return value", expectedValue[i], actualReturn[i], Double.NaN);
//      }
//   }
   
   /**
    * 
    *
    */
//   public void testIntArrayToBytes() {
//      //System.err.println(" CONVERTING INTS TO BYTES ");
//      byte[] actualReturn = f.IntArrayToBytes( testI );
//      byte[] expectedValue = expectedBfI;
//      for(int i=0;i<expectedValue.length;i++){
//        //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i]));   
//       assertEquals( "return value",expectedValue[i],actualReturn[i]);
//      }
//   }
   /**
    * 
    *
    */
//   public void testByteArrayToInts() {
//      //System.err.println(" CONVERTING BYTES TO INTS");
//      int[] actualReturn = f.ByteArrayToInts( f.IntArrayToBytes(testI) );
//      int[] expectedValue = testI;
//      for (int i=0;i<expectedValue.length;i++){
////       System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
//        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
//      }
//   }
   /**
    * 
    *
    */
//   public void testCharArrayToBytes() {
//      //System.err.println(" CONVERTING CHARS TO BYTES ");
//      byte[] actualReturn = f.CharArrayToBytes( testC );
//      byte[] expectedValue = expectedBfC;
//      for(int i=0;i<expectedValue.length;i++){
//        //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i]));   
//       assertEquals( "return value",expectedValue[i],actualReturn[i]);
//      }
//   }
   /**
    * 
    *
    */
//   public void testByteArrayToChars() {
//      //System.err.println(" CONVERTING BYTES TO INTS");
//      char[] actualReturn = f.ByteArrayToChars( f.CharArrayToBytes(testC) );
//      char[] expectedValue = testC;
//      for (int i=0;i<expectedValue.length;i++){
//         //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new Integer(actualReturn[i]).toBinaryString(actualReturn[i])); 
//        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
//      }
//   }  
// public void testBytesToFile() {
// //System.err.println(" CONVERTING BYTES TO FILE ");
// String filename = "C:\\Documents and Settings\\Kiran Keshav\\Desktop\\Neuro\\ross-nci60-data.txt";
// String outfile = "C:\\Documents and Settings\\Kiran Keshav\\Desktop\\Neuro\\out.txt";
// f.BytesToFile(g.DoubleArrayToBytes(f.StringToDoubles(filename)), outfile );
//}
/**
* 
*
*/
public void testFileToBytes() {
 //System.err.println(" CONVERTING FILE TO BYTES ");
 String filename = "C:\\Documents and Settings\\Kiran Keshav\\Desktop\\Neuro\\ross-nci60-data.txt";
 String outfile = "C:\\Documents and Settings\\Kiran Keshav\\Desktop\\Neuro\\out.txt";
 g.ByteArrayToDoubles(g.FileToBytes(g.BytesToFile(g.DoubleArrayToBytes(f.StringToDoubles(filename)), outfile )));
}
}
   