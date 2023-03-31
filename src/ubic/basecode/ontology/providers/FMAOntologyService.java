/*
 * The Gemma21 project
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
 * Holds a copy of the FMA Ontology on disk. This gets loaded on startup.
 * 
 * @author klc
 * 
 */
public class FMAOntologyService extends AbstractOntologyMemoryBackedService {

    private static final String FMA_ONTOLOGY_URL = "url.fmaOntology";

    @Override
    protected String getOntologyName() {
        return "fmaOntology";
    }

    @Override
    protected String getOntologyUrl() {
        return Configuration.getString( FMA_ONTOLOGY_URL );
    }

}
