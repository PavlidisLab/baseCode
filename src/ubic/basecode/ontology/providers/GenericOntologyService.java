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

import ubic.basecode.ontology.jena.AbstractOntologyMemoryBackedService;

/**
 * A way to create ad hoc ontology services (in memory) for testing
 *
 * @author Paul
 */
public class GenericOntologyService extends AbstractOntologyMemoryBackedService {

    private final String url;
    private final String name;
    private final boolean cache;

    public GenericOntologyService( String name, String url ) {
        this( name, url, false );
    }

    public GenericOntologyService( String name, String url, boolean cache ) {
        this( name, url, cache, true );
    }

    public GenericOntologyService( String name, String url, boolean cache, boolean processImports ) {
        this.name = name;
        this.url = url;
        this.cache = cache;
        setProcessImports( processImports );
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
    protected String getCacheName() {
        return this.cache ? this.name : null;
    }
}
