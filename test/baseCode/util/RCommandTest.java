package baseCode.util;

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

    RCommand rc = null;

    public void setUp() {
        rc = RCommand.newInstance();
    }

    public void tearDown() {
        rc.disconnect();
        rc.stopServer();
    }

    /*
     * Test method for 'edu.columbia.gemma.tools.RCommand.exec(String)'
     */
    public void testExec() throws Exception {
        assertTrue( rc.eval( "R.version.string" ).asString().startsWith( "R version 2" ) );
    }

    /*
     * Test method for 'edu.columbia.gemma.tools.RCommand.exec(String)'
     */
    public void testExecDoubleArray() throws Exception {
        assertTrue( RegressionTesting.closeEnough( new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, rc
                .doubleArrayEval( "rep(1, 10)" ), 0.001 ) );
    }
}
