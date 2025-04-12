package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;
import ubic.basecode.ontology.model.OntologyModel;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * An implementation based on Jena TDB.
 * @author poirigui
 */
public class TdbOntologyService extends AbstractOntologyService {

    private final Path tdbDir;
    private final String modelName;

    @Nullable
    private Dataset dataset;

    public TdbOntologyService( String ontologyName, Path tdbDir, @Nullable String modelName, boolean ontologyEnabled, @Nullable String cacheName ) {
        super( ontologyName, tdbDir.toUri().toString(), ontologyEnabled, cacheName );
        this.tdbDir = tdbDir;
        this.modelName = modelName;
    }

    @Override
    protected OntologyModel loadModel( boolean processImports, LanguageLevel languageLevel, InferenceMode inferenceMode ) {
        if ( dataset == null ) {
            dataset = TDBFactory.createDataset( tdbDir.toString() );
        }
        return new OntologyModelImpl( OntologyLoader.createTdbModel( dataset, modelName, processImports, getSpec( languageLevel, inferenceMode ) ) );
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
        }
    }
}
