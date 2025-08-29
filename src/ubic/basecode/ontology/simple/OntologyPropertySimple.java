package ubic.basecode.ontology.simple;

import ubic.basecode.ontology.model.OntologyProperty;

import javax.annotation.Nullable;

/**
 * Simple in-memory implementation of {@link OntologyProperty}.
 * @author poirigui
 */
public class OntologyPropertySimple extends AbstractOntologyResourceSimple implements OntologyProperty {

    /**
     *
     * @param uri   an URI or null if this is a free-text property
     * @param label a label for the property
     */
    public OntologyPropertySimple( @Nullable String uri, @Nullable String label ) {
        super( uri, label );
    }

    @Override
    public boolean isFunctional() {
        return false;
    }
}
