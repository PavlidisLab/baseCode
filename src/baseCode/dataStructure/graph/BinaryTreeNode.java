package baseCode.dataStructure.graph;

import java.util.Set;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class BinaryTreeNode extends DirectedGraphNode {

   /**
    * @param key
    * @param value
    * @param graph
    */
   public BinaryTreeNode( Object key, Object value, Graph graph ) {
      super( key, value, graph );
      // TODO Auto-generated constructor stub
   }

   /**
    * get parent key ( a copy ) 
    * @return Object the parent of this node.
    */
   public Object getParent() {
      if ( parents == null ) {
         return null;
      }
      return parents.toArray()[0];
   }

   /**
    * @param newChildKey Object
    */
   public void addChild( Object newChildKey ) {
      if ( children.size() > 1 ) {
         throw new IllegalStateException( "Violation of binary status" );
      }
      children.add( newChildKey );
   }

}
