/*
 * Created on Feb 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package baseCode.io;
import java.io.ByteArrayOutputStream;

/**
 * @author Kiran Keshav
 *
 * $Id$
 */
public class ByteArrayConverter {
   
   
   public byte[] DoubleArrayToBytes(double[] darray) {
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] barray = new byte[darray.length];
      for(int i = 0; i<barray.length; i++ ) {
         barray[i]=(new Double(darray[i])).byteValue();
      }
      return barray;
      //return null;
   }
   
   public double[] ByteArrayToDoubles(byte[] barray) {
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
      double [] darray = new double[barray.length];
      for(int i = 0; i<barray.length; i++ ) {
         darray[i]=(new Byte(barray[i])).doubleValue();
      }
      return darray;     
      //return null;
   }
   
   public byte[] IntArrayToBytes(int[] iarray) {
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] barray = new byte[iarray.length];
      for(int i = 0; i<barray.length; i++ ) {
         barray[i]=(new Integer(iarray[i])).byteValue();
      }
      return barray;
      //return null;
   }
   
   public int[] ByteArrayToInts(byte[] barray) {
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int [] iarray = new int[barray.length];
      for(int i = 0; i<barray.length; i++ ) {
         iarray[i]=(new Byte(barray[i])).intValue();
      }
      return iarray;     
      //return null;
   }
   public byte[] CharArrayToBytes(char[] carray) {
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] barray = new byte[carray.length];
      //convert character array to a String
      String s = new String(carray);
      //use the String's getBytes to encode String into a sequence of bytes.
      barray = s.getBytes();
      return barray;
   }
   
//   public int[] ByteArrayToInts(byte[] barray) {
////    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//      int [] iarray = new int[barray.length];
//      for(int i = 0; i<barray.length; i++ ) {
//         iarray[i]=(new Byte(barray[i])).intValue();
//      }
//      return iarray;     
//      //return null;
//   }
}
