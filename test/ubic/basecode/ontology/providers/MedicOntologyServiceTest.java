package ubic.basecode.ontology.providers;

import org.junit.Test;

public class MedicOntologyServiceTest {

    @Test
    public void test() {
        new MedicOntologyService().initialize( true, false );
    }
}
