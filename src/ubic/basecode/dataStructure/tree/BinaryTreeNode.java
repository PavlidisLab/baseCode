package ubic.basecode.dataStructure.tree;

import ubic.basecode.dataStructure.Visitable;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class BinaryTreeNode extends Visitable {

   private BinaryTreeNode left;
   private BinaryTreeNode right;
   private Object contents;

   /**
    * @param left
    * @param right
    * @param contents
    */
   public BinaryTreeNode( Object contents ) {
      super();
      this.contents = contents;
   }
   
   public BinaryTreeNode() {
      super();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo( Object o ) {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * 
    * @return
    */
   public Object getContents() {
      return contents;
   }
   
   /**
    * 
    * @param contents
    */
   public void setContents( Object contents ) {
      this.contents = contents;
   }
   
   /**
    * 
    * @return
    */
   public BinaryTreeNode getLeft() {
      return left;
   }
   
   /**
    * 
    * @param left
    */
   public void setLeft( BinaryTreeNode left ) {
      this.left = left;
   }
   
   /**
    * 
    * @return
    */
   public BinaryTreeNode getRight() {
      return right;
   }
   
   /**
    * 
    * @param right
    */
   public void setRight( BinaryTreeNode right ) {
      this.right = right;
   }
   
   /**
    * 
    * @return
    */
   public boolean isLeaf() {
      return left == null && right == null;
   }
}