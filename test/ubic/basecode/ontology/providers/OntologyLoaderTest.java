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
package ubic.basecode.ontology.providers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Collection;

import org.junit.Test;

import ubic.basecode.ontology.model.OntologyTerm;

/**
 * Test loading a database-backed ontology
 * 
 * @author Paul
 * @version $Id$
 */
public class OntologyLoaderTest {

    @Test
    public void testGenericOntologyServiceMem() throws Exception {
        URL resource = this.getClass().getResource( "/data/nif.organism.test.owl.xml" );
        assertNotNull( resource );
        GenericOntologyService s = new GenericOntologyService( "foo", resource.toString() );

        s.startInitializationThread( true );
        int i = 0;
        while ( !s.isOntologyLoaded() ) {
            Thread.sleep( 1000 );
            if ( ++i > 100 ) {
                break;
            }
        }
        Collection<OntologyTerm> r = s.findTerm( "Mouse" );
        assertTrue( !r.isEmpty() );

    }

}
