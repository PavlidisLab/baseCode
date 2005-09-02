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

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import baseCode.util.RCommand;
import baseCode.util.RegressionTesting;
import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class LinearModelTest extends TestCase {

    private static Log log = LogFactory.getLog( LinearModelTest.class.getName() );

    public void setUp() {
    }

    public void tearDown() {
    }

    /*
     * Test method for 'baseCode.math.LinearModel.fitNoInteractions()'
     */
    public void testFitNoInteractions() {
        double[] x = new double[] { 2, 1, 2, 4, 6, 4 };
        double[] a = new double[] { 0, 0, 0, 1, 1, 1 };
        double[] b = new double[] { 1, 0, 1, 0, 1, 0 };
        LinearModel lm = new LinearModel( x, a, b );
        lm.fitNoInteractions();
        double[] c = lm.getCoefficients();
        assertTrue( RegressionTesting.closeEnough( new double[] { 0.6666667, 3.5000000, 1.5000000 }, c, 0.0001 ) );
    }
}
