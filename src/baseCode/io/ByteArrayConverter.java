/*
 * Created on Feb 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package baseCode.io;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;


/**
 * @author Kiran Keshav
 *
 * $Id$
 */
public class ByteArrayConverter {
   
   
   public byte[] DoubleArrayToBytes(double[] darray) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    for (int i=0;i<darray.length;i++){
       try{
          dos.writeDouble(darray[i]);
       }
       catch(Exception e){
          e.printStackTrace();
       }
    }
    return bos.toByteArray();
   }
   
   public double[] ByteArrayToDoubles(byte[] barray) {
    ByteArrayInputStream bis = new ByteArrayInputStream(barray);
    DataInputStream dis = new DataInputStream(bis);
    double [] darray = new double[barray.length/8];
    int i=0;
    boolean EOF=false;
      while(!EOF) {
         try{
            darray[i]=dis.readDouble();
            System.err.println("double["+i+"] = "+ darray[i]);
            i++;
         }
         catch(EOFException e){
            EOF=true;
            //e.printStackTrace();
         }
         catch(IOException e){
            EOF=true;
            e.printStackTrace();
         }
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
   

}
