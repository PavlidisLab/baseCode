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

import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologySearchResult;
import ubic.basecode.util.Configuration;

import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * @author mjacobson
 */
public class GenericOntologyServiceTest extends AbstractOntologyTest {

    private static final String dataResource = "/data/nif.organism.test.owl.xml";

    @Test
    public void testGenericOntologyServiceMem() throws Exception {
        URL resource = this.getClass().getResource( dataResource );
        assertNotNull( resource );
        GenericOntologyService s1 = new GenericOntologyService( "foo", resource.toString(), true, false );
        s1.initialize( true, false );
        GenericOntologyService s = s1;

        Collection<OntologySearchResult<OntologyTerm>> r = s.findTerm( "Mouse", 500 );
        assertFalse( r.isEmpty() );
    }

    @Test
    public void testInitializeInBackgroundThread() throws InterruptedException {
        URL resource = this.getClass().getResource( dataResource );
        assertNotNull( resource );
        GenericOntologyService s = new GenericOntologyService( "foo", resource.toString() );
        s.startInitializationThread( true, false );
        assertTrue( s.isInitializationThreadAlive() );
        s.cancelInitializationThread();
        assertTrue( s.isInitializationThreadCancelled() );
        s.waitForInitializationThread();
        assertFalse( s.isInitializationThreadAlive() );
    }

    @Test
    public void testWithoutOntologyCacheDir() {
        String prevCacheDir = Configuration.getString( "ontology.cache.dir" );
        String prevIndexDir = Configuration.getString( "ontology.index.dir" );
        try {
            Configuration.setString( "ontology.cache.dir", "" );
            Configuration.setString( "ontology.index.dir", "" );
            URL resource = this.getClass().getResource( dataResource );
            assertNotNull( resource );
            new GenericOntologyService( "foo", resource.toString(), false, false )
                    .initialize( true, false );
            new GenericOntologyService( "foo", resource.toString(), true, false )
                    .initialize( true, true );
            // cannot force index without a cache directory
            IllegalArgumentException e = assertThrows( IllegalArgumentException.class, () -> {
                new GenericOntologyService( "foo", resource.toString(), false, false )
                        .initialize( true, true );
            } );
            assertTrue( e.getMessage().matches( "No cache directory is set for foo \\[file:.+], cannot force indexing." ) );
        } finally {
            Configuration.setString( "ontology.cache.dir", prevCacheDir );
            Configuration.setString( "ontology.index.dir", prevIndexDir );
        }
    }
}
