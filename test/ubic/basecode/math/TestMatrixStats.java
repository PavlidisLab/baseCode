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
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed2D;
import ubic.basecode.datafilter.AbstractTestFilter;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.util.RegressionTesting;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestMatrixStats extends TestCase {

    protected DoubleMatrixNamed2D testdata = null;
    protected DoubleMatrixNamed2D testdatahuge = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        DoubleMatrixReader f = new DoubleMatrixReader();

        testdata = ( DoubleMatrixNamed2D ) f.read( AbstractTestFilter.class.getResourceAsStream( "/data/testdata.txt" ) );

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

    public final void testMin() throws Exception {
        double expectedReturn = -965.3;
        double actualReturn = MatrixStats.min( testdata );
        assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
    }

    public final void testMax() throws Exception {
        double expectedReturn = 44625.7;
        double actualReturn = MatrixStats.max( testdata );
        assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
    }

    public final void testCorrelationMatrix() throws Exception {
        DoubleMatrixNamed2D actualReturn = MatrixStats.correlationMatrix( testdata );
        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrixNamed2D expectedReturn = null;
        try {
            expectedReturn = ( DoubleMatrixNamed2D ) f.read( AbstractTestFilter.class
                    .getResourceAsStream( "/data/correlation-matrix-testoutput.txt" ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        assertEquals( true, RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.001 ) );
    }

    public final void testRbfNormalize() throws Exception {
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
