package baseCode.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;

import baseCode.util.BrowserLauncher;

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
 * @author Homin Lee
 * @version $Id$
 */

public abstract class AppDialog extends JDialog {
   /**
    * <hr>
    * <p>
    * Copyright (c) 2004 Columbia University
    * 
    * @author pavlidis
    * @version $Id$
    */

   JPanel mainPanel;
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel contentPanel = new JPanel();
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
      mainPanel = ( JPanel ) this.getContentPane();
      mainPanel.setPreferredSize( new Dimension( width, height ) );
      mainPanel.setLayout( borderLayout1 );

      contentPanel.setPreferredSize( new Dimension( width, height - 40 ) );
      BorderLayout borderLayout4 = new BorderLayout();
      contentPanel.setLayout( borderLayout4 );

      bottomPanel.setPreferredSize( new Dimension( width, 40 ) );
      cancelButton.setText( "Cancel" );
      cancelButton.setMnemonic( 'c' );
      cancelButton.addActionListener( new AppDialog_cancelButton_actionAdapter(
            this ) );
      actionButton.addActionListener( new AppDialog_actionButton_actionAdapter(
            this ) );
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

   class LinkFollower implements HyperlinkListener {

      /*
       * (non-Javadoc)
       * 
       * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
       */
      public void hyperlinkUpdate( HyperlinkEvent e ) {
         System.err.println( "wow" );
         if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
            try {
               System.err.println( "wow" );
               BrowserLauncher.openURL( e.getURL().toExternalForm() );
            } catch ( IOException e1 ) {
               GuiUtil.error( "Could not open link" );
            }
         }
      }
   }

   class HelpTextArea extends JEditorPane {

      HelpTextArea( String text ) {
         super();
         this.setEditable( false );
         this.setContentType( "text/html" );
         this.setText( text );
      }

   }

   // @todo why using spaces for layout?
   protected void addHelp( String text ) {

      HelpTextArea helpArea = null;

      helpArea = new HelpTextArea( text );
      helpArea.addHyperlinkListener( new LinkFollower() );
      JLabel jLabel1 = new JLabel( "      " );
      JLabel jLabel2 = new JLabel( " " );
      JLabel jLabel3 = new JLabel( " " );
      JLabel jLabel4 = new JLabel( "      " );
      BorderLayout borderLayout2 = new BorderLayout();
      JPanel labelPanel = new JPanel();
      labelPanel.setBackground( Color.WHITE );
      labelPanel.setLayout( borderLayout2 );
      labelPanel.add( helpArea, BorderLayout.CENTER );
      labelPanel.add( jLabel1, BorderLayout.WEST );
      labelPanel.add( jLabel2, BorderLayout.NORTH );
      labelPanel.add( jLabel3, BorderLayout.SOUTH );
      labelPanel.add( jLabel4, BorderLayout.EAST );
      contentPanel.add( labelPanel, BorderLayout.NORTH );

      helpArea
            .addMouseListener( new AppDialog_mouselistener_actionAdapter( this ) );

   }

   protected void addMain( JPanel panel ) {
      contentPanel.add( panel, BorderLayout.CENTER );
   }

   protected void setActionButtonText( String val ) {
      actionButton.setText( val );
   }

   protected void setCancelButtonText( String val ) {
      cancelButton.setText( val );
   }

   protected abstract void cancelButton_actionPerformed( ActionEvent e );

   protected abstract void actionButton_actionPerformed( ActionEvent e );

   /**
    * @param e
    */
   public void mouseButton_actionPerformed( MouseEvent e ) {
      // TODO Auto-generated method stub
   }

}

class AppDialog_cancelButton_actionAdapter implements
      java.awt.event.ActionListener {
   AppDialog adaptee;

   AppDialog_cancelButton_actionAdapter( AppDialog adaptee ) {
      this.adaptee = adaptee;
   }

   public void actionPerformed( ActionEvent e ) {
      adaptee.cancelButton_actionPerformed( e );
   }
}

class AppDialog_actionButton_actionAdapter implements
      java.awt.event.ActionListener {
   AppDialog adaptee;

   AppDialog_actionButton_actionAdapter( AppDialog adaptee ) {
      this.adaptee = adaptee;
   }

   public void actionPerformed( ActionEvent e ) {
      adaptee.actionButton_actionPerformed( e );
   }
}

class AppDialog_mouselistener_actionAdapter implements MouseListener {

   AppDialog adaptee;

   /**
    * @param adaptee
    */
   public AppDialog_mouselistener_actionAdapter( AppDialog adaptee ) {
      this.adaptee = adaptee;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
    */
   public void mouseClicked( MouseEvent e ) {
      adaptee.mouseButton_actionPerformed( e );
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
    */
   public void mouseEntered( MouseEvent e ) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
    */
   public void mouseExited( MouseEvent e ) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
    */
   public void mousePressed( MouseEvent e ) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
    */
   public void mouseReleased( MouseEvent e ) {
   }

}