package baseCode.dataStructure.graph;

import java.util.Set;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public class BinaryTree extends DirectedGraph {

   /**
    * 
    */
   public BinaryTree() {
      super();
      // TODO Auto-generated constructor stub
   }

   /**
    * @param nodes
    */
   public BinaryTree( Set nodes ) {
      super( nodes );
      // TODO Auto-generated constructor stub
   }
   
   
 // can't add chidren or parents to arbitary nodes.
   public void addChildTo( Object key, Object newChildKey, Object newChild ) {
      // TODO Auto-generated method stub
      super.addChildTo( key, newChildKey, newChild );
   }
   public void addChildTo( Object key, Object newChildKey )
         throws IllegalStateException {
      // TODO Auto-generated method stub
      super.addChildTo( key, newChildKey );
   }
   public void addNode( Object key, Object item ) {
      // TODO Auto-generated method stub
      super.addNode( key, item );
   }
   public void addParentTo( Object key, Object newParentKey, Object newParent ) {
      // TODO Auto-generated method stub
      super.addParentTo( key, newParentKey, newParent );
   }
   public void addParentTo( Object key, Object newParentKey )
         throws IllegalStateException {
      // TODO Auto-generated method stub
      super.addParentTo( key, newParentKey );
   }
}
