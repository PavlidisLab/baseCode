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
package ubic.basecode.ontology.ncbo;

import java.util.Collection;

import ubic.basecode.ontology.model.OntologyTerm;

/**
 * Abstraction of a service that can fetch information about ontologies and terms.
 * 
 * @author Paul
 * @version $Id$
 */
public interface OntologyLookup {

    /**
     * Get the definition associated with the term.
     * 
     * @param uri
     * @return definition, or null if there isn't one.
     */
    public String getDefinition( String uri );

    public Collection<String> getSynonyms( String uri );

    /**
     * Find ontology terms that match the given query
     * 
     * @param query
     * @param limit max to return
     * @return
     */
    public Collection<OntologyTerm> match( String query, int limit );

}
