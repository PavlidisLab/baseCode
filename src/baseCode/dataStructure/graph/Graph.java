package baseCode.dataStructure.graph;

public interface Graph {

   public void addNode( Object key, Object item );

   public GraphNode get( Object key );

   public Object getNodeContents( Object key );

   public boolean containsKey( Object key );

   public void unmarkAll();
}