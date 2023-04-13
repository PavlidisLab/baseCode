package ubic.basecode.ontology.providers;

import com.hp.hpl.jena.vocabulary.OWL2;
import org.junit.BeforeClass;
import org.junit.Test;
import ubic.basecode.ontology.AbstractOntologyTest;
import ubic.basecode.ontology.OntologyTermTest;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.search.OntologySearchException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class UberonServiceTest extends AbstractOntologyTest {

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
        assertEquals( 30, children.size() );
        assertFalse( children.contains( uberon.getTerm( OWL2.Nothing.getURI() ) ) );
    }

    @Test
    public void testGetParentsHasPart() {
        OntologyTerm t = uberon.getTerm( "http://purl.obolibrary.org/obo/UBERON_0000955" );
        assertNotNull( t );
        Collection<OntologyTerm> parents = t.getParents( true );
        assertEquals( 3, parents.size() );
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
        assertEquals( 76, t.getChildren( true ).size() );
        Collection<OntologyTerm> children = t.getChildren( false );
        assertEquals( 1496, children.size() );
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
        assertEquals( 1562, children.size() );
    }

    @Test
    public void testGetChildrenFromMultipleTermsWithSearch() throws OntologySearchException {
        Collection<OntologyTerm> terms = uberon.findTerm( "brain" );
        Collection<OntologyTerm> matches = uberon.getChildren( terms, false, true );
        assertEquals( 1870, matches.size() );
    }

    @Test
    public void testFindTerm() throws OntologySearchException {
        assertEquals( 123, uberon.findTerm( "brain" ).size() );
        assertEquals( 128, uberon.findTerm( "brain", true ).size() );
        OntologyTerm firstResult = uberon.findTerm( "brain" ).iterator().next();
        assertNotNull( firstResult.getScore() );
        assertEquals( 2.8577, firstResult.getScore(), 0.0001 );
    }
}
