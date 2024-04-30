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

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

/**
 * A way to create ad-hoc in-memory ontology services.
 *
 * @author Paul
 */
public class GenericOntologyService extends AbstractOntologyService {

    private final String url;
    private final String name;
    @Nullable
    private final String cacheName;

    public GenericOntologyService( String name, String url, @Nullable String cacheName ) {
        this.name = name;
        this.url = url;
        this.cacheName = cacheName;
    }

    public GenericOntologyService( String name, String url ) {
        this( name, url, null );
    }

    /**
     * @deprecated use {@link #GenericOntologyService(String, String, String)} with an explicit cache name instead
     */
    @Deprecated
    public GenericOntologyService( String name, String url, boolean cache ) {
        this( name, url, cache ? StringUtils.deleteWhitespace( name ) : null );
    }

    /**
     * @deprecated use {@link #GenericOntologyService(String, String, String)} with an explicit cache name instead and
     * {@link #setProcessImports(boolean)}
     */
    @Deprecated
    public GenericOntologyService( String name, String url, boolean cache, boolean processImports ) {
        this( name, url, cache );
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
    protected boolean isOntologyEnabled() {
        return true;
    }

    @Override
    @Nullable
    protected String getCacheName() {
        return cacheName;
    }
}
