package ubic.basecode.ontology.simple;

import ubic.basecode.ontology.model.OntologyResource;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author poirigui
 */
public abstract class AbstractOntologyResourceSimple implements OntologyResource, Serializable {

    @Nullable
    private final String uri, label;

    protected AbstractOntologyResourceSimple( @Nullable String uri, @Nullable String label ) {
        this.uri = uri;
        this.label = label;
    }

    @Override
    public String getLocalName() {
        return uri;
    }

    @Nullable
    @Override
    public String getLabel() {
        return label;
    }

    @Nullable
    @Override
    public String getComment() {
        return null;
    }

    @Override
    @Nullable
    public String getUri() {
        return uri;
    }

    @Override
    public boolean isObsolete() {
        return false;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final OntologyResource other = ( OntologyResource ) obj;
        if ( getLabel() == null ) {
            if ( other.getLabel() != null ) return false;
        } else if ( !getLabel().equals( other.getLabel() ) ) return false;
        if ( getUri() == null ) {
            if ( other.getUri() != null ) return false;
        } else if ( !getUri().equals( other.getUri() ) ) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash( label, uri );
    }
}
