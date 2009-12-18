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
package ubic.basecode.math;

import java.io.IOException;

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.datafilter.AbstractTestFilter;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.util.RegressionTesting;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestMatrixStats extends TestCase {

    protected DoubleMatrix<String, String> testdata = null;
    protected DoubleMatrix<String, String> testdatahuge = null;
    DoubleMatrix<String, String> smallT = null;

    double[][] testrdm = { { 1, 2, 3, 4 }, { 11, 12, 13, 14 }, { 21, Double.NaN, 23, 24 } };

    public final void testCorrelationMatrix() throws Exception {
        DoubleMatrix<String, String> actualReturn = MatrixStats.correlationMatrix( testdata );
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> expectedReturn = null;
        try {
            expectedReturn = f.read( AbstractTestFilter.class
                    .getResourceAsStream( "/data/correlation-matrix-testoutput.txt" ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        assertEquals( true, RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.001 ) );
    }

    public final void testMax() throws Exception {
        double expectedReturn = 44625.7;
        double actualReturn = MatrixStats.max( testdata );
        assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
    }

    public final void testMin() throws Exception {
        double expectedReturn = -965.3;
        double actualReturn = MatrixStats.min( testdata );
        assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
    }

    public final void testNan() throws Exception {
        boolean[][] actual = MatrixStats.nanStatusMatrix( testrdm );
        assertFalse( actual[0][0] );
        assertFalse( actual[0][3] );
        assertTrue( actual[2][1] );
    }

    public final void testRbfNormalize() throws Exception {
        double[][] actual = { { 0.001, 0.2, 0.13, 0.4 }, { 0.11, 0.12, 0.00013, 0.14 }, { 0.21, 0.0001, 0.99, 0.24 } };
        DenseDoubleMatrix<String, String> av = new DenseDoubleMatrix<String, String>( actual );
        MatrixStats.rbfNormalize( av, 1 );
        double[][] expected = { { 0.2968, 0.2432, 0.2609, 0.1991 }, { 0.2453, 0.2429, 0.2738, 0.2381 },
                { 0.273, 0.3368, 0.1252, 0.265 } };
        assertEquals( true, RegressionTesting
                .closeEnough( new DenseDoubleMatrix<String, String>( expected ), av, 0.001 ) );
        for ( int i = 0; i < 3; i++ ) {
            assertEquals( 1.0, av.viewRow( i ).zSum(), 0.0001 );
        }
    }

    public final void testSelfSquare() throws Exception {
        double[][] actual = MatrixStats.selfSquaredMatrix( testrdm );
        assertEquals( 1, actual[0][0], 0.000001 );
        assertEquals( 16, actual[0][3], 0.00001 );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DoubleMatrixReader f = new DoubleMatrixReader();

        testdata = f.read( AbstractTestFilter.class.getResourceAsStream( "/data/testdata.txt" ) );

        smallT = DoubleMatrixFactory.dense( testrdm );
        smallT.setRowNames( java.util.Arrays.asList( new String[] { "a", "b", "c" } ) );
        smallT.setColumnNames( java.util.Arrays.asList( new String[] { "w", "x", "y", "z" } ) );

        // testdatahuge = ( DoubleMatrixNamed ) f.read( AbstractTestFilter.class
        // .getResourceAsStream( "/data/melanoma_and_sarcomaMAS5.txt" ) );
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testdata = null;
        testdatahuge = null;
    }

    /**
     * public final void testCorrelationMatrixHuge() { SparseDoubleMatrix2DNamed actualReturn =
     * MatrixStats.correlationMatrix(testdatahuge, 0.9); DoubleMatrixReader f = new DoubleMatrixReader();
     * DoubleMatrixNamed expectedReturn = null; try { expectedReturn = (DoubleMatrixNamed)f.read(
     * AbstractTestFilter.class .getResourceAsStream( "/data/correlation-matrix-testoutput.txt" ) ); } catch (
     * IOException e ) { e.printStackTrace(); } catch ( OutOfMemoryError e ) { e.printStackTrace(); } // assertEquals(
     * true, RegressionTesting.closeEnough(expectedReturn, actualReturn, 0.001 )); }
     */
}
