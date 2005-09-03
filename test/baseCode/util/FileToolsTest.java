package baseCode.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class FileToolsTest extends TestCase {

    File plain;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        plain = File.createTempFile( "foo", ".bar" );
        plain.deleteOnExit();
        FileOutputStream tmp = new FileOutputStream( plain );
        tmp.write( "fooblydoobly\n".getBytes() );
        tmp.close();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        plain.delete();
    }

    /*
     * Test method for 'baseCode.util.FileTools.checkPathIsReadableFile(String)'
     */
    public void testCheckPathIsReadableFile() throws Exception {
        try {
            FileTools.checkPathIsReadableFile( plain.getPath() );
        } catch ( IOException e ) {
            fail( "Should not have thrown an IOException" );
        }
    }

    /*
     * Test method for 'baseCode.util.FileTools.getExtension(String)'
     */
    public void testGetExtension() throws Exception {
        assertEquals( "bar", FileTools.getExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.getWithoutExtension(String)'
     */
    public void testGetWithoutExtension() throws Exception {
        assertFalse( FileTools.getWithoutExtension( plain.getPath() ).endsWith( ".bar" ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.changeExtension(String, String)'
     */
    public void testChangeExtension() throws Exception {
        assertTrue( FileTools.changeExtension( plain.getPath(), "barbie" ).endsWith( ".barbie" ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.hasImageExtension(String)'
     */
    public void testHasImageExtension() throws Exception {
        assertFalse( FileTools.hasImageExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.hasXMLExtension(String)'
     */
    public void testHasXMLExtension() throws Exception {
        assertFalse( FileTools.hasXMLExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.addImageExtension(String)'
     */
    public void testAddImageExtension() throws Exception {
        assertTrue( FileTools.addImageExtension( plain.getPath() ).endsWith( ".png" ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.addDataExtension(String)'
     */
    public void testAddDataExtension() {
        assertTrue( FileTools.addDataExtension( plain.getPath() ).endsWith( ".txt" ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.testDir(String)'
     */
    public void testTestDir() throws Exception {
        assertFalse( FileTools.testDir( plain.getPath() ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.testFile(String)'
     */
    public void testTestFileString() throws Exception {
        assertTrue( FileTools.testFile( plain.getPath() ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.testFile(File)'
     */
    public void testTestFileFile() throws Exception {
        assertTrue( FileTools.testFile( plain ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.isZipped(String)'
     */
    public void testIsZipped() throws Exception {
        assertFalse( FileTools.isZipped( plain.getPath() ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.isGZipped(String)'
     */
    public void testIsGZipped() throws Exception {
        assertFalse( FileTools.isGZipped( plain.getPath() ) );
    }

    /*
     * Test method for 'baseCode.util.FileTools.getInputStreamFromPlainOrCompressedFile(String)'
     */
    public void testGetInputStreamFromPlainOrCompressedFile() throws Exception {
        FileTools.getInputStreamFromPlainOrCompressedFile( plain.getPath() );
    }

}
