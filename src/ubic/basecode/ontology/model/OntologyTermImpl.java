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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.SomeValuesFromRestriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Represents a class in an ontology
 * 
 * @author Paul
 * @version $Id$
 */
public class OntologyTermImpl extends AbstractOntologyResource implements OntologyTerm {

    private static final String HAS_ALTERNATE_ID = "http://www.geneontology.org/formats/oboInOwl#hasAlternativeId";
    private static final String NOTHING = "http://www.w3.org/2002/07/owl#Nothing";
    private static Set<String> REJECT_PARENT_URI = new HashSet<String>();

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Should has_proper_part be used to indicate additional parent/child relations.
     */
    private static final boolean USE_PROPER_PART_RESTRICTIONS = true;
    static {
        REJECT_PARENT_URI.add( "http://www.ifomis.org/bfo/1.1/snap#IndependentContinuant" );
        REJECT_PARENT_URI.add( "http://www.ifomis.org/bfo/1.1/snap#Continuant" );
        REJECT_PARENT_URI.add( "http://www.ifomis.org/bfo/1.1/snap#MaterialEntity" );

        // anatomical entity
        REJECT_PARENT_URI.add( "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_6" );
    }

    private String label = null;
    private String localName = null;

    private transient OntClass ontResource = null;

    public OntologyTermImpl( OntClass resource ) {
        this.ontResource = resource;
        if ( ontResource != null ) {
            this.label = ontResource.getLabel( null );
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
            return ObjectUtils.equals( this.getUri(), that.getUri() );
        }
        return ObjectUtils.equals( this.getTerm(), that.getTerm() );
    }

    @Override
    public Collection<String> getAlternativeIds() {
        Collection<String> results = new HashSet<String>();

        Property alternate = ResourceFactory.createProperty( HAS_ALTERNATE_ID );
        for ( StmtIterator it = this.ontResource.listProperties( alternate ); it.hasNext(); ) {
            Statement statement = it.next();
            results.add( statement.asTriple().getMatchObject().getLiteralLexicalForm() );
        }

        return results;

    }

    @Override
    public Collection<AnnotationProperty> getAnnotations() {
        Collection<AnnotationProperty> annots = new HashSet<AnnotationProperty>();
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
        Collection<OntologyTerm> result = new HashSet<OntologyTerm>();
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

    /**
     * @param direct
     * @return
     */
    @Override
    public Collection<OntologyIndividual> getIndividuals( boolean direct ) {
        Collection<OntologyIndividual> inds = new HashSet<OntologyIndividual>();
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
        Collection<OntologyTerm> result = new HashSet<OntologyTerm>();
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
        Collection<OntologyRestriction> result = new HashSet<OntologyRestriction>();
        ExtendedIterator<OntClass> iterator = ontResource.listSuperClasses( false );
        while ( iterator.hasNext() ) {
            OntClass c = iterator.next();
            Restriction r = null;
            try {
                r = c.asRestriction();
                result.add( RestrictionFactory.asRestriction( r ) );
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
                loop: while ( supClassesIt.hasNext() ) {
                    OntClass sc = supClassesIt.next();
                    Restriction sr = null;
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
        String res = null;
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
                log.warn( "URI for " + parentOntologyTerm + " was null" );
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
            // some reasoners will infer owl#Nothing as a subclass of everything
            if ( c.getURI().equals( NOTHING ) ) continue;

            if ( USE_PROPER_PART_RESTRICTIONS && c.isRestriction() ) {

                Restriction restriction = c.asRestriction();

                OntProperty onProperty = restriction.getOnProperty();

                if ( !onProperty.getURI().equals( "http://www.obofoundry.org/ro/ro.owl#has_proper_part" ) ) {
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

        if ( USE_PROPER_PART_RESTRICTIONS ) {
            ExtendedIterator<OntClass> sciterator = this.ontResource.listSuperClasses( true );
            while ( sciterator.hasNext() ) {
                OntClass c = sciterator.next();
                if ( !c.isRestriction() ) {
                    continue;
                }

                Restriction restriction = c.asRestriction();

                OntProperty onProperty = restriction.getOnProperty();

                if ( !onProperty.getURI().equals( "http://www.obofoundry.org/ro/ro.owl#has_proper_part" ) ) {
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
            OntClass c = iterator.next();
            if ( USE_PROPER_PART_RESTRICTIONS && c.isRestriction() ) {
                Restriction restriction = c.asRestriction();

                OntProperty onProperty = restriction.getOnProperty();

                if ( !onProperty.getURI().equals( "http://www.obofoundry.org/ro/ro.owl#proper_part_of" ) ) {
                    continue;
                }

                Resource r = getRestrictionValue( restriction );

                if ( r == null ) continue;

                assert r != null;
                if ( log.isDebugEnabled() ) log.debug( " Some from:" + r + " " + onProperty.getURI() );

                OntologyTerm parent = fromOntClass( ( OntClass ) r );

                if ( REJECT_PARENT_URI.contains( parent.getUri() ) ) continue;

                // avoid endless regression
                if ( !work.contains( parent ) ) {
                    work.add( parent );
                    if ( !direct ) ( ( OntologyTermImpl ) parent ).getParents( direct, work );
                }

            } else {
                // not a restriction.
                OntologyTerm parent = this.fromOntClass( c );

                if ( REJECT_PARENT_URI.contains( parent.getUri() ) ) continue;

                if ( !work.contains( parent ) ) {
                    work.add( parent );
                    if ( !direct ) {
                        // recurse.
                        ( ( OntologyTermImpl ) parent ).getParents( direct, work );
                    }
                }
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
