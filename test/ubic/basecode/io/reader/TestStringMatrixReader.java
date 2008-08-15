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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.StringMatrix;

/** 
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestStringMatrixReader extends TestCase {

    StringMatrix<String, String> matrix = null;
    InputStream is = null;
    StringMatrixReader reader = null;

    public void testReadInputStreamColumnCount() {
        try {
            matrix =   reader.read( is );
            int actualReturn = matrix.columns();
            int expectedReturn = 12;
            assertEquals( "return value", expectedReturn, actualReturn );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

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

    public void testReadInputStreamGotRowName() {
        try {
            matrix =   reader.read( is );
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
    public void testReadInputStreamRowCount() {
        try {
            matrix =reader.read( is );
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
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        reader = new StringMatrixReader();
        is = TestStringMatrixReader.class.getResourceAsStream( "/data/testdata.txt" );
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}