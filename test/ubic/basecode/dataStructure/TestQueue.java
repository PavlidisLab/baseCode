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
public class TestQueue extends TestCase {
    private Queue queue = null;

    protected void setUp() throws Exception {
        super.setUp();
        queue = new Queue();
        queue.enqueue( new Integer( 1 ) );
        queue.enqueue( new Integer( 2 ) );
        queue.enqueue( new Integer( 3 ) );
        queue.enqueue( new Integer( 4 ) );
        queue.enqueue( new Integer( 5 ) );

    }

    protected void tearDown() throws Exception {
        queue = null;
        super.tearDown();
    }

    public void testEnqueue() {
        Integer expectedReturn = new Integer( 101 );
        queue.enqueue( new Integer( 100 ) );
        queue.enqueue( new Integer( 101 ) );
        queue.enqueue( new Integer( 102 ) );
        queue.enqueue( new Integer( 103 ) );
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        Integer actualReturn = ( Integer ) queue.dequeue();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testDequeue() {
        Integer expectedReturn = new Integer( 3 );
        queue.dequeue();
        queue.dequeue();
        Integer actualReturn = ( Integer ) queue.dequeue();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testEmptyDequeue() {
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        Integer actualReturn = ( Integer ) queue.dequeue();
        Integer expectedReturn = null;
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    // public void testFullEnqueue() {
    // try {
    // shortQueue.enqueue( new Integer( 495 ) );
    // shortQueue.enqueue( new Integer( 495 ) );
    // shortQueue.enqueue( new Integer( 495 ) );
    // shortQueue.enqueue( new Integer( 495 ) );
    // shortQueue.enqueue( new Integer( 495 ) );
    // fail( "Should raise an IndexOutOfBoundsException" );
    // } catch ( IndexOutOfBoundsException success ) {
    //
    // }
    //
    // }

}