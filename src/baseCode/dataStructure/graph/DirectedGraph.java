package baseCode.dataStructure.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.LinkedHashMap;

/**
 * A graph that contains DirectedGraphNodes. It can be cyclic. Small unconnected parts of the graph will
 * be ignored for many operation.
 * Tree traversals start from the root node, which is defined as the node with the most children.
 *
 * <p>Copyright (c) Columbia University
 * @todo do something about cyclicity
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DirectedGraph
    extends AbstractGraph {

   public DirectedGraph() {
      super();
   }

   public DirectedGraph( Set nodes ) {
      items = new LinkedHashMap();
      for (Iterator it = nodes.iterator(); it.hasNext();) {
         DirectedGraphNode n = (DirectedGraphNode)it.next();
         this.addNode(n.getKey(), n.getItem());
      }
   }

   protected void addNode( Object key, Object item, Graph graph ) {
      if ( !items.containsKey( key ) ) {
         items.put( key, new DirectedGraphNode( key, item, this ) );
      }
   }

   /**
    * Add a child to a particualar node identified by key.
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
    * Add a child to a particular node identified by key; if the node is not in
    * the graph, an exception is thrown.
    *
    * @param key Object
    * @param newChildKey Object
    * @throws IllegalStateException if the graph doesn't contain the child node.
    */
   public void addChildTo( Object key, Object newChildKey ) throws IllegalStateException {
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
    *
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
    *
    * @param key Object
    * @param newParentKey Object
    * @throws IllegalStateException
    */
   public void addParentTo( Object key, Object newParentKey ) throws IllegalStateException {
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
    * @return String
    */
   public String toString() {

      List nodes = new ArrayList( items.values() );
      Collections.sort( nodes );
      Collections.reverse( nodes );
      Iterator it = nodes.iterator();
      DirectedGraphNode root = ( DirectedGraphNode ) it.next();
      StringBuffer buf = new StringBuffer();
      int tablevel = 0;
      //    for (Iterator it = nodes.iterator(); it.hasNext();) {
      makeString( root, buf, tablevel );
      //     }
      return ( buf.toString() );
   }

   /* Helper for toString. Together with toString,
    * demonstrates how to iterate over the tree */
   private String makeString(
       DirectedGraphNode startNode,
       StringBuffer buf,
       int tabLevel ) {

      if ( buf == null ) {
         buf = new StringBuffer();
      }

      Set children = startNode.getChildNodes();

      if ( !startNode.isVisited() ) {
         buf.append( startNode );
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
    * Generate a JTree corresponding to this graph.
    *
    * @todo note that nodes are only allowed to have one parent in
    *   DefaultMutableTreeNodes
    * @return javax.swing.JTree
    */
   public JTree treeView() {
      List nodes = new ArrayList( items.values() );
      Collections.sort( nodes );
      Collections.reverse( nodes );
      Iterator it = nodes.iterator();
      DirectedGraphNode root = ( DirectedGraphNode ) it.next();
      DefaultMutableTreeNode top = new DefaultMutableTreeNode( root );
      root.mark();
      addJTreeNode( top, root );
      JTree tree = new JTree( top );
      return tree;
   }

   private void addJTreeNode(
       DefaultMutableTreeNode startJTreeNode,
       DirectedGraphNode startNode ) {
      Set children = startNode.getChildNodes();

      for ( Iterator it = children.iterator(); it.hasNext(); ) {
         DirectedGraphNode nextNode = ( DirectedGraphNode ) it.next();
         if ( !nextNode.isVisited() ) {
            DefaultMutableTreeNode newJTreeNode =
                new DefaultMutableTreeNode( nextNode );
            startJTreeNode.add( newJTreeNode );
            nextNode.mark();
            this.addJTreeNode( newJTreeNode, nextNode );
         }
      }

   }

}
