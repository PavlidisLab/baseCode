/*
 * The basecode project
 * 
 * Copyright (c) 2007-2019 University of British Columbia
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

import ubic.basecode.ontology.jena.AbstractOntologyMemoryBackedService;
import ubic.basecode.util.Configuration;

/**
 * Loads the CHEBI Ontology at startup in its own thread. Controlled in build.properties by load.chebiOntology
 * 
 * @author klc
 * 
 */
public class ChebiOntologyService extends AbstractOntologyMemoryBackedService {

    private static final String CHEBI_ONTOLOGY_URL = "url.chebiOntology";

    @Override
    protected String getOntologyName() {
        return "chebiOntology";
    }

    @Override
    protected String getOntologyUrl() {
        return Configuration.getString( CHEBI_ONTOLOGY_URL );
    }

}
