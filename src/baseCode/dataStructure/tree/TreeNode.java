package baseCode.dataStructure.tree;

import baseCode.common.Visitable;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public class TreeNode extends Visitable {

   private Object element;
   private TreeNode firstChild;
   private TreeNode nextSibling;
   
   public Object getElement() {
      return element;
   }
   public void setElement( Object element ) {
      this.element = element;
   }
   public TreeNode getFirstChild() {
      return firstChild;
   }
   public void setFirstChild( TreeNode firstChild ) {
      this.firstChild = firstChild;
   }
   public TreeNode getNextSibling() {
      return nextSibling;
   }
   public void setNextSibling( TreeNode nextSibling ) {
      this.nextSibling = nextSibling;
   }
   /**
    * 
    */
   public TreeNode(Object k) {
      element = k;
   }
   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo( Object o ) {
      return 0;
   }
   
   

}
