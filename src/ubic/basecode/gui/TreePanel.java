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
package ubic.basecode.gui;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import ubic.basecode.dataStructure.graph.DirectedGraphNode;

/**
 * @author Paul Pavlidis
 * @version $Id$
 * @todo - this really doesn't do much.
 */
public class TreePanel extends JPanel implements TreeSelectionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 5618153031969647463L;
    private JTree tree;
    private JTextArea detailPane;

    public TreePanel( JTree t ) {
        this.tree = t;
        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );

        tree.addTreeSelectionListener( this );

        JScrollPane treeView = new JScrollPane( tree );
        // Create the viewing pane that shows the contents of the node.
        detailPane = new JTextArea();
        detailPane.setEditable( false );
        detailPane.setLineWrap( true );
        detailPane.setWrapStyleWord( true );

        JScrollPane detailView = new JScrollPane( detailPane );

        // Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        splitPane.setTopComponent( treeView );
        splitPane.setBottomComponent( detailPane );
        Dimension minimumSize = new Dimension( 200, 250 );
        detailView.setMinimumSize( minimumSize );
        treeView.setMinimumSize( minimumSize );
        splitPane.setDividerLocation( 100 );
        treeView.setPreferredSize( new Dimension( 500, 200 ) );

        splitPane.setPreferredSize( new Dimension( 500, 500 ) );
        add( splitPane );
    }

    public void valueChanged( TreeSelectionEvent e ) {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) tree.getLastSelectedPathComponent();

        if ( node == null ) return;

        DirectedGraphNode nodeInfo = ( DirectedGraphNode ) node.getUserObject();
        detailPane.setText( nodeInfo.toString() );
    }

}