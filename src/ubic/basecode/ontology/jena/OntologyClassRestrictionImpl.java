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

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import ubic.basecode.ontology.model.OntologyClassRestriction;
import ubic.basecode.ontology.model.OntologyTerm;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author pavlidis
 */
class OntologyClassRestrictionImpl extends OntologyRestrictionImpl implements OntologyClassRestriction {
    private final RDFNode value;
    private final OntologyTerm restrictedTo;
    private final Set<Restriction> additionalRestrictions;

    public OntologyClassRestrictionImpl( Restriction term, @Nullable Set<Restriction> additionalRestrictions ) {
        super( term, additionalRestrictions );
        this.additionalRestrictions = additionalRestrictions;

        if ( term.isAllValuesFromRestriction() ) {
            AllValuesFromRestriction a = term.asAllValuesFromRestriction();
            OntProperty onProp = a.getOnProperty();
            convertProperty( onProp );
            value = null;
            restrictedTo = new OntologyTermImpl( ( OntClass ) a.getAllValuesFrom(), additionalRestrictions );
        } else if ( term.isSomeValuesFromRestriction() ) {
            SomeValuesFromRestriction s = term.asSomeValuesFromRestriction();
            OntProperty onProp = s.getOnProperty();
            convertProperty( onProp );
            value = null;
            restrictedTo = new OntologyTermImpl( ( OntClass ) s.getSomeValuesFrom(), additionalRestrictions );
        } else if ( term.isHasValueRestriction() ) {
            HasValueRestriction h = term.asHasValueRestriction();
            OntProperty onProp = h.getOnProperty();
            convertProperty( onProp );
            value = h.getHasValue();
            restrictedTo = null;
        } else if ( term.isMaxCardinalityRestriction() || term.isMinCardinalityRestriction() ) {
            log.warn( "Can't handle cardinality restrictions" );
            value = null;
            restrictedTo = null;
        } else {
            throw new IllegalArgumentException( "Can't handle that type of restriction for: " + term );
        }

    }

    @Override
    public OntologyTerm getRestrictedTo() {
        return restrictedTo;
    }

    //
    // public RDFNode getRestrictedToValue() {
    // return value;
    // }

    @Override
    public String toString() {
        assert restrictionOn != null;
        if ( restrictedTo != null ) {
            return "Class restriction: " + this.getRestrictionOn() + " class=" + this.getRestrictedTo();
        } else if ( value != null ) {
            return "Class restriction: " + this.getRestrictionOn() + " = " + this.value.toString() + "[value]";
        } else {
            throw new IllegalStateException( "Value or restriction class must be non-null" );
        }
    }

    private void convertProperty( OntProperty restrictionOnProperty ) {
        this.restrictionOn = PropertyFactory.asProperty( restrictionOnProperty, additionalRestrictions );
    }

}
