/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
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
 * Support for the units ontology
 * 
 * @author Paul
 * 
 */
public class UnitsOntologyService extends AbstractOntologyMemoryBackedService {

    private static final String UNITs_ONTOLOGY_URL = "url.unitsOntology";

    @Override
    protected String getOntologyName() {
        return "unitsOntology";
    }

    @Override
    protected String getOntologyUrl() {
        return Configuration.getString( UNITs_ONTOLOGY_URL );
    }

}
