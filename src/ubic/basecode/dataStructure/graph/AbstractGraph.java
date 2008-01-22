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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.Visitable;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public abstract class AbstractGraph<K, V> implements Graph<K, V> {

    private static Log log = LogFactory.getLog( AbstractGraph.class.getName() );
    protected Map<K, GraphNode<K, V>> items;

    public AbstractGraph() {
        items = new LinkedHashMap<K, GraphNode<K, V>>();
    }

    /**
     * Create a new graph from a set of nodes. This allows us to easily make subtrees.
     * 
     * @param nodes Set of AbstractGraphNodes.
     */
    public AbstractGraph( Set<GraphNode<K, V>> nodes ) {
        this();
        for ( GraphNode<K, V> n : nodes ) {
            this.addNode( n.getKey(), n.getItem() );
        }
    }

    /**
     * @param node GraphNode
     */
    public void addNode( GraphNode<K, V> node ) {
        node.setGraph( this );
        items.put( node.getKey(), node );
    }

    /**
     * Retrieve a node by key. To get the contents of a node use getNodeContents(key)
     * 
     * @param key Object
     * @see #getNodeContents(Object)
     * @return AbstractGraphNode referenced by the key.
     */
    public GraphNode<K, V> get( K key ) {
        return items.get( key );
    }

    /**
     * @return Map
     */
    public Map getItems() {
        return items;
    }

    /**
     * Retrieve the contents of a node by key.
     * 
     * @see #get
     * @param key Object
     * @return The object contained by a node, not the node itself.
     */
    public V getNodeContents( K key ) {
        if ( !items.containsKey( key ) ) return null;
        return items.get( key ).getItem();
    }

    /**
     * @param key Object
     * @return true if the graph contains an item referenced by key, false otherwise.
     */
    public boolean containsKey( K key ) {
        return items.containsKey( key );
    }

    /**
     * Reset the 'visited' marks of the graph to false.
     */
    public void unmarkAll() {
        for ( Iterator<K> it = items.keySet().iterator(); it.hasNext(); ) {
            K item = it.next();
            if ( !( item instanceof Visitable ) ) {
                log.debug( "Got " + item.getClass().getName() );
                break;
            }
            ( ( Visitable ) item ).unMark();
        }
    }
}