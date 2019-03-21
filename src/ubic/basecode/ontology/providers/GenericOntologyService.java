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
package ubic.basecode.ontology.providers;

import ubic.basecode.ontology.OntologyLoader;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * A way to create ad hoc ontology services (in memory) for testing
 * 
 * @author Paul
 * 
 */
public class GenericOntologyService extends AbstractOntologyService {

    private String url;
    private String name;
    private boolean cache;

    public GenericOntologyService( String name, String url ) {
        this.name = name;
        this.url = url;
        this.cache = false;
    }

    public GenericOntologyService( String name, String url, boolean cache ) {
        this.name = name;
        this.url = url;
        this.cache = cache;
    }

    @Override
    protected String getOntologyName() {
        return name;
    }

    @Override
    protected String getOntologyUrl() {
        return url;
    }

    @Override
    protected OntModel loadModel() {
        return OntologyLoader.loadMemoryModel( this.getOntologyUrl(), this.cache ? this.name : null );
    }

}
