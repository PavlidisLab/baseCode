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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.Visitable;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public abstract class AbstractGraphNode<K, V> extends Visitable implements GraphNode<K, V> {
    protected static Log log = LogFactory.getLog( GraphNode.class );
    protected K key;
    protected V item;
    protected boolean visited = false;

    /**
     * Create a new node when given only a key.
     * 
     * @param key
     */
    public AbstractGraphNode( K key ) {
        this.key = key;
        this.item = null;
    }

    public AbstractGraphNode( K key, V value ) {
        this.key = key;
        this.item = value;
    }

    /**
     * Get the actual contents of the node.
     * 
     * @return
     */
    @Override
    public V getItem() {
        return item;
    }

    /**
     * Get the key for the node.
     * 
     * @return Object
     */
    @Override
    public K getKey() {
        return key;
    }

    @Override
    public boolean isVisited() {
        return visited;
    }

    @Override
    public void mark() {
        visited = true;
    }

    @Override
    public void setItem( V value ) {
        this.item = value;
    }

    @Override
    public void setValue( K key, V value ) {
        this.item = value;
        this.key = key;
    }

    @Override
    public String toString() {
        return item.toString();
    }

    @Override
    public void unMark() {
        visited = false;
    }

}