/*
 * Created on Jun 20, 2004
 *
 */
package baseCode.dataStructure.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public abstract class AbstractGraph {
   protected Map items;

   public AbstractGraph() {
      items = new HashMap();
   }

   /**
    * Add a new node to the graph identifed by key, with contents item.
    * @param key An object (typically a String or Integer, but can be any kind of object)
    * @param item An object that the node contains.
    */
   protected abstract void addNode(
      Object key,
      Object item,
      AbstractGraph graph);

   /**
    * Retrieve a node by key. To get the contents of a node use getNodeContents(key)
    * @param key
    * @see getNodeContents
    * @return AbstractGraphNode referenced by the key.
    */
   public AbstractGraphNode get(Object key) {
      return (AbstractGraphNode)items.get(key);
   }

   /**
    * Retrieve the contents of a node by key. 
    * @see get
    * @param key
    * @return The object contained by a node, not the node itself.
    */
   public Object getNodeContents(Object key) {
      return ((AbstractGraphNode)items.get(key)).getItem();
   }

   /**
    * 
    * @param key
    * @return true if the graph contains an item referenced by key, false otherwise.
    */
   public boolean containsKey(Object key) {
      return items.containsKey(key);
   }

   /**
    * Reset the 'visited' marks of the graph to false.
    *
    */
   public void unmarkAll() {
      for (Iterator it = items.keySet().iterator(); it.hasNext();) {
         ((AbstractGraphNode)it.next()).unMark();
      }
   }
}
