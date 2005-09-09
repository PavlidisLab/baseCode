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
package baseCode.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import baseCode.io.reader.DoubleMatrixReader;

import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class RCommandTest extends TestCase {
    private static Log log = LogFactory.getLog( RCommandTest.class.getName() );

    RCommand rc = null;
    boolean connected = false;
    DoubleMatrixNamed tester;

    public void setUp() throws Exception {
        try {
            rc = RCommand.newInstance();
            connected = true;
        } catch ( RuntimeException e ) {
            connected = false;
        }

        DoubleMatrixReader reader = new DoubleMatrixReader();

        tester = ( DoubleMatrixNamed ) reader.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
    }

    public void tearDown() throws Exception {
        rc.disconnect();
        rc.stopServer();
        tester = null;
    }

    /*
     * Test method for 'edu.columbia.gemma.tools.RCommand.exec(String)'
     */
    public void testExec() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        assertTrue( rc.eval( "R.version.string" ).asString().startsWith( "R version 2" ) );
    }

    /*
     * Test method for 'edu.columbia.gemma.tools.RCommand.exec(String)'
     */
    public void testExecDoubleArray() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        assertTrue( RegressionTesting.closeEnough( new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, rc
                .doubleArrayEval( "rep(1, 10)" ), 0.001 ) );
    }

    public void testAssignAndRetrieveMatrix() throws Exception {
        if ( !connected ) {
            log.warn( "Could not connect to RServe, skipping test." );
            return;
        }
        DoubleMatrixNamed result = rc.retrieveMatrix( rc.assignMatrix( tester ) );
        assertTrue( RegressionTesting.closeEnough( tester, result, 0.0001 ) );
    }

}
