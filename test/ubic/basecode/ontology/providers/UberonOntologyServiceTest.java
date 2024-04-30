package ubic.basecode.ontology.providers;

import com.hp.hpl.jena.vocabulary.OWL2;
import org.junit.BeforeClass;
import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;
import ubic.basecode.ontology.OntologyTermTest;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologySearchException;
import ubic.basecode.ontology.search.OntologySearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.*;

public class UberonOntologyServiceTest extends AbstractOntologyTest {

    private static OntologyService uberon;

    @BeforeClass
    public static void initializeUberon() throws IOException {
        uberon = new UberonOntologyService();
        try ( InputStream is = new GZIPInputStream( requireNonNull( OntologyTermTest.class.getResourceAsStream( "/data/uberon.owl.gz" ) ) ) ) {
            // FIXME: indexing Uberon is very slow, so we disable it so if the tests are breaking, try force-indexing
            uberon.initialize( is, false );
        }
    }

    @Test
    public void testUberon() {
        assertEquals( "Uber-anatomy ontology", uberon.getName() );
        assertNotNull( uberon.getDescription() );

        OntologyTerm t = uberon.getTerm( "http://purl.obolibrary.org/obo/BFO_0000001" );
        assertNotNull( t );
        assertTrue( t.isRoot() );
        assertFalse( t.isObsolete() );

        OntologyTerm t2 = uberon.getTerm( "http://purl.obolibrary.org/obo/BFO_0000002" );
        assertNotNull( t2 );
        assertFalse( t2.isRoot() );
        assertFalse( t2.isObsolete() );

        OntologyTerm t3 = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0007234" );
        assertNotNull( t3 );
        assertTrue( t3.isObsolete() );
    }

    @Test
    public void testGetParentsFromMultipleTerms() {
        OntologyTerm brain = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        OntologyTerm liver = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0002107" );
        Collection<OntologyTerm> children = uberon.getParents( Arrays.asList( brain, liver ), false, true );
        assertEquals( 41, children.size() );
        assertFalse( children.contains( uberon.getTerm( OWL2.Nothing.getURI() ) ) );
    }

    @Test
    public void testGetParentsHasPart() {
        OntologyTerm t = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( t );
        Collection<OntologyTerm> parents = t.getParents( true );
        assertEquals( 4, parents.size() );
        // does not contain itself
        assertFalse( parents.contains( t ) );
        // via subclass
        assertTrue( parents.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0004121" ) ) );
        assertTrue( parents.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000062" ) ) );
        // via part of, central nervous system
        assertTrue( parents.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0001017" ) ) );
        assertFalse( parents.contains( uberon.getTerm( OWL2.Thing.getURI() ) ) );
    }

    @Test
    public void testGetChildrenHasPart() {
        OntologyTerm t = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( t );
        assertEquals( 81, t.getChildren( true ).size() );
        Collection<OntologyTerm> children = t.getChildren( false );
        assertEquals( 1995, children.size() );
        // via subclass of, insect adult brain
        assertTrue( children.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_6003624" ) ) );
        // via part of, nucleus of brain
        assertTrue( children.contains( uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0002308" ) ) );
        // does not contain owl:Nothing
        assertFalse( children.contains( uberon.getTerm( OWL2.Nothing.getURI() ) ) );
    }

    @Test
    public void testGetChildrenFromMultipleTerms() {
        OntologyTerm brain = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        OntologyTerm liver = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0002107" );
        Collection<OntologyTerm> children = uberon.getChildren( Arrays.asList( brain, liver ), false, true );
        assertEquals( 2077, children.size() );
    }

    @Test
    public void testGetChildrenFromMultipleTermsWithSearch() throws OntologySearchException {
        Collection<OntologySearchResult<OntologyTerm>> terms = uberon.findTerm( "brain", 500 );
        Collection<OntologyTerm> matches = uberon.getChildren( terms.stream().map( OntologySearchResult::getResult ).collect( Collectors.toSet() ), false, true );
        assertEquals( 2684, matches.size() );
    }

    @Test
    public void testFindTerm() throws OntologySearchException {
        assertEquals( 98, uberon.findTerm( "brain", 500 ).size() );
        assertEquals( 103, uberon.findTerm( "brain", 500, true ).size() );
        OntologySearchResult<OntologyTerm> firstResult = uberon.findTerm( "brain", 500 ).iterator().next();
        assertNotNull( firstResult );
        assertEquals( 1.5367, firstResult.getScore(), 0.0001 );
    }
}
