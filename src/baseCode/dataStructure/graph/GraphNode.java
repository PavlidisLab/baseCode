package baseCode.dataStructure.graph;

public interface GraphNode {
   
   /**
    * 
    * @return the contents of the node.
    */
   public Object getItem();

   /**
    * 
    * @return the key for this node.
    */
   public Object getKey();

   /**
    * Set the contents of the node.
    * @param value
    */
   public void setItem( Object value );

   /**
    * Set the key and value associated with this node.
    * @param key
    * @param value
    */
   public void setValue( Object key, Object value );

   /**
    * 
    * @return the Graph this belongs to.
    */
   public Graph getGraph();

   /**
    * Mark this node (i.e., to track whether it has been visited)
    *
    */
   public void mark();

   /**
    * Unmark this node.
    *
    */
   public void unMark();

   /**
    * 
    * @return true if the node is 'marked', false otherwise.
    */
   public boolean isVisited();

   /**
    * 
    * @param graph
    */
    public void setGraph(Graph graph);
}
