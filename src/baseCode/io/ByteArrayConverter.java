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
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(bos);
      for (int i=0;i<iarray.length;i++){
         try{
            dos.writeInt(iarray[i]);
         }
         catch(Exception e){
            e.printStackTrace();
         }
      }
      return bos.toByteArray();
   }
   
   public int[] ByteArrayToInts(byte[] barray) {
      ByteArrayInputStream bis = new ByteArrayInputStream(barray);
      DataInputStream dis = new DataInputStream(bis);
      int [] iarray = new int[barray.length/4];
      int i=0;
      boolean EOF=false;
        while(!EOF) {
           try{
              iarray[i]=dis.readInt();
              System.err.println("int["+i+"] = "+ iarray[i]);
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
        return iarray;     
   }
   
   public byte[] CharArrayToBytes(char[] carray) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(bos);
      for (int i=0;i<carray.length;i++){
         try{
            dos.writeChar(carray[i]);
         }
         catch(Exception e){
            e.printStackTrace();
         }
      }
      return bos.toByteArray();
   }
   public char[] ByteArrayToChars(byte[] barray) {
      ByteArrayInputStream bis = new ByteArrayInputStream(barray);
      DataInputStream dis = new DataInputStream(bis);
      char [] carray = new char[barray.length/2];
      int i=0;
      boolean EOF=false;
        while(!EOF) {
           try{
              carray[i]=dis.readChar();
              System.err.println("char["+i+"] = "+ carray[i]);
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
        return carray;     
   }

}
