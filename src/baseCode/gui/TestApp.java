package baseCode.gui;

import javax.swing.UIManager;
import java.awt.*;


/**
 * This is an example of how you'd display a microarray.
 *
 * @version $Id$
 */
public class TestApp {
  boolean packFrame = false;

  //Construct the application
  public TestApp( String dataFilename, String outPngFilename ) {

    TestFrame frame = new TestFrame();

    //
    // Here is an example of how you'd display a matrix of doubles
    // visually with colors
    //
    ColorMatrix matrix = null;
    try {
       matrix = new ColorMatrix( dataFilename );
    }
    catch (java.io.IOException e) {
       System.err.println("Unable to open file " + dataFilename);
       return;
    }
    
    JMatrixDisplay matrixDisplay = new JMatrixDisplay( matrix );
    matrixDisplay.getMatrix().standardize();
    matrixDisplay.setLabelsVisible( true );
    
    try {
       boolean showLabels = true;
       matrixDisplay.saveToFile( outPngFilename, showLabels );
    }
    catch (java.io.IOException e) {
       System.err.println("Unable to save screenshot to file " + outPngFilename);
       return;
    }

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
    if (args.length > 1)
       new TestApp( args[0], args[1] );
    else
       System.err.println( "Please specify dataFilename and outPngFilename by passing them as program arguments" );
  }
}
