/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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

import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.SomeValuesFromRestriction;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author pavlidis
 * @version $Id$
 */
public class OntologyClassRestrictionImpl extends OntologyRestrictionImpl implements OntologyClassRestriction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    RDFNode value = null;
    private OntologyTerm restrictedTo = null;

    public OntologyClassRestrictionImpl( Restriction term ) {
        super( term );

        if ( term.isAllValuesFromRestriction() ) {
            AllValuesFromRestriction a = term.asAllValuesFromRestriction();
            OntProperty onProp = a.getOnProperty();
            convertProperty( onProp );
            restrictedTo = new OntologyTermImpl( ( OntClass ) a.getAllValuesFrom() );
        } else if ( term.isSomeValuesFromRestriction() ) {
            SomeValuesFromRestriction s = term.asSomeValuesFromRestriction();
            OntProperty onProp = s.getOnProperty();
            convertProperty( onProp );
            restrictedTo = new OntologyTermImpl( ( OntClass ) s.getSomeValuesFrom() );
        } else if ( term.isHasValueRestriction() ) {
            HasValueRestriction h = term.asHasValueRestriction();
            OntProperty onProp = h.getOnProperty();
            convertProperty( onProp );
            value = h.getHasValue();
        } else if ( term.isMaxCardinalityRestriction() ) {
            log.warn( "Can't handle cardinality restrictions" );
        } else if ( term.isMinCardinalityRestriction() ) {
            log.warn( "Can't handle cardinality restrictions" );
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
        this.restrictionOn = PropertyFactory.asProperty( restrictionOnProperty );
    }

}
