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
import com.hp.hpl.jena.util.iterator.Filter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a class in an ontology
 *
 * @author Paul
 */
public class OntologyTermImpl extends AbstractOntologyResource implements OntologyTerm {

    private static final String HAS_ALTERNATE_ID = "http://www.geneontology.org/formats/oboInOwl#hasAlternativeId";
    private static final String NOTHING = "http://www.w3.org/2002/07/owl#Nothing";

    /**
     * Properties through which propagation is allowed for {@link #getParents(boolean)}
     */
    private static final Set<String> PROPAGATE_PARENT_URIS = new HashSet<>();

    private static final Set<String> REJECT_PARENT_URIS = new HashSet<>();

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    static {
        CollectionUtils.addAll( PROPAGATE_PARENT_URIS,
                "http://www.obofoundry.org/ro/ro.owl#proper_part_of",
                "http://purl.obolibrary.org/obo/BFO_0000050" // part of
        );
        CollectionUtils.addAll( REJECT_PARENT_URIS,
                "http://www.ifomis.org/bfo/1.1/snap#IndependentContinuant",
                "http://www.ifomis.org/bfo/1.1/snap#Continuant",
                "http://www.ifomis.org/bfo/1.1/snap#MaterialEntity",
                // anatomical entity
                "http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GrossAnatomy.owl#birnlex_6" );
    }

    private String label = null;
    private String localName = null;

    /**
     * Ontology class underlying this term.
     */
    private final transient OntClass ontResource;

    /**
     * Extra sets of properties to use when navigating parents and children of a term.
     */
    private final transient Set<Property> propagateParentsProperties;

    public OntologyTermImpl( OntClass resource ) {
        this.ontResource = resource;
        if ( ontResource != null ) {
            this.label = ontResource.getLabel( "EN" );
            if ( this.label == null ) this.label = ontResource.getLabel( null );
            this.localName = ontResource.getLocalName();
            this.propagateParentsProperties = PROPAGATE_PARENT_URIS.stream()
                    .map( uri -> resource.getModel().getProperty( uri ) )
                    .filter( Objects::nonNull )
                    .collect( Collectors.toSet() );
        } else {
            this.propagateParentsProperties = Collections.emptySet();
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
        Collection<OntClass> result = new HashSet<>();
        ExtendedIterator<OntClass> iterator = ontResource.listSubClasses( direct )
                .filterDrop( new EqualityByUriFilter( NOTHING ) );
        OntModel model = ontResource.getOntModel();

        while ( iterator.hasNext() ) {
            OntClass c = iterator.next();

            // bnode
            if ( c.getURI() == null )
                continue;

            result.add( c );
        }

        Property subClassOf = model.getProfile().SUB_CLASS_OF();
        ExtendedIterator<Restriction> restrictionsIterator = model.listRestrictions()
                .filterKeep( new RestrictionWithPropertyAndValueFilter( propagateParentsProperties, ontResource ) );
        while ( restrictionsIterator.hasNext() ) {
            Restriction r = restrictionsIterator.next();
            ResIterator ss = model.listResourcesWithProperty( subClassOf, r );
            while ( ss.hasNext() ) {
                Resource s = ss.next();
                if ( s.getURI() != null ) {
                    OntClass o = model.getOntClass( s.getURI() );
                    if ( o != null ) {
                        result.add( o );
                    }
                }
            }
        }

        return result.stream().map( OntologyTermImpl::new ).collect( Collectors.toSet() );
    }

    /**
     * Filter that retain resources with the given URI.
     */
    private static class EqualityByUriFilter extends Filter<OntClass> {
        private final String uri;

        private EqualityByUriFilter( String uri ) {
            this.uri = uri;
        }

        @Override
        public boolean accept( OntClass o ) {
            return uri.equals( o.getURI() );
        }
    }

    /**
     * Filter that retain only the restrictions with any of the given properties and resource as value.
     */
    private static class RestrictionWithPropertyAndValueFilter extends Filter<Restriction> {
        private final Set<Property> properties;
        private final Resource resource;

        private RestrictionWithPropertyAndValueFilter( Set<Property> properties, OntClass resource ) {
            this.properties = properties;
            this.resource = resource;
        }

        @Override
        public boolean accept( Restriction o ) {
            return hasRestrictionValue( o, resource ) && properties.stream().anyMatch( o::onProperty );
        }
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
            OntResource r = iterator.next();
            if ( r.isIndividual() ) {
                inds.add( new OntologyIndividualImpl( r.asIndividual() ) );
            }
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
        Collection<OntClass> result = new HashSet<>();
        ExtendedIterator<OntClass> iterator;
        Set<String> excludeProperties;
        iterator = ontResource.listSuperClasses( direct );
        excludeProperties = REJECT_PARENT_URIS;

        while ( iterator.hasNext() ) {
            OntClass c = iterator.next();

            // handles part of some {parent container} or part of all {parent container}
            if ( c.isRestriction() ) {
                Restriction r = c.asRestriction();
                if ( propagateParentsProperties.contains( r.getOnProperty() ) ) {
                    Resource value = getRestrictionValue( c.asRestriction() );
                    if ( value instanceof OntClass ) {
                        c = ( OntClass ) value;
                    } else {
                        continue;
                    }
                }
            }

            // bnode
            if ( c.getURI() == null )
                continue;

            // excluded terms
            if ( excludeProperties.contains( c.getURI() ) )
                continue;

            // already visited
            if ( result.contains( c ) )
                continue;

            result.add( c );
        }

        return result.stream().map( OntologyTermImpl::new ).collect( Collectors.toSet() );
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
                result.add( RestrictionFactory.asRestriction( c.asRestriction() ) );
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
                    if ( n.toString().contains( "true" ) ) {
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

    private static Resource getRestrictionValue( Restriction r ) {
        if ( r.isSomeValuesFromRestriction() ) {
            return r.asSomeValuesFromRestriction().getSomeValuesFrom();
        } else if ( r.isAllValuesFromRestriction() ) {
            return r.asAllValuesFromRestriction().getAllValuesFrom();
        } else {
            return null;
        }
    }

    private static boolean hasRestrictionValue( Restriction r, Resource value ) {
        if ( r.isSomeValuesFromRestriction() ) {
            return r.asSomeValuesFromRestriction().hasSomeValuesFrom( value );
        } else if ( r.isAllValuesFromRestriction() ) {
            return r.asAllValuesFromRestriction().hasAllValuesFrom( value );
        } else {
            return false;
        }
    }
}
