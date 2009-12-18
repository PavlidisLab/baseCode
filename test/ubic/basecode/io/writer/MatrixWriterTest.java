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
package ubic.basecode.io.writer;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.DenseDouble3dMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix3D;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.TestStringMatrixReader;
import ubic.basecode.util.RegressionTesting;

/**
 * @author Paul
 * @version $Id$
 */
public class MatrixWriterTest extends TestCase {
    DoubleMatrix<String, String> matrix = null;

    Writer w;

    double[][][] data3d = { { { 1, 2 }, { 3, 4 } }, { { 5, 6 }, { 7, 8 } } };

    /**
     * Test method for
     * {@link ubic.basecode.io.writer.MatrixWriter#writeMatrix(ubic.basecode.dataStructure.matrix.NamedMatrix, boolean)}
     * .
     */
    public void testWriteMatrix() throws Exception {
        MatrixWriter<String, String> writer = new MatrixWriter<String, String>( w, "\t" );
        writer.setTopLeft( "gene" );
        writer.writeMatrix( matrix, true );
        String actual = w.toString();
        String expected = RegressionTesting.readTestResult( "/data/testmatrixwriter2doutput.txt" );
        assertEquals( expected, actual );
    }

    public void testWriteMatrixB() throws Exception {
        File file = File.createTempFile( "foo", "bar" );
        OutputStream os = new PrintStream( file );
        assertNotNull( os );
        MatrixWriter<String, String> writer = new MatrixWriter<String, String>( os );
        writer.setTopLeft( "gene" );
        writer.writeMatrix( matrix, true );
        os.close();
        String actual = RegressionTesting.readTestResult( file );
        String expected = RegressionTesting.readTestResult( "/data/testmatrixwriter2doutput.txt" );
        assertEquals( expected, actual );

    }

    public void testWrite3DMatrix() throws Exception {
        MatrixWriter<String, String> writer = new MatrixWriter<String, String>( w );
        DoubleMatrix3D<String, String, String> m3 = new DenseDouble3dMatrix<String, String, String>( data3d );
        List<String> sln = new ArrayList<String>();
        sln.add( "Slice1" );
        sln.add( "Slice2" );
        List<String> rn = new ArrayList<String>();
        rn.add( "row1" );
        rn.add( "row2" );
        List<String> cn = new ArrayList<String>();
        cn.add( "col1" );
        cn.add( "col2" );

        m3.setColumnNames( cn );
        m3.setRowNames( rn );
        m3.setSliceNames( sln );
        writer.writeMatrix( m3, true );
        String actual = w.toString();
        String expected = RegressionTesting.readTestResult( "/data/testmatrixwriter3doutput.txt" );
        assertEquals( expected, actual );

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DoubleMatrixReader reader = new DoubleMatrixReader();
        InputStream is = TestStringMatrixReader.class.getResourceAsStream( "/data/testdata.txt" );
        matrix = reader.read( is );
        w = new StringWriter();
    }
}
