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

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.StringMatrix;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestStringMatrixReader {

    InputStream is = null;
    StringMatrix<String, String> matrix = null;
    StringMatrixReader reader = null;

    @Test
    public void testReadInputStreamColumnCount() {
        try {
            matrix = reader.read( is );
            int actualReturn = matrix.columns();
            int expectedReturn = 12;
            assertEquals( "return value", expectedReturn, actualReturn );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadInputStreamGotColName() {
        try {
            matrix = reader.read( is );
            boolean actualReturn = matrix.containsColumnName( "sample1" ) && matrix.containsColumnName( "sample12" );
            boolean expectedReturn = true;
            assertEquals( "return value (for sample1 and sample12)", expectedReturn, actualReturn );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadInputStreamGotRowName() {
        try {
            matrix = reader.read( is );
            boolean actualReturn = matrix.containsRowName( "gene1_at" ) && matrix.containsRowName( "AFFXgene30_at" );
            boolean expectedReturn = true;
            assertEquals( "return value", expectedReturn, actualReturn );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /*
     * Class under test for NamedMatrix read(InputStream)
     */
    @Test
    public void testReadInputStreamRowCount() {
        try {
            matrix = reader.read( is );
            int actualReturn = matrix.rows();
            int expectedReturn = 30;
            assertEquals( "return value", expectedReturn, actualReturn );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /*
     * @see TestCase#setUp()
     */
    @Before
    protected void setUp() throws Exception {
        reader = new StringMatrixReader();
        is = TestStringMatrixReader.class.getResourceAsStream( "/data/testdata.txt" );
    }

}