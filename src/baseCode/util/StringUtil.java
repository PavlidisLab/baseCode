package baseCode.util;

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

}
