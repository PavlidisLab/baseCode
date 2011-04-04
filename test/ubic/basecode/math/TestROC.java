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

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestROC extends TestCase {

    Set<Integer> ranksOfPositives;

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

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // set up the ranks of the opsitives
        ranksOfPositives = new HashSet<Integer>();
        ranksOfPositives.add( 0 );
        ranksOfPositives.add( 3 );
        ranksOfPositives.add( 5 );
    }

}