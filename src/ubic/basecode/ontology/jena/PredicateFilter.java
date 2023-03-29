package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.util.iterator.Filter;

import java.util.function.Predicate;

/**
 * Iterator filter based on a {@link Predicate}.
 *
 * @param <T>
 */
public class PredicateFilter<T> extends Filter<T> {
    private final Predicate<T> predicate;

    public PredicateFilter( Predicate<T> predicate ) {
        this.predicate = predicate;
    }

    @Override
    public boolean accept( T o ) {
        return predicate.test( o );
    }
}
