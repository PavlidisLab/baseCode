package baseCode.Gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author not attributable
 * @version 1.0
 */

public class TestFrame extends JFrame {
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JMatrixDisplayPanel m_matrixDisplayPanel = new JMatrixDisplayPanel();

  //Construct the frame
  public TestFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    contentPane.add( m_matrixDisplayPanel, BorderLayout.CENTER );
  }

  //Component initialization
  private void jbInit() throws Exception  {
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(600, 550));
    this.setTitle("Eisen Plot");
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }
  
  public JMatrixDisplay getMatrixDisplay() {
      
      return m_matrixDisplayPanel.getMatrixDisplay();
  }
}
