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
package ubic.basecode.ontology;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ubic.basecode.util.Configuration;

/**
 * Test loading a database-backed ontology
 * 
 * @author Paul
 * @version $Id$
 */
public class OntologyLoaderTest {

    private static String prevDir = null;
    private static String dataResource = "/data/nif.organism.test.owl.xml";

    @BeforeClass
    public static void setup() throws Exception {
        prevDir = Configuration.getString( "ontology.cache.dir" );
        Configuration.setString( "ontology.cache.dir", System.getProperty( "java.io.tmpdir" ) );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Configuration.setString( "ontology.cache.dir", prevDir );
    }

//    @Test
//    public void testHasChanged() throws Exception {
//        String name = "fooTEST1234";
//
//        File f = OntologyLoader.getDiskCachePath( name );
//        if ( f.exists() ) {
//            f.delete();
//        }
//
//        File oldFile = OntologyLoader.getOldDiskCachePath( name );
//        if ( oldFile.exists() ) {
//            oldFile.delete();
//        }
//
//        assertTrue( !f.exists() );
//        assertTrue( !oldFile.exists() );
//
//        URL resource = this.getClass().getResource( dataResource );
//        try (InputStream in = resource.openStream();) {
//            Files.copy( in, f.toPath(), StandardCopyOption.REPLACE_EXISTING );
//        }
//
//        try (InputStream in = resource.openStream();) {
//            Files.copy( in, oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
//        }
//
//        assertTrue( !OntologyLoader.hasChanged( name ) );
//
//        // Now if the dataResource has changed
//        resource = this.getClass().getResource( "/data/nif.organism.test.owl-off-by-one.xml" );
//        try (InputStream in = resource.openStream();) {
//            Files.copy( in, f.toPath(), StandardCopyOption.REPLACE_EXISTING );
//        }
//        assertTrue( OntologyLoader.hasChanged( name ) );
//
//    }

}
