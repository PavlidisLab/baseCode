package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.Filter;

/**
 * Detect bnodes, which are resources with null URIs.
 *
 * @param <T>
 */
public class BnodeFilter<T extends Resource> extends Filter<T> {

    @Override
    public boolean accept( T o ) {
        return o.getURI() == null;
    }
}
