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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.io.FileUtils;

/**
 * Convenience methods for loading configurations
 *
 * @author Paul
 *
 */
public class ConfigUtils {

    /**
     * @param file
     * @return
     */
    public static FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigBuilder( File file )
            throws ConfigurationException {
        if ( !file.exists() ) {
            try {
                FileTools.touch( file );
            } catch ( IOException e ) {
                throw new ConfigurationException( "Couldn't create the file: " + e.getMessage() );
            }
        }
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
                PropertiesConfiguration.class );
        builder.configure( new FileBasedBuilderParametersImpl().setFile( file ) );
        return builder;
    }

    /**
     * @param name If the file does not exist, attempts to create it in the user's home directory.
     * @return
     */
    public static FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigBuilder( String name )
            throws ConfigurationException {
        File f = locateConfig( name );
        return getConfigBuilder( f );
    }

    /**
     * @param url
     * @return
     * @throws ConfigurationException
     */
    public static FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigBuilder( URL url )
            throws ConfigurationException {
        File file;
        try {
            file = new File( url.toURI() );
            return getConfigBuilder( file );
        } catch ( URISyntaxException e ) {
            throw new ConfigurationException( "Couldn't map url to a uri: " + url );
        }

    }

    /**
     * @param name the classpath location, such as "project.properties" in the base package, or
     *        org/foo/project.properties.
     * @return
     * @throws ConfigurationException
     */
    public static PropertiesConfiguration loadClasspathConfig( String name ) throws ConfigurationException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        URL url = loader.getResource( name );
        if ( url == null ) {
            throw new ConfigurationException( "Couldn't locate: " + name );
        }

        PropertiesConfiguration pc = new PropertiesConfiguration();
        FileHandler handler = new FileHandler( pc );
        handler.setURL( url );
        handler.load();
        return pc;
    }

    /**
     * @param file
     * @return
     * @throws ConfigurationException
     */
    public static PropertiesConfiguration loadConfig( File file ) throws ConfigurationException {
        if ( !file.exists() ) {
            try {
                FileTools.touch( file );
            } catch ( IOException e ) {
                throw new ConfigurationException( "Couldn't create the file: " + e.getMessage() );
            }
        }
        PropertiesConfiguration pc = new PropertiesConfiguration();
        FileHandler handler = new FileHandler( pc );
        handler.setFile( file );
        handler.load();
        return pc;
    }

    /**
     * @param name If the file does not exist, attempts to create it in the user's home directory.
     * @return
     * @throws ConfigurationException
     */
    public static PropertiesConfiguration loadConfig( String name ) throws ConfigurationException {

        /*
         * We have to locate it, if it's not a path.
         */
        File f = locateConfig( name );

        if ( f == null ) {
            /*
             * Try again from the classpath?
             */
            return loadClasspathConfig( name );
        }

        return loadConfig( f );
    }

    /**
     * @param url
     * @return
     * @throws ConfigurationException
     */
    public static PropertiesConfiguration loadConfig( URL url ) throws ConfigurationException {

        try {
            File file = new File( url.toURI() );
            return loadConfig( file );
        } catch ( Exception e ) {
            throw new ConfigurationException( "Couldn't map url to a uri: " + url );
        }
    }

    /**
     * @param name
     * @return
     * @throws ConfigurationException
     */
    private static File locateConfig( String name ) throws ConfigurationException {
        File f;
        FileLocator fl = FileLocatorUtils.fileLocator().fileName( name ).create();
        URL location = FileLocatorUtils.locate( fl );
        if ( location == null ) {
            f = new File( name );
            if ( f.isAbsolute() ) {
                return f;
            }
            return new File( FileUtils.getUserDirectory(), name );
        }
        try {
            return new File( location.toURI() );
        } catch ( Exception e ) {
            throw new ConfigurationException( "Couldn't map url to a uri: " + name );
        }
    }

    public static URL locate( String name ) {
        FileLocator fl = FileLocatorUtils.fileLocator().fileName( name ).create();
        return FileLocatorUtils.locate( fl );
    }
}
