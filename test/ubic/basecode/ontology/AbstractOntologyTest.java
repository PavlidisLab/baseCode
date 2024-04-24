package ubic.basecode.ontology;

import org.apache.commons.io.file.PathUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ubic.basecode.util.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base class for ontology-based tests.
 * <p>
 * This class ensures that the ontology are stored and indexed in a temporary directory.
 */
public class AbstractOntologyTest {

    protected static Path tempDir;

    @BeforeClass
    public static void setUpOntologyCacheDir() throws IOException {
        tempDir = Files.createTempDirectory( "baseCode" );
        Configuration.setString( "ontology.cache.dir", tempDir.resolve( "ontologyCache" ).toAbsolutePath().toString() );
        Configuration.setString( "ontology.index.dir", tempDir.resolve( "searchIndices" ).toAbsolutePath().toString() );
    }

    @AfterClass
    public static void clearOntologyCacheDir() throws IOException {
        try {
            PathUtils.deleteDirectory( tempDir );
        } finally {
            Configuration.reset( "ontology.cache.dir" );
            Configuration.reset( "ontology.index.dir" );
        }
    }
}
