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
 * <p>
 * Class to convert byte arrays (e.g., Blobs) to and from other types of arrays.
 * <hr>
 * 
 * @author Kiran Keshav
 * @version $Id$
 */
public class ByteArrayConverter {

   private static final int DOUBLE_SIZE = 8;
   private static final int INT_SIZE = 4;
   private static final int CHAR_SIZE = 2;

   /**
    * @param darray
    * @return byte[]
    */
   public byte[] DoubleArrayToBytes( double[] darray ) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream( bos );
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      try {

         for ( int i = 0; i < darray.length; i++ ) {
            dos.writeDouble( darray[i] );
         }

      } catch ( IOException e ) {
         e.printStackTrace();
      }
      stopWatch.stop();
      System.err.println( "DoubleArrayToBytes: Total doubles processed = " + darray.length + " Time taken = " + stopWatch.getTime()
            + " ms" );
      return bos.toByteArray();
   }

   /**
    * @param barray
    * @return double[]
    */
   public double[] ByteArrayToDoubles( byte[] barray ) {
      ByteArrayInputStream bis = new ByteArrayInputStream( barray );
      DataInputStream dis = new DataInputStream( bis );

      double[] darray = new double[barray.length / DOUBLE_SIZE];
      int i = 0;

      StopWatch stopWatch = new StopWatch();
      stopWatch.start();

      try {
         while ( true ) {
            darray[i] = dis.readDouble();
            i++;
         }

      } catch ( IOException e ) {
         // do nothing.
      }

      try {
         bis.close();
      } catch ( IOException e1 ) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

      stopWatch.stop();
      System.err.println( "ByteArrayToDoubles: Total doubles processed = " + i + " Time taken = " + stopWatch.getTime()
            + " ms" );
      return darray;
   }

   /**
    * @param iarray
    * @return byte[]
    */
   public byte[] IntArrayToBytes( int[] iarray ) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream( bos );
      try {
         for ( int i = 0; i < iarray.length; i++ ) {
            dos.writeInt( iarray[i] );
         }
         dos.close();
         bos.close();
      } catch ( IOException e ) {
         // do nothing
      }
      return bos.toByteArray();
   }

   /**
    * @param barray
    * @return int[]
    */
   public int[] ByteArrayToInts( byte[] barray ) {
      ByteArrayInputStream bis = new ByteArrayInputStream( barray );
      DataInputStream dis = new DataInputStream( bis );
      int[] iarray = new int[barray.length / INT_SIZE];
      int i = 0;

      try {
         while ( true ) {
            iarray[i] = dis.readInt();
            i++;
         }

      } catch ( IOException e ) {
         // do nothing.
      }

      try {
         dis.close();
         bis.close();
      } catch ( IOException e1 ) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

      return iarray;
   }

   /**
    * @param carray
    * @return byte[]
    */
   public byte[] CharArrayToBytes( char[] carray ) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream( bos );

      try {

         for ( int i = 0; i < carray.length; i++ ) {
            dos.writeChar( carray[i] );
         }

         dos.close();
         bos.close();

      } catch ( IOException e ) {

      }

      return bos.toByteArray();
   }

   /**
    * @param barray
    * @return char[]
    */
   public char[] ByteArrayToChars( byte[] barray ) {
      ByteArrayInputStream bis = new ByteArrayInputStream( barray );
      DataInputStream dis = new DataInputStream( bis );
      char[] carray = new char[barray.length / CHAR_SIZE];
      int i = 0;
      boolean EOF = false;
      while ( !EOF ) {
         try {
            carray[i] = dis.readChar();
            i++;
         } catch ( EOFException e ) {
            EOF = true;
         } catch ( IOException e ) {
            EOF = true;
            e.printStackTrace();
         }
      }

      try {
         dis.close();
         bis.close();
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      return carray;
   }

   //   public String BytesToFile( byte[] barray, String outfile ) {
   //      int count = 0;
   //      int barraylength = barray.length;
   //      try {
   //         File file = new File( outfile );
   //         FileOutputStream fos = new FileOutputStream( file );
   //         //while ( count < barray.length) {
   //         fos.write( barray );
   //         //System.err.println("barray:["+count+"]"+ barray[count]);
   //         //count++;
   //         //}
   //         fos.flush();
   //         fos.close();
   //      } catch ( Exception e ) {
   //         e.printStackTrace();
   //      }
   //      return outfile;
   //   }
   //
   //   public byte[] FileToBytes( String outfile ) {
   //      byte[] barray = new byte[barraylength];
   //      try {
   //         File file = new File( outfile );
   //         FileInputStream fis = new FileInputStream( file );
   //         StopWatch stopWatch = new StopWatch();
   //         stopWatch.start();
   //         fis.read( barray );
   //         stopWatch.stop();
   //         fis.close();
   //         System.out.println( "\n *** Reading File of Bytes *** " );
   //         System.out.println( "Add this to the time taken for ByteArrayToDoubles." );
   //         System.out.println( "Time taken = " + stopWatch.getTime() + " ms" );
   //      } catch ( Exception e ) {
   //         e.printStackTrace();
   //      }
   //      return barray;
   //   }

}