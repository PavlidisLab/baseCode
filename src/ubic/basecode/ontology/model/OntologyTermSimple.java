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

import com.hp.hpl.jena.ontology.OntClass;

/**
 * A light-weight version of OntologyTerms. Only supports a subset of the functionality of OntologyTermImpl (namely, it
 * is missing the inference components)
 * 
 * @author Paul
 * @version $Id$
 */
public class OntologyTermSimple extends OntologyTermImpl {

    private String description = "";
    private boolean obsolete;
    private String term;
    private String uri;

    public OntologyTermSimple( OntClass resource ) {
        super( resource );
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    public OntologyTermSimple( String uri, String term ) {
        super( null );
        this.uri = uri;
        this.term = term;
    }

    public OntologyTermSimple( String uri, String term, String description, boolean isObsolete ) {
        this( uri, term );
        this.description = description;
        this.obsolete = isObsolete;
    }

    @Override
    public boolean equals( Object obj ) {
        return super.equals( obj );
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
    public Collection<OntologyTerm> getChildren( boolean direct ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public String getComment() {
        return this.description;
    }

    @Override
    public Collection<OntologyIndividual> getIndividuals() {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );

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
    public Collection<OntologyTerm> getParents( boolean direct ) {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public Collection<OntologyRestriction> getRestrictions() {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
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
    public int hashCode() {
        if ( this.getUri() != null ) {
            return this.getUri().hashCode();
        }
        return this.getTerm().hashCode();
    }

    @Override
    public boolean isRoot() {
        throw new UnsupportedOperationException( "Use a OntologyTermImpl" );
    }

    @Override
    public boolean isTermObsolete() {
        return obsolete;
    }

}
