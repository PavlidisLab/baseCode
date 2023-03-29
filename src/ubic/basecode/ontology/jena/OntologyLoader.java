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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.util.Configuration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Reads ontologies from OWL resources
 *
 * @author paul
 */
public class OntologyLoader {

    private static Logger log = LoggerFactory.getLogger( OntologyLoader.class );
    private static final int MAX_CONNECTION_TRIES = 3;
    private static final String OLD_CACHE_SUFFIX = ".old";
    private static final String TMP_CACHE_SUFFIX = ".tmp";

    /**
     * Load an ontology into memory. Use this type of model when fast access is critical and memory is available.
     */
    public static OntModel loadMemoryModel( InputStream is, String url ) {
        OntModel model = getMemoryModel( url );
        model.read( is, null );
        return model;
    }

    /**
     * Load an ontology into memory. Use this type of model when fast access is critical and memory is available.
     *
     * @see #loadMemoryModel(String, String)
     */
    public static OntModel loadMemoryModel( String url ) {
        return loadMemoryModel( url, null );
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
    public static OntModel loadMemoryModel( String url, String cacheName ) {
        StopWatch timer = new StopWatch();
        timer.start();
        OntModel model = getMemoryModel( url );

        URLConnection urlc = null;
        int tries = 0;
        while ( tries < MAX_CONNECTION_TRIES ) {
            try {
                urlc = new URL( url ).openConnection();
                // help ensure mis-configured web servers aren't causing trouble.
                urlc.setRequestProperty( "Accept", "application/rdf+xml" );

                try {
                    HttpURLConnection c = ( HttpURLConnection ) urlc;
                    c.setInstanceFollowRedirects( true );
                } catch ( ClassCastException e ) {
                    // not via http, using a FileURLConnection.
                }

                if ( tries > 0 ) {
                    log.info( "Retrying connecting to " + url + " [" + tries + "/" + MAX_CONNECTION_TRIES
                            + " of max tries" );
                } else {
                    log.info( "Connecting to " + url );
                }

                urlc.connect(); // Will error here on bad URL

                if ( urlc instanceof HttpURLConnection ) {
                    String newUrl = urlc.getHeaderField( "Location" );

                    if ( StringUtils.isNotBlank( newUrl ) ) {
                        log.info( "Redirect to " + newUrl );
                        urlc = new URL( newUrl ).openConnection();
                        // help ensure mis-configured web servers aren't causing trouble.
                        urlc.setRequestProperty( "Accept", "application/rdf+xml" );
                        urlc.connect();
                    }
                }

                break;
            } catch ( IOException e ) {
                // try to recover.
                log.error( e + " retrying?" );
                tries++;
            }
        }

        if ( urlc != null ) {
            try ( InputStream in = urlc.getInputStream(); ) {
                Reader reader;
                if ( cacheName != null ) {
                    // write tmp to disk
                    File tempFile = getTmpDiskCachePath( cacheName );
                    if ( tempFile == null ) {
                        reader = new InputStreamReader( in );
                    } else {
                        tempFile.getParentFile().mkdirs();
                        Files.copy( in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
                        reader = new FileReader( tempFile );
                    }

                } else {
                    // Skip the cache
                    reader = new InputStreamReader( in );
                }

                assert reader != null;
                try ( BufferedReader buf = new BufferedReader( reader ); ) {
                    model.read( buf, url );
                }

                log.info( "Load model: " + timer.getTime() + "ms" );
            } catch ( IOException e ) {
                log.error( e.getMessage(), e );
            }
        }

        if ( cacheName != null ) {

            File f = getDiskCachePath( cacheName );
            File tempFile = getTmpDiskCachePath( cacheName );
            File oldFile = getOldDiskCachePath( cacheName );

            if ( model.isEmpty() ) {
                // Attempt to load from disk cache

                if ( f == null ) {
                    throw new RuntimeException(
                            "Ontology cache directory required to load from disk: ontology.cache.dir" );
                }

                if ( f.exists() && !f.isDirectory() ) {
                    try ( BufferedReader buf = new BufferedReader( new FileReader( f ) ); ) {
                        model.read( buf, url );
                        // We successfully loaded the cached ontology. Copy the loaded ontology to oldFile
                        // so that we don't recreate indices during initialization based on a false change in
                        // the ontology.
                        Files.copy( f.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
                        log.info( "Load model from disk: " + timer.getTime() + "ms" );
                    } catch ( IOException e ) {
                        log.error( e.getMessage(), e );
                        throw new RuntimeException(
                                "Ontology failed load from URL (" + url + ") and disk cache: " + cacheName );
                    }
                } else {
                    throw new RuntimeException(
                            "Ontology failed load from URL (" + url + ") and disk cache does not exist: " + cacheName );
                }

            } else {
                // Model was successfully loaded into memory from URL with given cacheName
                // Save cache to disk (rename temp file)
                log.info( "Caching ontology to disk: " + cacheName );
                if ( f != null ) {
                    try {
                        // Need to compare previous to current so instead of overwriting we'll move the old file
                        f.createNewFile();
                        Files.move( f.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
                        Files.move( tempFile.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING );
                    } catch ( IOException e ) {
                        log.error( e.getMessage(), e );
                    }
                } else {
                    log.warn( "Ontology cache directory required to save to disk: ontology.cache.dir" );
                }
            }

        }

        assert !model.isEmpty();

        return model;
    }

    public static boolean hasChanged( String cacheName ) {
        boolean changed = false; // default
        if ( StringUtils.isBlank( cacheName ) ) {
            return changed;
        }

        File newFile = getDiskCachePath( cacheName );
        File oldFile = getOldDiskCachePath( cacheName );

        try {
            // This might be slow considering it calls IOUtils.contentsEquals which compares byte-by-byte
            // in the worst case scenario.
            // In this case consider using NIO for higher-performance IO using Channels and Buffers.
            // Ex. Use a 4MB Memory-Mapped IO operation.
            if ( newFile != null && oldFile != null )
                changed = !FileUtils.contentEquals( newFile, oldFile );
        } catch ( IOException e ) {
            log.error( e.getMessage() );
        }

        return changed;

    }

    public static boolean deleteOldCache( String cacheName ) {
        File f = getOldDiskCachePath( cacheName );
        if ( f != null )
            return f.delete();
        return false;
    }

    /**
     * Get model that is entirely in memory.
     *
     * @param url
     * @return
     */
    private static OntModel getMemoryModel( String url ) {
        OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM_TRANS_INF );
        ModelMaker maker = ModelFactory.createMemModelMaker();
        Model base = maker.createModel( url, false );
        spec.setImportModelMaker( maker );
        spec.getDocumentManager().setProcessImports( false );

        OntModel model = ModelFactory.createOntologyModel( spec, base );
        model.setStrictMode( false ); // fix for owl2 files
        return model;
    }

    /**
     * @param name
     * @return
     */
    public static File getDiskCachePath( String name ) {
        String ontologyDir = Configuration.getString( "ontology.cache.dir" ); // e.g., /something/gemmaData/ontologyCache
        if ( StringUtils.isBlank( ontologyDir ) || StringUtils.isBlank( name ) ) {
            return null;
        }

        if ( !new File( ontologyDir ).exists() ) {
            new File( ontologyDir ).mkdirs();
        }

        assert ontologyDir != null;

        String path = ontologyDir + File.separator + "ontology" + File.separator + name;

        File indexFile = new File( path );

        return indexFile;
    }

    static File getOldDiskCachePath( String name ) {
        File indexFile = getDiskCachePath( name );
        if ( indexFile == null ) {
            return null;
        }
        return new File( indexFile.getAbsolutePath() + OLD_CACHE_SUFFIX );

    }

    static File getTmpDiskCachePath( String name ) {
        File indexFile = getDiskCachePath( name );
        if ( indexFile == null ) {
            return null;
        }
        return new File( indexFile.getAbsolutePath() + TMP_CACHE_SUFFIX );

    }

}
