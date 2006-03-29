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
public class BinaryTreeNode extends Visitable {

    private BinaryTreeNode left;
    private BinaryTreeNode right;
    private Object contents;

    /**
     * @param left
     * @param right
     * @param contents
     */
    public BinaryTreeNode( Object contents ) {
        super();
        this.contents = contents;
    }

    public BinaryTreeNode() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( Object o ) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @return
     */
    public Object getContents() {
        return contents;
    }

    /**
     * @param contents
     */
    public void setContents( Object contents ) {
        this.contents = contents;
    }

    /**
     * @return
     */
    public BinaryTreeNode getLeft() {
        return left;
    }

    /**
     * @param left
     */
    public void setLeft( BinaryTreeNode left ) {
        this.left = left;
    }

    /**
     * @return
     */
    public BinaryTreeNode getRight() {
        return right;
    }

    /**
     * @param right
     */
    public void setRight( BinaryTreeNode right ) {
        this.right = right;
    }

    /**
     * @return
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }
}