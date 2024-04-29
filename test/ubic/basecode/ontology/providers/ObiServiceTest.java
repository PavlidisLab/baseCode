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
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.search.OntologySearchResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author paul
 */
public class ObiServiceTest extends AbstractOntologyTest {

    @Test
    public void testLoadAndSearch() throws Exception {
        ObiService m = new ObiService();
        m.setInferenceMode( OntologyService.InferenceMode.NONE );
        m.initialize( true, false );
        assertThat( m.isOntologyLoaded() ).isTrue();

        assertThat( m.findTerm( "batch", 500 ) )
                .extracting( OntologySearchResult::getResult )
                .extracting( OntologyResource::getUri )
                .contains( "http://purl.obolibrary.org/obo/IAO_0000132" );

        assertThat( m.findIndividuals( "failed exploratory term", 500 ) )
                .extracting( OntologySearchResult::getResult )
                .extracting( OntologyResource::getUri )
                .contains( "http://purl.obolibrary.org/obo/IAO_0000103" );

        assertThat( m.findResources( "batch", 500 ) )
                .extracting( OntologySearchResult::getResult )
                .extracting( OntologyResource::getUri )
                .contains( "http://purl.obolibrary.org/obo/IAO_0000132" );
    }
}
