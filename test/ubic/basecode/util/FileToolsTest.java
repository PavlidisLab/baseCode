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
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
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
     * Test method for 'basecode.util.FileTools.checkPathIsReadableFile(String)'
     */
    public void testCheckPathIsReadableFile() throws Exception {
        try {
            FileTools.checkPathIsReadableFile( plain.getPath() );
        } catch ( IOException e ) {
            fail( "Should not have thrown an IOException" );
        }
    }

    /*
     * Test method for 'basecode.util.FileTools.getExtension(String)'
     */
    public void testGetExtension() throws Exception {
        assertEquals( "bar", FileTools.getExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.getWithoutExtension(String)'
     */
    public void testGetWithoutExtension() throws Exception {
        assertFalse( FileTools.chompExtension( plain.getPath() ).endsWith( ".bar" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.changeExtension(String, String)'
     */
    public void testChangeExtension() throws Exception {
        assertTrue( FileTools.changeExtension( plain.getPath(), "barbie" ).endsWith( ".barbie" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.hasImageExtension(String)'
     */
    public void testHasImageExtension() throws Exception {
        assertFalse( FileTools.hasImageExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.hasXMLExtension(String)'
     */
    public void testHasXMLExtension() throws Exception {
        assertFalse( FileTools.hasXMLExtension( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.addImageExtension(String)'
     */
    public void testAddImageExtension() throws Exception {
        assertTrue( FileTools.addImageExtension( plain.getPath() ).endsWith( ".png" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.addDataExtension(String)'
     */
    public void testAddDataExtension() {
        assertTrue( FileTools.addDataExtension( plain.getPath() ).endsWith( ".txt" ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testDir(String)'
     */
    public void testTestDir() throws Exception {
        assertFalse( FileTools.testDir( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testFile(String)'
     */
    public void testTestFileString() throws Exception {
        assertTrue( FileTools.testFile( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.testFile(File)'
     */
    public void testTestFileFile() throws Exception {
        assertTrue( FileTools.testFile( plain ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.isZipped(String)'
     */
    public void testIsZipped() throws Exception {
        assertFalse( FileTools.isZipped( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.isGZipped(String)'
     */
    public void testIsGZipped() throws Exception {
        assertFalse( FileTools.isGZipped( plain.getPath() ) );
    }

    /*
     * Test method for 'basecode.util.FileTools.getInputStreamFromPlainOrCompressedFile(String)'
     */
    public void testGetInputStreamFromPlainOrCompressedFile() throws Exception {
        FileTools.getInputStreamFromPlainOrCompressedFile( plain.getPath() );
    }

}
