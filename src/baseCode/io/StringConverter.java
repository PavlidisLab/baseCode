package baseCode.io;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;

/**
 * @author Kiran Keshav
 *
 * $Id$
 */
public class StringConverter {
   /**
    * 
    * @param filename
    * @return double[]
    */   
   public double[] StringToDoubles(String filename){ 

    BufferedReader br = null;
    String[] sArray = null;
    String line = null;
    boolean fileExists = false;
    int totalDoublesProcessed=0;
    double [] dArray = new double[446853];
    StopWatch stopWatch = new StopWatch(); 
    try {
       File file = new File( filename );
       br = new BufferedReader( new FileReader( file ) );
       fileExists = true;
    } catch ( Exception e ) {
       System.out.println( "Cannot find file." );
       fileExists = false;
    }    
    try{
       br.readLine();
       stopWatch.start();
	    while ( ( line = br.readLine() ) != null ) {
	       sArray = StringUtils.split( line, "\t" );
	       //DoubleArrayList vals = new DoubleArrayList( new double[sArray.length] );
	       for ( int i = 0; i < sArray.length; i++ ) {
	          if ( sArray[i].length() == 0 || sArray[i].equals( "" ) || sArray[i].equals( " " ) || sArray[i].equals( "384(92H11)" )) {
	             //vals.setQuick( i, Double.NaN );
	             dArray[totalDoublesProcessed] = Double.NaN;
	             
	          } else {
	             //vals.setQuick( i, Double.parseDouble( sArray[i] ) );
	             dArray[totalDoublesProcessed] = Double.parseDouble( sArray[i] );
	          }
	          //System.err.println("dArray["+totalDoublesProcessed+"]: "+dArray[totalDoublesProcessed]);
	          totalDoublesProcessed++;
	          
	       }
	    }
	    stopWatch.stop();
	    br.close();
	    System.out.println( " *** StringToDoubles *** ");
	    System.out.println( "Total doubles processed = " + totalDoublesProcessed
              + " Time taken = " + stopWatch.getTime() + " ms" );
    }
    catch(IOException e){
       e.printStackTrace();
    }
    return dArray;
   }
   /**
    * 
    * @param filename
    * @return byte[]
    */
   public byte[] StringToBytes(String filename){ 
      
      BufferedReader br = null;
      String line = null;
      boolean fileExists = false;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(bos);
      try {
         File file = new File( filename );
         br = new BufferedReader( new FileReader( file ) );
         fileExists = true;
      } catch ( Exception e ) {
         System.out.println( "Cannot find file." );
         fileExists = false;
      }
      StopWatch stopWatch = new StopWatch(); 
      try{
         br.readLine();
        stopWatch.start();
  	    while ( ( line = br.readLine() ) != null ) {
  	       dos.writeBytes(line);
  	    }
  	    stopWatch.stop();
  	    br.close();
//  	    System.out.println( "Total lines processed = " + totalLinesProcessed
//                + " Time taken = " + stopWatch.getTime() + " ms" );
      }
      catch(IOException e){
         e.printStackTrace();
      }
      return bos.toByteArray();
     }
}
