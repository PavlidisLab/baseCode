/*
 * Created on Jun 20, 2004
 *
 */
package baseCodeTest.dataStructure.graph;

import baseCode.dataStructure.graph.DirectedGraph;
import junit.framework.TestCase;

/**
 * 
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestDirectedGraph extends TestCase {

   /**
    * Constructor for TestDirectedGraph.
    * @param arg0
    */
   public TestDirectedGraph(String arg0) {
      super(arg0);
   }

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testDirectedGraph() {
      DirectedGraph f = new DirectedGraph();
      f.addNode("b", "bee.");
      f.addNode("a", "aaa.");
      f.addNode("c", "cee.");
      f.addNode("d", "dee.");
      f.addNode("f", "eff.");
      f.addNode("e", "eee.");
      f.addParentTo("b", "a");
      f.addParentTo("c", "a");
      f.addChildTo("a", "c"); // redundant
      f.addChildTo("a", "b"); // redundant

      f.addChildTo("c", "d"); // top down
      f.addChildTo("c", "e"); // top down
      f.addParentTo("f", "c"); // bottom up

      System.err.println(f);

   }

}
