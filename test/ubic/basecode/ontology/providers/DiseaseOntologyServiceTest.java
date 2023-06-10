package ubic.basecode.ontology.providers;

import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;

import static org.junit.Assert.assertTrue;

public class DiseaseOntologyServiceTest extends AbstractOntologyTest {

    @Test
    public void testLoadModelWithImports() {
        DiseaseOntologyService service = new DiseaseOntologyService();
        assertTrue( service.getProcessImports() );
        service.setInferenceMode( OntologyService.InferenceMode.NONE );
        service.setSearchEnabled( false );
        service.initialize( true, false );
    }
}
