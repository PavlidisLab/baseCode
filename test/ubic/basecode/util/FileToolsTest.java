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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import junit.framework.TestCase;

import org.apache.commons.lang.RandomStringUtils;

/**
 * @author keshav
 * @author pavlidis
 * @version $Id$
 */
public class FileToolsTest extends TestCase {
    File compressed;

    File plain;
    File tempdir;
    File tempoutput;
    File zipped;

    /*
     * Test method for 'basecode.util.FileTools.addDataExtension(String)'
     */
    public void testAddDataExtension() {
        assertTrue( FileTools.addDataExtension( plain.getPath() ).endsWith( ".txt" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.addImageExtension(String)'
     */
    public void testAddImageExtension() {
        assertTrue( FileTools.addImageExtension( plain.getPath() ).endsWith( ".png" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.changeExtension(String, String)'
     */
    public void testChangeExtension() {
        assertTrue( FileTools.changeExtension( plain.getPath(), "barbie" ).endsWith( ".barbie" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.checkPathIsReadableFile(String)'
     */
    public void testCheckPathIsReadableFile() {
        try {
            FileTools.checkPathIsReadableFile( plain.getPath() );
        } catch ( IOException e ) {
            fail( "Should not have thrown an IOException" );
        }
    }

    public void testCheckPathIsReadableFileNot() {
        try {
            FileTools.checkPathIsReadableFile( plain.getPath() + RandomStringUtils.randomNumeric( 10 ) );
            fail( "Should have thrown an IOException" );
        } catch ( IOException e ) {

        }
    }

    public void testCleanFileName() {
        assertTrue( !FileTools.cleanForFileName( "#foo/bar/crud" ).contains( "/" ) );
        assertTrue( !FileTools.cleanForFileName( "#foo/bar/crud" ).contains( "#" ) );
        assertTrue( !FileTools.cleanForFileName( "#foo/bar'''\"\"/crud" ).contains( "\"" ) );
        assertTrue( !FileTools.cleanForFileName( "#foo/bar'''\"\"/crud" ).contains( "'" ) );
        assertTrue( !FileTools.cleanForFileName( "#foo/bar/crud" ).contains( "/" ) );
        assertTrue( !FileTools.cleanForFileName( "#foo/bar/crud" ).contains( "/" ) );
        assertTrue( !FileTools.cleanForFileName( "foo\bar/crud" ).contains( "\\" ) );
        assertTrue( !FileTools.cleanForFileName( "foo   ;/crud" ).contains( " " ) );
        assertEquals( "foo_bar_crud", FileTools.cleanForFileName( "#foo/   bar'''\"\"/crud" ) );

    }

    public void testCleanFileNameErr() {
        try {
            FileTools.cleanForFileName( "   " );
            fail();
        } catch ( IllegalArgumentException e ) {

        }
        try {
            FileTools.cleanForFileName( null );
            fail();
        } catch ( IllegalArgumentException e ) {

        }
    }

    public void testCopyFile() throws Exception {
        InputStream is = new GZIPInputStream( this.getClass().getResourceAsStream( "/data/testdata.gz" ) );
        File testout = File.createTempFile( "testcopy", ".txt" );
        testout.deleteOnExit();
        OutputStream output = new FileOutputStream( testout );
        FileTools.copy( is, output );
        InputStream inout = new FileInputStream( testout );
        assertTrue( inout.available() > 0 );
        long expected = 2736;
        long actual = testout.length();
        assertEquals( "Output file had the wrong size", expected, actual );
        inout.close();
        testout.delete();
    }

    public void testCopyFileFailOnDirectoryInput() throws Exception {
        try {

            FileTools.copyPlainOrCompressedFile( System.getProperty( "java.io.tmpdir" ), tempoutput.getAbsolutePath() );
            fail( "Should have gotten an exception" );
        } catch ( UnsupportedOperationException e ) {
            // expected
        }
    }

    public void testCopyFileFailOnDirectoryOutput() throws Exception {

        try {
            FileTools.copyPlainOrCompressedFile( File.createTempFile( "junkme", ".txt" ).getAbsolutePath(),
                    tempdir.getAbsolutePath() );

            fail( "Should have gotten an exception" );
        } catch ( UnsupportedOperationException e ) {
            // expected
        }

    }

    /**
     * Tests trying to delete a directory without first deleting the files. The expected result is the directory (and
     * subdirectories) should not be deleted.
     */
    public void testDeleteDir() throws Exception {
        File dir = FileTools.createDir( tempdir.getAbsolutePath() + File.separatorChar + "testdir" );

        File subdir = FileTools.createDir( dir.getAbsolutePath() + File.separatorChar + "testsubdir" );

        File file0 = File.createTempFile( "junk", ".txt", dir.getAbsoluteFile() );
        File file1 = File.createTempFile( "junk", ".txt", dir.getAbsoluteFile() );
        File file2 = File.createTempFile( "junk", ".txt", subdir.getAbsoluteFile() );

        Collection<File> files = new HashSet<File>();
        files.add( file0 );
        files.add( file1 );
        files.add( file2 );

        int numDeleted = FileTools.deleteDir( dir );
        assertEquals( 0, numDeleted );

    }

    /**
     * Tests deleting files in a directory tree, then the directories.
     */
    public void testDeleteFilesAndDir() throws Exception {
        File dir = FileTools.createDir( tempdir.getAbsolutePath() + File.separatorChar + "dir" );

        File subdir = FileTools.createDir( dir.getAbsolutePath() + File.separatorChar + "subdir" );

        File file0 = File.createTempFile( "junk", ".txt", dir.getAbsoluteFile() );
        File file1 = File.createTempFile( "junk", ".txt", dir.getAbsoluteFile() );
        File file2 = File.createTempFile( "junk", ".txt", subdir.getAbsoluteFile() );

        Collection<File> files = new HashSet<File>();
        files.add( file0 );
        files.add( file1 );
        files.add( file2 );

        FileTools.deleteFiles( files );
        int numDeleted = FileTools.deleteDir( dir );

        assertEquals( 2, numDeleted );
    }

    /*
     * Test method for 'basecode.util.FileTools.getExtension(String)'
     */
    public void testGetExtension() {
        assertEquals( "bar", FileTools.getExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.getInputStreamFromPlainOrCompressedFile(String)'
     */
    public void testGetInputStreamFromPlainOrCompressedFile() throws Exception {
        InputStream is = FileTools.getInputStreamFromPlainOrCompressedFile( plain.getPath() );
        assertTrue( is != null && is.available() > 0 );
    }

    /*
     * Test method for 'basecode.util.FileTools.getWithoutExtension(String)'
     */
    public void testGetWithoutExtension() {
        assertFalse( FileTools.chompExtension( plain.getPath() ).endsWith( ".bar" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.getWithoutExtension(String)'
     */
    public void testGetWithoutExtensionB() {
        assertEquals( "a.b", FileTools.chompExtension( "a.b.jar" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.getWithoutExtension(String)'
     */
    public void testGetWithoutExtensionC() {
        assertEquals( "a.b.", FileTools.chompExtension( "a.b..jar" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.hasImageExtension(String)'
     */
    public void testHasImageExtension() {
        assertFalse( FileTools.hasImageExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.hasXMLExtension(String)'
     */
    public void testHasXMLExtension() {
        assertFalse( FileTools.hasXMLExtension( plain.getPath() ) );
    }

    /*
     * 
     * 
     * Test method for 'basecode.util.FileTools.isGZipped(String)'
     */
    public void testIsGZipped() {
        assertFalse( FileTools.isGZipped( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.isZipped(String)'
     */
    public void testIsZipped() {
        assertFalse( FileTools.isZipped( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testDir(String)'
     */
    public void testTestDir() {
        assertFalse( FileTools.testDir( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testFile(File)'
     */
    public void testTestFileFile() {
        assertTrue( FileTools.testFile( plain ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testFile(String)'
     */
    public void testTestFileString() {
        assertTrue( FileTools.testFile( plain.getPath() ) );
    }

    public void testUnGzipFile() throws Exception {
        String result = FileTools.unGzipFile( compressed.getAbsolutePath() );
        Reader r = new FileReader( new File( result ) );
        char[] buf = new char[1024];
        int j = r.read( buf );
        assertEquals( "unexpected character count", 13, j );
    }

    public void testUnzipFile() throws Exception {
        zipped = File.createTempFile( "test", ".zip" );
        ZipOutputStream out = new ZipOutputStream( new FileOutputStream( zipped ) );
        for ( int i = 0; i < 3; i++ ) {
            out.putNextEntry( new ZipEntry( "foo" + i ) );
            out.write( ( new Byte( "34" ) ).byteValue() );
            out.closeEntry();
        }
        out.close();
        Collection<File> result = FileTools.unZipFiles( zipped.getAbsolutePath() );
        assertEquals( 3, result.size() );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        plain = File.createTempFile( "foo", ".bar" );
        plain.deleteOnExit();
        FileOutputStream tmp = new FileOutputStream( plain );
        tmp.write( "fooblydoobly\n".getBytes() );
        tmp.close();

        compressed = new File( plain.getAbsolutePath() + ".gz" );
        OutputStream fos = new FileOutputStream( compressed );
        OutputStream cos = new GZIPOutputStream( fos );

        InputStream input = new FileInputStream( plain );

        byte[] buf = new byte[1024];
        int len;
        while ( ( len = input.read( buf ) ) > 0 ) {
            cos.write( buf, 0, len );
        }
        input.close();
        cos.close();
        tempoutput = File.createTempFile( "junkme", ".txt" );

        tempdir = FileTools.createDir( System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "junk.tmpdir."
                + RandomStringUtils.randomAlphabetic( 10 ) );

    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if ( plain != null ) plain.delete();
        if ( compressed != null ) compressed.delete();
        if ( tempoutput != null ) tempoutput.delete();
        if ( tempdir != null ) tempdir.delete();
    }
}