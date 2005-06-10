package baseCode.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import org.apache.commons.lang.time.StopWatch;

/**
 * @author Kiran Keshav
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StringConverter {
    private static Log log = LogFactory.getLog( StringConverter.class.getName() );
    private String delimiter = "\t"; // it's a regexp.

    public void setDelimiter( String delimiter ) {
        this.delimiter = delimiter;
    }

    /**
     * @param stringToParse
     * @return double[]
     */
    public double[] stringToDoubles( String stringToParse ) {
        if ( stringToParse == null ) return null;
        String[] sArray = stringToParse.split( delimiter );
        double[] result = new double[sArray.length];
        for ( int i = 0; i < sArray.length; i++ ) {
            result[i] = Double.parseDouble( sArray[i] );
        }

        return result;
    }

    /**
     * Convert a double array to a delimited string.
     * 
     * @param arrayToConvert
     * @return
     */
    public String doubleArrayToString( double[] arrayToConvert ) {
        if ( arrayToConvert == null ) return null;
        StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < arrayToConvert.length; i++ ) {
            buf.append( arrayToConvert[i] );
            if ( i > 0 ) buf.append( delimiter );
        }

        return buf.toString();
    }

    // /**
    // * FIXME this is broken.
    // *
    // * @param stringToConvert
    // * @return byte[]
    // */
    // public byte[] stringArrayToBytes( String[] stringsToConvert ) {
    // if ( stringsToConvert == null ) return null;
    // for ( int i = 0; i < stringsToConvert.length; i++ ) {
    // String s = stringsToConvert[i];
    // }
    // return null;
    // }
}