package baseCode.gui;

import java.awt.EventQueue;

import javax.swing.JLabel;

import baseCode.util.StatusViewer;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class StatusJlabel implements StatusViewer {
   private JLabel jlabel;

   public StatusJlabel(JLabel l) {
      this.jlabel = l;
   }

   public void setStatus(String s) {
      final String message = s;
      System.err.println(s);
//      try {
//         Thread.sleep(100);
//      } catch (InterruptedException ex) {
//      }

      EventQueue.invokeLater(new Runnable() {
         public void run() {
            if (jlabel != null) {
               jlabel.setText(message);
            }
         }
      });
   }
}
