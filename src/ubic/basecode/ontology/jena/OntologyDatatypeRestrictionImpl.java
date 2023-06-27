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
package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import ubic.basecode.ontology.model.OntologyDatatypeRestriction;

import java.util.Set;

/**
 * @author pavlidis
 */
class OntologyDatatypeRestrictionImpl extends OntologyRestrictionImpl implements OntologyDatatypeRestriction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Class<?> type;

    public OntologyDatatypeRestrictionImpl( Restriction resource, Set<Restriction> additionalRestrcitions ) {
        super( resource, additionalRestrcitions );
        assert restrictionOn != null;
        this.type = PropertyFactory.convertType( resource.getOnProperty().asDatatypeProperty() );
    }

    @Override
    public Class<?> getRestrictedTo() {
        return type;
    }

    @Override
    public String toString() {
        return " datatype restriction: " + this.getRestrictionOn();
    }
}
