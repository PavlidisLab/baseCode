package baseCode.gui;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;

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
   
   Wizard owner;
   
   public WizardStep( Wizard wiz ) {
      super();
      owner = wiz;
      try {
         BorderLayout layout = new BorderLayout();
         this.setLayout( layout );
         jbInit();
      } catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   //Component initialization
   protected abstract void jbInit() throws Exception;

   abstract public boolean isReady();

   protected void addMain( JPanel panel ) {
      this.add( panel, BorderLayout.CENTER );
   }

   /** @todo why the spaces for layout? */
   protected void addHelp( String text ) {
      JLabel label = new JLabel( text );
      JLabel jLabel1 = new JLabel( "      " );
      JLabel jLabel2 = new JLabel( " " );
      JLabel jLabel3 = new JLabel( " " );
      JLabel jLabel4 = new JLabel( "      " );
      BorderLayout borderLayout1 = new BorderLayout();
      JPanel labelPanel = new JPanel();
      labelPanel.setBackground(Color.WHITE);
      labelPanel.setLayout( borderLayout1 );
      labelPanel.add( label, BorderLayout.CENTER );
      labelPanel.add( jLabel1, BorderLayout.WEST );
      labelPanel.add( jLabel2, BorderLayout.NORTH );
      labelPanel.add( jLabel3, BorderLayout.SOUTH );
      labelPanel.add( jLabel4, BorderLayout.EAST );
      this.add( labelPanel, BorderLayout.NORTH );
   }
   
   
   /**
    * Print a message to the status bar.
    * @param a
    */
    public void showStatus( String a ) {
       owner.showStatus( a );
    }
    
    /**
     * Print a message to the status bar.
     * @param a
     */
     public void showError( String a ) {
        owner.showError( a );
     }
   

}