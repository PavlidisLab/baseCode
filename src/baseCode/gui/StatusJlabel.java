package baseCode.gui;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

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

public class StatusJlabel extends JLabel implements StatusViewer {
   private JLabel jlabel;

   Image alertIcon = null;

   public StatusJlabel( JLabel l ) {
      InputStream in = getClass().getResourceAsStream( "alert.gif" );

      byte[] buffer = null;
      try {
         buffer = new byte[in.available()];
         in.read( buffer );
      } catch ( IOException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      alertIcon = Toolkit.getDefaultToolkit().createImage( buffer );
      this.jlabel = l;
   }

   public void setStatus( String s ) {
      final String message = s;
      System.err.println( s );
      try {
         Thread.sleep( 20 );
      } catch ( InterruptedException ex ) {//      }

         EventQueue.invokeLater( new Runnable() {
            public void run() {
               if ( jlabel != null ) {
                  jlabel.setText( message );
               }
            }
         } );
      }
   }
}