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

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import ubic.basecode.ontology.jena.OntologyLoader;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.*;

/**
 * Test loading a database-backed ontology
 *
 * @author Paul
 */
public class OntologyLoaderTest extends AbstractOntologyTest {

    @Test
    public void testHasChanged() throws Exception {
        String name = "fooTEST1234";

        File f = OntologyLoader.getDiskCachePath( name );
        File oldFile = new File( f.getAbsolutePath() + ".old" );

        assertFalse( f.exists() );
        assertFalse( oldFile.exists() );

        FileUtils.forceMkdirParent( f );

        URL resource = this.getClass().getResource( "/data/nif.organism.test.owl.xml" );
        assertNotNull( resource );
        try ( InputStream in = resource.openStream(); ) {
            Files.copy( in, f.toPath(), StandardCopyOption.REPLACE_EXISTING );
        }

        try ( InputStream in = resource.openStream(); ) {
            Files.copy( in, oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
        }

        assertFalse( OntologyLoader.hasChanged( name ) );

        // Now if the dataResource has changed
        resource = this.getClass().getResource( "/data/nif.organism.test.owl-off-by-one.xml" );
        assertNotNull( resource );
        try ( InputStream in = resource.openStream(); ) {
            Files.copy( in, f.toPath(), StandardCopyOption.REPLACE_EXISTING );
        }
        assertTrue( OntologyLoader.hasChanged( name ) );
    }

    @Test
    public void testLoadModelWithImports() {
        OntologyLoader.loadMemoryModel( "http://purl.obolibrary.org/obo/doid.owl" );
    }
}
