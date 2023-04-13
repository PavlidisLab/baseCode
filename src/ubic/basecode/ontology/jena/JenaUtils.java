package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import org.apache.commons.lang3.time.StopWatch;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static com.hp.hpl.jena.reasoner.ReasonerRegistry.makeDirect;

public class JenaUtils {

    public static Collection<OntClass> getParents( OntModel model, Collection<OntClass> ontClasses, boolean direct, @Nullable Set<Restriction> additionalRestrictions ) {
        if ( ontClasses.isEmpty() ) {
            return Collections.emptySet();
        }
        Iterator<OntClass> it = ontClasses.iterator();
        ExtendedIterator<OntClass> iterator = it.next()
                .inModel( model )
                .as( OntClass.class )
                .listSuperClasses( direct );
        while ( it.hasNext() ) {
            iterator = iterator.andThen( it.next().inModel( model ).as( OntClass.class ).listSuperClasses( direct ) );
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
        if ( terms.isEmpty() ) {
            return Collections.emptySet();
        }
        StopWatch timer = StopWatch.createStarted();
        Iterator<OntClass> it = terms.iterator();
        ExtendedIterator<OntClass> iterator = it.next()
                .inModel( model )
                .as( OntClass.class )
                .listSubClasses( direct );
        while ( it.hasNext() ) {
            iterator = iterator.andThen( it.next().inModel( model ).as( OntClass.class ).listSubClasses( direct ) );
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
