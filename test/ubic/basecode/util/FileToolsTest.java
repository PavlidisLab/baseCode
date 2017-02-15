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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author keshav
 * @author pavlidis
 * @version $Id$
 */
public class FileToolsTest {
    File compressed;

    File plain;
    File tempdir;
    File tempoutput;
    File zipped;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {

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
    @After
    public void tearDown() throws Exception {
        if ( plain != null ) plain.delete();
        if ( compressed != null ) compressed.delete();
        if ( tempoutput != null ) tempoutput.delete();
        if ( tempdir != null ) tempdir.delete();
    }

    /*
     * Test method for 'basecode.util.FileTools.addDataExtension(String)'
     */
    @Test
    public void testAddDataExtension() {
        assertTrue( FileTools.addDataExtension( plain.getPath() ).endsWith( ".txt" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.addImageExtension(String)'
     */
    @Test
    public void testAddImageExtension() {
        assertTrue( FileTools.addImageExtension( plain.getPath() ).endsWith( ".png" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.changeExtension(String, String)'
     */
    @Test
    public void testChangeExtension() {
        assertTrue( FileTools.changeExtension( plain.getPath(), "barbie" ).endsWith( ".barbie" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.checkPathIsReadableFile(String)'
     */
    @Test
    public void testCheckPathIsReadableFile() {
        try {
            FileTools.checkPathIsReadableFile( plain.getPath() );
        } catch ( IOException e ) {
            fail( "Should not have thrown an IOException" );
        }
    }

    @Test
    public void testCheckPathIsReadableFileNot() {
        try {
            FileTools.checkPathIsReadableFile( plain.getPath() + RandomStringUtils.randomNumeric( 10 ) );
            fail( "Should have thrown an IOException" );
        } catch ( IOException e ) {
            // ok
        }
    }

    @Test
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

    @Test
    public void testCleanFileNameErr() {
        try {
            FileTools.cleanForFileName( "   " );
            fail();
        } catch ( IllegalArgumentException e ) {
            // ok
        }
        try {
            FileTools.cleanForFileName( null );
            fail();
        } catch ( IllegalArgumentException e ) {
            // ok
        }
    }

    @Test
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

    @Test
    public void testCopyFileFailOnDirectoryInput() throws Exception {
        try {

            FileTools.copyPlainOrCompressedFile( System.getProperty( "java.io.tmpdir" ), tempoutput.getAbsolutePath() );
            fail( "Should have gotten an exception" );
        } catch ( UnsupportedOperationException e ) {
            // expected
        }
    }

    @Test
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
    @Test
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

    @Test
    public void testGetCompressedFile() throws Exception {
        InputStream is = FileTools.getInputStreamFromPlainOrCompressedFile( new File( this.getClass()
                .getResource( "/data/testdata.gz" ).toURI() ).getAbsolutePath() );
        assertNotNull( is );
    }

    @Test
    public void testGetCompressedFileZip() throws Exception {
        InputStream is = FileTools.getInputStreamFromPlainOrCompressedFile( new File( this.getClass()
                .getResource( "/data/multtest.test.zip" ).toURI() ).getAbsolutePath() );
        assertNotNull( is );
    }

    /*
     * Test method for 'basecode.util.FileTools.getExtension(String)'
     */
    @Test
    public void testGetExtension() {
        assertEquals( "bar", FileTools.getExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.getInputStreamFromPlainOrCompressedFile(String)'
     */
    @Test
    public void testGetInputStreamFromPlainOrCompressedFile() throws Exception {
        InputStream is = FileTools.getInputStreamFromPlainOrCompressedFile( plain.getPath() );
        assertTrue( is != null && is.available() > 0 );
    }

    @Test
    public void testGetLines() throws Exception {
        List<String> lines = FileTools
                .getLines( new File( this.getClass().getResource( "/data/testdata.txt" ).toURI() ) );
        assertEquals( 31, lines.size() );
        File tmp = File.createTempFile( "junk.", ".txt" );
        FileTools.stringsToFile( lines, tmp );
        lines = FileTools.getLines( tmp );
        assertEquals( 31, lines.size() );
    }

    @Test
    public void testgetStringListFromFile() throws Exception {
        List<String> strings = FileTools.getStringListFromFile( new File( FileTools
                .resourceToPath( "/data/stringlisttest.txt" ) ) );
        assertEquals( 6, strings.size() );
    }

    /*
     * Test method for 'basecode.util.FileTools.getWithoutExtension(String)'
     */
    @Test
    public void testGetWithoutExtension() {
        assertFalse( FileTools.chompExtension( plain.getPath() ).endsWith( ".bar" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.getWithoutExtension(String)'
     */
    @Test
    public void testGetWithoutExtensionB() {
        assertEquals( "a.b", FileTools.chompExtension( "a.b.jar" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.getWithoutExtension(String)'
     */
    @Test
    public void testGetWithoutExtensionC() {
        assertEquals( "a.b.", FileTools.chompExtension( "a.b..jar" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.hasImageExtension(String)'
     */
    @Test
    public void testHasImageExtension() {
        assertFalse( FileTools.hasImageExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.hasXMLExtension(String)'
     */
    @Test
    public void testHasXMLExtension() {
        assertFalse( FileTools.hasXMLExtension( plain.getPath() ) );
    }

    /*
     * 
     * 
     * Test method for 'basecode.util.FileTools.isGZipped(String)'
     */
    @Test
    public void testIsGZipped() {
        assertFalse( FileTools.isGZipped( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.isZipped(String)'
     */
    @Test
    public void testIsZipped() {
        assertFalse( FileTools.isZipped( plain.getPath() ) );
    }

    @Test
    public void testListDirectoryFiles() throws Exception {
        Collection<File> files = FileTools.listDirectoryFiles( new File( FileTools.resourceToPath( "/data/" ) ) );
        assertTrue( !files.isEmpty() );
        boolean found = false;
        for ( File file : files ) {
            if ( file.getAbsolutePath().contains( "multtest.test.zip" ) ) {
                found = true;
            }
        }
        assertTrue( found );
    }

    @Test
    public void testResourcetoPath() throws Exception {
        assertNotNull( FileTools.resourceToPath( "/data/multtest.test.zip" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testDir(String)'
     */
    @Test
    public void testTestDir() {
        assertFalse( FileTools.testDir( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testFile(File)'
     */
    @Test
    public void testTestFileFile() {
        assertTrue( FileTools.testFile( plain ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testFile(String)'
     */
    @Test
    public void testTestFileString() {
        assertTrue( FileTools.testFile( plain.getPath() ) );
    }

    @Test
    public void testTouch() throws Exception {

        String p = plain.getAbsolutePath();
        plain.delete();

        File f = new File( p );
        FileTools.touch( f );
        assertTrue( f.exists() );
        FileTools.touch( f );
        assertTrue( f.exists() );
    }

    @Test
    public void testUnGzipFile() throws Exception {
        String result = FileTools.unGzipFile( compressed.getAbsolutePath() );
        Reader r = new FileReader( new File( result ) );
        char[] buf = new char[1024];
        int j = r.read( buf );
        assertEquals( "unexpected character count", 13, j );
    }

    @Test
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
}