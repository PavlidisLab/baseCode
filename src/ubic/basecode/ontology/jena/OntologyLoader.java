/*
 * The baseCode project
 *
 * Copyright (c) 2010 University of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.CannotCreateException;
import com.hp.hpl.jena.shared.JenaException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.util.Configuration;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Reads ontologies from OWL resources
 *
 * @author paul
 */
public class OntologyLoader {

    private static final Logger log = LoggerFactory.getLogger( OntologyLoader.class );
    private static final int MAX_CONNECTION_TRIES = 3;
    private static final String OLD_CACHE_SUFFIX = ".old";
    private static final String TMP_CACHE_SUFFIX = ".tmp";

    public static OntModel loadMemoryModel( InputStream is, String url ) {
        return loadMemoryModel( is, url, true );
    }

    /**
     * Load an ontology into memory. Use this type of model when fast access is critical and memory is available.
     */
    public static OntModel loadMemoryModel( InputStream is, String url, boolean processImports ) {
        OntModel model = getMemoryModel( url, processImports );
        model.read( is, null );
        return model;
    }

    /**
     * Load an ontology into memory. Use this type of model when fast access is critical and memory is available.
     *
     * @see #loadMemoryModel(String, String, boolean)
     */
    public static OntModel loadMemoryModel( String url ) {
        return loadMemoryModel( url, null, true );
    }

    public static OntModel loadMemoryModel( String url, @Nullable String cacheName ) {
        return loadMemoryModel( url, cacheName, true );
    }

    /**
     * Load an ontology into memory. Use this type of model when fast access is critical and memory is available.
     * If load from URL fails, attempt to load from disk cache under @cacheName.
     * <p>
     * Uses {@link OntModelSpec#OWL_MEM_TRANS_INF}.
     *
     * @param url       a URL where the OWL file is stored
     * @param cacheName unique name of this ontology, will be used to load from disk in case of failed url connection
     */
    public static OntModel loadMemoryModel( String url, @Nullable String cacheName, boolean processImports ) {
        StopWatch timer = new StopWatch();
        timer.start();
        OntModel model = getMemoryModel( url, processImports );

        boolean attemptToLoadFromDisk = false;
        URLConnection urlc = null;
        try {
            urlc = openConnection( url );
            try ( InputStream in = urlc.getInputStream() ) {
                Reader reader;
                if ( cacheName != null ) {
                    // write tmp to disk
                    File tempFile = getTmpDiskCachePath( cacheName );
                    FileUtils.createParentDirectories( tempFile );
                    Files.copy( in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
                    reader = new FileReader( tempFile );
                } else {
                    // Skip the cache
                    reader = new InputStreamReader( in );
                }
                try ( BufferedReader buf = new BufferedReader( reader ) ) {
                    model.read( buf, url );
                }
                log.info( "Loading ontology model for " + url + " took " + timer.getTime() + "ms" );
            }
        } catch ( IOException e ) {
            log.error( "Failed to load ontology model for " + url + ", will attempt to load from disk.", e );
            attemptToLoadFromDisk = true;
        } finally {
            if ( urlc instanceof HttpURLConnection ) {
                ( ( HttpURLConnection ) urlc ).disconnect();
            }
        }

        if ( cacheName != null ) {
            File f = getDiskCachePath( cacheName );
            File tempFile = getTmpDiskCachePath( cacheName );
            File oldFile = getOldDiskCachePath( cacheName );
            if ( attemptToLoadFromDisk ) {
                // Attempt to load from disk cache
                if ( f.isFile() ) {
                    try ( BufferedReader buf = new BufferedReader( new FileReader( f ) ) ) {
                        model.read( buf, url );
                        // We successfully loaded the cached ontology. Copy the loaded ontology to oldFile
                        // so that we don't recreate indices during initialization based on a false change in
                        // the ontology.
                        FileUtils.createParentDirectories( oldFile );
                        Files.copy( f.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
                        log.info( "Load model from disk: " + timer.getTime() + "ms" );
                    } catch ( IOException e ) {
                        throw new RuntimeException(
                                "Ontology failed load from URL (" + url + ") and disk cache: " + cacheName );
                    }
                } else {
                    throw new RuntimeException(
                            "Ontology failed load from URL (" + url + ") and disk cache does not exist: " + cacheName );
                }
            } else if ( tempFile.exists() ) {
                // Model was successfully loaded into memory from URL with given cacheName
                // Save cache to disk (rename temp file)
                log.info( "Caching ontology to disk: " + cacheName + " under " + f.getAbsolutePath() );
                try {
                    // Need to compare previous to current so instead of overwriting we'll move the old file
                    if ( f.exists() ) {
                        FileUtils.createParentDirectories( oldFile );
                        Files.move( f.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
                    } else {
                        FileUtils.createParentDirectories( f );
                    }
                    Files.move( tempFile.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING );
                } catch ( IOException e ) {
                    log.error( "Failed to cache ontology " + url + " to disk.", e );
                }
            }
        }

        return model;
    }

    public static boolean hasChanged( String cacheName ) {
        // default
        if ( StringUtils.isBlank( cacheName ) ) {
            return false;
        }
        try {
            File newFile = getDiskCachePath( cacheName );
            File oldFile = getOldDiskCachePath( cacheName );
            // This might be slow considering it calls IOUtils.contentsEquals which compares byte-by-byte
            // in the worst case scenario.
            // In this case consider using NIO for higher-performance IO using Channels and Buffers.
            // Ex. Use a 4MB Memory-Mapped IO operation.
            return !FileUtils.contentEquals( newFile, oldFile );
        } catch ( IOException e ) {
            log.error( "Failed to compare current and previous cached ontologies, will report as not changed.", e );
            return false;
        }
    }

    public static void deleteOldCache( String cacheName ) throws IOException {
        File dir = getOldDiskCachePath( cacheName );
        if ( dir.exists() ) {
            FileUtils.delete( dir );
        }
    }

    /**
     * Get model that is entirely in memory.
     */
    private static OntModel getMemoryModel( String url, boolean processImports ) {
        OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM_TRANS_INF );
        ModelMaker maker = ModelFactory.createMemModelMaker();
        Model base = maker.createModel( url, false );
        spec.setImportModelMaker( maker );
        // the spec is a shallow copy, so we need to copy the document manager as well to modify it
        spec.setDocumentManager( new OntDocumentManager() );
        spec.getDocumentManager().setProcessImports( processImports );
        spec.setImportModelGetter( new ModelGetter() {
            @Override
            public Model getModel( String URL ) {
                return null;
            }

            @Override
            public Model getModel( String URL, ModelReader loadIfAbsent ) {
                Model model = maker.createModel( URL );
                URLConnection urlc = null;
                try {
                    urlc = openConnection( URL );
                    try ( InputStream in = urlc.getInputStream() ) {
                        return model.read( in, URL );
                    }
                } catch ( JenaException | IOException e ) {
                    throw new CannotCreateException( String.format( "Failed to resolve import %s for %s.", URL, url ), e );
                } finally {
                    if ( urlc instanceof HttpURLConnection ) {
                        ( ( HttpURLConnection ) urlc ).disconnect();
                    }
                }
            }
        } );
        OntModel model = ModelFactory.createOntologyModel( spec, base );
        model.setStrictMode( false ); // fix for owl2 files
        return model;
    }

    public static URLConnection openConnection( String url ) throws IOException {
        URLConnection urlc = openConnectionInternal( url );

        // this happens if there is a change of protocol (http:// -> https://)
        if ( urlc instanceof HttpURLConnection ) {
            int code = ( ( HttpURLConnection ) urlc ).getResponseCode();
            String newUrl = urlc.getHeaderField( "Location" );
            if ( code >= 300 && code < 400 ) {
                if ( StringUtils.isBlank( newUrl ) ) {
                    throw new RuntimeException( String.format( "Redirect response for %s is lacking a 'Location' header.", url ) );
                }
                log.info( "Redirect to " + newUrl + " from " + url );
                urlc = openConnectionInternal( newUrl );
            }
        }

        return urlc;
    }

    private static URLConnection openConnectionInternal( String url ) throws IOException {
        URLConnection urlc = new URL( url ).openConnection();
        // help ensure mis-configured web servers aren't causing trouble.
        urlc.setRequestProperty( "Accept", "application/rdf+xml" );
        if ( urlc instanceof HttpURLConnection ) {
            ( ( HttpURLConnection ) urlc ).setInstanceFollowRedirects( true );
        }
        log.info( "Connecting to " + url );
        urlc.connect(); // Will error here on bad URL
        return urlc;
    }

    /**
     * Obtain the path for the ontology cache.
     */
    public static File getDiskCachePath( String name ) {
        if ( StringUtils.isBlank( name ) ) {
            throw new IllegalArgumentException( "The ontology must have a suitable name for being loaded from cache." );
        }
        String ontologyDir = Configuration.getString( "ontology.cache.dir" ); // e.g., /something/gemmaData/ontologyCache
        if ( StringUtils.isBlank( ontologyDir ) ) {
            return Paths.get( System.getProperty( "java.io.tmpdir" ), "ontologyCache", "ontology", name ).toFile();
        }
        return Paths.get( ontologyDir, "ontology", name ).toFile();
    }

    static File getOldDiskCachePath( String name ) {
        File indexFile = getDiskCachePath( name );
        return new File( indexFile.getAbsolutePath() + OLD_CACHE_SUFFIX );
    }

    static File getTmpDiskCachePath( String name ) {
        File indexFile = getDiskCachePath( name );
        return new File( indexFile.getAbsolutePath() + TMP_CACHE_SUFFIX );
    }
}
