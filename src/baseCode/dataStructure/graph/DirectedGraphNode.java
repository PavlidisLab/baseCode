package baseCode.dataStructure.graph;

import java.util.Set;

public class DirectedGraphNode extends AbstractGraphNode {

   private Set parents; // immediate parents, references to other GraphNodes.
   private Set children; // immediate children, references to other GraphNodes.

   public DirectedGraphNode() {
      super();
   }

   public DirectedGraphNode(Object j) {
      super(j);
   }

   public Object getParents() {
      return parents;
   }

   public Object getChildren() {
      return children;
   }

}
