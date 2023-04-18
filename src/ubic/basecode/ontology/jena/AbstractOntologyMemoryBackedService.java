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

import com.hp.hpl.jena.ontology.OntModel;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class has some stuff that's specific to in-memory ontologies. Unlike database backed ontologies we don't use a
 * pool keeping only one instance of model in memory.
 *
 * @author paul
 */
public abstract class AbstractOntologyMemoryBackedService extends AbstractOntologyService {

    protected boolean getProcessImport() {
        return true;
    }

    @Override
    protected OntModel loadModel() throws IOException {
        return OntologyLoader.loadMemoryModel( this.getOntologyUrl(), this.getCacheName(), this.getProcessImport() );
    }

    @Override
    protected OntModel loadModelFromStream( InputStream is ) {
        return OntologyLoader.loadMemoryModel( is, this.getOntologyUrl(), this.getProcessImport() );
    }
}
