/*
 * Created on Jun 21, 2004
 *
 */
package baseCodeTest.gui;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.UIManager;

import org.xml.sax.SAXException;
import baseCode.gui.TreePanel;
import baseCode.xml.GOParser;

/**
 * Not a 'real' test.
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TreePanelApp {
   private GOParser gOParser = null;
   private TreePanel tree = null;

   /**
    * Constructor for TestTreePanel.
    * @param arg0
    */
   public TreePanelApp() throws IOException, SAXException, ParserConfigurationException,
       IOException {

      InputStream i =
    //           GOParser.class.getResourceAsStream("/data/go-termdb-sample.xml");
          GOParser.class.getResourceAsStream("/data/go_200406-termdb.xml");
      gOParser = new GOParser(i);
      final JTree t = gOParser.getGraph().treeView();

      //Create and set up the window.
      JFrame frame = new JFrame("GOTreeDemo");
      frame.setSize(200, 200);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      //Create and set up the content pane.
      TreePanel newContentPane = new TreePanel(t);
      newContentPane.setOpaque(true); //content panes must be opaque
      frame.setContentPane(newContentPane);
      //Display the window.
      frame.pack();
      frame.setVisible(true);

   }

   public static void main(String[] args) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         new TreePanelApp();
      }
      catch (Exception e) {
         e.printStackTrace();
      }

   }

}
