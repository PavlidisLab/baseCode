package baseCode.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Homin Lee
 * @version $Id$
 */

public abstract class AppDialog
    extends JDialog {
   JPanel mainPanel;
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel contentPanel= new JPanel();
   JPanel bottomPanel = new JPanel();
   protected JButton actionButton = new JButton();
   protected JButton cancelButton = new JButton();
   protected JFrame callingframe;

   public AppDialog( JFrame callingframe, int width, int height ) {
      this.callingframe = callingframe;
      setModal( true );
      jbInit( width, height );
   }

   private void jbInit( int width, int height ) {
      setResizable( true );
      mainPanel = ( JPanel )this.getContentPane();
      mainPanel.setPreferredSize( new Dimension( width, height ) );
      mainPanel.setLayout( borderLayout1 );

      contentPanel.setPreferredSize( new Dimension( width, height-40 ) );
      BorderLayout borderLayout4 = new BorderLayout();
      contentPanel.setLayout(borderLayout4);

      bottomPanel.setPreferredSize( new Dimension( width, 40 ) );
      cancelButton.setText("Cancel" );
      cancelButton.setMnemonic( 'c' );
      cancelButton.addActionListener( new
                                    AppDialog_cancelButton_actionAdapter( this ) );
      actionButton.addActionListener( new
                                      AppDialog_actionButton_actionAdapter( this ) );
      bottomPanel.add( cancelButton, null );
      bottomPanel.add( actionButton, null );
      mainPanel.add( contentPanel, BorderLayout.CENTER );
      mainPanel.add( bottomPanel, BorderLayout.SOUTH );
   }

   public void showDialog() {
      Dimension dlgSize = getPreferredSize();
      Dimension frmSize = callingframe.getSize();
      Point loc = callingframe.getLocation();
      setLocation( ( frmSize.width - dlgSize.width ) / 2 + loc.x,
            ( frmSize.height - dlgSize.height ) / 2 + loc.y );
      pack();
      actionButton.requestFocusInWindow();
      show();
   }

   protected void addHelp( String text ) {
      JLabel label = new JLabel( text );
      JLabel jLabel1 = new JLabel( "      " );
      JLabel jLabel2 = new JLabel( " " );
      JLabel jLabel3 = new JLabel( " " );
      JLabel jLabel4 = new JLabel( "      " );
      BorderLayout borderLayout2 = new BorderLayout();
      JPanel labelPanel = new JPanel();
      labelPanel.setBackground(Color.WHITE);
      labelPanel.setLayout( borderLayout2 );
      labelPanel.add( label, BorderLayout.CENTER );
      labelPanel.add( jLabel1, BorderLayout.WEST );
      labelPanel.add( jLabel2, BorderLayout.NORTH );
      labelPanel.add( jLabel3, BorderLayout.SOUTH );
      labelPanel.add( jLabel4, BorderLayout.EAST );
      contentPanel.add( labelPanel, BorderLayout.NORTH );
   }

   protected void addMain( JPanel panel ) {
      contentPanel.add( panel, BorderLayout.CENTER );
   }

   protected void setActionButtonText(String val )
   {
      actionButton.setText(val);
   }

   protected void setCancelButtonText(String val )
   {
      cancelButton.setText(val);
   }

   protected abstract void cancelButton_actionPerformed( ActionEvent e );

   protected abstract void actionButton_actionPerformed( ActionEvent e );

}


class AppDialog_cancelButton_actionAdapter
    implements java.awt.event.
    ActionListener {
   AppDialog adaptee;

   AppDialog_cancelButton_actionAdapter( AppDialog adaptee ) {
      this.adaptee = adaptee;
   }

   public void actionPerformed( ActionEvent e ) {
      adaptee.cancelButton_actionPerformed( e );
   }
}

class AppDialog_actionButton_actionAdapter
    implements java.awt.event.
    ActionListener {
   AppDialog adaptee;

   AppDialog_actionButton_actionAdapter( AppDialog adaptee ) {
      this.adaptee = adaptee;
   }

   public void actionPerformed( ActionEvent e ) {
      adaptee.actionButton_actionPerformed( e );
   }
}
