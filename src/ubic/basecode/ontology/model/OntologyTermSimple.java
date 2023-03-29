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
package ubic.basecode.ontology.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 * A light-weight version of OntologyTerms. Only supports a subset of the functionality of OntologyTermImpl (namely, it
 * is missing the inference components)
 *
 * @author Paul
 */
public class OntologyTermSimple implements OntologyTerm {

    /**
     *
     */
    private static final long serialVersionUID = -2589717267279872909L;

    private final String description;
    private final boolean obsolete;
    private final String term;
    private final String uri;

    public OntologyTermSimple( String uri, String term ) {
        this( uri, term, "", false );
    }

    public OntologyTermSimple( String uri, String term, String description, boolean isObsolete ) {
        this.uri = uri;
        this.term = term;
        this.description = description;
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
    public Collection<OntologyTerm> getChildren( boolean direct, boolean includeAdditionalProperties, boolean keepObsoletes ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public String getComment() {
        return this.description;
    }

    @Override
    public Collection<OntologyIndividual> getIndividuals( boolean direct ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public String getLabel() {
        return this.getTerm();
    }

    @Override
    public String getLocalName() {
        return this.getTerm();
    }

    @Override
    public Object getModel() {
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
    public String getTerm() {
        return this.term;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public boolean isObsolete() {
        return obsolete;
    }

    @Override
    public int compareTo( OntologyResource other ) {
        return Objects.compare( getUri(), other.getUri(), Comparator.nullsLast( Comparator.naturalOrder() ) );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final OntologyResource other = ( OntologyResource ) obj;
        if ( getLabel() == null ) {
            if ( other.getLabel() != null ) return false;
        } else if ( !getLabel().equals( other.getLabel() ) ) return false;
        if ( getUri() == null ) {
            if ( other.getUri() != null ) return false;
        } else if ( !getUri().equals( other.getUri() ) ) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash( getLabel(), getUri() );
    }
}
