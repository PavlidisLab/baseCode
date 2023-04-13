/*
 * The basecode project
 *
 * Copyright (c) 2007-2019 Columbia University
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
import ubic.basecode.ontology.model.OntologyProperty;
import ubic.basecode.ontology.model.OntologyRestriction;

import java.util.Set;

/**
 * Represents a restriction on instances that are subclasses of this.
 *
 * @author Paul
 */
public abstract class OntologyRestrictionImpl extends OntologyTermImpl implements OntologyRestriction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    protected OntologyProperty restrictionOn;

    public OntologyRestrictionImpl( Restriction resource, Set<Restriction> additionalRestrictions ) {
        super( resource, additionalRestrictions );
        this.restrictionOn = PropertyFactory.asProperty( resource.getOnProperty(), additionalRestrictions );
    }

    @Override
    public OntologyProperty getRestrictionOn() {
        return restrictionOn;
    }

}
