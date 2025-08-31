package ubic.basecode.ontology.simple;

import ubic.basecode.ontology.model.OntologyProperty;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.basecode.ontology.model.OntologyStatement;

import java.util.Objects;

/**
 * @author poirigui
 */
public class OntologyStatementSimple implements OntologyStatement {

    private final OntologyResource subject;
    private final OntologyProperty predicate;
    private final OntologyResource object;

    public OntologyStatementSimple( OntologyResource subject, OntologyProperty predicate, OntologyResource object ) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public OntologyResource getSubject() {
        return subject;
    }

    @Override
    public OntologyProperty getPredicate() {
        return predicate;
    }

    @Override
    public OntologyResource getObject() {
        return object;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( !( obj instanceof OntologyStatement ) ) {
            return false;
        }
        return Objects.equals( subject, ( ( OntologyStatement ) obj ).getSubject() )
            && Objects.equals( predicate, ( ( OntologyStatement ) obj ).getPredicate() )
            && Objects.equals( object, ( ( OntologyStatement ) obj ).getObject() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( subject, predicate, object );
    }
}
