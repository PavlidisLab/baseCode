package baseCode.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestFrame
    extends JFrame {
   JPanel contentPane;
   BorderLayout borderLayout1 = new BorderLayout();
   JMatrixDisplay m_matrixDisplay;

   //Construct the frame
   public TestFrame() {
      enableEvents( AWTEvent.WINDOW_EVENT_MASK );
      try {
         jbInit();
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   //Component initialization
   private void jbInit() throws Exception {
      contentPane = ( JPanel )this.getContentPane();
      contentPane.setLayout( borderLayout1 );
      this.setSize( new Dimension( 600, 550 ) );
      this.setTitle( "Eisen Plot" );
   }

   //Overridden so we can exit when window is closed
   protected void processWindowEvent( WindowEvent e ) {
      super.processWindowEvent( e );
      if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
         System.exit( 0 );
      }
   }
}
