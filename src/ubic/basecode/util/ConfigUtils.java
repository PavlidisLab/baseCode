/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration.io.FileHandler;

/**
 * Convenience methods for loading configurations
 * 
 * @author Paul
 * @version $Id$
 */
public class ConfigUtils {

    /**
     * @param name
     * @return
     * @throws ConfigurationException
     */
    public static PropertiesConfiguration loadConfig( String name ) throws ConfigurationException {
        PropertiesConfiguration pc = new PropertiesConfiguration();
        FileHandler handler = new FileHandler( pc );
        handler.setFileName( name );
        handler.load();
        return pc;
    }

    /**
     * @param name
     * @return
     */
    public static FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigBuilder( String name ) {
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
                PropertiesConfiguration.class );
        builder.configure( new FileBasedBuilderParametersImpl().setFileName( name ) );
        return builder;
    }

    /**
     * @param file
     * @return
     */
    public static FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigBuilder( File file ) {
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
                PropertiesConfiguration.class );
        builder.configure( new FileBasedBuilderParametersImpl().setFile( file ) );
        return builder;
    }

    /**
     * @param url
     * @return
     * @throws ConfigurationException
     */
    public static FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigBuilder( URL url )
            throws ConfigurationException {
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
                PropertiesConfiguration.class );
        try {
            builder.configure( new FileBasedBuilderParametersImpl().setFile( new File( url.toURI() ) ) );
        } catch ( URISyntaxException e ) {
            throw new ConfigurationException( "Couldn't map url to a uri" );
        }
        return builder;
    }

    /**
     * @param file
     * @return
     * @throws ConfigurationException
     */
    public static PropertiesConfiguration loadConfig( File file ) throws ConfigurationException {
        PropertiesConfiguration pc = new PropertiesConfiguration();
        FileHandler handler = new FileHandler( pc );
        handler.setFile( file );
        handler.load();
        return pc;
    }

    /**
     * @param url
     * @return
     * @throws ConfigurationException
     */
    public static PropertiesConfiguration loadConfig( URL url ) throws ConfigurationException {
        PropertiesConfiguration pc = new PropertiesConfiguration();
        FileHandler handler = new FileHandler( pc );
        try {
            handler.setFile( new File( url.toURI() ) );
        } catch ( URISyntaxException e ) {
            throw new ConfigurationException( "Couldn't map url to a uri" );
        }
        handler.load();
        return pc;
    }
}
