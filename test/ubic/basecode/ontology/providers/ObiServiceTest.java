/*
 * The baseCode project
 * 
 * Copyright (c) 2012 University of British Columbia
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;

/**
 * @author paul
 */
public class ObiServiceTest {

    private static Logger log = LoggerFactory.getLogger( ObiServiceTest.class );

    @Test
    public void testLoadAndSearch() throws Exception {
        ObiService m = new ObiService();
        m.startInitializationThread( true, false );
        m.startInitializationThread( true, false );
        m.startInitializationThread( true, false );
        int i = 0;
        while ( !m.isOntologyLoaded() ) {

            Thread.sleep( 3000 );
            i++;
            log.info( "Waiting for OBI to load ... " + i );

            if ( i > 20 ) fail( "OBI Ontology didn't load in time" );
        }

        assertTrue( m.isOntologyLoaded() );

        Collection<OntologyTerm> hits = m.findTerm( "batch" );
        assertTrue( !hits.isEmpty() );

        Collection<OntologyIndividual> ihits = m.findIndividuals( "batch" );
        assertTrue( !ihits.isEmpty() );

        Collection<OntologyResource> rhits = m.findResources( "batch" );
        assertTrue( !rhits.isEmpty() );
    }
}
