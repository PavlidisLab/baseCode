package baseCode.gui;


import java.io.File;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Little oft-used functions.
 *
 * @version $Id$
 */

public class GuiUtil {

   protected static final Log log = LogFactory
   .getLog( GuiUtil.class );
   
   /**
    * 
    * @param message
    * @param e
    */
   public static void error( String message, Exception e ) {
      JOptionPane.showMessageDialog( null, "Error: " + message + "\n"
            + e,"Error", JOptionPane.ERROR_MESSAGE );
      log.error(e);
      e.printStackTrace();
   }

   /**
    * 
    * @param message
    */
   public static void error( String message ) {
      JOptionPane.showMessageDialog( null, "Error: " + message + "\n","Error", JOptionPane.ERROR_MESSAGE );
      log.error(message);
   }

   /**
    * 
    * @param filename
    * @return
    * @see baseCode.util.FileTools#checkPathIsReadableFile(String)
    */
   public static boolean testFile( String filename ) {
      if ( filename != null && filename.length() > 0 ) {
         File f = new File( filename );
         if ( f.exists() ) {
            return true;
         }
         error( "File " + filename + " doesn't exist.  " );
         return false;

      }
      error( "A required file field is blank." );
      return false;

   }

}
