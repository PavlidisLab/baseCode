package baseCode.io;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Kiran Keshav *
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StringConverter {

   private String delimiter = "\t"; // it's a regexp.

   public void setDelimiter( String delimiter ) {
      this.delimiter = delimiter;
   }

   /**
    * @param stringToParse
    * @return double[]
    */
   public double[] StringToDoubles( String stringToParse ) {
      
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      String[]  sArray = stringToParse.split( delimiter );
      double[] result = new double[sArray.length];
      for ( int i = 0; i < sArray.length; i++ ) {
         result[i] = Double.parseDouble( sArray[i] );
      }

      stopWatch.stop();

      System.err.println( "StringToDoubles: Total doubles processed = " + result.length + " Time taken = "
            + stopWatch.getTime() + " ms" );

      return result;
   }

   /**
    * Convert a double array to a delimited string.
    * 
    * @param arrayToConvert
    * @return
    */
   public String DoubleArrayToString( double[] arrayToConvert ) {
      StringBuffer buf = new StringBuffer();

      StopWatch stopWatch = new StopWatch();
      stopWatch.start();

      for ( int i = 0; i < arrayToConvert.length; i++ ) {
         buf.append( arrayToConvert[i] );
         if ( i > 0 ) buf.append( delimiter );
      }

      stopWatch.stop();

      System.err.println( "DoubleArrayToString: Total doubles processed = " + arrayToConvert.length + " Time taken = "
            + stopWatch.getTime() + " ms" );

      return buf.toString();
   }

   /**
    * FIXME this is broken.
    * 
    * @param stringToConvert
    * @return byte[]
    */
   public byte[] StringArrayToBytes( String[] stringsToConvert ) {

      for ( int i = 0; i < stringsToConvert.length; i++ ) {
         String s = stringsToConvert[i];
      }

      return null;

   }
}