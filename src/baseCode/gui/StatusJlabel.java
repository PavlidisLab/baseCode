package baseCode.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import baseCode.util.StatusViewer;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */

public class StatusJlabel implements StatusViewer {
   private JLabel jlabel;

   Icon errorIcon = null;

   public StatusJlabel( JLabel l ) {
      errorIcon = new ImageIcon( StatusJlabel.class
            .getResource( "resources/alert.gif" ) );
      this.jlabel = l;
   }

   public void setStatus( String s ) {
      final String message = s;
      System.err.println( s );
      jlabel.setText( message );
      jlabel.setIcon( null );
      jlabel.repaint();
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.util.StatusViewer#setError(java.lang.String)
    */
   public void setError( String s ) {
      final String message = s;
      System.err.println( s );
      jlabel.setText( message );
      jlabel.setIcon( errorIcon );
      jlabel.repaint();
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.util.StatusViewer#clear()
    */
   public void clear() {
      jlabel.setText( "" );
      jlabel.setIcon( null );
      jlabel.repaint();
   }
}