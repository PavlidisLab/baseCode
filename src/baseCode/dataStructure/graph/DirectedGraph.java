package baseCode.dataStructure.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import baseCode.dataStructure.Queue;

/**
 * A graph that contains DirectedGraphNodes. It can be cyclic. Small unconnected parts of the graph will be ignored for
 * many operation. Tree traversals start from the root node, which is defined as the node with the most children.
 * <p>
 * Copyright (c) Columbia University
 * 
 * @todo do something about cyclicity; make this a dag or a subclass...
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DirectedGraph extends AbstractGraph {

   public DirectedGraph() {
      super();
   }

   /**
    * @param nodes Set of DirectedGraphNodes
    */
   public DirectedGraph( Set nodes ) {
      items = new LinkedHashMap();
      for ( Iterator it = nodes.iterator(); it.hasNext(); ) {
         DirectedGraphNode a = ( DirectedGraphNode ) it.next();
         this.addNode( a );
      }
   }

   /**
    * @param key Object
    * @param item Object
    */
   public void addNode( Object key, Object item ) {
      if ( !items.containsKey( key ) ) {
         items.put( key, new DirectedGraphNode( key, item, this ) );
      }
   }

   /**
    * Add a child to a particualar node identified by key.
    * 
    * @param key Object
    * @param newChildKey Object
    * @param newChild Object
    */
   public void addChildTo( Object key, Object newChildKey, Object newChild ) {
      if ( !items.containsKey( newChild ) ) {
         this.addNode( newChildKey, newChild );
      }

      this.addChildTo( key, newChildKey );
      this.addParentTo( newChildKey, key );
   }

   /**
    * Add a child to a particular node identified by key; if the node is not in the graph, an exception is thrown.
    * 
    * @param key Object
    * @param newChildKey Object
    * @throws IllegalStateException if the graph doesn't contain the child node.
    */
   public void addChildTo( Object key, Object newChildKey )
         throws IllegalStateException {
      if ( !items.containsKey( newChildKey ) ) {
         throw new IllegalStateException(
               "Attempt to add link to node that is not in the graph" );
      }

      if ( items.containsKey( key ) ) {
         ( ( DirectedGraphNode ) items.get( key ) ).addChild( newChildKey );
         ( ( DirectedGraphNode ) items.get( newChildKey ) ).addParent( key );
      }

   }

   /**
    * @param key Object
    * @param newParentKey Object
    * @param newParent Object
    */
   public void addParentTo( Object key, Object newParentKey, Object newParent ) {
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
   public void addParentTo( Object key, Object newParentKey )
         throws IllegalStateException {
      if ( !items.containsKey( newParentKey ) ) {
         throw new IllegalStateException(
               "Attempt to add link to node that is not in the graph" );
      }

      if ( items.containsKey( key ) ) {
         ( ( DirectedGraphNode ) items.get( key ) ).addParent( newParentKey );
         ( ( DirectedGraphNode ) items.get( newParentKey ) ).addChild( key );
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
      List nodes = new ArrayList( items.values() );
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
   private String makeString( DirectedGraphNode startNode, StringBuffer buf,
         int tabLevel ) {

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
      Queue q = new Queue();
      int counter = 0;
      DirectedGraphNode v;
      Map degrees = new HashMap();

      /* Get the degrees of all items, and enqueue zero-indegree nodes */
      for ( Iterator it = this.items.keySet().iterator(); it.hasNext(); ) {
         v = ( DirectedGraphNode ) items.get( it.next() );
         degrees.put( v, new Integer( v.inDegree() ) );
         if ( ( ( Integer ) degrees.get( v ) ).intValue() == 0 ) {
            q.enqueue( v );
         }
      }

      while ( !q.isEmpty() ) {
         v = ( DirectedGraphNode ) q.dequeue();
         v.setTopoSortOrder( ++counter );
         for ( Iterator vit = v.getChildNodes().iterator(); vit.hasNext(); ) {
            DirectedGraphNode w = ( DirectedGraphNode ) vit.next();
            /* decrement the degree of this node */
            int inDegree = ( ( Integer ) degrees.get( w ) ).intValue();
            inDegree--;
            degrees.put( w, new Integer( inDegree ) );

            /* see if this now is one of the zero-indegree nodes */
            if ( inDegree == 0 ) {
               q.enqueue( w );
            }
         }
      }

      if ( counter != items.size() ) {
         throw new IllegalStateException( "Graph contains a cycle; " + counter
               + " items found, " + items.size() + " expected" );
      }

   }

   /**
    * Generate a JTree corresponding to this graph.
    * 
    * @todo note that nodes are only allowed to have one parent in DefaultMutableTreeNodes
    * @return javax.swing.JTree
    */
   public JTree treeView() {
      this.topoSort();
      List nodes = new ArrayList( items.values() );
      Collections.sort( nodes );
      DirectedGraphNode root = ( DirectedGraphNode ) nodes.get( 0 );
      DefaultMutableTreeNode top = new DefaultMutableTreeNode( root );
      root.mark();
      addJTreeNode( top, root );
      JTree tree = new JTree( top );
      return tree;
   }

   private void addJTreeNode( DefaultMutableTreeNode startJTreeNode,
         DirectedGraphNode startNode ) {
      Set children = startNode.getChildNodes();

      for ( Iterator it = children.iterator(); it.hasNext(); ) {
         DirectedGraphNode nextNode = ( DirectedGraphNode ) it.next();
         if ( !nextNode.isVisited() ) {
            DefaultMutableTreeNode newJTreeNode = new DefaultMutableTreeNode(
                  nextNode );
            startJTreeNode.add( newJTreeNode );
            nextNode.mark();
            this.addJTreeNode( newJTreeNode, nextNode );
         }
      }

   }

}