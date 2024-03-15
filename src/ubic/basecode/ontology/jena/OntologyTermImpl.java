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
package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import ubic.basecode.ontology.model.AnnotationProperty;
import ubic.basecode.ontology.model.OntologyIndividual;
import ubic.basecode.ontology.model.OntologyRestriction;
import ubic.basecode.ontology.model.OntologyTerm;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static ubic.basecode.ontology.jena.JenaUtils.where;

/**
 * Represents a class in an ontology
 *
 * @author Paul
 */
class OntologyTermImpl extends AbstractOntologyResource implements OntologyTerm {

    private static final String HAS_ALTERNATE_ID = "http://www.geneontology.org/formats/oboInOwl#hasAlternativeId";

    /**
     * Ontology class underlying this term.
     */
    private final OntClass ontResource;

    /**
     * Extra sets of properties to use when navigating parents and children of a term.
     */
    private final Set<Restriction> additionalRestrictions;

    public OntologyTermImpl( OntClass resource, @Nullable Set<Restriction> additionalRestrictions ) {
        super( resource );
        this.ontResource = resource;
        this.additionalRestrictions = additionalRestrictions;
    }

    public OntologyTermImpl( OntClass resource, @Nullable Set<Restriction> additionalRestrictions, double score ) {
        super( resource, score );
        this.ontResource = resource;
        this.additionalRestrictions = additionalRestrictions;
    }

    @Override
    public Collection<String> getAlternativeIds() {
        Collection<String> results = new HashSet<>();

        Property alternate = ResourceFactory.createProperty( HAS_ALTERNATE_ID );
        StmtIterator it = this.ontResource.listProperties( alternate );
        while ( it.hasNext() ) {
            Statement statement = it.next();
            results.add( statement.asTriple().getMatchObject().getLiteralLexicalForm() );
        }

        return results;

    }

    @Override
    public Collection<AnnotationProperty> getAnnotations() {
        Collection<AnnotationProperty> annots = new HashSet<>();
        StmtIterator iterator = ontResource.listProperties();
        // this is a little slow because we have to go through all statements for the term.
        while ( iterator.hasNext() ) {
            Statement state = iterator.next();
            if ( state.getPredicate().canAs( OntResource.class ) ) {
                try {
                    OntResource res = state.getPredicate().as( OntResource.class );
                    if ( res.isAnnotationProperty() ) {
                        com.hp.hpl.jena.ontology.AnnotationProperty p = res.asAnnotationProperty();
                        RDFNode n = state.getObject();
                        annots.add( new AnnotationPropertyImpl( p, n ) );
                    }
                } catch ( ConversionException e ) {
                    log.error( "Conversion failed for " + state.getPredicate(), e );
                }
            }
        }
        return annots;
    }

    @Override
    public Collection<OntologyTerm> getChildren( boolean direct, boolean includeAdditionalProperties, boolean keepObsoletes ) {
        return JenaUtils.getChildren( ontResource.getOntModel(), Collections.singleton( ontResource ), direct, includeAdditionalProperties ? additionalRestrictions : null )
                .stream()
                .map( o -> new OntologyTermImpl( o, additionalRestrictions ) )
                .filter( o -> keepObsoletes || !o.isObsolete() )
                .collect( Collectors.toSet() );
    }

    /*
     * (non-Javadoc)
     *
     * @see ubic.gemma.ontology.OntologyTerm#getComment()
     */
    @Override
    public String getComment() {
        String comment = this.ontResource.getComment( null );
        return comment == null ? "" : comment;
    }

    @Override
    public Collection<OntologyIndividual> getIndividuals( boolean direct ) {
        return this.ontResource.listInstances( direct )
                .filterKeep( where( OntResource::isIndividual ) )
                .mapWith( r -> ( OntologyIndividual ) new OntologyIndividualImpl( r.asIndividual(), additionalRestrictions ) )
                .toSet();
    }

    @Override
    public String getLocalName() {
        return ontResource.getLocalName();
    }

    @Override
    public Object getModel() {
        return ontResource.getModel();
    }

    @Override
    public Collection<OntologyTerm> getParents( boolean direct, boolean includeAdditionalProperties, boolean keepObsoletes ) {
        return JenaUtils.getParents( ontResource.getOntModel(), Collections.singleton( ontResource ), direct, includeAdditionalProperties ? additionalRestrictions : null )
                .stream()
                .map( o -> new OntologyTermImpl( o, additionalRestrictions ) )
                .filter( o -> keepObsoletes || !o.isObsolete() )
                .collect( Collectors.toSet() );
    }

    /**
     *
     */
    @Override
    public Collection<OntologyRestriction> getRestrictions() {
        /*
         * Remember that restrictions are superclasses.
         */
        Collection<OntologyRestriction> result = new HashSet<>();
        ExtendedIterator<OntClass> iterator = ontResource.listSuperClasses( false );
        while ( iterator.hasNext() ) {
            OntClass c = iterator.next();
            if ( c.isRestriction() ) {
                result.add( RestrictionFactory.asRestriction( c.asRestriction(), additionalRestrictions ) );
            }
        }

        // Check superclasses for any ADDITIONAL restrictions.
        iterator = ontResource.listSuperClasses( false );
        while ( iterator.hasNext() ) {
            OntClass c = iterator.next();

            try {
                c.asRestriction(); // throw it away, we already processed it above.
            } catch ( Exception e ) {
                // not a restriction, but a superclass that might have restrictions
                ExtendedIterator<OntClass> supClassesIt = c.listSuperClasses( false );
                loop:
                while ( supClassesIt.hasNext() ) {
                    OntClass sc = supClassesIt.next();
                    Restriction sr;
                    try {
                        sr = sc.asRestriction();

                        // only add it if the class doesn't already have one.
                        OntologyRestriction candidateRestriction = RestrictionFactory.asRestriction( sr, additionalRestrictions );
                        for ( OntologyRestriction restr : result ) {
                            if ( restr.getRestrictionOn().equals( candidateRestriction.getRestrictionOn() ) )
                                continue loop;
                        }
                        result.add( candidateRestriction );

                    } catch ( Exception ex ) {
                        // superclass isn't a restriction.
                    }
                }
            }

        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see ubic.gemma.analysis.ontology.OntologyTerm#getTerm()
     */
    @Override
    public String getTerm() {
        String res = getLabel();
        if ( res == null ) {
            res = ontResource.toString();
        }
        return res;
    }

    @Override
    public boolean isObsolete() {
        return super.isObsolete() || ontResource.hasSuperClass( OBO.ObsoleteClass );
    }

    /*
     * (non-Javadoc)
     *
     * @see ubic.gemma.analysis.ontology.OntologyTerm#isRoot()
     */
    @Override
    public boolean isRoot() {
        return getParents( true, true, true ).isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see ubic.basecode.ontology.model.OntologyTerm#isTermObsolete()
     */
    @Override
    public boolean isTermObsolete() {
        return isObsolete();
    }

    public OntClass getOntClass() {
        return ontResource;
    }
}
