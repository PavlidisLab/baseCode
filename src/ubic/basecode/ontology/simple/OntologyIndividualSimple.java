package ubic.basecode.ontology.simple;

import ubic.basecode.ontology.model.OntologyIndividual;

import javax.annotation.Nullable;

/**
 * @author poirigui
 */
public class OntologyIndividualSimple extends AbstractOntologyResourceSimple implements OntologyIndividual {

    private final OntologyTermSimple instanceOf;

    public OntologyIndividualSimple( @Nullable String uri, @Nullable String label, @Nullable OntologyTermSimple instanceOf ) {
        this( uri, null, label, instanceOf );
    }

    /**
     * Create a new simple ontology individual.
     * @param uri        a URI for the term, of null for a free-text term
     * @param label      a label for the term
     * @param instanceOf the term this individual is an instance of which must be simple since this class has to be
     *                   {@link java.io.Serializable}.
     */
    public OntologyIndividualSimple( @Nullable String uri, @Nullable String localName, @Nullable String label, @Nullable OntologyTermSimple instanceOf ) {
        super( uri, localName, label );
        this.instanceOf = instanceOf;
    }

    @Nullable
    @Override
    public String getComment() {
        return null;
    }

    @Override
    public boolean isObsolete() {
        return false;
    }

    @Nullable
    @Override
    public OntologyTermSimple getInstanceOf() {
        return instanceOf;
    }
}