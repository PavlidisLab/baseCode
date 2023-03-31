package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.Filter;

import java.util.Collection;

/**
 * Match {@link Restriction} with values from any of the given resources.
 */
public class RestrictionWithValuesFromFilter extends Filter<Restriction> {

    private final Collection<? extends Resource> resource;

    public RestrictionWithValuesFromFilter( Collection<? extends Resource> resource ) {
        this.resource = resource;
    }

    @Override
    public boolean accept( Restriction o ) {
        return resource.contains( JenaUtils.getRestrictionValue( o ) );
    }
}
