package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.junit.BeforeClass;
import org.junit.Test;
import ubic.basecode.ontology.search.OntologySearchException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
            uberon = OntologyLoader.createMemoryModel( is, "UBERON_TEST2", true, OntModelSpec.OWL_MEM );
            HashSet<OntologyIndexer.IndexableProperty> indexableProperties = new HashSet<>( OntologyIndexer.DEFAULT_INDEXABLE_PROPERTIES );
            indexableProperties.add( new OntologyIndexer.IndexableProperty( RDFS.comment, true ) );
            uberonIndex = OntologyIndexer.indexOntology( "UBERON_TEST2", uberon, indexableProperties, Collections.emptySet(), true );
        }
    }

    @Test
    public void testOmitDefinition() throws OntologySearchException {
        OntClass brain = uberon.getOntClass( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( brain );
        List<SearchIndex.JenaSearchResult> searchResults = uberonIndex.searchClasses( uberon, "brain", 500 );
        assertEquals( 128, searchResults.size() );
    }

    @Test
    public void testScore() throws OntologySearchException {
        OntClass brain = uberon.getOntClass( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( brain );
        List<SearchIndex.JenaSearchResult> searchResults = uberonIndex.searchClasses( uberon, "brain", 500 );
        assertEquals( 128, searchResults.size() );
        assertEquals( 3.85, searchResults.get( 0 ).score, 0.01 );
    }
}
