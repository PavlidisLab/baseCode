package baseCode.util;

import junit.framework.TestCase;
import baseCode.util.*;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class RCommandTest extends TestCase {

    /*
     * Test method for 'edu.columbia.gemma.tools.RCommand.exec(String)'
     */
    public void testExec() throws Exception {
        assertTrue( RCommand.exec( "R.version.string" ).asString().startsWith( "R version 2" ) );
    }

    /*
     * Test method for 'edu.columbia.gemma.tools.RCommand.exec(String)'
     */
    public void testExecDoubleArray() throws Exception {
        assertTrue( RegressionTesting.closeEnough( new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, RCommand
                .execDoubleArray( "rep(1, 10)" ), 0.001 ) );
    }
}
