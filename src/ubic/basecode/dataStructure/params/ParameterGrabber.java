/*
 * The baseCode project
 * 
 * Copyright (c) 2009 University of British Columbia
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
package ubic.basecode.dataStructure.params;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generates a parameter map from a class using reflection, usefull for storing experimental parameters
 * 
 * @author leon
 * @version $Id$
 */
public class ParameterGrabber {
    public static String paramsToLine( Map<String, String> params ) {
        return paramsToLine( params, true );
    }

    public static String paramsToLine( Map<String, String> params, boolean sort ) {
        List<String> sortedKeys = new LinkedList<String>( params.keySet() );

        if ( sort ) Collections.sort( sortedKeys );

        String line = "";
        for ( String key : sortedKeys ) {
            line += key + "=" + params.get( key ) + ", ";
        }
        line = line.substring( 0, line.length() - 2 );
        return line;
    }

    /**
     * Given an instance of a class this method will extract its primitive variable names and their values using
     * reflection.
     * 
     * @param c - class
     * @param o - object
     * @return
     */
    public static Map<String, String> getParams( Class<?> c, Object o ) {
        // go up to parents?
        Map<String, String> params = new HashMap<String, String>();
        for ( Field f : c.getDeclaredFields() ) {
            // a bit pushy here
            f.setAccessible( true );
            String name = f.getName();
            Class<?> type = f.getType();

            if ( !type.equals( String.class ) ) {
                continue;
            }

            try {
                Object value = f.get( o );
                String stringValue = value.toString();
                params.put( name, stringValue );
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }
        if ( !c.getSuperclass().equals( Object.class ) ) {
            Map<String, String> parents = getParams( c.getSuperclass(), o );
            params.putAll( parents );
        }
        return params;
    }

}
