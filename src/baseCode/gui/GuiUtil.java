package baseCode.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import baseCode.util.FileTools;

/**
 * Little oft-used functions.
 * 
 * @version $Id$
 */

public class GuiUtil {

    protected static final Log log = LogFactory.getLog( GuiUtil.class );

    /**
     * @param message
     * @param e
     */
    public static void error( String message, Throwable e ) {
        JOptionPane.showMessageDialog( null, "Error: " + message + "\n" + e, "Error", JOptionPane.ERROR_MESSAGE );
        log.error( e );
        e.printStackTrace();
    }

    /**
     * @param message
     */
    public static void error( String message ) {
        JOptionPane.showMessageDialog( null, "Error: " + message + "\n", "Error", JOptionPane.ERROR_MESSAGE );
        log.error( message );
    }

    /**
     * @param filename
     * @return
     * @see baseCode.util.FileTools#checkPathIsReadableFile(String)
     */
    public static boolean testFile( String filename ) {
        if ( !FileTools.testFile( filename ) ) {
            error( "A required file field is not valid." );
            return false;
        }
        return false;
    }

    /**
     * @param text
     * @return
     */
    public static boolean testDir( String filename ) {
        if ( !FileTools.testDir( filename ) ) {
            error( "A required directory field is not valid." );
            return false;
        }
        return true;
    }

    /**
     * Center a frame on the screen.
     * 
     * @param frame
     */
    public static void centerContainer( Container frame ) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if ( frameSize.height > screenSize.height ) {
            frameSize.height = screenSize.height;
        }
        if ( frameSize.width > screenSize.width ) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation( ( screenSize.width - frameSize.width ) / 2, ( screenSize.height - frameSize.height ) / 2 );
    }

    /**
     * @return
     */
    public static Point chooseChildLocation( Container child, Container parent ) {
        Dimension childSize = child.getPreferredSize();
        Dimension parentSize = parent.getSize();
        Point loc = parent.getLocation();
        Point childLoc = new Point( ( parentSize.width - childSize.width ) / 2 + loc.x,
                ( parentSize.height - childSize.height ) / 2 + loc.y );
        return childLoc;
    }

}