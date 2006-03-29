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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A graph node that has the concept of parents and children. Keys can be anything, but probably Strings or Integers.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DirectedGraphNode extends AbstractGraphNode implements Comparable {

    protected Set parents;
    // immeddiate parents, references to other GraphNodes by keys.
    protected Set children;
    // immediate children, references to other GraphNodes by keys.

    protected int topoSortOrder = 0;

    /**
     * @param key Object
     * @param value Object
     * @param graph Graph
     */
    public DirectedGraphNode( Object key, Object value, Graph graph ) {
        super( key, value, graph );
        parents = new LinkedHashSet();
        children = new LinkedHashSet();
    }

    /**
     * @param i int
     */
    public void setTopoSortOrder( int i ) {
        topoSortOrder = i;
    }

    /**
     * @return int
     */
    public int getTopoSortOrder() {
        return topoSortOrder;
    }

    /**
     * @param newChildKey Object
     */
    public void addChild( Object newChildKey ) {
        children.add( newChildKey );
    }

    /**
     * @param newParentKey Object
     */
    public void addParent( Object newParentKey ) {
        parents.add( newParentKey );
    }

    /**
     * @return Object
     */
    public Object getParentKeys() {
        return parents;
    }

    /**
     * @return Object
     */
    public Object getChildKeys() {
        return children;
    }

    /**
     * Get the immediate children of this node. References to the DirectedGraphNodes are given, as opposed to key
     * values.
     * 
     * @return Set containing the child nodes of this node.
     */
    public Set getChildNodes() {
        Set f = new LinkedHashSet();
        for ( Iterator i = this.getChildIterator(); i.hasNext(); ) {
            Object k = i.next();
            f.add( getGraph().get( k ) );
        }
        return f;
    }

    /**
     * Get the immediate parents of this node. References to the DirectedGraphNodes are given, as opposed to key values.
     * 
     * @return Set
     */
    public Set getParentNodes() {
        Set f = new LinkedHashSet();
        for ( Iterator i = this.getParentIterator(); i.hasNext(); ) {
            Object k = i.next();
            f.add( getGraph().get( k ) );
        }
        return f;
    }

    /**
     * Get the subgraph starting from this node, including this node.
     * 
     * @return Graph
     */
    public Graph getChildGraph() {
        Set k = this.getAllChildNodes();
        k.add( this );

        DirectedGraph returnVal = new DirectedGraph();
        for ( Iterator it = k.iterator(); it.hasNext(); ) {
            DirectedGraphNode m = ( DirectedGraphNode ) it.next();
            returnVal.addNode( ( DirectedGraphNode ) m.clone() );
        }
        returnVal.prune(); // failing to do this will cause all kinds of problems
        return returnVal;
    }

    /**
     * @return
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * @return int number of immediate children this node has.
     */
    public int outDegree() {
        return children.size();
    }

    /**
     * @return int number of immediate parents this node has.
     */
    public int inDegree() {
        return parents.size();
    }

    /**
     * @return int how many children this node has, determined recursively.
     */
    public int getChildCount() {
        return getAllChildNodes( null ).size();
    }

    /**
     * @return int how many parents this node has, determined recursively.
     */
    public int numParents() {
        return getAllParentNodes( null ).size();
    }

    /**
     * Get all the children of this node, recursively.
     */
    public Set getAllChildNodes() {
        return this.getAllChildNodes( null );
    }

    /**
     * Get all the parents of this node, recursively.
     * 
     * @return
     */
    public Set getAllParentNodes() {
        return this.getAllParentNodes( null );
    }

    /**
     * Check to see if this node has a particular immediate child.
     * 
     * @param j Object
     * @return boolean
     */
    public boolean hasChild( Object j ) {
        return children.contains( j );
    }

    /**
     * Check to see if this node has a particular immediate parent.
     * 
     * @param j Object
     * @return boolean
     */
    public boolean hasParent( Object j ) {
        return parents.contains( j );
    }

    public String toString() {
        return this.getItem().toString();
    }

    /**
     * Remove connections that are to nodes not contained in this graph
     */
    public void prune() {
        for ( Iterator it = this.getChildIterator(); it.hasNext(); ) {
            Object j = it.next();
            DirectedGraphNode k = ( DirectedGraphNode ) getGraph().get( j );
            if ( k == null ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "Pruned child " + j + " from " + this );
                }
                children.remove( j );
            }

        }

        for ( Iterator it = this.getParentIterator(); it.hasNext(); ) {
            Object j = it.next();
            DirectedGraphNode k = ( DirectedGraphNode ) getGraph().get( j );
            if ( k == null ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "Pruned parent " + j + " from " + this );
                }
                parents.remove( j );
            }

        }

    }

    /** ************* private methods *************** */

    private Set getAllChildNodes( Set list ) {
        if ( list == null ) {
            list = new LinkedHashSet();
        }

        for ( Iterator it = this.getChildIterator(); it.hasNext(); ) {
            Object j = it.next();
            list.add( getGraph().get( j ) );
            ( ( DirectedGraphNode ) getGraph().get( j ) ).getAllChildNodes( list );
        }
        return list;
    }

    private Set getAllParentNodes( Set list ) {
        if ( list == null ) {
            list = new LinkedHashSet();
        }

        for ( Iterator it = this.getParentIterator(); it.hasNext(); ) {
            Object j = it.next();
            list.add( getGraph().get( j ) );
            ( ( DirectedGraphNode ) getGraph().get( j ) ).getAllParentNodes( list );
        }
        return list;
    }

    private Iterator getChildIterator() {
        return children.iterator();
    }

    private Iterator getParentIterator() {
        return parents.iterator();
    }

    /**
     * Uses the topological sort order.
     * 
     * @param o Object
     * @return int
     */
    public int compareTo( Object o ) {
        DirectedGraphNode k = ( DirectedGraphNode ) o;
        int ord = k.getTopoSortOrder();
        if ( ord < this.topoSortOrder ) {
            return 1;
        } else if ( ord > this.topoSortOrder ) {
            return -1;
        }
        return 0;
    }

    /**
     * Makes a copy of this node. It does not make a deep copy of the contents. This should be used when making
     * subgraphs.
     * 
     * @return Object
     */
    public Object clone() {
        DirectedGraphNode r = new DirectedGraphNode( key, item, graph );
        for ( Iterator it = this.getParentIterator(); it.hasNext(); ) {
            Object j = it.next();
            r.addParent( j );
        }

        for ( Iterator it = this.getChildIterator(); it.hasNext(); ) {
            Object j = it.next();
            r.addChild( j );
        }
        return r;
    }

}