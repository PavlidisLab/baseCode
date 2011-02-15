/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.math;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import junit.framework.TestCase;

/**
 * @author paul
 * @version $Id$
 */
public class KruskalWallisTest extends TestCase {

    public void testKW() {

        double[] s = new double[] { 2.9, 3.0, 2.5, 2.6, 3.2, 3.8, 2.7, 4.0, 2.4, 2.8, 3.4, 3.7, 2.2, 2.0 };

        int[] g = new int[] { 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3 };

        double actual = KruskalWallis.test( new DoubleArrayList( s ), new IntArrayList( g ) );

        double expected = 0.68;

        assertEquals( expected, actual, 0.001 );
    }

}
