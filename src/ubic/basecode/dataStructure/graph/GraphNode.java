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

/**
 * @author pavlidis
 * 
 */
public interface GraphNode<K, V> {

    /**
     * @return the Graph this belongs to.
     */
    public Graph<? extends GraphNode<K, V>, K, V> getGraph();

    /**
     * @return the contents of the node.
     */
    public V getItem();

    /**
     * @return the key for this node.
     */
    public K getKey();

    /**
     * Set the contents of the node.
     * 
     * @param value
     */
    public void setItem( V value );

    /**
     * Set the key and value associated with this node.
     * 
     * @param key
     * @param value
     */
    public void setValue( K key, V value );
}
