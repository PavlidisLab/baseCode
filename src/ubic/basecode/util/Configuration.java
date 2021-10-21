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
package ubic.basecode.util;

import java.util.Iterator;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration of ontology services and other things.
 *
 * @author paul
 *
 */
public class Configuration {

    private static CompositeConfiguration config;

    /**
     * Name of the resource containing defaults
     */
    private static final String DEFAULT_CONFIGURATION = "ontology.properties";

    private static Logger log = LoggerFactory.getLogger( Configuration.class );

    /**
     * The name of the file users can use to customize.
     */
    private static final String USER_CONFIGURATION = "basecode.properties";

    static {

        config = new CompositeConfiguration();
        config.addConfiguration( new SystemConfiguration() );

        /*
         * the order matters - first come, first serve. Items added later do not overwrite items defined earlier. Thus
         * the user configuration has to be listed first.
         */

        try {
            // purely for backwards compatibility, if the user hasn't set up ontology.properties.
            PropertiesConfiguration pc = new PropertiesConfiguration();
            FileHandler handler = new FileHandler( pc );
            handler.setFileName( "Gemma.properties" );
            handler.load();
            config.addConfiguration( pc );
        } catch ( ConfigurationException e ) {
        }

        try {
            PropertiesConfiguration pc = new PropertiesConfiguration();
            FileHandler handler = new FileHandler( pc );
            handler.setFileName( USER_CONFIGURATION );
            handler.load();
            config.addConfiguration( pc );
        } catch ( ConfigurationException e ) {
        }

        try {
            PropertiesConfiguration pc = new PropertiesConfiguration();
            FileHandler handler = new FileHandler( pc );
            handler.setFileName( DEFAULT_CONFIGURATION );
            handler.load();
            config.addConfiguration( pc );
        } catch ( ConfigurationException e ) {
            log.error( DEFAULT_CONFIGURATION + " is missing, ontology loading may fail" );
        }

        // step through the result and do a final round of variable substitution
        for ( Iterator<String> it = config.getKeys(); it.hasNext(); ) {
            String key = it.next();
            String property = config.getString( key );
            if ( property != null && property.startsWith( "${" ) && property.endsWith( "}" ) ) {
                String keyToSubstitute = property.substring( 2, property.length() - 1 );
                String valueToSubstitute = config.getString( keyToSubstitute );
                log.debug( key + "=" + property + " -> " + valueToSubstitute );
                config.setProperty( key, valueToSubstitute );
            }
        }

    }

    /**
     * @param key
     * @return
     */
    public static boolean getBoolean( String key ) {
        return config.getBoolean( key, false );
    }

    /**
     * @param key
     * @return
     */
    public static String getString( String key ) {
        return config.getString( key );
    }

    /**
     * @param key
     * @return
     */
    public static void setString( String key, Object value ) {
        config.setProperty( key, value );
    }
}
