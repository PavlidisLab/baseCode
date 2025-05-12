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

import org.assertj.core.api.Assertions;
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
        GenericOntologyService s1 = new GenericOntologyService( "foo", resource.toString(), "foo" );
        s1.setProcessImports( false );
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
        try {
            Configuration.setString( "ontology.cache.dir", "" );
            Configuration.setString( "ontology.index.dir", "" );
            URL resource = this.getClass().getResource( dataResource );
            assertNotNull( resource );
            GenericOntologyService os;
            os = new GenericOntologyService( "foo", resource.toString(), "foo" );
            os.setProcessImports( false );
            os.initialize( true, false );
            os = new GenericOntologyService( "foo", resource.toString(), "foo" );
            os.setProcessImports( false );
            os.initialize( true, true );
            // cannot force index without a cache directory
            IllegalArgumentException e = assertThrows( IllegalArgumentException.class, () -> {
                OntologyService os1 = new GenericOntologyService( "foo", resource.toString(), null );
                os1.setProcessImports( false );
                os1.initialize( true, true );
            } );
            Assertions.assertThat( e )
                    .hasMessageMatching( "No cache directory is set for foo.+, cannot force indexing\\." );
        } finally {
            Configuration.reset();
        }
    }
}
