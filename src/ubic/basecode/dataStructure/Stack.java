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
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Stack {

    private LinkedList stack;

    /**
     * Build a stack with the default capacity.
     */
    public Stack() {
        stack = new LinkedList();
    }

    /**
     * Remove the most recently added item.
     * 
     * @return Object
     */
    public Object pop() {
        if ( isEmpty() ) {
            return null;
        }
        return stack.removeFirst();
    }

    /**
     * Add an item to the stack.
     * 
     * @param obj Object
     */
    public void push( Object obj ) {
        stack.addFirst( obj );
    }

    /**
     * Get the most recently added item, without removing it.
     * 
     * @return Object
     */
    public Object top() {
        if ( isEmpty() ) {
            return null;
        }
        return stack.getFirst();
    }

    /**
     * @return boolean
     */
    public boolean isEmpty() {
        return stack.size() == 0;
    }

}