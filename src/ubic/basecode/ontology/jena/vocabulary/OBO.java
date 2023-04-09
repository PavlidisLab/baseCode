package ubic.basecode.ontology.jena.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class OBO {

    private static final String NS = "http://www.geneontology.org/formats/oboInOwl" + "#";

    private static Property property( String name ) {
        return ResourceFactory.createProperty( NS + name );
    }

    public static final Property id = property( "id" );
    public static final Property hasDbXref = property( "hasDbXref" );
    public static final Property hasSynonym = property( "hasSynonym" );
    public static final Property hasExactSynonym = property( "hasExactSynonym" );
    public static final Property hasBroadSynonym = property( "hasBroadSynonm" );
    public static final Property hasNarrowSynonym = property( "hasNarrowSynonym" );
    public static final Property hasRelatedSynonym = property( "hasRelatedSynonym" );
    public static final Property alternativeLabel = ResourceFactory.createProperty( "http://purl.obolibrary.org/obo/IAO_0000118" );
    public static final Resource ObsoleteClass = ResourceFactory.createResource( "http://www.geneontology.org/formats/oboInOwl#ObsoleteClass" );
    public static final Property ObsoleteProperty = ResourceFactory.createProperty( "http://www.geneontology.org/formats/oboInOwl#ObsoleteProperty" );
}
