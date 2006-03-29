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

/**
 * @author Paul Pavlidis
 * @version $Id$
 * @deprecated -- use java.util.List instead.
 */
public class Stack {

    private Object[] stack;
    private int top;
    private final static int DEFAULTCAPACITY = 10000;

    /**
     * Build a stack with the default capacity.
     */
    public Stack() {
        this( DEFAULTCAPACITY );
    }

    /**
     * Build a stack with a given capacity.
     * 
     * @param capacity int
     */
    public Stack( int capacity ) {
        stack = new Object[capacity];
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
        Object topObj = top();
        stack[top--] = null;
        return topObj;

    }

    /**
     * Add an item to the stack.
     * 
     * @param obj Object
     */
    public void push( Object obj ) {
        if ( isFull() ) {
            throw new IndexOutOfBoundsException( "Stack overflow" );
        }
        stack[++top] = obj;
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
        return stack[top];
    }

    /**
     * @return boolean
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * @return boolean
     */
    public boolean isFull() {
        return top == stack.length - 1;
    }

}