package ubic.basecode.ontology.model;

/**
 * Represents a statement triplet in an ontology.
 * @author poirigui
 */
public interface OntologyStatement {

    OntologyResource getSubject();

    OntologyProperty getPredicate();

    OntologyResource getObject();
}
