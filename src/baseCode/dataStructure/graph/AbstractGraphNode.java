package baseCode.dataStructure.graph;

import java.util.Set;

public abstract class AbstractGraphNode {

   private Object value;

   public Object getValue() {
      return value;
   }

   public void setValue( Object j ) {
      value = j;
   }

   public AbstractGraphNode() {
   }

   public AbstractGraphNode(Object v) {
      this.value = v;
   }

   public String toString() {
      return value.toString();
   }

}
