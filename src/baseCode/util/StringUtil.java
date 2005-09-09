package baseCode.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.Ostermiller.util.CSVParser;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class StringUtil {

    /**
     * @param numFields
     * @param line
     * @return
     */
    public static String[] csvSplit( int numFields, String line ) {
        String[][] parsedFields = CSVParser.parse( line );
        return parsedFields[0];
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
     * Borrowed from apache commons-lang-2.1, which clashes with something in our build environment (this is
     * "splitworker")
     * 
     * @param str the String to parse, may be <code>null</code>
     * @param separatorChar the separate character
     * @param preserveAllTokens if <code>true</code>, adjacent separators are treated as empty token separators; if
     *        <code>false</code>, adjacent separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] splitPreserveAllTokens( String str, char separatorChar ) {
        boolean preserveAllTokens = true;
        // Performance tuned for 2.0 (JDK1.4)

        if ( str == null ) {
            return null;
        }
        int len = str.length();
        if ( len == 0 ) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while ( i < len ) {
            if ( str.charAt( i ) == separatorChar ) {
                if ( match || preserveAllTokens ) {
                    list.add( str.substring( start, i ) );
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;

            match = true;
            i++;
        }
        if ( match || ( preserveAllTokens && lastMatch ) ) {
            list.add( str.substring( start, i ) );
        }
        return ( String[] ) list.toArray( new String[list.size()] );
    }
}
