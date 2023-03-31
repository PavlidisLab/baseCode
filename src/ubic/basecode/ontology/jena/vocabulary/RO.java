package ubic.basecode.ontology.jena.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RO {

    public static final Property properPartOf = ResourceFactory.createProperty( "http://www.obofoundry.org/ro/ro.owl#proper_part_of" );
}
