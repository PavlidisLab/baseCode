package baseCode.gui;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
 * @author not attributable
 * @version $Id$
 */

public abstract class WizardStep extends JPanel {
   public WizardStep( Wizard wiz ) {
      super();
      try {
         jbInit();
      } catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   //Component initialization
   protected abstract void jbInit() throws Exception;

   abstract public boolean isReady();

   protected boolean testfile( String filename ) {
      if ( filename != null && filename.length() > 0 ) {
         File f = new File( filename );
         if ( f.exists() ) {
            return true;
         } else {
            JOptionPane.showMessageDialog( null, "File " + filename
                  + " doesn't exist.  " );
         }
         return false;
      } else {
         JOptionPane
               .showMessageDialog( null, "A required file field is blank." );
         return false;
      }
   }

}