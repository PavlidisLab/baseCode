package baseCode.dataStructure.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A graph node that has the concept of parents and children.
 * Keys can be anything, but probably Strings or Integers.
 *
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DirectedGraphNode
    extends AbstractGraphNode {

   private Set parents;
   // immediate parents, references to other GraphNodes by keys.
   private Set children;
   // immediate children, references to other GraphNodes by keys.

   public DirectedGraphNode( Object key, Object value, Graph graph ) {
      super( key, value, graph );
      parents = new LinkedHashSet();
      children = new LinkedHashSet();
   }

   /**
    *
    * @param newChildKey Object
    */
   public void addChild( Object newChildKey ) {
      children.add( newChildKey );
   }

   /**
    *
    * @param newParentKey Object
    */
   public void addParent( Object newParentKey ) {
      parents.add( newParentKey );
   }

   /**
    *
    * @return Object
    */
   public Object getParentKeys() {
      return parents;
   }

   /**
    *
    * @return Object
    */
   public Object getChildKeys() {
      return children;
   }

   /**
    * Get the immediate children of this node.
    * References to the DirectedGraphNodes are given, as opposed to key values.
    * @return Set containing the child nodes of this node.
    */
   public Set getChildNodes() {
      Set f = new LinkedHashSet();
      for ( Iterator i = this.getChildIterator(); i.hasNext(); ) {
         Object k = ( Object ) i.next();
         f.add( getGraph().get( k ) );
      }
      return f;
   }

   /**
    * Get the immediate parents of this node.
    * References to the DirectedGraphNodes are given, as opposed to key values.
    * @return Set
    */
   public Set getParentNodes() {
      Set f = new LinkedHashSet();
      for ( Iterator i = this.getParentIterator(); i.hasNext(); ) {
         Object k = ( Object ) i.next();
         f.add( getGraph().get( k ) );
      }
      return f;
   }

   /**
    * Get the subtree of the graph starting from this node.
    * @return Graph
    * @todo the nodes in the new graph could have references to nodes that are not in the subtree; other methods must ignore.
    */
   public Graph getChildGraph() {
      return new DirectedGraph( this.getAllChildNodes() );
   }

   /**
    *
    * @return int number of immediate children this node has.
    */
   public int numDirectChildren() {
      return children.size();
   }

   /**
    *
    * @return int how many children this node has, determined recursively.
    */
   public int numChildren() {
      return getAllChildNodes( null ).size();
   }

   /**
    * Get all the children of this node, recursively.
    * @param list Set
    * @return Set
    */
   public Set getAllChildNodes() {
      return this.getAllChildNodes( null );
   }

   /**
    * Get all the parents of this node, recursively.
    * @param list Set
    * @return Set
    */

   public Set getAllParentNodes() {
      return this.getAllParentNodes( null );
   }

   /**
    * Check to see if this node has a particular immediate child.
    * @param j Object
    * @return boolean
    */
   public boolean hasChild( Object j ) {
      return children.contains( j );
   }

   /**
    * Check to see if this node has a particular immediate parent.
    * @param j Object
    * @return boolean
    */
   public boolean hasParent( Object j ) {
      return parents.contains( j );
   }

   public String toString() {
      return this.getItem().toString();
   }

   public int compareTo( Object o ) {
      if ( ( ( DirectedGraphNode ) o ).numChildren() > this.numChildren() ) {
         return -1;
      } else if ( ( ( DirectedGraphNode ) o ).numChildren() < this.numChildren() ) {
         return 1;
      }
      return 0;
   }

   /* private methods */

   private Set getAllChildNodes( Set list ) {
      if ( list == null ) {
         list = new LinkedHashSet();
      }

      for ( Iterator it = this.getChildIterator(); it.hasNext(); ) {
         Object j = it.next();
         list.add( ( DirectedGraphNode ) getGraph().get( j ) );
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
         list.add( ( DirectedGraphNode ) getGraph().get( j ) );
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

}
