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
public class DirectedGraph<K, V> extends AbstractGraph<K, V> {
    private static Log log = LogFactory.getLog( DirectedGraph.class.getName() );
    protected DefaultTreeModel dtm;
    private JTree treeView;

    public DirectedGraph() {
        super();
    }

    /**
     * @param nodes Set of DirectedGraphNodes
     */
    public DirectedGraph( Set<DirectedGraphNode<K, V>> nodes ) {
        super();
        for ( Iterator<DirectedGraphNode<K, V>> it = nodes.iterator(); it.hasNext(); ) {
            DirectedGraphNode<K, V> a = it.next();
            this.addNode( a );
        }
    }

    /**
     * @param key Object
     * @param item Object
     */
    public void addNode( K key, V item ) {
        if ( !items.containsKey( key ) ) {
            items.put( key, new DirectedGraphNode<K, V>( key, item, this ) );
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
     * @param user_defined
     * @param classID
     */
    public void deleteChildFrom( K parent, K childKey ) {
        items.remove( childKey ); // minor memory leak danger.
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
            ( ( DirectedGraphNode<K, V> ) items.get( key ) ).addChild( newChildKey );
            ( ( DirectedGraphNode<K, V> ) items.get( newChildKey ) ).addParent( key );
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
     * @param key Object
     * @param newParentKey Object
     * @throws IllegalStateException
     */
    public void addParentTo( K key, K newParentKey ) throws IllegalStateException {
        if ( !items.containsKey( newParentKey ) ) {
            throw new IllegalStateException( "Attempt to add link to node that is not in the graph" );
        }

        if ( items.containsKey( key ) ) {
            ( ( DirectedGraphNode<K, V> ) items.get( key ) ).addParent( newParentKey );
            ( ( DirectedGraphNode<K, V> ) items.get( newParentKey ) ).addChild( key );
        }

    }

    /**
     * Shows the tree as a tabbed list. Items that have no parents are shown at the 'highest' level.
     * 
     * @return String
     */
    public String toString() {
        prune();
        this.topoSort();
        List<GraphNode<K, V>> nodes = new ArrayList<GraphNode<K, V>>( items.values() );
        Collections.sort( nodes );
        DirectedGraphNode root = ( DirectedGraphNode ) nodes.get( 0 );
        StringBuffer buf = new StringBuffer();
        int tablevel = 0;
        makeString( root, buf, tablevel );
        return ( buf.toString() );
    }

    /*
     * Helper for toString. Together with toString, demonstrates how to iterate over the tree
     */
    private String makeString( DirectedGraphNode startNode, StringBuffer buf, int tabLevel ) {

        if ( buf == null ) {
            buf = new StringBuffer();
        }

        Set children = startNode.getChildNodes();

        if ( !startNode.isVisited() ) {
            buf.append( startNode + "\n" );
            startNode.mark();
        }
        tabLevel++;
        for ( Iterator it = children.iterator(); it.hasNext(); ) {
            DirectedGraphNode f = ( DirectedGraphNode ) it.next();
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

    /**
     * Remove vertices to nodes that aren't in the graph.
     */
    public void prune() {
        for ( Iterator it = this.items.keySet().iterator(); it.hasNext(); ) {
            DirectedGraphNode v = ( DirectedGraphNode ) items.get( it.next() );
            v.prune();
        }
    }

    /**
     * Fills in the topoSortOrder for each node.
     */
    public void topoSort() {
        Queue<DirectedGraphNode> q = new LinkedList<DirectedGraphNode>();
        int counter = 0;

        Map<DirectedGraphNode, Integer> degrees = new HashMap<DirectedGraphNode, Integer>();

        /* Get the degrees of all items, and enqueue zero-indegree nodes */
        for ( Iterator it = this.items.keySet().iterator(); it.hasNext(); ) {
            DirectedGraphNode v = ( DirectedGraphNode ) items.get( it.next() );
            degrees.put( v, new Integer( v.inDegree() ) );
            if ( degrees.get( v ).intValue() == 0 ) {
                q.offer( v );
            }
        }

        while ( !q.isEmpty() ) {
            DirectedGraphNode v = q.remove();
            v.setTopoSortOrder( ++counter );
            for ( Iterator vit = v.getChildNodes().iterator(); vit.hasNext(); ) {
                DirectedGraphNode w = ( DirectedGraphNode ) vit.next();
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
     * Generate a JTree corresponding to this graph.
     * 
     * @param nodeClass The class to be used for TreeNodes. Defaults to DefaultMutableTreeNode.
     * @return javax.swing.JTree
     */
    public JTree treeView( Class nodeClass ) {
        log.debug( "Constructing tree view of graph" );
        DirectedGraphNode root = getRoot();
        Constructor constructor;
        DefaultMutableTreeNode top = null;
        treeView = null;
        try {
            constructor = nodeClass.getConstructor( new Class[] { root.getClass() } );
            top = ( DefaultMutableTreeNode ) constructor.newInstance( new Object[] { root } );
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

    public DefaultTreeModel getTreeModel() {
        if ( treeView == null ) this.treeView();
        return dtm;
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

    private void addJTreeNode( DefaultMutableTreeNode startJTreeNode, DirectedGraphNode startNode,
            Constructor constructor ) {
        if ( startJTreeNode == null ) return;
        Set children = startNode.getChildNodes();

        for ( Iterator it = children.iterator(); it.hasNext(); ) {
            DirectedGraphNode nextNode = ( DirectedGraphNode ) it.next();
            if ( !nextNode.isVisited() ) {
                DefaultMutableTreeNode newJTreeNode = null;
                try {
                    newJTreeNode = ( DefaultMutableTreeNode ) constructor.newInstance( new Object[] { nextNode } );
                } catch ( Exception e ) {
                    log.error( e, e );
                }
                startJTreeNode.add( newJTreeNode );
                nextNode.mark();
                this.addJTreeNode( newJTreeNode, nextNode, constructor );
            }
        }
    }

    /**
     * @return root of the tree.
     */
    public DirectedGraphNode getRoot() {
        this.topoSort();
        List nodes = new ArrayList( items.values() );
        Collections.sort( nodes );
        return ( DirectedGraphNode ) nodes.get( 0 );
    }

}