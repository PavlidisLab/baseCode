/*
 * Created on Jun 21, 2004
 *
 */
package baseCodeTest.gui;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.xml.parsers.ParserConfigurationException;

import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.JFCTestHelper;
import junit.extensions.jfcunit.TestHelper;

import org.xml.sax.SAXException;

import baseCode.gui.TreePanel;
import baseCode.xml.GOParser;

/**
 * 
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TestTreePanel extends JFCTestCase {
   private GOParser gOParser = null;
   private TreePanel tree = null;

   /**
    * Constructor for TestTreePanel.
    * @param arg0
    */
   public TestTreePanel(String arg0) {
      super(arg0);
   }

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();

      setHelper(new JFCTestHelper()); // Uses the AWT Event Queue.

      InputStream i =
         GOParser.class.getResourceAsStream("/data/go-termdb-sample.xml");

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

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      gOParser = null;
      tree = null;
  //    TestHelper.cleanUp(this); // using this seems to cause problems.
      super.tearDown();
   }

   public void testTreeGUI()
      throws IOException, SAXException, ParserConfigurationException {

      assertEquals("return", true, true);
   }

}
