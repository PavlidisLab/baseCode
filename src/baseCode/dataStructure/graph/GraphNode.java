package baseCode.dataStructure.graph;

public interface GraphNode {
   public Object getItem();

   public Object getKey();

   public void setItem( Object value );

   public void setValue( Object key, Object value );

   public Graph getGraph();

   public void mark();

   public void unMark();

   public boolean isVisited();

    public void setGraph(Graph graph);
}
