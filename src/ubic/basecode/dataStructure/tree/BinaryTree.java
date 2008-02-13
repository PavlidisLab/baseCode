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
package ubic.basecode.dataStructure.tree;

/**
 * @author Paul Pavlidis
 * @version $Id$
 */
public class BinaryTree<T> {

    BinaryTreeNode<T> root;

    public BinaryTree( T root ) {
        this.root = new BinaryTreeNode<T>( root );
    }

    /**
     * @return Returns the root.
     */
    public BinaryTreeNode<T> getRoot() {
        return root;
    }

    public BinaryTreeNode<T> insertLeft( BinaryTreeNode<T> p, T o ) {
        if ( p != null && p.getLeft() == null ) {
            BinaryTreeNode<T> newNode = new BinaryTreeNode<T>( o );
            p.setLeft( newNode );
            return newNode;
        }
        return null;
    }

    public BinaryTreeNode<T> insertRight( BinaryTreeNode<T> p, T o ) {
        if ( p != null && p.getRight() == null ) {
            BinaryTreeNode<T> newNode = new BinaryTreeNode<T>( o );
            p.setRight( newNode );
            return newNode;
        }
        return null;
    }

    public boolean isEmpty() {
        return getRoot() == null;

    }

}