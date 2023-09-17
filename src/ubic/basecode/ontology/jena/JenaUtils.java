package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.hp.hpl.jena.reasoner.ReasonerRegistry.makeDirect;

class JenaUtils {

    protected static Logger log = LoggerFactory.getLogger( JenaUtils.class );

    public static Collection<OntClass> getParents( OntModel model, Collection<OntClass> ontClasses, boolean direct, @Nullable Set<Restriction> additionalRestrictions ) {
        Collection<OntClass> parents = getParentsInternal( model, ontClasses, direct, additionalRestrictions );
        if ( shouldRevisit( parents, direct, model, additionalRestrictions ) ) {
            // if there are some missing direct parents, revisit the hierarchy
            Set<OntClass> parentsToRevisit = new HashSet<>( parents );
            while ( !parentsToRevisit.isEmpty() ) {
                log.debug( "Revisiting the direct parents of {} terms...", parentsToRevisit.size() );
                parentsToRevisit = new HashSet<>( getParentsInternal( model, parentsToRevisit, true, additionalRestrictions ) );
                parentsToRevisit.removeAll( parents );
                log.debug( "Found {} missed parents.", parentsToRevisit.size() );
                parents.addAll( parentsToRevisit );
            }
        }
        return parents;
    }

    private static Collection<OntClass> getParentsInternal( OntModel model, Collection<OntClass> ontClasses, boolean direct, @Nullable Set<Restriction> additionalRestrictions ) {
        ontClasses = ontClasses.stream()
                .map( t -> t.inModel( model ) )
                .filter( t -> t.canAs( OntClass.class ) )
                .map( t -> t.as( OntClass.class ) )
                .collect( Collectors.toList() );
        if ( ontClasses.isEmpty() ) {
            return Collections.emptySet();
        }
        Iterator<OntClass> it = ontClasses.iterator();
        ExtendedIterator<OntClass> iterator = it.next().listSuperClasses( direct );
        while ( it.hasNext() ) {
            iterator = iterator.andThen( it.next().listSuperClasses( direct ) );
        }

        Collection<OntClass> result = new HashSet<>();
        while ( iterator.hasNext() ) {
            OntClass c = iterator.next();

            // handles part of some {parent container} or part of all {parent container}
            if ( additionalRestrictions != null && !additionalRestrictions.isEmpty() && c.isRestriction() ) {
                Restriction r = c.asRestriction();
                if ( additionalRestrictions.contains( r ) ) {
                    Resource value = getRestrictionValue( r );
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

            // owl:Thing
            if ( c.equals( model.getProfile().THING() ) )
                continue;

            result.add( c );
        }

        return result;
    }

    public static Collection<OntClass> getChildren( OntModel model, Collection<OntClass> terms, boolean direct, @Nullable Set<Restriction> additionalRestrictions ) {
        Collection<OntClass> children = getChildrenInternal( model, terms, direct, additionalRestrictions );
        if ( shouldRevisit( children, direct, model, additionalRestrictions ) ) {
            // if there are some missing direct children, revisit the hierarchy
            Set<OntClass> childrenToRevisit = new HashSet<>( children );
            while ( !childrenToRevisit.isEmpty() ) {
                log.debug( "Revisiting the direct parents of {} terms...", childrenToRevisit.size() );
                childrenToRevisit = new HashSet<>( JenaUtils.getChildrenInternal( model, childrenToRevisit, true, additionalRestrictions ) );
                childrenToRevisit.removeAll( children );
                log.debug( "Found {} missed children.", childrenToRevisit.size() );
                children.addAll( childrenToRevisit );
            }
        }
        return children;
    }

    public static Collection<OntClass> getChildrenInternal( OntModel model, Collection<OntClass> terms, boolean direct, @Nullable Set<Restriction> additionalRestrictions ) {
        terms = terms.stream()
                .map( t -> t.inModel( model ) )
                .filter( t -> t.canAs( OntClass.class ) )
                .map( t -> t.as( OntClass.class ) )
                .collect( Collectors.toList() );
        if ( terms.isEmpty() ) {
            return Collections.emptySet();
        }
        StopWatch timer = StopWatch.createStarted();
        Iterator<OntClass> it = terms.iterator();
        ExtendedIterator<OntClass> iterator = it.next().listSubClasses( direct );
        while ( it.hasNext() ) {
            iterator = iterator.andThen( it.next().listSubClasses( direct ) );
        }
        Set<OntClass> result = iterator
                .filterDrop( new BnodeFilter<>() )
                .filterDrop( new PredicateFilter<>( o -> o.equals( model.getProfile().NOTHING() ) ) )
                .toSet();
        if ( additionalRestrictions != null && !additionalRestrictions.isEmpty() ) {
            timer.reset();
            timer.start();
            Property subClassOf = model.getProfile().SUB_CLASS_OF();
            if ( direct ) {
                subClassOf = ResourceFactory.createProperty( makeDirect( subClassOf.getURI() ) );
            }
            Set<Restriction> restrictions = UniqueExtendedIterator.create( additionalRestrictions.iterator() )
                    .filterKeep( new RestrictionWithValuesFromFilter( terms ) )
                    .toSet();
            for ( Restriction r : restrictions ) {
                result.addAll( model.listResourcesWithProperty( subClassOf, r )
                        .filterDrop( new BnodeFilter<>() )
                        .mapWith( r2 -> r2.as( OntClass.class ) )
                        .toSet() );
            }
        }
        return result;
    }

    /**
     * Check if a set of terms should be revisited to find missing parents or children.
     * <p>
     * To be considered, the model must have a reasoner that lacks one of {@code rdfs:subClassOf}, {@code owl:subValuesFrom}
     * or {@code owl:allValuesFrom} inference capabilities. If a model has no reasoner, revisiting is not desirable and
     * thus this will return false.
     * <p>
     * If direct is false or terms is empty, it's not worth revisiting.
     */
    private static boolean shouldRevisit( Collection<OntClass> terms, boolean direct, OntModel model, @Nullable Set<Restriction> additionalRestrictions ) {
        return !direct
                && !terms.isEmpty()
                && additionalRestrictions != null
                && model.getReasoner() != null
                && ( !supportsSubClassInference( model ) || !supportsAdditionalRestrictionsInference( model ) );
    }

    /**
     * Check if an ontology model supports inference of {@code rdfs:subClassOf}.
     */
    public static boolean supportsSubClassInference( OntModel model ) {
        return model.getReasoner() != null
                && model.getReasoner().supportsProperty( model.getProfile().SUB_CLASS_OF() );
    }

    /**
     * Checks if an ontology model supports inference of with additional restrictions.
     * <p>
     * This covers {@code owl:subValuesFrom} and {@code owl:allValuesFrom} restrictions.
     */
    public static boolean supportsAdditionalRestrictionsInference( OntModel model ) {
        return model.getReasoner() != null
                && model.getReasoner().supportsProperty( model.getProfile().SOME_VALUES_FROM() )
                && model.getReasoner().supportsProperty( model.getProfile().ALL_VALUES_FROM() );
    }

    public static Resource getRestrictionValue( Restriction r ) {
        if ( r.isSomeValuesFromRestriction() ) {
            return r.asSomeValuesFromRestriction().getSomeValuesFrom();
        } else if ( r.isAllValuesFromRestriction() ) {
            return r.asAllValuesFromRestriction().getAllValuesFrom();
        } else {
            return null;
        }
    }


    /**
     * Use to pretty-print a RDFNode
     */
    public static String asString( RDFNode object ) {
        return ( String ) object.visitWith( new RDFVisitor() {

            @Override
            public Object visitBlank( Resource r, AnonId id ) {
                return r.getLocalName();
            }

            @Override
            public Object visitLiteral( Literal l ) {
                return l.toString().replaceAll( "\\^\\^.+", "" );
            }

            @Override
            public Object visitURI( Resource r, String uri ) {
                return r.getLocalName();
            }
        } );
    }

    public static <T> Filter<T> where( Predicate<T> predicate ) {
        return new PredicateFilter<>( predicate );
    }
}
