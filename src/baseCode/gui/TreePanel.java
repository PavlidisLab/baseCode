/*
 * Created on Jun 20, 2004
 *
 */
package baseCode.gui;

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

import baseCode.dataStructure.graph.DirectedGraphNode;

/**
 * 
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 * @todo - this really doesn't do much.
 */
public class TreePanel extends JPanel implements TreeSelectionListener {
   //Optionally set the look and feel.
   private static boolean useSystemLookAndFeel = false;

   private JTree tree;
   private JTextArea detailPane;

   public TreePanel(JTree t) {
      this.tree = t;
      tree.getSelectionModel().setSelectionMode(
         TreeSelectionModel.SINGLE_TREE_SELECTION);

      tree.addTreeSelectionListener(this);

      JScrollPane treeView = new JScrollPane(tree);
      // Create the viewing pane that shows the contents of the node.
      detailPane = new JTextArea();
      detailPane.setEditable(false);
      detailPane.setLineWrap(true);
      detailPane.setWrapStyleWord(true);

      JScrollPane detailView = new JScrollPane(detailPane);

      //Add the scroll panes to a split pane.
      JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      splitPane.setTopComponent(treeView);
      splitPane.setBottomComponent(detailPane);
      Dimension minimumSize = new Dimension(200, 250);
      detailView.setMinimumSize(minimumSize);
      treeView.setMinimumSize(minimumSize);
      splitPane.setDividerLocation(100);
      treeView.setPreferredSize(new Dimension(500, 200));

      splitPane.setPreferredSize(new Dimension(500, 500));
      add(splitPane);
   }

   public void valueChanged(TreeSelectionEvent e) {
      DefaultMutableTreeNode node =
         (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

      if (node == null)
         return;

      DirectedGraphNode nodeInfo = (DirectedGraphNode)node.getUserObject();
      //    if (node.isLeaf()) {
      //    System.out.println(nodeInfo); // debugging

      detailPane.setText(nodeInfo.toString());

      //     }

      //   System.out.println(nodeInfo); // debugging

   }

}
