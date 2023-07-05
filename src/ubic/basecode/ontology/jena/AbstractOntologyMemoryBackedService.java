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
package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntModelSpec;
import ubic.basecode.ontology.model.OntologyModel;
import ubic.basecode.util.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class has some stuff that's specific to in-memory ontologies. Unlike database backed ontologies we don't use a
 * pool keeping only one instance of model in memory.
 *
 * @author paul
 */
public abstract class AbstractOntologyMemoryBackedService extends AbstractOntologyService {

    @Override
    protected String getOntologyUrl() {
        return Configuration.getString( "url." + getOntologyName() );
    }

    @Override
    protected OntologyModel loadModel( boolean processImports, InferenceMode inferenceMode ) throws IOException {
        return new OntologyModelImpl( OntologyLoader.loadMemoryModel( this.getOntologyUrl(), this.getCacheName(), processImports, this.getSpec( inferenceMode ) ) );
    }

    @Override
    protected OntologyModel loadModelFromStream( InputStream is, boolean processImports, InferenceMode inferenceMode ) throws IOException {
        return new OntologyModelImpl( OntologyLoader.loadMemoryModel( is, this.getOntologyUrl(), processImports, this.getSpec( inferenceMode ) ) );
    }

    private OntModelSpec getSpec( InferenceMode inferenceMode ) {
        switch ( inferenceMode ) {
            case TRANSITIVE:
                return OntModelSpec.OWL_MEM_TRANS_INF;
            case NONE:
                return OntModelSpec.OWL_MEM;
            default:
                throw new UnsupportedOperationException( String.format( "Unsupported inference level %s.", inferenceMode ) );
        }
    }
}
