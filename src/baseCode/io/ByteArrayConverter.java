package baseCode.io;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.time.StopWatch;


/**
 * @author Kiran Keshav
 *
 * $Id$
 */
public class ByteArrayConverter {
   public static int barraylength = 0;
   /**
    * 
    * @param darray
    * @return byte[]
    */
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
   /**
    * 
    * @param barray
    * @return double[]
    */
   public double[] ByteArrayToDoubles(byte[] barray) {
    ByteArrayInputStream bis = new ByteArrayInputStream(barray);
    DataInputStream dis = new DataInputStream(bis);
    double [] darray = new double[barray.length/8]; 
    int i=0;
    boolean EOF=false;
    StopWatch stopWatch = new StopWatch();
    int totalDoublesProcessed=0;
    stopWatch.start();
      while(!EOF) {
         try{
            darray[i]=dis.readDouble();
            //System.err.println("darray["+i+"]: "+darray[i]);
            i++;
            totalDoublesProcessed++;
         }
         catch(EOFException e){
            EOF=true;
         }
         catch(IOException e){
            EOF=true;
            e.printStackTrace();
         }
      }
      stopWatch.stop();
      System.out.println( " *** ByteArrayToDoubles *** ");
      System.out.println( "Total doubles processed = " + totalDoublesProcessed
            + " Time taken = " + stopWatch.getTime() + " ms" );
      return darray;     
   }
   /**
    * 
    * @param iarray
    * @return byte[]
    */
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
   /**
    * 
    * @param barray
    * @return int[]
    */
   public int[] ByteArrayToInts(byte[] barray) {
      ByteArrayInputStream bis = new ByteArrayInputStream(barray);
      DataInputStream dis = new DataInputStream(bis);
      int [] iarray = new int[barray.length/4];
      int i=0;
      boolean EOF=false;
        while(!EOF) {
           try{
              iarray[i]=dis.readInt();
              //System.err.println("int["+i+"] = "+ iarray[i]);
              i++;
           }
           catch(EOFException e){
              EOF=true;
           }
           catch(IOException e){
              EOF=true;
              e.printStackTrace();
           }
        }
        return iarray;     
   }
   /**
    * 
    * @param carray
    * @return byte[]
    */
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
   /**
    * 
    * @param barray
    * @return char[]
    */
   public char[] ByteArrayToChars(byte[] barray) {
      ByteArrayInputStream bis = new ByteArrayInputStream(barray);
      DataInputStream dis = new DataInputStream(bis);
      char [] carray = new char[barray.length/2];
      int i=0;
      boolean EOF=false;
        while(!EOF) {
           try{
              carray[i]=dis.readChar();
              //System.err.println("char["+i+"] = "+ carray[i]);
              i++;
           }
           catch(EOFException e){
              EOF=true;
           }
           catch(IOException e){
              EOF=true;
              e.printStackTrace();
           }
        }
        return carray;     
   }
   
   public String BytesToFile(byte[] barray, String outfile){ 
      int count = 0;
      barraylength = barray.length;
      try {
         File file = new File( outfile );
         FileOutputStream fos = new FileOutputStream( file );
         //while ( count < barray.length) {
            fos.write(barray);
            //System.err.println("barray:["+count+"]"+ barray[count]);
            //count++;
         //}
         fos.flush();
         fos.close();
      } catch ( Exception e ) {
         e.printStackTrace();
      }
      return outfile;
     }
   
   public byte[] FileToBytes(String outfile){ 
      byte [] barray = new byte[barraylength];
      try {
         File file = new File( outfile );
         FileInputStream fis = new FileInputStream( file );
         StopWatch stopWatch = new StopWatch();
         stopWatch.start();
         fis.read(barray);
         stopWatch.stop();
         fis.close();
         System.out.println( "\n *** Reading File of Bytes ***  ");
         System.out.println("Add this to the time taken for ByteArrayToDoubles."); 
         System.out.println( "Time taken = " + stopWatch.getTime() + " ms" );
      } catch ( Exception e ) {
         e.printStackTrace();
      }
      return barray;
     }

}
