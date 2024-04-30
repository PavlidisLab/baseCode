/*
 * The baseCode project
 *
 * Copyright (c) 2011 University of British Columbia
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

import ubic.basecode.ontology.model.OntologyModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author paul
 */
@Deprecated
public class NIFSTDOntologyService extends AbstractBaseCodeOntologyService {

    private static final String NIFSTD_ONTOLOGY_FILE = "/data/loader/ontology/nif-gemma.owl.gz";

    public NIFSTDOntologyService() {
        super( "NISFTD", "nisftdOntology" );
        setProcessImports( false );
    }

    @Override
    protected String getOntologyUrl() {
        return "classpath:" + NIFSTD_ONTOLOGY_FILE;
    }

    @Override
    protected OntologyModel loadModel( boolean processImports, LanguageLevel languageLevel, InferenceMode inferenceMode ) throws IOException {
        try ( InputStream stream = getClass().getResourceAsStream( NIFSTD_ONTOLOGY_FILE ) ) {
            if ( stream == null ) {
                throw new RuntimeException( String.format( "The NIF ontology was not found in classpath at %s.", NIFSTD_ONTOLOGY_FILE ) );
            }
            return loadModelFromStream( new GZIPInputStream( stream ), processImports, languageLevel, inferenceMode );
        }
    }
}
