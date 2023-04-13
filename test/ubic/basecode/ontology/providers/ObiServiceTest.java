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

import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyTerm;

import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author paul
 */
public class ObiServiceTest extends AbstractOntologyTest {

    @Test
    public void testLoadAndSearch() throws Exception {
        ObiService m = new ObiService();
        m.initialize( true, false );

        assertTrue( m.isOntologyLoaded() );

        Collection<OntologyTerm> hits = m.findTerm( "batch" );
        assertFalse( hits.isEmpty() );

        Collection<OntologyIndividual> ihits = m.findIndividuals( "batch" );
        assertFalse( ihits.isEmpty() );

        Collection<OntologyResource> rhits = m.findResources( "batch" );
        assertFalse( rhits.isEmpty() );
    }
}
