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
package ubic.basecode.ontology.simple;

import org.jspecify.annotations.Nullable;
import ubic.basecode.ontology.model.AnnotationProperty;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyRestriction;
import ubic.basecode.ontology.model.OntologyTerm;

import java.util.Collection;

/**
 * A light-weight version of OntologyTerms. Only supports a subset of the functionality of OntologyTermImpl (namely, it
 * is missing the inference components)
 *
 * @author Paul
 */
public class OntologyTermSimple extends AbstractOntologyResourceSimple implements OntologyTerm {

    @Nullable
    private final String comment;
    private final boolean obsolete;

    public OntologyTermSimple( @Nullable String uri, @Nullable String label ) {
        this( uri, null, label, null, false );
    }

    public OntologyTermSimple( @Nullable String uri, @Nullable String localName, @Nullable String label, @Nullable String comment, boolean isObsolete ) {
        super( uri, localName, label );
        this.comment = comment;
        this.obsolete = isObsolete;
    }

    @Override
    public Collection<String> getAlternativeIds() {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public Collection<AnnotationProperty> getAnnotations() {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public Collection<AnnotationProperty> getAnnotations( String propertyUri ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Nullable
    @Override
    public AnnotationProperty getAnnotation( String propertyUri ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public Collection<OntologyTerm> getChildren( boolean direct, boolean includeAdditionalProperties, boolean keepObsoletes ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Nullable
    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public Collection<OntologyIndividual> getIndividuals( boolean direct ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public Collection<OntologyTerm> getParents( boolean direct, boolean includeAdditionalProperties, boolean keepObsoletes ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public Collection<OntologyRestriction> getRestrictions() {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public boolean isRoot() {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public boolean isTermObsolete() {
        return obsolete;
    }

    @Override
    @Nullable
    public String getTerm() {
        return getLabel();
    }

    @Override
    public boolean isObsolete() {
        return obsolete;
    }
}
