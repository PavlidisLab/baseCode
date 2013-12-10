/*
 * The Gemma21 project
 * 
 * Copyright (c) 2007 University of British Columbia
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

/**
 * MEDIC ONTOLOGY USED BY PHENOCARTA, its represents MESH terms as a tree so with can use the parent structure that a
 * normal mesh term doesnt have
 */
public class MedicOntologyService extends AbstractOntologyMemoryBackedService {

    private static final String MEDIC_ONTOLOGY_FILE = "/data/loader/ontology/medic.owl";

    public MedicOntologyService() {
        loadMedicOntologyFromFile();
    }

    @Override
    protected String getOntologyName() {
        return "medicOntology";
    }

    @Override
    protected String getOntologyUrl() {
        return MEDIC_ONTOLOGY_FILE;
    }

    public void loadMedicOntologyFromFile() {
        loadTermsInNameSpace( this.getClass().getResourceAsStream( MEDIC_ONTOLOGY_FILE ) );
    }

}
