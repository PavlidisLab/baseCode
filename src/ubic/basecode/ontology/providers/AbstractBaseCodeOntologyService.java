package ubic.basecode.ontology.providers;

import ubic.basecode.util.Configuration;

/**
 * Base class for all ontologies built-in to the baseCode project.
 * <p>
 * The ontologies that subclass this will honor settings in the {@code basecode.properties} file for loading and
 * locating the ontology.
 *
 * @author poirigui
 */
public abstract class AbstractBaseCodeOntologyService extends AbstractOntologyService {

    private final String name;
    private final String cacheName;

    /**
     * Intentionally package-private constructor.
     */
    AbstractBaseCodeOntologyService( String name, String cacheName ) {
        this.name = name;
        this.cacheName = cacheName;
    }

    @Override
    protected String getOntologyName() {
        return name;
    }

    @Override
    protected String getOntologyUrl() {
        return Configuration.getString( "url." + cacheName );
    }

    @Override
    protected boolean isOntologyEnabled() {
        return Boolean.TRUE.equals( Configuration.getBoolean( "load." + cacheName ) );
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }
}
