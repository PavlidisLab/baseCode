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
public class BinaryTree {

    BinaryTreeNode root;

    public BinaryTree( BinaryTreeNode root ) {
        this.root = root;
    }

    /**
     * @return Returns the root.
     */
    public BinaryTreeNode getRoot() {
        return root;
    }

    /**
     * @param root The root to set.
     */
    public void setRoot( BinaryTreeNode root ) {
        this.root = root;
    }

    /**
     * 
     */
    public BinaryTree() {
        super();
    }

    public void insertLeft( BinaryTreeNode p, Object o ) {
        if ( ( p != null ) && ( p.getLeft() == null ) ) p.setLeft( new BinaryTreeNode( o ) );
    }

    public void insertRight( BinaryTreeNode p, Object o ) {
        if ( ( p != null ) && ( p.getRight() == null ) ) p.setRight( new BinaryTreeNode( o ) );
    }

    public BinaryTreeNode getLeft() {
        if ( !isEmpty() ) return root.getLeft();
        return null;
    }

    public BinaryTreeNode getRight() {
        if ( !isEmpty() ) return root.getRight();
        return null;
    }

    public boolean isEmpty() {
        return getRoot() == null;

    }

}