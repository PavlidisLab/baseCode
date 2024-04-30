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

import ubic.basecode.ontology.model.OntologyModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * MEDIC ONTOLOGY USED BY PHENOCARTA, its represents MESH terms as a tree so with can use the parent structure that a
 * normal mesh term doesnt have
 * <p>
 * MEDIC comes from the CTD folks. See <a href="http://ctd.mdibl.org/voc.go?type=disease">...</a>. Unfortunately I do not know where our
 * medic.owl file came from (PP)
 *
 * @author Nicolas
 */
@Deprecated
public class MedicOntologyService extends AbstractBaseCodeOntologyService {

    /**
     * FIXME this shouldn't be hard-coded like this, we should load it like any other ontology service.
     */
    private static final String MEDIC_ONTOLOGY_FILE = "/data/loader/ontology/medic.owl.gz";

    public MedicOntologyService() {
        super( "Medic Ontology", "medicOntology" );
    }

    @Override
    protected String getOntologyUrl() {
        return "classpath:" + MEDIC_ONTOLOGY_FILE;
    }

    @Override
    protected OntologyModel loadModel( boolean processImports, LanguageLevel languageLevel, InferenceMode inferenceMode ) throws IOException {
        try ( InputStream is = this.getClass().getResourceAsStream( MEDIC_ONTOLOGY_FILE ) ) {
            if ( is == null ) {
                throw new RuntimeException( String.format( "The MEDIC ontology was not found in classpath at %s.", MEDIC_ONTOLOGY_FILE ) );
            }
            return loadModelFromStream( new GZIPInputStream( is ), processImports, languageLevel, inferenceMode );
        }
    }
}
