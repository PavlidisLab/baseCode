package baseCode.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version $Id$
 */

public abstract class Wizard extends JDialog {
   JPanel mainPanel;
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel BottomPanel = new JPanel();
   protected JButton nextButton = new JButton();
   protected JButton backButton = new JButton();
   protected JButton cancelButton = new JButton();
   protected JButton finishButton = new JButton();
   protected int step;
   Vector steps = new Vector();
   Vector texts = new Vector();
   protected JFrame callingframe;

   public Wizard( JFrame callingframe, int width, int height ) {
      //enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      this.callingframe = callingframe;
      setModal( true );
      jbInit( width, height );
   }

   //Component initialization
   private void jbInit( int width, int height ) {
      setResizable( true );
      mainPanel = ( JPanel ) this.getContentPane();
      mainPanel.setPreferredSize( new Dimension( width, height ) );
      mainPanel.setLayout( borderLayout1 );

      //bottom buttons/////////////////////////////////////////////////////////
      BottomPanel.setPreferredSize( new Dimension( width, 40 ) );
      nextButton.setText( "Next >" );
      nextButton
            .addActionListener( new Wizard_nextButton_actionAdapter( this ) );
      nextButton.setMnemonic( 'n' );
      backButton.setText( "< Back" );
      backButton
            .addActionListener( new Wizard_backButton_actionAdapter( this ) );
      backButton.setEnabled( false );
      backButton.setMnemonic( 'b' );
      cancelButton.setText( "Cancel" );
      cancelButton.addActionListener( new Wizard_cancelButton_actionAdapter(
            this ) );
      cancelButton.setMnemonic( 'c' );
      finishButton.setText( "Finish" );
      finishButton.setMnemonic( 'f' );
      finishButton.addActionListener( new Wizard_finishButton_actionAdapter(
            this ) );
      BottomPanel.add( cancelButton, null );
      BottomPanel.add( backButton, null );
      BottomPanel.add( nextButton, null );
      BottomPanel.add( finishButton, null );
      mainPanel.add( BottomPanel, BorderLayout.SOUTH );
   }

   protected void addStep( int step, WizardStep panel ) {
      steps.add( step - 1, panel );
      if ( step == 1 )
         mainPanel.add( ( JPanel ) steps.get( 0 ), BorderLayout.CENTER );
   }

   protected void addStepText( int step, JPanel panel ) {
      texts.add( step - 1, panel );
      if ( step == 1 )
         mainPanel.add( ( JPanel ) texts.get( 0 ), BorderLayout.NORTH );
   }

   public void showWizard() {
      Dimension dlgSize = getPreferredSize();
      Dimension frmSize = callingframe.getSize();
      Point loc = callingframe.getLocation();
      setLocation( ( frmSize.width - dlgSize.width ) / 2 + loc.x,
            ( frmSize.height - dlgSize.height ) / 2 + loc.y );
      pack();
      nextButton.requestFocusInWindow();
      show();
   }

   protected abstract void nextButton_actionPerformed( ActionEvent e );

   protected abstract void backButton_actionPerformed( ActionEvent e );

   protected abstract void cancelButton_actionPerformed( ActionEvent e );

   protected abstract void finishButton_actionPerformed( ActionEvent e );
}

class Wizard_nextButton_actionAdapter implements java.awt.event.ActionListener {
   Wizard adaptee;

   Wizard_nextButton_actionAdapter( Wizard adaptee ) {
      this.adaptee = adaptee;
   }

   public void actionPerformed( ActionEvent e ) {
      adaptee.nextButton_actionPerformed( e );
   }
}

class Wizard_backButton_actionAdapter implements java.awt.event.ActionListener {
   Wizard adaptee;

   Wizard_backButton_actionAdapter( Wizard adaptee ) {
      this.adaptee = adaptee;
   }

   public void actionPerformed( ActionEvent e ) {
      adaptee.backButton_actionPerformed( e );
   }
}

class Wizard_cancelButton_actionAdapter implements
      java.awt.event.ActionListener {
   Wizard adaptee;

   Wizard_cancelButton_actionAdapter( Wizard adaptee ) {
      this.adaptee = adaptee;
   }

   public void actionPerformed( ActionEvent e ) {
      adaptee.cancelButton_actionPerformed( e );
   }
}

class Wizard_finishButton_actionAdapter implements
      java.awt.event.ActionListener {
   Wizard adaptee;

   Wizard_finishButton_actionAdapter( Wizard adaptee ) {
      this.adaptee = adaptee;
   }

   public void actionPerformed( ActionEvent e ) {
      adaptee.finishButton_actionPerformed( e );
   }
}