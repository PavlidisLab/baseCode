package baseCode.dataStructure.graph;

import java.util.HashSet;
import java.util.Set;
/**
 * 
 * 
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class UndirectedGraphNode extends AbstractGraphNode {

   private Set neighbors;

   public UndirectedGraphNode(Object key, Object value, AbstractGraph graph) {
      super(key, value, graph);
      neighbors = new HashSet();
   }

   public UndirectedGraphNode(Object key) {
      super(key);
      neighbors = new HashSet();
   }

   public int numNeighbors() {
      return neighbors.size();
   }

   public int compareTo(Object o) {
      if (((UndirectedGraphNode)o).numNeighbors() > this.numNeighbors()) {
         return -1;
      } else if (
         ((UndirectedGraphNode)o).numNeighbors() < this.numNeighbors()) {
         return 1;
      }
      return 0;
   }

}
