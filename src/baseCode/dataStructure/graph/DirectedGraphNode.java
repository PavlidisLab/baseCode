package baseCode.dataStructure.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/**
 * A graph node that has the concept of parents and children. 
 * Keys can be anything, but probably Strings or Integers.
 * 
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DirectedGraphNode extends AbstractGraphNode {

   private Set parents;
   // immediate parents, references to other GraphNodes by keys.
   private Set children;
   // immediate children, references to other GraphNodes by keys.

   public DirectedGraphNode(Object key, Object value, AbstractGraph graph) {
      super(key, value, graph);
      parents = new HashSet();
      children = new HashSet();
   }

   public void addChild(Object newChildKey) {
      children.add(newChildKey);
   }

   public void addParent(Object newParentKey) {
      parents.add(newParentKey);
   }

   public Object getParentKeys() {
      return parents;
   }

   public Object getChildKeys() {
      return children;
   }

   /**
    * Get the immediate children of this node. References to the DirectedGraphNodes are given, as opposed to key values.
    * @return Set containing the child nodes of this node.  
    */
   public Set getChildNodes() {
      Set f = new HashSet();
      for (Iterator i = children.iterator(); i.hasNext();) {
         Object k = (Object)i.next();
         f.add(getGraph().get(k));
      }
      return f;
   }

   public Object getKey() {
      return key;
   }

   public int numDirectChildren() {
      return children.size();
   }

   public int numChildren() {
      return getAllChildren(null).size();
   }

   public Set getAllChildren(Set list) {
      if (list == null) {
         list = new HashSet();
      }
      Object j = null;
      for (Iterator it = this.getChildIterator(); it.hasNext();) {
         j = it.next();
         list.add(j);
         ((DirectedGraphNode)getGraph().get(j)).getAllChildren(list);
      }
      return list;
   }

   private Iterator getChildIterator() {
      return children.iterator();
   }

   private Iterator getParentIterator() {
      return parents.iterator();
   }

   //   public boolean hasChild(Object j) {
   //      Set k = this.getAllChildren(null);
   //      return k.contains(j);
   //   }

   public String toString() {
      return this.getItem().toString();
   }
   
   public int compareTo(Object o) {
      if (((DirectedGraphNode)o).numChildren() > this.numChildren()) {
         return -1;
      } else if (((DirectedGraphNode)o).numChildren() < this.numChildren()) {
         return 1;
      }
      return 0;
   }

}
