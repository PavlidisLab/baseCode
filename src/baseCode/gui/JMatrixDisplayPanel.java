package baseCode.Gui;

import java.awt.*;
import javax.swing.JPanel;

public class JMatrixDisplayPanel extends JPanel {
  FlowLayout flowLayout1 = new FlowLayout();

  protected JMatrixDisplay m_matrixDisplay;

  public JMatrixDisplayPanel() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }

    m_matrixDisplay = new JMatrixDisplay();
    add( m_matrixDisplay );

    setPreferredSize( m_matrixDisplay.getPreferredSize() );
  }

  void jbInit() throws Exception {
    this.setLayout(flowLayout1);
  }
  
  public JMatrixDisplay getMatrixDisplay() {
      
      return m_matrixDisplay;
  }
}
