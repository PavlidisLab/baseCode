package baseCode.Gui;

import javax.swing.UIManager;
import java.awt.*;


public class TestApp {
  boolean packFrame = false;

  //Construct the application
  public TestApp( String filename ) {

    TestFrame frame = new TestFrame();
    ColorMatrix matrix = new ColorMatrix( filename, true );
    //matrix.standardize();
    JMatrixDisplay matrixDisplay = new JMatrixDisplay( matrix );
    //matrixDisplay.getMatrix().standardize();
    frame.getContentPane().add( matrixDisplay, BorderLayout.CENTER );


    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

  //Main method: args[0] can contain the name of the data file
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    if (args.length > 0)
       new TestApp( args[0] );
    else
       System.err.println( "Please specify the name of the data file by passing it as a program argument." );
  }
}
