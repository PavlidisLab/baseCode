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
  public TestApp( String inDataFilename, String outPngFilename ) {

    TestFrame frame = new TestFrame();

    //
    // Here is an example of how you'd display a matrix of doubles
    // visually with colors
    //
    JMatrixDisplay matrixDisplay = null;
    try {
       matrixDisplay = new JMatrixDisplay( inDataFilename );
    }
    catch (java.io.IOException e) {
       System.err.println("Unable to open file " + inDataFilename);
       return;
    }

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
    
    // toggle between standardized and not a few times
    boolean isShowingStandardized = matrixDisplay.getStandardizedEnabled();
    for (int i = 0;  i < 5;  i++)
    {
       try {
          Thread.sleep( 1000 );
          isShowingStandardized = ! isShowingStandardized;
          matrixDisplay.setStandardizedEnabled( isShowingStandardized );
          matrixDisplay.repaint();
       }
       catch( InterruptedException e ) {}
    }
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
