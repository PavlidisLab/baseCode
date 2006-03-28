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
package ubic.basecode.io.reader;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.SparseRaggedDoubleMatrix2DNamed;
import ubic.basecode.util.RegressionTesting;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestSparseRaggedDouble2DNamedMatrixReader extends TestCase {

    SparseRaggedDoubleMatrix2DNamed matrix = null;
    InputStream is = null;
    SparseRaggedDouble2DNamedMatrixReader reader = null;
    InputStream isa = null;
    InputStream isbig = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        reader = new SparseRaggedDouble2DNamedMatrixReader();
        is = TestSparseRaggedDouble2DNamedMatrixReader.class.getResourceAsStream( "/data/JW-testmatrix.txt" );
        isa = TestSparseRaggedDouble2DNamedMatrixReader.class
                .getResourceAsStream( "/data/adjacencylist-testmatrix.txt" );
        isbig = TestSparseRaggedDouble2DNamedMatrixReader.class
                .getResourceAsStream( "/data/adjacency_list.7ormore.txt" );
    }

    /*
     * Class under test for NamedMatrix read(InputStream)
     */
    public void testReadInputStream() throws Exception {
        matrix = ( SparseRaggedDoubleMatrix2DNamed ) reader.read( is, 1 );
        String actualReturn = matrix.toString();
        String expectedReturn = RegressionTesting.readTestResult( TestSparseDoubleMatrixReader.class
                .getResourceAsStream( "/data/JW-testoutput.txt" ) );
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    /*
     * Class under test for NamedMatrix readFromAdjList(InputStream)
     */
    public void testReadStreamAdjList() throws Exception {

        matrix = ( SparseRaggedDoubleMatrix2DNamed ) reader.readFromAdjList( isa );
        String actualReturn = matrix.toString();
        String expectedReturn = RegressionTesting.readTestResult( TestSparseDoubleMatrixReader.class
                .getResourceAsStream( "/data/JW-testoutputSym.txt" ) );
        assertEquals( "return value", expectedReturn, actualReturn );

    }

    /*
     * Class under test for NamedMatrix readFromAdjList(InputStream) - bigger matrix
     */
    public void testReadStreamAdjListBig() throws Exception {

        matrix = ( SparseRaggedDoubleMatrix2DNamed ) reader.readFromAdjList( isbig );

        String actualReturn = matrix.toString();

        ZipInputStream zis = new ZipInputStream( TestSparseDoubleMatrixReader.class
                .getResourceAsStream( "/data/adjacency_list.7ormore.output.zip" ) );
        zis.getNextEntry();

        String expectedReturn = RegressionTesting.readTestResult( zis );

        assertEquals( "return value", expectedReturn, actualReturn );

    }

}