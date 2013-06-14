/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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
package ubic.basecode.dataStructure.tree;

import junit.framework.TestCase;

/**
 * @author Paul
 * @version $Id$
 */
public class BinaryTreeTest extends TestCase {

    BinaryTreeNode<String> testNode;

    BinaryTree<String> tree;

    private BinaryTreeNode<String> testEmptyNode;

    /**
     * Test method for {@link ubic.basecode.dataStructure.tree.BinaryTree#getLeft()}.
     */
    public void testGetLeft() {
        assertEquals( "foob", testNode.getLeft().getContents() );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.tree.BinaryTree#getRight()}.
     */
    public void testGetRight() {
        BinaryTreeNode<String> right = testNode.getRight();
        assertEquals( "foo", right.getContents() );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.tree.BinaryTree#getRoot()}.
     */
    public void testGetRoot() {
        assertEquals( "root", tree.getRoot().getContents() );
    }

    /**
     * Test method for
     * {@link ubic.basecode.dataStructure.tree.BinaryTree#insertLeft(ubic.basecode.dataStructure.tree.BinaryTreeNode, java.lang.Object)}.
     */
    public void testInsertLeft() {
        BinaryTreeNode<String> foo = testEmptyNode.insertLeft( "groob" );
        assertEquals( "groob", foo.getContents() );
        assertNull( foo.getLeft() );
    }

    /**
     * Test method for
     * {@link ubic.basecode.dataStructure.tree.BinaryTree#insertRight(ubic.basecode.dataStructure.tree.BinaryTreeNode, java.lang.Object)}.
     */
    public void testInsertRight() {
        BinaryTreeNode<String> foo = testEmptyNode.insertRight( "groob" );
        assertEquals( "groob", foo.getContents() );
        assertNull( foo.getLeft() );
    }

    /**
     * Test method for {@link ubic.basecode.dataStructure.tree.BinaryTree#isEmpty()}.
     */
    public void testIsEmpty() {
        assertFalse( tree.isEmpty() );
    }

    public void testIsLeaf() {
        assertTrue( testEmptyNode.isLeaf() );
        assertFalse( testNode.isLeaf() );
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tree = new BinaryTree<String>( "root" );
        BinaryTreeNode<String> r = tree.getRoot();
        BinaryTreeNode<String> l = tree.insertLeft( r, "left" );
        testNode = tree.insertRight( r, "moreright" );
        tree.insertLeft( l, "moreleft" );
        testNode.insertRight( "foo" );
        testEmptyNode = testNode.insertLeft( "foob" );

    }
}
