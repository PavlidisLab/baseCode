/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.io;

/**
 * @author Kiran Keshav
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StringConverter {
    private String delimiter = "\t"; // it's a regexp.

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

    public void setDelimiter( String delimiter ) {
        this.delimiter = delimiter;
    }

    /**
     * @param sArray
     * @return double[]
     */
    public double[] stringArrayToDoubles( String[] sArray ) {
        double[] result = new double[sArray.length];
        for ( int i = 0; i < sArray.length; i++ ) {
            result[i] = Double.parseDouble( sArray[i] );
        }

        return result;
    }

    /**
     * @param stringToParse
     * @return double[]
     */
    public double[] stringToDoubles( String stringToParse ) {
        if ( stringToParse == null ) return null;
        String[] sArray = stringToParse.split( delimiter );
        return stringArrayToDoubles( sArray );
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