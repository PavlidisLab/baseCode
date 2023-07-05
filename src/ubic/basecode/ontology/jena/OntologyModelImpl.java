package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntModel;
import ubic.basecode.ontology.model.OntologyModel;

class OntologyModelImpl implements OntologyModel {

    private final OntModel ontModel;

    public OntologyModelImpl( OntModel ontModel ) {
        this.ontModel = ontModel;
    }

    @Override
    public <T> T unwrap( Class<T> clazz ) {
        return clazz.cast( ontModel );
    }
}
