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

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestROC extends TestCase {

    Set ranksOfPositives;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        DoubleArrayList m = new DoubleArrayList( new double[] {} );

        IntArrayList ranks = Rank.rankTransform( m );

        // set up the ranks of the opsitives
        ranksOfPositives = new HashSet();
        ranksOfPositives.add( new Integer( 0 ) );
        ranksOfPositives.add( new Integer( 3 ) );
        ranksOfPositives.add( new Integer( 5 ) );
    }

    public void testAroc() {
        double actualReturn = ROC.aroc( 10, ranksOfPositives );
        double expectedReturn = ( 21.0 - 5.0 ) / 21.0;
        assertEquals( "return value", expectedReturn, actualReturn, 0.00001 );
    }

    public void testArocN() {
        double actualReturn = ROC.aroc( 10, ranksOfPositives, 2 );
        double expectedReturn = 2.0 / 6.0;
        assertEquals( "return value", expectedReturn, actualReturn, 0.00001 );
    }

}