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
import java.util.Collections;
import java.util.HashMap;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A graph that contains DirectedGraphNodes. It can be cyclic. Small unconnected parts of the graph will be ignored for
 * many operation. Tree traversals start from the root node, which is defined as the node with the most children.
 * 
 * @todo do something about cyclicity; make this a dag or a subclass...
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DirectedGraph<K, V> extends AbstractGraph<DirectedGraphNode<K, V>, K, V> {
    private static Log log = LogFactory.getLog( DirectedGraph.class.getName() );
    protected DefaultTreeModel dtm;
    private JTree treeView;

    protected Map<K, DirectedGraphNode<K, V>> items;

    public DirectedGraph() {
        items = new LinkedHashMap<K, DirectedGraphNode<K, V>>();
    }

    public void addMode( DirectedGraphNode<K, V> node ) {
        node.setGraph( this );
        items.put( node.getKey(), node );

    }

    /**
     * @param nodes Set of DirectedGraphNodes
     */
    public DirectedGraph( Set<DirectedGraphNode<K, V>> nodes ) {
        this();
        for ( DirectedGraphNode<K, V> a : nodes ) {
            this.addNode( a );
        }
    }

    /**
     * Add a child to a particular node identified by key; if the node is not in the graph, an exception is thrown.
     * 
     * @param key Object
     * @param newChildKey Object
     * @throws IllegalStateException if the graph doesn't contain the child node.
     */
    public void addChildTo( K key, K newChildKey ) throws IllegalStateException {
        if ( !items.containsKey( newChildKey ) ) {
            throw new IllegalStateException( "Attempt to add link to node that is not in the graph" );
        }

        if ( items.containsKey( key ) ) {
            items.get( key ).addChild( newChildKey );
            items.get( newChildKey ).addParent( key );
        }

    }

    /**
     * Add a child to a particualar node identified by key.
     * 
     * @param key Object
     * @param newChildKey Object
     * @param newChild Object
     */
    public void addChildTo( K key, K newChildKey, V newChild ) {
        if ( !items.containsKey( newChild ) ) {
            this.addNode( newChildKey, newChild );
        }

        this.addChildTo( key, newChildKey );
        this.addParentTo( newChildKey, key );
    }

    /**
     * @param key Object
     * @param item Object
     */
    @Override
    public void addNode( K key, V item ) {
        if ( !items.containsKey( key ) ) {
            items.put( key, new DirectedGraphNode<K, V>( key, item, this ) );
        }
    }

    @Override
    public void addNode( DirectedGraphNode<K, V> node ) {
        node.setGraph( this );
        items.put( node.getKey(), node );
    }

    /**
     * @param key Object
     * @param newParentKey Object
     * @throws IllegalStateException
     */
    public void addParentTo( K key, K newParentKey ) throws IllegalStateException {
        if ( !items.containsKey( newParentKey ) ) {
            throw new IllegalStateException( "Attempt to add link to node that is not in the graph" );
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
        if ( !items.containsKey( newParent ) ) {
            this.addNode( newParentKey, newParent );
        }

        this.addChildTo( key, newParentKey );
        this.addParentTo( newParentKey, key );
    }

    /**
     * @param user_defined
     * @param classID
     */
    public void deleteChildFrom( @SuppressWarnings("unused") K parent, K childKey ) {
        items.remove( childKey ); // minor memory leak danger.
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
     * Remove vertices to nodes that aren't in the graph.
     */
    public void prune() {
        for ( K element : this.items.keySet() ) {
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
                int inDegree = degrees.get( w ).intValue();
                inDegree--;
                degrees.put( w, new Integer( inDegree ) );

                /* see if this now is one of the zero-indegree nodes */
                if ( inDegree == 0 ) {
                    q.offer( w );
                }
            }
        }

        if ( counter != items.size() ) {
            throw new IllegalStateException( "Graph contains a cycle; " + counter + " items found, " + items.size()
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
            return this.treeView( DefaultMutableTreeNode.class );
        }
        return treeView;
    }

    /**
     * Generate a JTree corresponding to this graph.
     * 
     * @param nodeClass The class to be used for TreeNodes. Defaults to DefaultMutableTreeNode.
     * @return javax.swing.JTree
     */
    @SuppressWarnings("unchecked")
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
            log.error( e, e );
        }
        return treeView;
    }

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
                    log.error( e, e );
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

    /*
     * (non-Javadoc)
     * @see ubic.basecode.dataStructure.graph.AbstractGraph#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey( K key ) {
        return getItems().containsKey( key );
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.dataStructure.graph.AbstractGraph#getItems()
     */
    @Override
    public Map<K, DirectedGraphNode<K, V>> getItems() {
        return this.items;
    }

}