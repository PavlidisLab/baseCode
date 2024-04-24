package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

class RO {

    /**
     * This is actually part of RO, see
     */
    public static final Property partOf = ResourceFactory.createProperty( "http://purl.obolibrary.org/obo/BFO_0000050" );
    public static final Property properPartOf = ResourceFactory.createProperty( "http://www.obofoundry.org/ro/ro.owl#proper_part_of" );
}
