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

import java.util.LinkedList;

/**
 * Simple Queue implementation.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Queue {

    private LinkedList queue;

    public Queue() {
        queue = new LinkedList();
    }

    /**
     * @param obj Object
     */
    public void enqueue( Object obj ) {
        queue.addLast( obj );
    }

    /**
     * @return Object
     */
    public Object dequeue() {
        if ( queue.isEmpty() ) {
            return null;
        }
        return queue.removeFirst();
    }

    /**
     * @return boolean
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     *  
     */
    public void makeEmpty() {
        queue.clear();
    }

}