/*
 * The baseCode project
 *
 * Copyright (c) 2017 University of British Columbia
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

package ubic.basecode.ontology.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ubic.basecode.ontology.jena.OntologyLoader;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.util.Configuration;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author mjacobson
 */
public class AbstractOntologyServiceTest {

    private static String prevDir = null;
    private static final String dataResource = "/data/nif.organism.test.owl.xml";

    @BeforeClass
    public static void setup() throws Exception {
        prevDir = Configuration.getString( "ontology.cache.dir" );
        Configuration.setString( "ontology.cache.dir", System.getProperty( "java.io.tmpdir" ) );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Configuration.setString( "ontology.cache.dir", prevDir );
    }

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
        GenericOntologyService s = createService( name, resource.toString(), true );

        Collection<OntologyTerm> r = s.findTerm( "Mouse" );
        assertFalse( r.isEmpty() );

        // Check if cache was created
        assertTrue( f.exists() );
        assertNotEquals( 0, f.length() );

        // Recreate OntologyService using this cache file
        s = createService( name, "/data/NONEXISTENT_RESOURCE", true );

        r = s.findTerm( "Mouse" );
        assertFalse( r.isEmpty() );

        // Recreate OntologyService with bad URL and no cache
        assertThrows( RuntimeException.class, () -> createService( "NO_CACHE_WITH_THIS_NAME", "/data/NONEXISTENT_RESOURCE", true ) );
    }

    @Test
    public void testGenericOntologyServiceMem() throws Exception {
        URL resource = this.getClass().getResource( dataResource );
        assertNotNull( resource );
        GenericOntologyService s = createService( "foo", resource.toString(), false );

        Collection<OntologyTerm> r = s.findTerm( "Mouse" );
        assertFalse( r.isEmpty() );

    }

    @Test
    public void testInitializeInBackgroundThread() throws InterruptedException {
        URL resource = this.getClass().getResource( dataResource );
        assertNotNull( resource );
        GenericOntologyService s = new GenericOntologyService( "foo", resource.toString(), false );
        s.startInitializationThread( true, false );
        assertTrue( s.isInitializationThreadAlive() );
        s.cancelInitializationThread();
        assertTrue( s.isInitializationThreadCancelled() );
        s.waitForInitializationThread();
        assertFalse( s.isInitializationThreadAlive() );
    }

    private GenericOntologyService createService( String name, String resourceURL, boolean cache ) {
        GenericOntologyService s = new GenericOntologyService( name, resourceURL, cache );
        s.initialize( true, false );
        return s;
    }
}
