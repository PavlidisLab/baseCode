package ubic.basecode.ontology.providers;

import ubic.basecode.ontology.Configuration;

public class NIFSTDOntologyService extends AbstractOntologyMemoryBackedService {

    private static final String NIFSTD_ONTOLOGY_URL = "url.nifstdOntology";
    
    @Override
    protected String getOntologyName() {
        return "nifstdOntology";
    }

    @Override
    protected String getOntologyUrl() {
        return Configuration.getString(NIFSTD_ONTOLOGY_URL);
    }

}
