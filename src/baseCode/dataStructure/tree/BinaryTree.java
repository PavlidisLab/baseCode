package baseCode.dataStructure.tree;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class BinaryTree {

   BinaryTreeNode root;

   public BinaryTree( BinaryTreeNode root ) {
      this.root = root;
   }

   /**
    * @return Returns the root.
    */
   public BinaryTreeNode getRoot() {
      return root;
   }

   /**
    * @param root The root to set.
    */
   public void setRoot( BinaryTreeNode root ) {
      this.root = root;
   }

   /**
    * 
    */
   public BinaryTree() {
      super();
      // TODO Auto-generated constructor stub
   }

   public void insertLeft( BinaryTreeNode p, Object o ) {
      if ( ( p != null ) && ( p.getLeft() == null ) )
            p.setLeft( new BinaryTreeNode( o ) );
   }

   public void insertRight( BinaryTreeNode p, Object o ) {
      if ( ( p != null ) && ( p.getRight() == null ) )
            p.setRight( new BinaryTreeNode( o ) );
   }

   public BinaryTreeNode getLeft() {
      if ( !isEmpty() ) return root.getLeft();
      return null;
   }

   public BinaryTreeNode getRight() {
      if ( !isEmpty() ) return root.getRight();
      return null;
   }

   public boolean isEmpty() {
      return getRoot() == null;

   }

}