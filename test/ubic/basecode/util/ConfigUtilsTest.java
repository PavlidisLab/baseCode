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

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.builder.FileBasedConfigurationBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Paul
 * @version $Id$
 */
public class ConfigUtilsTest {

    private String tmpPath;
    private String testConfigPath;

    @Test
    public void testGetConfigBuilderFile() throws Exception {
        FileBasedConfigurationBuilder<PropertiesConfiguration> config = ConfigUtils.getConfigBuilder( new File(
                testConfigPath ) );
        assertNotNull( config );
    }

    @Test
    public void testGetConfigBuilderString() throws Exception {
        FileBasedConfigurationBuilder<PropertiesConfiguration> config = ConfigUtils.getConfigBuilder( testConfigPath );
        assertNotNull( config );
    }

    @Test
    public void testGetConfigBuilderURL() throws Exception {
        FileBasedConfigurationBuilder<PropertiesConfiguration> config = ConfigUtils.getConfigBuilder( new File(
                testConfigPath ).toURI().toURL() );
        assertNotNull( config );
    }

    @Test
    public void testLoadConfigFile() throws Exception {
        PropertiesConfiguration config = ConfigUtils.loadConfig( new File( testConfigPath ) );
        assertNotNull( config );
    }

    @Test
    public void testLoadConfigString() throws Exception {
        PropertiesConfiguration config = ConfigUtils.loadConfig( testConfigPath );
        assertNotNull( config );
    }

    @Test
    public void testLoadConfigURL() throws Exception {
        PropertiesConfiguration config = ConfigUtils.loadConfig( new File( testConfigPath ).toURI().toURL() );
        assertNotNull( config );
    }

    @Before
    public void setup() throws Exception {
        this.testConfigPath = new File( this.getClass().getResource( "/data/test.properties" ).toURI() )
                .getAbsolutePath();

        File f = File.createTempFile( "test.", ".properties" );
        this.tmpPath = f.getAbsolutePath();
        f.delete();
        assertTrue( !f.exists() );
    }

    @Test
    public void testGetConfigBuilderFileNew() throws Exception {
        FileBasedConfigurationBuilder<PropertiesConfiguration> config = ConfigUtils
                .getConfigBuilder( new File( tmpPath ) );
        assertNotNull( config );
    }

    @Test
    public void testGetConfigBuilderStringNew() throws Exception {
        FileBasedConfigurationBuilder<PropertiesConfiguration> config = ConfigUtils.getConfigBuilder( tmpPath );
        assertNotNull( config );
    }

    @Test
    public void testGetConfigBuilderURLNew() throws Exception {
        FileBasedConfigurationBuilder<PropertiesConfiguration> config = ConfigUtils
                .getConfigBuilder( new File( tmpPath ).toURI().toURL() );
        assertNotNull( config );
    }

    @Test
    public void testLoadConfigFileNew() throws Exception {
        PropertiesConfiguration config = ConfigUtils.loadConfig( new File( tmpPath ) );
        assertNotNull( config );
    }

    @Test
    public void testLoadConfigStringNew() throws Exception {
        PropertiesConfiguration config = ConfigUtils.loadConfig( tmpPath );
        assertNotNull( config );
    }

    @Test
    public void testLoadConfigStringNewName() throws Exception {
        PropertiesConfiguration config = ConfigUtils.loadConfig( "testtemp.properties" );
        assertNotNull( config );
    }

    @Test
    public void testLoadConfigURLNew() throws Exception {
        PropertiesConfiguration config = ConfigUtils.loadConfig( new File( tmpPath ).toURI().toURL() );
        assertNotNull( config );
    }

}
