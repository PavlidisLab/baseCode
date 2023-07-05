package ubic.basecode.ontology.model;

public interface OntologyModel {

    /**
     * Unwrap the underlying implementation of the ontology model.
     * @throws ClassCastException if the implementation type does not match the given class
     */
    <T> T unwrap( Class<T> clazz ) throws ClassCastException;
}
