package ubic.basecode.ontology.providers;

import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test initialization of all providers not already covered by a specific test.
 *
 * @author poirigui
 */
public class OntologyProvidersTest extends AbstractOntologyTest {

    private final OntologyService[] providers = {
            new CellLineOntologyService(),
            new CellTypeOntologyService(),
            new ChebiOntologyService(),
            // new DiseaseOntologyService(),
            new ExperimentalFactorOntologyService(),
            new FMAOntologyService(),
            new HumanPhenotypeOntologyService(),
            new MammalianPhenotypeOntologyService(),
            // new MedicOntologyService(),
            new MouseDevelopmentOntologyService(),
            // new NIFSTDOntologyService(),
            // new ObiService(),
            new SequenceOntologyService(),
            // new UberonOntologyService(),
            new UnitsOntologyService()
    };

    @Test
    public void testInitializeAllProviders() throws InterruptedException {
        for ( OntologyService provider : providers ) {
            provider.setInferenceMode( OntologyService.InferenceMode.NONE );
            provider.setSearchEnabled( false );
            provider.startInitializationThread( true, false );
        }
        for ( OntologyService provider : providers ) {
            provider.waitForInitializationThread();
            assertTrue( provider.isOntologyLoaded() );
            assertFalse( provider.isSearchEnabled() );
        }
    }
}
