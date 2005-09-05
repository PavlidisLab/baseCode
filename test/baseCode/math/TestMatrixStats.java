/*
 * The baseCode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.math;

import java.io.IOException;

import junit.framework.TestCase;
import baseCode.dataFilter.AbstractTestFilter;
import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import baseCode.io.reader.DoubleMatrixReader;
import baseCode.util.RegressionTesting;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestMatrixStats extends TestCase {

    protected DoubleMatrixNamed testdata = null;
    protected DoubleMatrixNamed testdatahuge = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        DoubleMatrixReader f = new DoubleMatrixReader();

        testdata = ( DoubleMatrixNamed ) f.read( AbstractTestFilter.class.getResourceAsStream( "/data/testdata.txt" ) );

        // testdatahuge = ( DoubleMatrixNamed ) f.read( AbstractTestFilter.class
        // .getResourceAsStream( "/data/melanoma_and_sarcomaMAS5.txt" ) );
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        testdata = null;
        testdatahuge = null;
    }

    public final void testMin() {
        double expectedReturn = -965.3;
        double actualReturn = MatrixStats.min( testdata );
        assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
    }

    public final void testMax() {
        double expectedReturn = 44625.7;
        double actualReturn = MatrixStats.max( testdata );
        assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
    }

    public final void testCorrelationMatrix() {
        DoubleMatrixNamed actualReturn = MatrixStats.correlationMatrix( testdata );
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrixNamed expectedReturn = null;
        try {
            expectedReturn = ( DoubleMatrixNamed ) f.read( AbstractTestFilter.class
                    .getResourceAsStream( "/data/correlation-matrix-testoutput.txt" ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        assertEquals( true, RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.001 ) );
    }

    public final void testRbfNormalize() {
        MatrixStats.rbfNormalize( testdata, 100 );

        // System.err.println(testdata);

        // assertEquals( true, RegressionTesting.closeEnough(expectedReturn, testdata, 0.001 ));
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
