/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.basecode.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author keshav
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

    protected final static String[] IMAGE_EXTENSIONS = { PNG_EXTENSION, GIF_EXTENSION, "PNG", "GIF", "JPEG", "JPG" };
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
    public static String chompExtension( String filename ) {
        int j = filename.lastIndexOf( '.' );
        if ( j > 1 ) {
            return filename.substring( 0, filename.lastIndexOf( '.' ) );
        }
        return filename;
    }

    /**
     * @param filename
     * @param newExtension
     * @return the new filename with the changed extension, but does not modify the <code>filename</code> parameter.
     */
    public static String changeExtension( String filename, String newExtension ) {

        String filenameWithoutExtension = chompExtension( filename );
        return ( filenameWithoutExtension + "." + newExtension );
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
     * @return the new filename with the added extension, but does not modify the <code>filename</code> parameter.
     */
    public static String addImageExtension( String filename ) {
        return ( filename + ( FileTools.DEFAULT_IMAGE_EXTENSION.startsWith( "." ) ? "" : "." ) + FileTools.DEFAULT_IMAGE_EXTENSION );
    }

    /**
     * @param filename
     * @return the new filename with the added extension, but does not modify the <code>filename</code> parameter.
     */
    public static String addDataExtension( String filename ) {
        return ( filename + ( FileTools.DEFAULT_DATA_EXTENSION.startsWith( "." ) ? "" : "." ) + FileTools.DEFAULT_DATA_EXTENSION );
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
     * @param resourcePath
     * @return
     * @throws URISyntaxException
     */
    public static String resourceToPath( String resourcePath ) throws URISyntaxException {
        if ( StringUtils.isBlank( resourcePath ) ) throw new IllegalArgumentException();
        URL resource = FileTools.class.getResource( resourcePath );
        if ( resource == null ) throw new IllegalArgumentException( "Could not get URL for resource=" + resourcePath );
        return new File( resource.toURI() ).getAbsolutePath();
    }

    /**
     * Avoid getting file names with spaces, slashes, quotes, # etc; replace them with "_".
     * 
     * @param ee
     * @return
     * @throws IllegalArgumentException if the resulting string is empty, or if the input is empty.
     */
    public static String cleanForFileName( String name ) {
        if ( StringUtils.isBlank( name ) ) throw new IllegalArgumentException( "'name' cannot be blank" );
        String result = name.replaceAll( "[\\s\'\";,\\/#]+", "_" ).replaceAll( "(^_|_$)", "" );
        if ( StringUtils.isBlank( result ) ) {
            throw new IllegalArgumentException( "'" + name + "' was stripped down to an empty string" );
        }
        return result;
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
     * Given the path to a gzipped-file, unzips it into the same directory. If the file already exists it will be
     * overwritten.
     * 
     * @param seekFile
     * @throws IOException
     * @return path to the unzipped file.
     */
    public static String unGzipFile( final String seekFile ) throws IOException {

        if ( !isGZipped( seekFile ) ) {
            throw new IllegalArgumentException();
        }

        checkPathIsReadableFile( seekFile );

        String outputFilePath = chompExtension( seekFile );
        File outputFile = copyPlainOrCompressedFile( seekFile, outputFilePath );

        return outputFile.getAbsolutePath();
    }

    /**
     * @param seekFile
     * @return Collection of File objects
     * @throws IOException
     */
    public static Collection<File> unZipFiles( final String seekFile ) throws IOException {

        if ( !isZipped( seekFile ) ) {
            throw new IllegalArgumentException();
        }

        checkPathIsReadableFile( seekFile );

        String outputFilePath = chompExtension( seekFile );

        Collection<File> result = new HashSet<File>();
        try {
            ZipFile f = new ZipFile( seekFile );
            for ( Enumeration<? extends ZipEntry> entries = f.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = entries.nextElement();
                String outputFileTitle = entry.getName();
                InputStream is = f.getInputStream( entry );

                File out = new File( outputFilePath + outputFileTitle );
                OutputStream os = new FileOutputStream( out );
                copy( is, os );

                result.add( out );
                log.debug( outputFileTitle );
            }

        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        return result;
    }

    /**
     * @param sourcePath
     * @param outputFilePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File copyPlainOrCompressedFile( final String sourcePath, String outputFilePath )
            throws FileNotFoundException, IOException {
        File sourceFile = new File( sourcePath );
        if ( !sourceFile.exists() ) {
            throw new IllegalArgumentException( "Source file (" + sourcePath + ") does not exist" );
        }
        if ( sourceFile.exists() && sourceFile.isDirectory() ) {
            throw new UnsupportedOperationException( "Don't know how to copy directories (" + sourceFile + ")" );
        }

        File outputFile = new File( outputFilePath );
        if ( outputFile.exists() && outputFile.isDirectory() ) {
            throw new UnsupportedOperationException( "Don't know how to copy to directories (" + outputFile + ")" );
        }

        OutputStream out = new FileOutputStream( outputFile );

        InputStream is = FileTools.getInputStreamFromPlainOrCompressedFile( sourcePath );

        copy( is, out );
        return outputFile;
    }

    /**
     * On completion streams are closed.
     * 
     * @param input
     * @param output
     * @throws IOException
     */
    public static void copy( InputStream input, OutputStream output ) throws IOException {

        if ( input.available() == 0 ) return;

        byte[] buf = new byte[1024];
        int len;
        while ( ( len = input.read( buf ) ) > 0 ) {
            output.write( buf, 0, len );
        }
        input.close();
        output.close();
    }

    /**
     * Open a non-compresed, zipped, or gzipped file. Uses the file name pattern to figure this out.
     * 
     * @param fileName. If Zipped, only the first file in the archive is used.
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
            ZipEntry entry = f.entries().nextElement();
            if ( entry == null ) throw new IOException( "No zip entries" );

            if ( f.entries().hasMoreElements() ) {
                log.debug( "ZIP archive has more then one file, reading the first one." );
            }

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

    /**
     * Given a File object representing a directory, return a collection of File objects representing the files
     * contained in that directory.
     * 
     * @param directory
     * @return
     */
    public static Collection<File> listDirectoryFiles( File directory ) {

        if ( !directory.isDirectory() ) throw new IllegalArgumentException( "Must be a directory" );

        File[] files = directory.listFiles();

        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept( File file ) {
                return file.isFile();
            }
        };
        files = directory.listFiles( fileFilter );
        return Arrays.asList( files );
    }

    /**
     * Given a File object representing a directory, return a collection of File objects representing the directories
     * contained in that directory.
     * 
     * @param directory
     * @return
     */
    public static Collection<File> listSubDirectories( File directory ) {

        if ( !directory.isDirectory() ) throw new IllegalArgumentException( "Must be a directory" );

        File[] files = directory.listFiles();

        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept( File file ) {
                return file.isDirectory();
            }
        };
        files = directory.listFiles( fileFilter );
        return Arrays.asList( files );
    }

    /**
     * Creates the directory if it does not exist.
     * 
     * @param directory
     * @return
     */
    public static File createDir( String directory ) {
        File dirPath = new File( directory );
        if ( !dirPath.exists() ) {
            dirPath.mkdirs();
        }
        return dirPath;
    }

    /**
     * Deletes the specified <link>Collection<link> of files.
     * 
     * @param files
     * @return int The number of files deleted.
     * @see java.io.File#delete()
     */
    public static int deleteFiles( Collection<File> files ) {

        int numDeleted = 0;

        Iterator<File> iter = files.iterator();
        while ( iter.hasNext() ) {

            File file = iter.next();
            if ( file.isDirectory() ) {
                log.warn( "Cannot delete a directory." );
                continue;
            }

            if ( log.isDebugEnabled() ) log.debug( "Deleting file " + file.getAbsolutePath() + "." );

            file.getAbsoluteFile().delete();
            numDeleted++;

        }
        log.info( "Deleted " + numDeleted + " files." );
        return numDeleted;
    }

    /**
     * Deletes the directory and subdirectories if empty.
     * 
     * @param directory
     * @return int The number of directories deleted.
     * @see java.io.File#delete()
     */
    public static int deleteDir( File directory ) {
        int numDeleted = 0;
        Collection<File> directories = listSubDirectories( directory );

        Iterator<File> iter = directories.iterator();
        while ( iter.hasNext() ) {
            File dir = iter.next();

            if ( dir.listFiles().length == 0 ) {
                dir.getAbsoluteFile().delete();
                numDeleted++;
            } else {
                log.info( "Directory not empty.  Skipping deletion of " + dir.getAbsolutePath() + "." );
            }
        }

        /* The top level directory */
        if ( directory.listFiles().length == 0 ) {
            log.warn( "Deleting top level directory." );
            directory.getAbsoluteFile().delete();
            numDeleted++;
        }

        else {
            log.info( "Top level directory " + directory.getAbsolutePath() + " not empty.  Will not delete." );
        }

        log.info( "Deleted " + numDeleted + " directories." );
        return numDeleted;
    }

    // Leon added the below methods

    /**
     * Outputs a string to a file.
     */
    public static void stringToFile( String s, File f ) throws Exception {
        stringToFile( s, f, false );
    }

    /**
     * Outputs a many strings to a file, one line at a time.
     */
    public static void stringsToFile( Collection<String> lines, String f ) throws Exception {
        stringsToFile( lines, new File( f ) );
    }

    /**
     * Outputs a many strings to a file, one line at a time.
     */
    public static void stringsToFile( Collection<String> lines, File f ) throws Exception {
        stringsToFile( lines, f, false );
    }

    /**
     * Outputs many strings to a file, one line at a time.
     * 
     * @param lines - input lines
     * @param f - file that wrote to
     * @param append - add to end of file or overwrite
     * @throws Exception
     */
    public static void stringsToFile( Collection<String> lines, File f, boolean append ) throws Exception {
        PrintWriter fout = new PrintWriter( new FileWriter( f, append ) );
        for ( String line : lines ) {
            fout.println( line );
        }
        fout.close();
    }

    /**
     * Outputs a string to a file, one line at a time.
     * 
     * @param s - input line/string
     * @param f - file that wrote to
     * @param append - add to end of file or overwrite
     * @throws Exception
     */
    public static void stringToFile( String s, File f, boolean append ) throws Exception {
        FileWriter fout = new FileWriter( f, append );
        fout.write( s );
        fout.close();
    }

    /**
     * opens a file and returns its contents as a list of lines.
     * 
     * @return - List of strings representing the lines, first line is first in list
     * @throws IOException
     */
    public static List<String> getLines( String filename ) throws IOException {
        return getLines( new File( filename ) );
    }

    // is this code duplicated? I can't find any if so
    /**
     * opens a file and returns its contents as a list of lines.
     * 
     * @return - List of strings representing the lines, first line is first in list
     * @throws IOException
     */
    public static List<String> getLines( File file ) throws IOException {
        List<String> lines = new LinkedList<String>();
        BufferedReader in = new BufferedReader( new FileReader( file ) );
        String line;
        while ( ( line = in.readLine() ) != null ) {
            lines.add( line );
        }
        in.close();
        return lines;
    }

    /**
     * Used for reading output generated by Collection.toString(). For example [a,b,c] stored in a file would be
     * converted to a new List containing "a", "b" and "c".
     * 
     * @param f - input file, with only one line for the toString output.
     * @return - list created from the strings in the file
     * @throws Exception
     */
    public static List<String> getStringListFromFile( File f ) throws Exception {
        List<String> result = new LinkedList<String>();
        List<String> lines = FileTools.getLines( f );
        if ( lines.size() != 1 ) {
            throw new RuntimeException( "Too many lines in file" );
        }
        String line = lines.get( 0 );
        line = line.substring( 1, line.length() - 1 );
        StringTokenizer toke = new StringTokenizer( line, "," );
        while ( toke.hasMoreTokens() ) {
            result.add( toke.nextToken().trim() );
        }
        return result;
    }

}