package baseCode.gui;

import java.io.*;
import javax.swing.*;

/**
 * Little oft-used functions.
 *
 * @version $Id$
 */

public class GuiUtil {

   public static boolean testfile( String filename ) {
      if ( filename != null && filename.length() > 0 ) {
         File f = new File( filename );
         if ( f.exists() ) {
            return true;
         } else {
            error( "File " + filename + " doesn't exist.  " );
         }
         return false;
      } else {
         error( "A required file field is blank." );
         return false;
      }
   }

   public static void error( Exception e, String message ) {
      JOptionPane.showMessageDialog( null,
                                     "Error: " + message + "\n" + e.toString() +
                                     "\n" + e.getStackTrace() );
   }

   public static void error( String message ) {
      JOptionPane.showMessageDialog( null, "Error: " + message + "\n" );
   }
}
