package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.Filter;

import java.util.Set;

/**
 * Filter that retain only the restrictions on any of the given properties.
 */
class RestrictionWithOnPropertyFilter extends Filter<Restriction> {
    private final Set<Property> properties;

    public RestrictionWithOnPropertyFilter( Set<Property> properties ) {
        this.properties = properties;
    }

    @Override
    public boolean accept( Restriction o ) {
        return properties.contains( o.getOnProperty() );
    }
}
