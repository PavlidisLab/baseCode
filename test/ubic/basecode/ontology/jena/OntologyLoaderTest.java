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
package ubic.basecode.ontology.jena;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.providers.GenericOntologyService;
import ubic.basecode.ontology.search.OntologySearchResult;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Test loading a database-backed ontology
 *
 * @author Paul
 */
public class OntologyLoaderTest extends AbstractOntologyTest {

    private static final String dataResource = "/data/nif.organism.test.owl.xml";

    @Test
    public void testCacheOntologyToDisk() throws Exception {
        String name = "fooTEST1234";

        File f = OntologyLoader.getDiskCachePath( name );
        assertNotNull( f );
        if ( f.exists() ) {
            assumeTrue( String.format( "Failed to delete existing ontology cache file %s.", f.getAbsolutePath() ), f.delete() );
        }

        assertFalse( f.exists() );

        URL resource = this.getClass().getResource( dataResource );
        assertNotNull( resource );
        GenericOntologyService s = new GenericOntologyService( name, resource.toString(), true, false );
        s.initialize( true, false );

        // Check if cache was created
        assertTrue( f.exists() );
        assertNotEquals( 0, f.length() );
        assertFalse( OntologyLoader.getTmpDiskCachePath( name ).exists() );
        assertFalse( OntologyLoader.getOldDiskCachePath( name ).exists() );

        Collection<OntologySearchResult<OntologyTerm>> r = s.findTerm( "Mouse", 500 );
        assertFalse( r.isEmpty() );

        // Recreate OntologyService using this cache file
        s = new GenericOntologyService( name, "/data/NONEXISTENT_RESOURCE", true, false );
        s.initialize( true, false );

        assertTrue( f.exists() );
        assertFalse( OntologyLoader.getTmpDiskCachePath( name ).exists() );
        assertFalse( OntologyLoader.getOldDiskCachePath( name ).exists() );

        r = s.findTerm( "Mouse", 500 );
        assertFalse( r.isEmpty() );

        // Recreate OntologyService with bad URL and no cache
        assertThrows( RuntimeException.class, () -> {
            GenericOntologyService s1 = new GenericOntologyService( "NO_CACHE_WITH_THIS_NAME", "/data/NONEXISTENT_RESOURCE", true, false );
            s1.initialize( true, false );
        } );
    }

    @Test
    public void testHasChanged() throws Exception {
        String name = "fooTEST1235";

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
}
