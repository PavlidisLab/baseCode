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
package ubic.basecode.dataStructure;

import junit.framework.TestCase;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestStack extends TestCase {
    private Stack stack = null;
    private Stack stackShort = null;

    protected void setUp() throws Exception {
        super.setUp();

        stack = new Stack();
        stack.push( new Integer( 1 ) );
        stack.push( new Integer( 2 ) );
        stack.push( new Integer( 3 ) );
        stack.push( new Integer( 4 ) );
        stack.push( new Integer( 5 ) );

        stackShort = new Stack();
        stackShort.push( new Integer( 1 ) );
    }

    protected void tearDown() throws Exception {
        stack = null;
        super.tearDown();
    }

    public void testPop() {
        Integer expectedReturn = new Integer( 3 );
        stack.pop();
        stack.pop();
        Integer actualReturn = ( Integer ) stack.pop();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testPush() {
        Integer expectedReturn = new Integer( 100 );
        stack.push( new Integer( 101 ) );
        stack.push( new Integer( 102 ) );
        stack.push( new Integer( 103 ) );
        stack.push( expectedReturn );
        Integer actualReturn = ( Integer ) stack.pop();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testTop() {
        Integer expectedReturn = new Integer( 5 );
        Integer actualReturn = ( Integer ) stack.top();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testEmptyPop() {
        stackShort.pop();
        Integer actualReturn = ( Integer ) stackShort.pop();
        Integer expectedReturn = null;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}