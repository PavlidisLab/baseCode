package ubic.basecode.ontology.providers;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import org.junit.BeforeClass;
import org.junit.Test;
import ubic.basecode.ontology.jena.OntologyLoader;
import ubic.basecode.ontology.jena.search.OntologyIndexer;
import ubic.basecode.ontology.jena.search.OntologySearch;
import ubic.basecode.ontology.jena.search.SearchIndex;
import ubic.basecode.ontology.search.OntologySearchException;
import ubic.basecode.ontology.search.OntologySearchTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UberonOntologySearchTest {
    private static OntModel uberon;
    private static SearchIndex uberonIndex;

    @BeforeClass
    public static void setUpUberon() throws IOException {
        try ( InputStream is = new GZIPInputStream( requireNonNull( OntologySearchTest.class.getResourceAsStream( "/data/uberon.owl.gz" ) ) ) ) {
            uberon = OntologyLoader.loadMemoryModel( is, "UBERON_TEST2", true, OntModelSpec.OWL_MEM );
            uberonIndex = OntologyIndexer.indexOntology( "UBERON_TEST2", uberon, false );
        }
    }

    @Test
    public void testOmitDefinition() throws OntologySearchException {
        OntClass brain = uberon.getOntClass( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( brain );
        Set<OntologySearch.SearchResult<OntClass>> searchResults = OntologySearch.matchClasses( uberon, uberonIndex, "brain" ).toSet();
        assertEquals( 128, searchResults.size() );
    }

    @Test
    public void testScore() throws OntologySearchException {
        OntClass brain = uberon.getOntClass( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( brain );
        List<OntologySearch.SearchResult<OntClass>> searchResults = OntologySearch.matchClasses( uberon, uberonIndex, "brain" ).toList();
        assertEquals( 446, searchResults.size() );
        assertEquals( 3.33, searchResults.get( 0 ).score, 0.01 );
        assertEquals( 128, new HashSet<>( searchResults ).size() );
    }
}
