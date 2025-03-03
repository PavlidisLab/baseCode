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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

/**
 * @author pavlidis
 */
public class StringUtil {

    private static final Logger log = LoggerFactory.getLogger( StringUtil.class );

    /**
     * @param appendee  The string to be added to
     * @param appendant The string to add to the end of the appendee
     * @param separator The string to put between the joined strings, if necessary.
     * @return appendee + separator + separator unless appendee is empty, in which case the appendant is returned.
     */
    public static String append( String appendee, String appendant, String separator ) {
        if ( StringUtils.isBlank( appendee ) ) {
            return appendant;
        }
        return appendee + separator + appendant;

    }

    /**
     * Given a set of strings, identify any prefix they have in common.
     *
     * @param strings
     * @return the common prefix, null if there isn't one.
     */
    public static String commonPrefix( Collection<String> strings ) {
        // find the shortest string; this is the maximum length of the prefix. It is itself the prefix to look for.
        String shortest = shortestString( strings );

        if ( shortest == null || shortest.length() == 0 ) return null;

        String test = shortest;
        while ( test.length() > 0 ) {
            boolean found = true;
            for ( String string : strings ) {
                if ( !string.startsWith( test ) ) {
                    found = false;
                    break;
                }
            }
            if ( found ) return test;
            test = test.substring( 0, test.length() - 1 );
        }
        return null;
    }

    /**
     * Given a set of strings, identify any suffix they have in common.
     *
     * @param strings
     * @return the commons suffix, null if there isn't one.
     */
    public static String commonSuffix( Collection<String> strings ) {
        String shortest = shortestString( strings );

        if ( shortest == null || shortest.length() == 0 ) return null;

        String test = shortest;
        while ( test.length() > 0 ) {
            boolean found = true;
            for ( String string : strings ) {
                if ( !string.endsWith( test ) ) {
                    found = false;
                    break;
                }
            }
            if ( found ) return test;
            test = test.substring( 1 );
        }
        return null;
    }

    /**
     * Checks a string to find "strange" character, used by phenocarta to check evidence description
     *
     * @param the string to check
     * @return return false if something strange was found
     * @author Nicolas?
     */
    public static boolean containsValidCharacter( String s ) {

        if ( s != null ) {

            for ( int i = 0; i < s.length(); i++ ) {

                Character cha = s.charAt( i );

                if ( !( isLatinLetter( cha ) || Character.isDigit( cha ) || cha == '=' || cha == ',' || cha == '('
                    || cha == ')' || cha == '\'' || Character.isWhitespace( cha ) || cha == '/' || cha == '?'
                    || cha == '+' || cha == ':' || cha == '-' || cha == '<' || cha == '>' || cha == '"'
                    || cha == '%' || cha == '.' || cha == '*' || cha == '[' || cha == ']' || cha == ';'
                    || cha == '_' || cha == '\\' || cha == '|' || cha == '&' || cha == '^' || cha == '#'
                    || cha == '{' || cha == '}' || cha == '!' || cha == '~' || cha == '@' || cha == '—'
                    || cha == '×' || cha == '–' || cha == ' ' ) ) {

                    // new cha to be added, special Öö≤≥âμ etc... TODO and check later if found

                    log.warn( "Illegal character found: " + cha + " found on description: " + s );

                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param line
     * @return
     */
    public static String[] csvSplit( String line ) {
        try ( CSVParser parser = CSVParser.parse( new StringReader( line ), CSVFormat.DEFAULT ) ) {
            for ( CSVRecord record : parser ) {
                return record.values();
            }
            throw new IllegalArgumentException( "No CSV records found in line." );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Made by Nicolas
     *
     * @param a line in a file cvs format
     * @return the same line but in tsv format
     */
    public static String cvs2tsv( String line ) {

        StringBuffer newLine = new StringBuffer( line );

        boolean change = true;

        for ( int position = 0; position < newLine.length(); position++ ) {

            if ( newLine.charAt( position ) == ',' && change ) {
                newLine.setCharAt( position, '\t' );
            } else if ( newLine.charAt( position ) == '"' ) {

                if ( change ) {
                    change = false;
                } else {
                    change = true;
                }
            }
        }
        return newLine.toString().replaceAll( "\"", "" );
    }

    public static boolean isLatinLetter( char c ) {
        return ( c >= 'A' && c <= 'Z' ) || ( c >= 'a' && c <= 'z' );
    }

    /**
     * Mimics the make.names method in R (character.c) to make valid variables names; we use this for column headers in
     * some output files. This doesn't give the exact sames results as R; we avoid repeated '.'.
     *
     * @param s
     * @return modified string
     * @author paul
     */
    public static String makeValidForR( String s ) {

        // If string starts with a digit or "." and then a digit, prepend an X.
        if ( s.matches( "^\\.?[0-9].+" ) ) {
            s = "X" + s;
        }

        // TODO: check for reserved words. https://stat.ethz.ch/R-manual/R-devel/library/base/html/Reserved.html

        // no dashes or white space or other punctuation. '.' is okay and so is "_", now.
        return s.replaceAll( "[\\W]+", "." );
    }

    /**
     * @param stringi
     * @param stringj
     * @return
     */
    public static Long twoStringHashKey( String stringi, String stringj ) {
        // use arbitrary but consistent method for ordering.
        if ( stringi.hashCode() < stringj.hashCode() ) {
            return new Long( stringi.hashCode() | ( long ) stringj.hashCode() << 32 );
        }
        return new Long( stringj.hashCode() | ( long ) stringi.hashCode() << 32 );
    }

    private static String shortestString( Collection<String> strings ) {
        String shortest = null;
        for ( String string : strings ) {
            if ( shortest == null || string.length() < shortest.length() ) shortest = string;
        }
        return shortest;
    }

}
