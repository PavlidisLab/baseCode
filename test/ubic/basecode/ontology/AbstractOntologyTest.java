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

    private static Path tempDir;
    private static String prevCacheDir, prevIndexDir;

    @BeforeClass
    public static void setUpOntologyCacheDir() throws IOException {
        prevCacheDir = Configuration.getString( "ontology.cache.dir" );
        prevIndexDir = Configuration.getString( "ontology.index.dir" );
        tempDir = Files.createTempDirectory( "ontologyCache" );
        Configuration.setString( "ontology.cache.dir", tempDir.toAbsolutePath().toString() );
        Configuration.setString( "ontology.index.dir", tempDir.resolve( "indices" ).toAbsolutePath().toString() );
    }

    @AfterClass
    public static void clearOntologyCacheDir() throws IOException {
        try {
            PathUtils.deleteDirectory( tempDir );
        } finally {
            Configuration.setString( "ontology.cache.dir", prevCacheDir );
            Configuration.setString( "ontology.index.dir", prevIndexDir );
        }
    }
}
