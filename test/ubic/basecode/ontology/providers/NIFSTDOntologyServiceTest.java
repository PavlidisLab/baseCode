package ubic.basecode.ontology.providers;

import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;

public class NIFSTDOntologyServiceTest extends AbstractOntologyTest {

    @Test
    public void test() {
        NIFSTDOntologyService service = new NIFSTDOntologyService();
        service.initialize( true, false );
    }
}
