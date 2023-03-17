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

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a class in an ontology
 *
 * @author Paul
 */
public class OntologyTermImpl extends AbstractOntologyResource implements OntologyTerm {

    private static final String HAS_ALTERNATE_ID = "http://www.geneontology.org/formats/oboInOwl#hasAlternativeId";
    private static final String NOTHING = "http://www.w3.org/2002/07/owl#Nothing";

    /**
     * Properties through which propagation is allowed for {@link #getChildren(boolean)}
     */
    private static final Set<String> PROPAGATE_FROM_URIS = new HashSet<>();

    /**
     * Properties through which propagation is allowed for {@link #getParents(boolean)}
     */
    private static final Set<String> PROPAGATE_INTO_URIS = new HashSet<>();

    private static final Set<String> REJECT_PARENT_URIS = new HashSet<>();

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    static {
        CollectionUtils.addAll( PROPAGATE_INTO_URIS,
                "http://www.obofoundry.org/ro/ro.owl#proper_part_of",
                "http://purl.obolibrary.org/obo/BFO_0000050" );
        CollectionUtils.addAll( PROPAGATE_FROM_URIS,
                "http://www.obofoundry.org/ro/ro.owl#has_proper_part",
                "http://purl.obolibrary.org/obo/BFO_0000051" );
        REJECT_PARENT_URIS.add( "http://www.ifomis.org/bfo/1.1/snap#IndependentContinuant" );
        REJECT_PARENT_URIS.add( "http://www.ifomis.org/bfo/1.1/snap#Continuant" );
        REJECT_PARENT_URIS.add( "http://www.ifomis.org/bfo/1.1/snap#MaterialEntity" );

        // anatomical entity
        REJECT_PARENT_URIS.add( "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_6" );
    }

    private String label = null;
    private String localName = null;

    private final transient OntClass ontResource;

    public OntologyTermImpl( OntClass resource ) {
        this.ontResource = resource;
        if ( ontResource != null ) {
            this.label = ontResource.getLabel( "EN" );
            if ( this.label == null ) this.label = ontResource.getLabel( null );
            this.localName = ontResource.getLocalName();
        }
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;

        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;

        final OntologyTermImpl that = ( OntologyTermImpl ) obj;
        if ( this.getUri() != null ) {
            return Objects.equals( this.getUri(), that.getUri() );
        }
        return Objects.equals( this.getTerm(), that.getTerm() );
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
            OntResource res = state.getPredicate().as( OntResource.class );
            if ( res.isAnnotationProperty() ) {
                com.hp.hpl.jena.ontology.AnnotationProperty p = res.asAnnotationProperty();
                RDFNode n = state.getObject();
                annots.add( new AnnotationPropertyImpl( p, n ) );
            }
        }
        return annots;
    }

    @Override
    public Collection<OntologyTerm> getChildren( boolean direct ) {
        Collection<OntologyTerm> result = new HashSet<>();
        getChildren( direct, result );
        return result;
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

    /*
     * (non-Javadoc)
     *
     * @see ubic.gemma.ontology.OntologyTerm#getIndividuals()
     */
    @Override
    public Collection<OntologyIndividual> getIndividuals() {
        return getIndividuals( true );
    }

    @Override
    public Collection<OntologyIndividual> getIndividuals( boolean direct ) {
        Collection<OntologyIndividual> inds = new HashSet<>();
        ExtendedIterator<? extends OntResource> iterator = this.ontResource.listInstances( direct );
        while ( iterator.hasNext() ) {
            Individual i = ( Individual ) iterator.next();
            inds.add( new OntologyIndividualImpl( i ) );
        }
        return inds;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getLocalName() {
        return this.localName;
    }

    @Override
    public Object getModel() {
        return ontResource.getModel();
    }

    @Override
    public Collection<OntologyTerm> getParents( boolean direct ) {
        Collection<OntologyTerm> result = new HashSet<>();
        this.getParents( direct, result );
        return result;
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
            Restriction r;
            try {
                result.add( RestrictionFactory.asRestriction( c.asRestriction() ) );
            } catch ( Exception e ) {

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
                        OntologyRestriction candidateRestriction = RestrictionFactory.asRestriction( sr );
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
        String res;
        if ( this.label != null ) {
            res = this.label;
        } else if ( this.localName != null ) {
            res = localName;
        } else if ( this.getUri() != null ) {
            res = this.getUri();
        } else {
            res = ontResource.toString();
        }
        return res;
    }

    @Override
    public String getUri() {
        return this.ontResource.getURI();
    }

    @Override
    public int hashCode() {
        if ( ontResource == null ) {
            log.warn( "ontResource is null in hashCode()" );
            return 0;
        }
        // assert this.getUri() != null : "No URI for " + this.getTerm();
        if ( this.getUri() != null ) {
            return this.getUri().hashCode();
        }
        return this.getTerm().hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see ubic.gemma.analysis.ontology.OntologyTerm#isRoot()
     */
    @Override
    public boolean isRoot() {
        return !this.ontResource.listSuperClasses( true ).hasNext();
    }

    /*
     * (non-Javadoc)
     *
     * @see ubic.basecode.ontology.model.OntologyTerm#isTermObsolete()
     */
    @Override
    public boolean isTermObsolete() {

        Collection<OntologyTerm> parentsOntologyTerm = getParents( false );

        // this limit of 1 was because obsolete terms are connected right to the root. But ... not guaranteed.
        // / if ( parentsOntologyTerm.size() == 1 ) {

        for ( OntologyTerm parentOntologyTerm : parentsOntologyTerm ) {

            if ( parentOntologyTerm.getLocalName() != null
                    && parentOntologyTerm.getLocalName().equalsIgnoreCase( "ObsoleteClass" ) ) {
                return true;
            }

            // debug code.
            if ( parentOntologyTerm.getUri() == null ) {
                // log.warn( "URI for " + parentOntologyTerm + " was null" ); // not a problem.
                continue;
            }

            if ( parentOntologyTerm.getUri() != null
                    && parentOntologyTerm.getUri().equalsIgnoreCase(
                    "http://bioontology.org/projects/ontologies/birnlex#_birnlex_retired_class" )
                    || parentOntologyTerm
                    .getUri()
                    .equalsIgnoreCase(
                            "http://ontology.neuinfo.org/NIF/Backend/BIRNLex_annotation_properties.owl#_birnlex_retired_class" ) ) {
                return true;
            }
        }
        // }

        StmtIterator iterator = ontResource.listProperties();
        // this is a little slow because we have to go through all statements for the term.
        while ( iterator.hasNext() ) {
            Statement state = iterator.next();
            if ( state.getPredicate() == null ) continue;
            OntResource res = state.getPredicate().as( OntResource.class );
            if ( res.isAnnotationProperty() ) {
                com.hp.hpl.jena.ontology.AnnotationProperty p = res.asAnnotationProperty();
                RDFNode n = state.getObject();

                if ( p.getLocalName().equalsIgnoreCase( "deprecated" ) ) {
                    if ( n.toString().indexOf( "true" ) != -1 ) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String res = null;
        if ( this.getTerm() != null ) {
            res = this.getTerm();
            if ( this.localName != null && !this.getTerm().equals( this.localName ) ) {
                res = res + " [" + this.localName + "]";
            }
        } else if ( this.localName != null ) {
            res = localName;
        } else if ( this.getUri() != null ) {
            res = this.getUri();
        } else {
            res = ontResource.toString();
        }
        return res;
    }

    protected OntologyTerm fromOntClass( OntClass ontClass ) {
        return new OntologyTermImpl( ontClass );
    }

    /**
     * @param direct
     * @param work
     */
    private void getChildren( boolean direct, Collection<OntologyTerm> work ) {

        // get children by recursion, don't rely on the jena api.
        ExtendedIterator<OntClass> iterator = ontResource.listSubClasses( true );
        while ( iterator.hasNext() ) {
            OntClass c = iterator.next();
            // URI can be null if the ont is a bnode (no idea what it is, but we have to handle this)
            // some reasoners will infer owl#Nothing as a subclass of everything
            if ( c.getURI() == null || c.getURI().equals( NOTHING ) ) continue;

            if ( c.isRestriction() ) {

                Restriction restriction = c.asRestriction();

                OntProperty onProperty = restriction.getOnProperty();

                if ( !PROPAGATE_FROM_URIS.contains( onProperty.getURI() ) ) {
                    continue;
                }

                Resource r = getRestrictionValue( restriction );
                if ( r == null ) continue;

                OntologyTerm child = fromOntClass( ( OntClass ) r );

                // avoid risk of endless regression.
                if ( !work.contains( child ) ) {
                    work.add( child );
                    if ( !direct ) ( ( OntologyTermImpl ) child ).getChildren( false, work );
                }
            } else {
                OntologyTerm child = this.fromOntClass( c );
                work.add( child );
                if ( !direct ) ( ( OntologyTermImpl ) child ).getChildren( false, work );
            }
            // log.info( c );
        }

        ExtendedIterator<OntClass> sciterator = this.ontResource.listSuperClasses( true );
        while ( sciterator.hasNext() ) {
            OntClass c = sciterator.next();
            if ( !c.isRestriction() ) {
                continue;
            }

            Restriction restriction = c.asRestriction();

            try {
                OntProperty onProperty = restriction.getOnProperty();
                if ( !PROPAGATE_FROM_URIS.contains( onProperty.getURI() ) ) {
                    continue;
                }
            } catch ( ConversionException e ) {
                continue;
            }

            Resource r = getRestrictionValue( restriction );
            if ( r == null ) continue;

            // if ( !( r instanceof OntClass ) ) {
            // // means our owl file is incomplete, is in tests.
            // log.info( r );
            // continue;
            // }

            OntologyTerm child = fromOntClass( ( OntClass ) r );
            if ( !work.contains( child ) ) {
                work.add( child );
                if ( !direct ) ( ( OntologyTermImpl ) child ).getChildren( false, work );
            }

        }
    }

    /**
     * @param direct
     * @param work
     */
    private void getParents( boolean direct, Collection<OntologyTerm> work ) {
        assert work != null;
        if ( !ontResource.isClass() ) {
            return;
        }

        ExtendedIterator<OntClass> iterator = ontResource.listSuperClasses( true );

        while ( iterator.hasNext() ) {

            try {
                OntClass c = iterator.next();

                if ( c.isRestriction() ) {
                    Restriction restriction = c.asRestriction();

                    OntProperty onProperty = restriction.getOnProperty();

                    assert onProperty != null;

                    // We ignore this... hack.
                    if ( !PROPAGATE_INTO_URIS.contains( onProperty.getURI() ) ) {
                        continue;
                    }

                    Resource r = getRestrictionValue( restriction );

                    if ( r == null ) continue;

                    if ( log.isDebugEnabled() ) log.debug( " Some from:" + r + " " + onProperty.getURI() );

                    OntologyTerm parent = fromOntClass( ( OntClass ) r );

                    if ( REJECT_PARENT_URIS.contains( parent.getUri() ) ) continue;

                    // avoid endless regression
                    if ( !work.contains( parent ) ) {
                        work.add( parent );
                        if ( !direct ) ( ( OntologyTermImpl ) parent ).getParents( direct, work );
                    }

                } else {
                    // not a restriction.
                    OntologyTerm parent = this.fromOntClass( c );

                    if ( REJECT_PARENT_URIS.contains( parent.getUri() ) ) continue;

                    if ( !work.contains( parent ) ) {
                        work.add( parent );
                        if ( !direct ) {
                            // recurse.
                            ( ( OntologyTermImpl ) parent ).getParents( direct, work );
                        }
                    }
                }
            } catch ( ConversionException e ) {
                if ( log.isDebugEnabled() ) log.debug( e.getMessage() );
            }

        }
    }

    private Resource getRestrictionValue( Restriction restriction ) {
        Resource r = null;

        if ( restriction.isSomeValuesFromRestriction() ) {
            SomeValuesFromRestriction some = restriction.asSomeValuesFromRestriction();
            r = some.getSomeValuesFrom();
        } else if ( restriction.isAllValuesFromRestriction() ) {
            AllValuesFromRestriction allValues = restriction.asAllValuesFromRestriction();
            r = allValues.getAllValuesFrom();
        }
        return r;
    }

}
