/*
 * Created on Jun 20, 2004
 *
 */
package baseCodeTest.dataStructure.graph;

import baseCode.dataStructure.graph.DirectedGraph;
import junit.framework.TestCase;
import baseCode.dataStructure.graph.DirectedGraphNode;

/**
 *
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestDirectedGraph
    extends TestCase {
   DirectedGraph testGraph;
   /**
    * Constructor for TestDirectedGraph.
    * @param arg0
    */
   public TestDirectedGraph( String arg0 ) {
      super( arg0 );
      testGraph = new DirectedGraph();

      testGraph.addNode( "b", "bee." );
      testGraph.addNode( "a", "aaa." );
      testGraph.addNode( "c", "cee." );
      testGraph.addNode( "d", "dee." );
      testGraph.addNode( "f", "eff." );
      testGraph.addNode( "e", "eee." );

      testGraph.addParentTo( "b", "a" );
      testGraph.addParentTo( "c", "a" );
      testGraph.addChildTo( "a", "c" ); // redundant
      testGraph.addChildTo( "a", "b" ); // redundant

      testGraph.addChildTo( "c", "d" ); // top down
      testGraph.addChildTo( "c", "e" ); // top down
      testGraph.addParentTo( "f", "c" ); // bottom up

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

   public void testGetChildren() {
      DirectedGraphNode n = (DirectedGraphNode)testGraph.get("c");

      String actualReturn = n.getChildGraph().toString();
      System.err.print( actualReturn );
      String expectedReturn = "cee.\n\tdee.\n\teee.\n\teff.";
      assertEquals( "return", expectedReturn, actualReturn );
   }

}
