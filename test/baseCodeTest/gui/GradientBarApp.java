package baseCodeTest.gui;

import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JPanel;
import baseCode.gui.JGradientBar;
import baseCode.gui.ColorMap;


/**
 *
 * @author  Will Braynen
 * @version $Id$
 */
public class GradientBarApp {

   boolean packFrame = false;

   /** Creates a new instance of GradientBarApp */
   public GradientBarApp() {

      JFrame frame = new JFrame();
      frame.getContentPane().setLayout( new FlowLayout() );
      frame.setSize( new Dimension( 300, 300 ) );
      frame.setTitle( "JGradientBar Test" );

      Color[] colorMap = ColorMap.GREENRED_COLORMAP;
      JGradientBar gradientBar = new JGradientBar( "-2", "+2", colorMap );
      gradientBar.setLabels( "a", "b" );

      frame.getContentPane().add( gradientBar );

      //Validate frames that have preset sizes
      //Pack frames that have useful preferred size info, e.g. from their layout
      if ( packFrame ) {
         frame.pack();
      } else {
         frame.validate();
      }
      //Center the window
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      if ( frameSize.height > screenSize.height ) {
         frameSize.height = screenSize.height;
      }
      if ( frameSize.width > screenSize.width ) {
         frameSize.width = screenSize.width;
      }
      frame.setLocation( ( screenSize.width - frameSize.width ) / 2,
                         ( screenSize.height - frameSize.height ) / 2 );
      frame.setVisible( true );

   }

   //Main method: args[0] can contain the name of the data file
   public static void main( String[] args ) {
      try {
         UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
      new GradientBarApp();
   }
}
