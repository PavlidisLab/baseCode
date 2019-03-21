/*
 * The basecode project
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
package ubic.basecode.ontology.model;

/**
 * @author pavlidis
 * 
 */
public class DatatypePropertyImpl extends OntologyPropertyImpl implements DatatypeProperty {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    Class<?> type;

    private com.hp.hpl.jena.ontology.DatatypeProperty resource;

    public DatatypePropertyImpl( com.hp.hpl.jena.ontology.DatatypeProperty resource ) {
        this.resource = resource;
        this.type = PropertyFactory.convertType( resource );
    }

    @Override
    public String getLabel() {
        String label = resource.getLabel( null );
        if ( label == null ) label = resource.getLocalName();
        return label;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getUri() {
        return resource.getURI();
    }

    @Override
    public String toString() {
        String label = resource.getLabel( "en" );
        if ( label == null ) label = resource.getLabel( null );
        if ( label == null ) label = resource.getLocalName();
        return label + " (" + type.toString() + ")";
    }

}
