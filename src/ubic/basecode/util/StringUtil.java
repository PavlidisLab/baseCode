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
import java.util.Collection;

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
     * @param appendee The string to be added to
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

    private static String shortestString( Collection<String> strings ) {
        String shortest = null;
        for ( String string : strings ) {
            if ( shortest == null || string.length() < shortest.length() ) shortest = string;
        }
        return shortest;
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

}