/*
 * Created on Jun 20, 2004
 *
 */
package baseCode.dataStructure.graph;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public abstract class AbstractGraph implements Graph {
   protected Map items;

   public AbstractGraph() {
      items = new LinkedHashMap();
   }

   /**
    * Create a new graph from a set of nodes. This allows us to easily make subtrees.
    * @param nodes Set of AbstractGraphNodes.
    */
   public AbstractGraph(Set nodes) {
      items = new LinkedHashMap();
      for (Iterator it = nodes.iterator(); it.hasNext();) {
         GraphNode n = (GraphNode)it.next();
         this.addNode(n.getKey(), n.getItem());
      }
   }


   /**
    *
    * @param node GraphNode
    */
   public void addNode(GraphNode node) {
      items.put(node.getKey(), node);
   }


   /**
    * Add a new node to the graph identifed by key, with contents item.
    * @param key Object
    * @param item Object
    */
   public void addNode(Object key, Object item) {
       addNode(key, item, this);
    }


   /**
    * Add a new node to the graph identifed by key, with contents item.
    *
    * @param key An object (typically a String or Integer, but can be any kind
    *   of object)
    * @param item An object that the node contains.
    * @param graph Graph
    */
   protected abstract void addNode(
      Object key,
      Object item,
      Graph graph);

   /**
    * Retrieve a node by key. To get the contents of a node use
    * getNodeContents(key)
    *
    * @param key Object
    * @see getNodeContents
    * @return AbstractGraphNode referenced by the key.
    */
   public GraphNode get(Object key) {
      return (GraphNode)items.get(key);
   }

   /**
    * Retrieve the contents of a node by key.
    *
    * @see get
    * @param key Object
    * @return The object contained by a node, not the node itself.
    */
   public Object getNodeContents(Object key) {
      return ((GraphNode)items.get(key)).getItem();
   }

   /**
    *
    * @param key Object
    * @return true if the graph contains an item referenced by key, false
    *   otherwise.
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
         ((GraphNode)it.next()).unMark();
      }
   }
}
