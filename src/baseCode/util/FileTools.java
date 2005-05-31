package baseCode.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Copyright (c) 2004 Columbia University
 * 
 * @author Pavlidis
 * @author Will Braynen
 * @version $Id$
 */
public class FileTools {
    private static Log log = LogFactory.getLog( FileTools.class.getName() );
    protected final static String PNG_EXTENSION = ".png";
    protected final static String GIF_EXTENSION = ".gif";
    protected final static String TXT_EXTENSION = ".txt";
    protected final static String[] XML_EXTENSIONS = { ".XML", ".RDF-XML", ".rdf-xml.gz", ".rdf-xml.zip", ".xml.zip",
            ".xml.gz" };

    protected final static String[] IMAGE_EXTENSIONS = { PNG_EXTENSION, GIF_EXTENSION, "PNG", "GIF" };
    protected final static String[] DATA_EXTENSIONS = { TXT_EXTENSION, ".TXT", "txt.gz", "txt.zip", "txt.gzip" };
    // default values
    public final static String DEFAULT_DATA_EXTENSION = TXT_EXTENSION;
    public final static String DEFAULT_IMAGE_EXTENSION = PNG_EXTENSION;
    public final static String DEFAULT_XML_EXTENSION = ".xml";

    /**
     * @param file
     * @throws IOException
     */
    public static void checkPathIsReadableFile( String file ) throws IOException {
        File infile = new File( file );
        if ( !infile.exists() || !infile.canRead() ) {
            throw new IOException( "Could not find file: " + file );
        }
    }

    /**
     * Returns the extension of a file.
     * 
     * @param filename
     * @return
     * @return
     */
    public static String getExtension( String filename ) {

        String extension = null;
        int i = filename.lastIndexOf( '.' );

        if ( i > 0 && i < filename.length() - 1 ) {
            extension = filename.substring( i + 1 ).toLowerCase();
        }
        return extension;
    } // end getExtension

    /**
     * @param filename
     * @return
     */
    public static String getWithoutExtension( String filename ) {

        String[] s = filename.split( "." );
        String extension = s[s.length - 1];
        String filenameWithoutExtension = filename.substring( filename.length() - extension.length() - 1, filename
                .length() - 1 );

        return filenameWithoutExtension;
    } // end getFilenameWithoutExtension

    /**
     * @param filename
     * @param newExtension
     * @return the new filename with the changed extension, but does not modify the <code>filename</code> parameter.
     */
    public static String changeExtension( String filename, String newExtension ) {

        String filenameWithoutExtension = getWithoutExtension( filename );
        return ( filenameWithoutExtension + newExtension );
    } // end getWithChangedExtension

    /**
     * @param filename
     * @return
     */
    public static boolean hasImageExtension( String filename ) {
        for ( int i = 0; i < FileTools.IMAGE_EXTENSIONS.length; i++ ) {
            if ( filename.toUpperCase().endsWith( FileTools.IMAGE_EXTENSIONS[i].toUpperCase() ) ) {
                return true;
            }
        }
        return false;

    } // end hasImageExtension

    /**
     * @param filename
     * @return
     */
    public static boolean hasXMLExtension( String filename ) {
        for ( int i = 0; i < FileTools.XML_EXTENSIONS.length; i++ ) {
            if ( filename.toUpperCase().endsWith( FileTools.XML_EXTENSIONS[i].toUpperCase() ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param filename
     * @return
     */
    public static boolean hasDataExtension( String filename ) {
        // for ( int i = 0; i < FileTools.DATA_EXTENSIONS.length; i++ ) {
        // if ( filename.toUpperCase().endsWith( FileTools.DATA_EXTENSIONS[i].toUpperCase() ) ) {
        // return true;
        // }
        // }
        // return false;
        return true;
    } // end hasImageExtension

    /**
     * @param filename
     * @return the new filename with the added extension, but does not modify the <code>filename</code> parameter.
     */
    public static String addImageExtension( String filename ) {
        return ( filename + "." + FileTools.DEFAULT_IMAGE_EXTENSION );
    }

    /**
     * @param filename
     * @return the new filename with the added extension, but does not modify the <code>filename</code> parameter.
     */
    public static String addDataExtension( String filename ) {
        return ( filename + "." + FileTools.DEFAULT_DATA_EXTENSION );
    }

    /**
     * @param dirname directory name
     * @return
     */
    public static boolean testDir( String dirname ) {
        if ( dirname != null && dirname.length() > 0 ) {
            File f = new File( dirname );
            if ( f.isDirectory() && f.canRead() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param filename
     * @return
     */
    public static boolean testFile( String filename ) {
        if ( filename != null && filename.length() > 0 ) {
            File f = new File( filename );
            if ( f.isFile() && f.canRead() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test whether a File is writeable.
     * 
     * @param file
     * @return
     */
    public static boolean testFile( File file ) {
        if ( file != null ) {
            if ( file.isFile() && file.canRead() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param filename
     * @return
     */
    public static boolean isZipped( String filename ) {
        String capfileName = filename.toUpperCase();
        if ( capfileName.endsWith( ".ZIP" ) ) {
            return true;
        }
        return false;
    }

    /**
     * @param fileName
     * @return
     */
    public static boolean isGZipped( String fileName ) {
        String capfileName = fileName.toUpperCase();
        if ( capfileName.endsWith( ".GZ" ) || capfileName.endsWith( ".GZIP" ) ) {
            return true;
        }
        return false;
    }
    
    

    /**
     * @param fileName. If Zipped, this only works if there is just one file in the archive.
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static InputStream getInputStreamFromPlainOrCompressedFile( String fileName ) throws IOException,
            FileNotFoundException {
        if ( !FileTools.testFile( fileName ) ) {
            throw new IOException( "Could not read from " + fileName );
        }
        InputStream i;
        if ( FileTools.isZipped( fileName ) ) {
            log.debug( "Reading from zipped file" );
            ZipFile f = new ZipFile( fileName );
            ZipEntry entry = ( ZipEntry ) f.entries().nextElement();
            if ( entry == null ) throw new IOException( "No zip entries" );
            i = f.getInputStream( entry );
        } else if ( FileTools.isGZipped( fileName ) ) {
            log.debug( "Reading from gzipped file" );
            i = new GZIPInputStream( new FileInputStream( fileName ) );
        } else {
            log.debug( "Reading from uncompressed file" );
            i = new FileInputStream( fileName );
        }
        return i;
    }
}