package baseCode.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

//import javax.sql.rowset.serial;

/**
 * $Id$
 */
public class TestByteArrayConverter extends TestCase {
   ByteArrayConverter bac;
   StringConverter sc;

   public double x = 424542.345;
   public double y = 25425.5652;
   public double z = 24524523.254;

   public int a = 424542;
   public int b = 25425;
   public int c = 24524523;

   public char u = 'k';
   public char v = 'i';
   public char w = 'r';

   double[] testD = new double[] {
         x, y, z
   };

   int[] testI = new int[] {
         a, b, c
   };

   char[] testC = new char[] {
         u, v, w
   };

   byte[] expectedBfD = new byte[] {
         65, 25, -23, 121, 97, 71, -82, 20, 64, -40, -44, 100, 44, 60, -98, -19, 65, 119, 99, 110, -76, 16, 98, 78
   };

   byte[] expectedBfI = new byte[] {
         0, 6, 122, 94, 0, 0, 99, 81, 1, 118
   };

   byte[] expectedBfC = new byte[] {
         0, 107, 0, 105, 0, 114
   };

   String longDoubleString = "";
   double[] wholeBunchOfDoubles;

   String filename = "C:\\Documents and Settings\\Kiran Keshav\\Desktop\\Neuro\\ross-nci60-data.txt";

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();

      bac = new ByteArrayConverter();
      sc = new StringConverter();

      InputStream is = TestByteArrayConverter.class.getResourceAsStream( "/data/melanoma_and_sarcomaMAS5.txt" );
      BufferedReader br = new BufferedReader( new InputStreamReader( is ) );

      StringBuffer buf = new StringBuffer();
      String line;
      br.readLine(); // ditch the first row.
      while ( ( line = br.readLine() ) != null ) {
         buf.append( line.split( "\t", 2 )[1] + "\t" ); // so we get a very long delimited string, albeit with a
                                                        // trailing tab.
      }

      longDoubleString = buf.toString();

      if ( longDoubleString == null ) {
         throw new IllegalStateException( "Couldn't setup string" );
      }

      wholeBunchOfDoubles = sc.StringToDoubles( longDoubleString );
      br.close();
      is.close();

   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
      longDoubleString = null;
      wholeBunchOfDoubles = null;
      bac = null;
      sc = null;
   }

   /**
    * 
    *
    */
   public void testDoubleArrayToBytes() {
      byte[] actualReturn = bac.doubleArrayToBytes( testD );
      byte[] expectedValue = expectedBfD;
      for ( int i = 0; i < expectedValue.length; i++ ) {
         assertEquals( "return value", expectedValue[i], actualReturn[i] );
      }
   }

   /**
    * 
    *
    */
      public void testByteArrayToDoubles() {
         double[] actualReturn = bac.byteArrayToDoubles( bac.doubleArrayToBytes(testD) );
         double[] expectedValue = testD;
         for (int i=0;i<actualReturn.length;i++){
           assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
         }
      }
   /**
    * Second Method to test the ByteArrayToDoubles. This is used for Benchmarking purposes.
    */

   //   public void testByteArrayToDoubles() {
   //// System.err.println(" CONVERTING BYTES TO DOUBLES ");
   //      double[] actualReturn = f.ByteArrayToDoubles( f.DoubleArrayToBytes(g.StringToDoubles(filename)));
   //      double[] expectedValue = actualReturn;
   //      for (int i=0;i<actualReturn.length;i++){
   //        //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new
   // Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
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
   //        //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new
   // Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
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
   //// System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new
   // Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
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
   //        //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new
   // Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
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
   //         //System.err.println("actualReturn:["+i+"]"+ actualReturn[i]);//new
   // Integer(actualReturn[i]).toBinaryString(actualReturn[i]));
   //        assertEquals( "return value", expectedValue[i], actualReturn[i], 0);
   //      }
   //   }

   // test blob -> double[]
   public void testByteArrayToDoubleConversionSpeed() {
      byte[] lottaBytes = bac.doubleArrayToBytes( wholeBunchOfDoubles );
      bac.byteArrayToDoubles( lottaBytes );
   }

   // test string -> double[]
   public void testStringToDoubleArrayConversionSpeed() {
      sc.StringToDoubles( longDoubleString );
   }

   // test double[] -> blob.
   public void testDoubleArrayToByteArrayConversionSpeed() {
      bac.doubleArrayToBytes( wholeBunchOfDoubles );
   }

   // test double[] -> delimited string.
   public void testDoubleArrayToDelimitedStringConversionSpeed() {
      sc.DoubleArrayToString( wholeBunchOfDoubles );
   }

}