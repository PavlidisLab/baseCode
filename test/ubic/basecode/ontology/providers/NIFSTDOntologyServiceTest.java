package ubic.basecode.ontology.providers;

import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NIFSTDOntologyServiceTest extends AbstractOntologyTest {

    @Test
    public void test() {
        NIFSTDOntologyService service = new NIFSTDOntologyService();
        assertEquals( OntologyService.InferenceMode.TRANSITIVE, service.getInferenceMode() );
        assertFalse( service.getProcessImports() );
        service.initialize( true, false );
    }
}
