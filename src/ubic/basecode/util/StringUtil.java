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
import java.util.HashMap;
import java.util.Map;

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
     * Mimics the {@code make.names} method in R (character.c) to make valid variables names; we use this for column
     * headers in some output files.
     * <p>
     * This was modified in 1.1.26 to match the behavior of R more closely, if not exactly.
     *
     * @param s a string to be made valid for R
     * @return modified string
     * @author paul
     * @deprecated use {@link #makeNames(String[], boolean)} instead
     */
    public static String makeValidForR( String s ) {
        return makeNames( s );
    }

    /**
     * Mimics the {@code make.names} method in R when using with a vector of strings and the unique argument set to TRUE.
     * @author poirigui
     * @deprecated use {@link #makeNames(String[], boolean)} instead
     */
    @Deprecated
    public static String[] makeValidForR( String[] strings ) {
        return makeNames( strings, true );
    }

    /**
     * Mimics the {@code make.names} method in R.
     * @param strings a list of strings to be made valid for R
     * @param unique  if true, will ensure that the names are unique by appending a number to duplicates as per
     * {@link #makeUnique(String[])}
     * @author poirigui
     */
    public static String[] makeNames( String[] strings, boolean unique ) {
        String[] result = new String[strings.length];
        if ( unique ) {
            Map<String, Integer> counts = new HashMap<>();
            for ( int i = 0; i < strings.length; i++ ) {
                String s = strings[i];
                String rs = makeNames( s );
                if ( counts.containsKey( rs ) ) {
                    int count = counts.get( rs );
                    result[i] = rs + "." + count;
                    counts.put( rs, count + 1 );
                } else {
                    result[i] = rs;
                    counts.put( rs, 1 );
                }
            }
        } else {
            for ( int i = 0; i < strings.length; i++ ) {
                result[i] = makeNames( strings[i] );
            }
        }
        return result;
    }

    private static final String[] R_RESERVED_WORDS = {
        "if", "else", "repeat", "while", "function", "for", "in", "next", "break",
        "TRUE", "FALSE", "NULL", "Inf", "NaN", "NA", "NA_integer_", "NA_real_", "NA_character_", "NA_complex_",
    };

    /**
     * Mimics the {@code make.names} method in R for a single string.
     * @author paul
     */
    public static String makeNames( String s ) {
        if ( s == null ) {
            return "NA";
        }
        if ( s.isEmpty()
            // starts with a non-letter or non-dot
            || ( !Character.isAlphabetic( s.charAt( 0 ) ) && s.charAt( 0 ) != '.' )
            // dot followed by a digit
            || ( s.charAt( 0 ) == '.' && s.length() > 1 && Character.isDigit( s.charAt( 1 ) ) ) ) {
            return "X" + s.replaceAll( "[^A-Za-z0-9._]", "." );
        }
        if ( StringUtils.equalsAny( s, R_RESERVED_WORDS ) ) {
            return s + ".";
        }
        return s.replaceAll( "[^A-Za-z0-9._]", "." );
    }

    /**
     * Mimics the {@code make.unique} method in R.
     * <p>
     * Duplicated values in the input array will be suffixed with a dot and a number, starting from 1.
     * @author poirigui
     */
    public static String[] makeUnique( String[] strings ) {
        Map<String, Integer> counts = new HashMap<>();
        String[] result = new String[strings.length];
        for ( int i = 0; i < strings.length; i++ ) {
            String cn = strings[i];
            if ( counts.containsKey( cn ) ) {
                int count = counts.get( cn );
                result[i] = cn + "." + count;
                counts.put( cn, count + 1 );
            } else {
                result[i] = cn;
                counts.put( cn, 1 );
            }
        }
        return result;

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
