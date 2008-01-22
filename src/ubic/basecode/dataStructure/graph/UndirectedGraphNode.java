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
package ubic.basecode.dataStructure.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public class UndirectedGraphNode<K, V> extends AbstractGraphNode<K, V> {

    private Set neighbors;

    public UndirectedGraphNode( K key, V value, AbstractGraph<K, V> graph ) {
        super( key, value, graph );
        neighbors = new HashSet();
    }

    public UndirectedGraphNode( K key ) {
        super( key );
        neighbors = new HashSet();
    }

    public int numNeighbors() {
        return neighbors.size();
    }

    public int compareTo( Object o ) {
        if ( ( ( UndirectedGraphNode ) o ).numNeighbors() > this.numNeighbors() ) {
            return -1;
        } else if ( ( ( UndirectedGraphNode ) o ).numNeighbors() < this.numNeighbors() ) {
            return 1;
        }
        return 0;
    }

}