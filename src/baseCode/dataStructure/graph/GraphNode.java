package baseCode.dataStructure.graph;

public interface GraphNode   {

   /**
    * @return the contents of the node.
    */
   public Object getItem();

   /**
    * @return the key for this node.
    */
   public Object getKey();

   /**
    * Set the contents of the node.
    * 
    * @param value
    */
   public void setItem( Object value );

   /**
    * Set the key and value associated with this node.
    * 
    * @param key
    * @param value
    */
   public void setValue( Object key, Object value );

   /**
    * @return the Graph this belongs to.
    */
   public Graph getGraph();

   /**
    * @param graph
    */
   public void setGraph( Graph graph );
}
