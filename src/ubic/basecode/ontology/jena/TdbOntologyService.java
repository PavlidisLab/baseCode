package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import org.apache.commons.io.file.PathUtils;
import ubic.basecode.ontology.model.OntologyModel;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An implementation based on Jena TDB.
 * @author poirigui
 */
public class TdbOntologyService extends AbstractOntologyService {

    private final Path tdbDir;
    private final String modelName;
    private final boolean readOnly;

    @Nullable
    private Dataset dataset;

    /**
     * Temporary d
     */
    private Path tempDir;

    /**
     * @param readOnly open the TDB database in read-only mode, allowing multiple JVMs to share a common TDB. For this
     *                 to work safely, all the TDB files must not be writable. Additionally, a temporary directory with
     *                 symbolic links will be created since Jena will still be using a lock file.
     */
    public TdbOntologyService( String ontologyName, Path tdbDir, @Nullable String modelName, boolean ontologyEnabled, @Nullable String cacheName, boolean readOnly ) {
        super( ontologyName, tdbDir.toUri().toString(), ontologyEnabled, cacheName );
        this.tdbDir = tdbDir;
        this.modelName = modelName;
        this.readOnly = readOnly;
    }

    @Override
    protected OntologyModel loadModel( boolean processImports, LanguageLevel languageLevel, InferenceMode inferenceMode ) throws IOException {
        if ( dataset == null ) {
            if ( readOnly ) {
                // lock the location and make a copy
                Location loc = Location.create( tdbDir.toString() );
                loc.getLock().obtain();
                try {
                    Set<Path> filesToLink;
                    try ( Stream<Path> z = Files.list( tdbDir ) ) {
                        filesToLink = z.collect( Collectors.toSet() );
                    }
                    tempDir = Files.createTempDirectory( getOntologyName() + ".tdb" );
                    for ( Path p : filesToLink ) {
                        Files.copy( p, tempDir.resolve( p.getFileName() ) );
                    }
                } finally {
                    loc.getLock().release();
                }
                log.info( "Reading read-only TDB model from {}.", tempDir );
                dataset = TDBFactory.createDataset( tempDir.toString() );
            } else {
                dataset = TDBFactory.createDataset( tdbDir.toString() );
            }
        }
        return new OntologyModelImpl( OntologyLoader.createTdbModel( dataset, modelName, processImports, getSpec( languageLevel, inferenceMode ), readOnly ) );
    }

    @Override
    protected OntologyModel loadModelFromStream( InputStream is, boolean processImports, LanguageLevel languageLevel, InferenceMode inferenceMode ) {
        throw new UnsupportedOperationException( "TDB cannot be loaded from an input stream." );
    }

    @Override
    public void close() throws Exception {
        try {
            super.close();
        } finally {
            if ( dataset != null ) {
                TDBFactory.release( dataset );
            }
            if ( tempDir != null ) {
                PathUtils.delete( tempDir );
            }
        }
    }
}
