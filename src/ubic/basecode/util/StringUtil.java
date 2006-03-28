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
package ubic.basecode.util;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;
import au.com.bytecode.opencsv.CSVReader;

/**
 * @author pavlidis
 * @version $Id$
 */
public class StringUtil {

    /**
     * @param numFields
     * @param line
     * @return
     */
    public static String[] csvSplit( String line ) {

        CSVReader reader = new CSVReader( new StringReader( line ) );

        try {
            return reader.readNext();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Method to wrap lines. It attempts to wrap on whitespace - it will not break within a word.
     * 
     * @param string
     * @param charPerLine
     * @param delim The string to put at the end of each new line. For example, "\n" or "&lt;br /&gt;";
     * @return
     */
    public static String wrap( String string, int charPerLine, String delim ) {
        if ( string == null || string.length() <= charPerLine ) return string;
        StringBuffer buf = new StringBuffer();
        char[] chars = string.toCharArray();
        int lastLineCount = 0;
        boolean needDelim = false;
        for ( int i = 0; i < chars.length; i++ ) {
            char c = chars[i];
            buf.append( c );
            lastLineCount++;

            if ( needDelim ) {
                if ( ( c == ' ' || c == '\t' || c == '\n' ) ) {
                    buf.append( delim );
                    lastLineCount = 0;
                    needDelim = false;
                    continue;
                }
            }

            if ( i > 0 && lastLineCount >= charPerLine ) {
                if ( ( c == ' ' || c == '\t' || c == '\n' ) ) {
                    buf.append( delim );
                    lastLineCount = 0;
                    needDelim = false;
                } else {
                    needDelim = true;
                }
            }
        }
        return buf.toString();
    }

    /**
     * Convert "Property" to "property"
     * 
     * @param input
     * @return the input with the first character in lowercase.
     */
    public static String lowerCaseFirstLetter( String input ) {
        return ( new String( new char[] { input.charAt( 0 ) } ) ).toLowerCase() + input.substring( 1 );
    }

    /**
     * Convert "property" to "Property"
     * 
     * @param input
     * @return the input with the first character in upper case..
     */
    public static String upperCaseFirstLetter( String input ) {
        return ( new String( new char[] { input.charAt( 0 ) } ) ).toUpperCase() + input.substring( 1 );
    }

    /**
     * @param stringi
     * @param stringj
     * @return
     */
    public static Object twoStringHashKey( String stringi, String stringj ) {
        // use arbitrary but consistent method for ordering.
        if ( stringi.hashCode() < stringj.hashCode() ) {
            return new Long( stringi.hashCode() | ( long ) stringj.hashCode() << 32 );
        }
        return new Long( stringj.hashCode() | ( long ) stringi.hashCode() << 32 );

    }

    /**
     * @param str the String to parse, may be <code>null</code>
     * @param separatorChar the separate character
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] splitPreserveAllTokens( String str, char separatorChar ) {

        return StringUtils.splitPreserveAllTokens( str, separatorChar );
    }
}
