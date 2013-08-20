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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A graph that contains DirectedGraphNodes. It can be cyclic. Small unconnected parts of the graph will be ignored for
 * many operation. Tree traversals start from the root node. There can be only one root.
 * 
 * @todo do something about cyclicity; make this a dag or a subclass...
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DirectedGraph<K, V> extends AbstractGraph<DirectedGraphNode<K, V>, K, V> {

    private static Logger log = LoggerFactory.getLogger( DirectedGraph.class );
    protected DefaultTreeModel dtm;
    protected Map<K, DirectedGraphNode<K, V>> items;

    private JTree treeView;

    public DirectedGraph() {
        items = new LinkedHashMap<K, DirectedGraphNode<K, V>>();
    }

    /**
     * Add a child to a particular node identified by key; if the node is not in the graph, an exception is thrown.
     * 
     * @param key Object
     * @param newChildKey Object
     * @throws IllegalStateException if the graph doesn't contain the child node.
     */
    public void addChildTo( K key, K newChildKey ) throws IllegalStateException {

        assert key != null;
        assert newChildKey != null;

        if ( !items.containsKey( newChildKey ) ) {
            throw new IllegalArgumentException( "Attempt to add link to node that is not in the graph" );
        }

        if ( items.containsKey( key ) ) {
            items.get( key ).addChild( newChildKey );
            items.get( newChildKey ).addParent( key );
        } else {
            throw new IllegalArgumentException( "Attempt to add child to a node that is not in the graph" );
        }

    }

    /**
     * Add a child to a particualar node identified by key. If it is a new node, it will be added to the graph first.
     * 
     * @param key Object
     * @param newChildKey Object
     * @param newChild Object
     */
    public void addChildTo( K key, K newChildKey, V newChild ) {

        assert key != null;
        assert newChildKey != null;
        assert newChild != null;

        if ( !items.containsKey( key ) ) {
            throw new IllegalArgumentException( "Attempt to add a child to a non-existent node: " + key );
        }

        if ( !items.containsKey( newChildKey ) ) {
            this.addNode( newChildKey, newChild );
        }

        this.addChildTo( key, newChildKey );
        this.addParentTo( newChildKey, key );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.graph.AbstractGraph#addNode(ubic.basecode.dataStructure.graph.GraphNode)
     */
    @Override
    public void addNode( DirectedGraphNode<K, V> node ) {
        assert node != null;
        node.setGraph( this );
        items.put( node.getKey(), node );
    }

    /**
     * Will not be attached to any other node.
     * 
     * @param key Object
     * @param item Object
     */
    @Override
    public void addNode( K key, V item ) {
        assert key != null;
        assert item != null;

        if ( !items.containsKey( key ) ) {
            items.put( key, new DirectedGraphNode<K, V>( key, item, this ) );
        }
    }

    /**
     * @param key Object
     * @param newParentKey Object
     * @throws IllegalArgumentException if the new parent isn't already in the graph.
     */
    public void addParentTo( K key, K newParentKey ) throws IllegalStateException {
        assert key != null;
        assert newParentKey != null;

        if ( !items.containsKey( newParentKey ) ) {
            throw new IllegalArgumentException( "Attempt to add as parent a node that is not in the graph: "
                    + newParentKey );
        }

        if ( items.containsKey( key ) ) {
            items.get( key ).addParent( newParentKey );
            items.get( newParentKey ).addChild( key );
        }

    }

    /**
     * @param key Object
     * @param newParentKey Object
     * @param newParent Object
     */
    public void addParentTo( K key, K newParentKey, V newParent ) {

        assert newParentKey != null;
        assert newParent != null;

        if ( !items.containsKey( newParent ) ) {
            this.addNode( newParentKey, newParent );
        }

        assert key != null;

        this.addChildTo( key, newParentKey );
        this.addParentTo( newParentKey, key );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.graph.AbstractGraph#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey( K key ) {
        if ( key == null ) return false;
        return getItems().containsKey( key );
    }

    /**
     * @param leaf the key for the node. If it is not leaf, nothing will happen.
     */
    public void deleteLeaf( K leaf ) {
        assert leaf != null;
        DirectedGraphNode<K, V> leafNode = this.get( leaf );
        if ( leafNode == null ) {
            throw new IllegalArgumentException( "No such node" );
        }

        if ( !leafNode.isLeaf() ) {
            return;
        }

        Set<DirectedGraphNode<K, V>> parents = leafNode.getAllParentNodes();

        items.remove( leaf );

        for ( DirectedGraphNode<K, V> node : parents ) {
            node.prune();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.graph.AbstractGraph#getItems()
     */
    @Override
    public Map<K, DirectedGraphNode<K, V>> getItems() {
        return this.items;
    }

    /**
     * @return root of the tree.
     */
    public DirectedGraphNode<K, V> getRoot() {
        this.topoSort();
        List<DirectedGraphNode<K, V>> nodes = new ArrayList<DirectedGraphNode<K, V>>( items.values() );
        Collections.sort( nodes );
        return nodes.get( 0 );
    }

    public DefaultTreeModel getTreeModel() {
        if ( treeView == null ) this.treeView();
        return dtm;
    }

    /**
     * Get all the values in thee graph.
     * 
     * @return
     */
    public Collection<V> getValues() {
        Collection<V> result = new HashSet<V>();
        for ( K element : this.items.keySet() ) {
            DirectedGraphNode<K, V> v = items.get( element );
            result.add( v.getItem() );
        }
        return result;
    }

    /**
     * Remove edges to nodes that aren't in the graph.
     */
    public void prune() {
        for ( K element : this.items.keySet() ) {
            assert element != null;
            DirectedGraphNode<K, V> v = items.get( element );
            v.prune();
        }
    }

    /**
     * Fills in the topoSortOrder for each node.
     */
    public void topoSort() {
        Queue<DirectedGraphNode<K, V>> q = new LinkedList<DirectedGraphNode<K, V>>();
        int counter = 0;

        Map<DirectedGraphNode<K, V>, Integer> degrees = new HashMap<DirectedGraphNode<K, V>, Integer>();

        /* Get the degrees of all items, and enqueue zero-indegree nodes */
        for ( K element : this.items.keySet() ) {
            DirectedGraphNode<K, V> v = items.get( element );
            degrees.put( v, new Integer( v.inDegree() ) );
            if ( degrees.get( v ).intValue() == 0 ) {
                q.offer( v );
            }
        }

        while ( !q.isEmpty() ) {
            DirectedGraphNode<K, V> v = q.remove();
            v.setTopoSortOrder( ++counter );
            for ( DirectedGraphNode<K, V> w : v.getChildNodes() ) {
                /* decrement the degree of this node */
                assert w != null;
                assert degrees.containsKey( w );

                Integer inDegree = degrees.get( w );
                inDegree--;
                degrees.put( w, inDegree );

                /* see if this now is one of the zero-indegree nodes */
                if ( inDegree == 0 ) {
                    q.offer( w );
                }
            }
        }

        if ( counter != items.size() ) {
            throw new IllegalStateException( "Graph contains a cycle? " + counter + " items found, " + items.size()
                    + " expected" );
        }

    }

    /**
     * Shows the tree as a tabbed list. Items that have no parents are shown at the 'highest' level.
     * 
     * @return String
     */
    @Override
    public String toString() {
        prune();
        this.topoSort();
        List<DirectedGraphNode<K, V>> nodes = new ArrayList<DirectedGraphNode<K, V>>( items.values() );
        Collections.sort( nodes );
        DirectedGraphNode<K, V> root = nodes.get( 0 );
        StringBuffer buf = new StringBuffer();
        int tablevel = 0;
        makeString( root, buf, tablevel );
        return buf.toString();
    }

    /**
     * Generate a JTree corresponding to this graph.
     * 
     * @return javax.swing.JTree
     */
    public JTree treeView() {
        if ( treeView == null ) {
            throw new IllegalStateException(
                    "You must call treeview(Class<? extends DefaultMutableTreeNode> nodeClass ) first" );
        }
        return treeView;
    }

    /**
     * Generate a JTree corresponding to this graph.
     * 
     * @param nodeClass The class to be used for TreeNodes. Must provide a constructor that takes a DirectedGraphNode
     *        (the root)
     * @return javax.swing.JTree
     */
    public JTree treeView( Class<? extends DefaultMutableTreeNode> nodeClass ) {
        log.debug( "Constructing tree view of graph" );
        DirectedGraphNode<K, V> root = getRoot();
        Constructor<DefaultMutableTreeNode> constructor;
        DefaultMutableTreeNode top = null;
        treeView = null;
        try {
            constructor = ( Constructor<DefaultMutableTreeNode> ) nodeClass.getConstructor( new Class[] { root
                    .getClass() } );
            top = constructor.newInstance( new Object[] { root } );
            log.debug( "Starting tree with: " + top.getClass().getName() );
            root.mark();
            addJTreeNode( top, root, constructor );
            dtm = new DefaultTreeModel( top );
            treeView = new JTree( dtm );

        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return treeView;
    }

    /**
     * @param startJTreeNode
     * @param startNode
     * @param constructor
     */
    private void addJTreeNode( DefaultMutableTreeNode startJTreeNode, DirectedGraphNode<K, V> startNode,
            Constructor<DefaultMutableTreeNode> constructor ) {
        if ( startJTreeNode == null ) return;
        Set<DirectedGraphNode<K, V>> children = startNode.getChildNodes();

        for ( Iterator<DirectedGraphNode<K, V>> it = children.iterator(); it.hasNext(); ) {
            DirectedGraphNode<K, V> nextNode = it.next();
            if ( !nextNode.isVisited() ) {
                DefaultMutableTreeNode newJTreeNode = null;
                try {
                    newJTreeNode = constructor.newInstance( new Object[] { nextNode } );
                } catch ( Exception e ) {
                    log.error( e.getMessage(), e );
                }
                startJTreeNode.add( newJTreeNode );
                nextNode.mark();
                this.addJTreeNode( newJTreeNode, nextNode, constructor );
            }
        }
    }

    /*
     * Helper for toString. Together with toString, demonstrates how to iterate over the tree
     */
    private String makeString( DirectedGraphNode<K, V> startNode, StringBuffer buf, int tabLevel ) {

        if ( buf == null ) {
            buf = new StringBuffer();
        }

        Set<DirectedGraphNode<K, V>> children = startNode.getChildNodes();

        if ( !startNode.isVisited() ) {
            buf.append( startNode + "\n" );
            startNode.mark();
        }
        tabLevel++;
        for ( Iterator<DirectedGraphNode<K, V>> it = children.iterator(); it.hasNext(); ) {
            DirectedGraphNode<K, V> f = it.next();
            if ( !f.isVisited() ) {
                for ( int i = 0; i < tabLevel; i++ ) {
                    buf.append( "\t" );
                }
                buf.append( f + "\n" );
                f.mark();
                this.makeString( f, buf, tabLevel );
            }
        }

        return buf.toString();
    }

}