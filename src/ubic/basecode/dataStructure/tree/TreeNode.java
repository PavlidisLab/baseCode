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
public class TreeNode extends Visitable {

    private Object element;
    private TreeNode firstChild;
    private TreeNode nextSibling;

    public Object getElement() {
        return element;
    }

    public void setElement( Object element ) {
        this.element = element;
    }

    public TreeNode getFirstChild() {
        return firstChild;
    }

    public void setFirstChild( TreeNode firstChild ) {
        this.firstChild = firstChild;
    }

    public TreeNode getNextSibling() {
        return nextSibling;
    }

    public void setNextSibling( TreeNode nextSibling ) {
        this.nextSibling = nextSibling;
    }

    /**
     * 
     */
    public TreeNode( Object k ) {
        element = k;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( Object o ) {
        return 0;
    }

}
