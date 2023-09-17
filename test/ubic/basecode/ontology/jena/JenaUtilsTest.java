package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.junit.Test;

import static org.junit.Assert.*;
import static ubic.basecode.ontology.jena.JenaUtils.supportsAdditionalRestrictionsInference;
import static ubic.basecode.ontology.jena.JenaUtils.supportsSubClassInference;

public class JenaUtilsTest {
    @Test
    public void testInferenceCapabilities() {
        OntModel model;

        model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        assertNull( model.getReasoner() );
        assertFalse( supportsSubClassInference( model ) );
        assertFalse( supportsAdditionalRestrictionsInference( model ) );

        model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_TRANS_INF );
        assertNotNull( model.getReasoner() );
        assertTrue( supportsSubClassInference( model ) );
        assertFalse( supportsAdditionalRestrictionsInference( model ) );

        model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF );
        assertNotNull( model.getReasoner() );
        assertTrue( supportsSubClassInference( model ) );
        assertFalse( supportsAdditionalRestrictionsInference( model ) );

        model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MINI_RULE_INF );
        assertNotNull( model.getReasoner() );
        assertTrue( supportsSubClassInference( model ) );
        assertFalse( supportsAdditionalRestrictionsInference( model ) );

        model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_RULE_INF );
        assertNotNull( model.getReasoner() );
        assertTrue( supportsSubClassInference( model ) );
        assertTrue( supportsAdditionalRestrictionsInference( model ) );
    }
}