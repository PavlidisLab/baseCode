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

import ubic.basecode.dataStructure.Visitable;

/**
 * @author pavlidis
 * @version $Id$
 */
public class BinaryTreeNode<T> extends Visitable {

    private BinaryTreeNode<T> left = null;
    private BinaryTreeNode<T> right = null;
    private BinaryTreeNode<T> parent = null;
    private T contents;

    public BinaryTreeNode() {

    }

    public BinaryTreeNode( BinaryTreeNode<T> parent ) {
        this.parent = parent;
    }

    /**
     * @param left
     * @param right
     * @param contents
     */
    public BinaryTreeNode( T contents ) {
        super();
        this.contents = contents;
    }

    public BinaryTreeNode<T> insertLeft( T c ) {
        if ( this.getLeft() != null ) {
            throw new IllegalArgumentException( "Already has left" );
        }
        this.left = new BinaryTreeNode<T>( c );
        return left;
    }

    public BinaryTreeNode<T> insertRight( T c ) {
        if ( this.getRight() != null ) {
            throw new IllegalArgumentException( "Already has right" );
        }
        this.right = new BinaryTreeNode<T>( c );
        return right;
    }

    /**
     * @return
     */
    public Object getContents() {
        return contents;
    }

    /**
     * @return
     */
    public BinaryTreeNode<T> getLeft() {
        return left;
    }

    /**
     * @return
     */
    public BinaryTreeNode<T> getRight() {
        return right;
    }

    /**
     * @return
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * @param contents
     */
    public void setContents( T contents ) {
        this.contents = contents;
    }

    /**
     * @param left
     */
    public void setLeft( BinaryTreeNode<T> left ) {
        this.left = left;
    }

    /**
     * @param right
     */
    public void setRight( BinaryTreeNode<T> right ) {
        this.right = right;
    }
}