/*
 * The basecode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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