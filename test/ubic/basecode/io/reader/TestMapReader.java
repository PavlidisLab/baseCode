/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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
package ubic.basecode.io.reader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ubic.basecode.util.FileTools;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestMapReader {
    private MapReader mapReader = null;

    @Before
    public void setUp() throws Exception {
        mapReader = new MapReader();
    }

    @After
    public void tearDown() throws Exception {
        mapReader = null;
    }

    @Test
    public void testRead() throws IOException {
        InputStream m = TestMapReader.class.getResourceAsStream( "/data/testmap.txt" );
        int expectedReturn = 100;
        int actualReturn = mapReader.read( m, true ).size(); // file has header
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testReadNoHeader() throws IOException {
        InputStream m = TestMapReader.class.getResourceAsStream( "/data/testmap.txt" );
        int expectedReturn = 101;
        int actualReturn = mapReader.read( m ).size(); // file has header
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    @Test
    public void testReadNoHeaderFile() throws Exception {
        String f = FileTools.resourceToPath( "/data/testmap.txt" );
        int expectedReturn = 101;
        int actualReturn = mapReader.read( f ).size(); // file has header
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}