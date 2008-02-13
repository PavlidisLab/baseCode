/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.dataStructure.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestDirectedGraph extends TestCase {
    DirectedGraph<String, String> testGraph;
    DirectedGraph<String, String> testGraphCycle; // has a cycle.

    /**
     * Constructor for TestDirectedGraph.
     * 
     * @param arg0
     */
    public TestDirectedGraph( String arg0 ) {
        super( arg0 );
        testGraph = new DirectedGraph<String, String>();

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

        testGraphCycle = new DirectedGraph<String, String>();

        testGraphCycle.addNode( "b", "bee." );
        testGraphCycle.addNode( "a", "aaa." );
        testGraphCycle.addNode( "c", "cee." );
        testGraphCycle.addNode( "d", "dee." );
        testGraphCycle.addNode( "f", "eff." );
        testGraphCycle.addNode( "e", "eee." );

        testGraphCycle.addParentTo( "b", "a" );
        testGraphCycle.addParentTo( "c", "a" );
        testGraphCycle.addChildTo( "a", "c" ); // redundant
        testGraphCycle.addChildTo( "a", "b" ); // redundant

        testGraphCycle.addChildTo( "c", "d" ); // top down
        testGraphCycle.addChildTo( "c", "e" ); // top down
        testGraphCycle.addParentTo( "f", "c" ); // bottom up
        testGraphCycle.addParentTo( "f", "e" ); // cycle
    }

    public void testGetChildren() {
        DirectedGraphNode n = testGraph.get( "c" );
        String actualReturn = n.getChildGraph().toString();
        String expectedReturn = "cee.\n\tdee.\n\teee.\n\teff.\n";
        assertEquals( "return", expectedReturn, actualReturn );
    }

    public void testTopoSort() {
        testGraph.topoSort();
        List<DirectedGraphNode<String, String>> nodes = new ArrayList<DirectedGraphNode<String, String>>( testGraph
                .getItems().values() );
        Collections.sort( nodes );
        StringBuffer buf = new StringBuffer();
        for ( Iterator it = nodes.iterator(); it.hasNext(); ) {
            buf.append( it.next().toString() );
        }
        String actualReturn = buf.toString();
        String expectedReturn = "aaa.bee.cee.dee.eee.eff.";

        assertEquals( "return", expectedReturn, actualReturn );
    }

    public void testToString() {
        String expectedReturn = "aaa.\n\tbee.\n\tcee.\n\t\tdee.\n\t\teee.\n\t\teff.\n";
        String actualReturn = testGraph.toString();
        assertEquals( "return", expectedReturn, actualReturn );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}