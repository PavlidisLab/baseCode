package ubic.basecode.ontology.providers;

import ubic.basecode.ontology.jena.ClasspathOntologyService;
import ubic.basecode.util.Configuration;

/**
 * @author paul
 */
@Deprecated
public class NIFSTDOntologyService extends AbstractDelegatingOntologyService {

    private static final String NIFSTD_ONTOLOGY_FILE = "/data/loader/ontology/nif-gemma.owl.gz";

    public NIFSTDOntologyService() {
        super( new ClasspathOntologyService( "NIFSTD", NIFSTD_ONTOLOGY_FILE,
            Boolean.TRUE.equals( Configuration.getBoolean( "load.nifstdOntology" ) ), "nifstdOntology" ) );
        setProcessImports( false );
    }
}
