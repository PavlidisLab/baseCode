package ubic.basecode.ontology.providers;

import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;

public class DiseaseOntologyServiceTest extends AbstractOntologyTest {

    @Test
    public void testLoadModelWithImports() {
        new DiseaseOntologyService().initialize( true, false );
    }
}
