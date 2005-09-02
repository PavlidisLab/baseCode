package baseCode.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import baseCode.util.StatusViewer;

/**
 * Simple "wizard" implementation. To use, call the "addStep" method with a new WizardStep as an argument. Actions must
 * be defined for the "back", "cancel", "finish" and "next" buttons.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @author Homin Lee
 * @version $Id$
 */
public abstract class Wizard extends JDialog {
    protected JPanel mainPanel;
    protected JPanel BottomPanel = new JPanel();
    protected JPanel BottomPanelWrap = new JPanel();
    protected JLabel jLabelStatus = new JLabel();
    protected JPanel jPanelStatus = new JPanel();

    protected JButton nextButton = new JButton();
    protected JButton backButton = new JButton();
    protected JButton cancelButton = new JButton();
    protected JButton finishButton = new JButton();

    Vector steps = new Vector();
    protected JFrame callingframe;
    private StatusViewer statusMessenger;

    public Wizard( JFrame callingframe, int width, int height ) {
        this.callingframe = callingframe;
        setModal( true );
        jbInit( width, height );
    }

    // Component initialization
    private void jbInit( int width, int height ) {
        setResizable( true );
        mainPanel = ( JPanel ) this.getContentPane();
        mainPanel.setPreferredSize( new Dimension( width, height ) );
        mainPanel.setLayout( new BorderLayout() );

        // holds the buttons and the status bar.
        BottomPanelWrap.setLayout( new BorderLayout() );

        // bottom buttons/////////////////////////////////////////////////////////
        BottomPanel.setPreferredSize( new Dimension( width, 40 ) );
        nextButton.setText( "Next >" );
        nextButton.addActionListener( new Wizard_nextButton_actionAdapter( this ) );
        nextButton.setMnemonic( 'n' );
        backButton.setText( "< Back" );
        backButton.addActionListener( new Wizard_backButton_actionAdapter( this ) );
        backButton.setEnabled( false );
        backButton.setMnemonic( 'b' );
        cancelButton.setText( "Cancel" );
        cancelButton.addActionListener( new Wizard_cancelButton_actionAdapter( this ) );
        cancelButton.setMnemonic( 'c' );
        finishButton.setText( "Finish" );
        finishButton.setMnemonic( 'f' );
        finishButton.addActionListener( new Wizard_finishButton_actionAdapter( this ) );
        BottomPanel.add( cancelButton, null );
        BottomPanel.add( backButton, null );
        BottomPanel.add( nextButton, null );
        BottomPanel.add( finishButton, null );

        // status bar
        jPanelStatus.setBorder( BorderFactory.createEtchedBorder() );
        jPanelStatus.setLayout( new BorderLayout() );
        jLabelStatus.setFont( new java.awt.Font( "Dialog", 0, 11 ) );
        jLabelStatus.setPreferredSize( new Dimension( width - 40, 19 ) );
        jLabelStatus.setHorizontalAlignment( SwingConstants.LEFT );
        jPanelStatus.add( jLabelStatus, BorderLayout.WEST );
        statusMessenger = new StatusJlabel( jLabelStatus );

        BottomPanelWrap.add( BottomPanel, BorderLayout.NORTH );
        BottomPanelWrap.add( jPanelStatus, BorderLayout.SOUTH );

        mainPanel.add( BottomPanelWrap, BorderLayout.SOUTH );

    }

    /**
     * Print a message to the status bar.
     * 
     * @param a
     */
    public void showStatus( String a ) {
        statusMessenger.showStatus( a );
    }

    /**
     * Print an error message to the status bar.
     * 
     * @param a
     */
    public void showError( String a ) {
        statusMessenger.showError( a );
    }

    /**
     * Make the status bar empty.
     */
    public void clearStatus() {
        statusMessenger.showStatus( "" );
    }

    protected void addStep( WizardStep panel, boolean first ) {
        this.addStep( panel );
        if ( first ) mainPanel.add( ( JPanel ) steps.get( 0 ), BorderLayout.CENTER );
    }

    protected void addStep( WizardStep panel ) {
        steps.add( panel );
    }

    public void showWizard() {
        Dimension dlgSize = getPreferredSize();
        Dimension frmSize = callingframe.getSize();
        Point loc = callingframe.getLocation();
        setLocation( ( frmSize.width - dlgSize.width ) / 2 + loc.x, ( frmSize.height - dlgSize.height ) / 2 + loc.y );
        pack();
        nextButton.requestFocusInWindow();
        this.setVisible( true );
    }

    /**
     * Define what happens when the 'next' button is pressed
     * 
     * @param e
     */
    protected abstract void nextButton_actionPerformed( ActionEvent e );

    /**
     * Define what happens when the 'back' button is pressed
     * 
     * @param e
     */
    protected abstract void backButton_actionPerformed( ActionEvent e );

    /**
     * Define what happens when the 'cancel' button is pressed.
     * 
     * @param e
     */
    protected abstract void cancelButton_actionPerformed( ActionEvent e );

    /**
     * Define what happens when the 'finish' button is pressed.
     * 
     * @param e
     */
    protected abstract void finishButton_actionPerformed( ActionEvent e );

    /**
     * Disable the "finish" button, indicating the user has some steps to do yet.
     */
    public void setFinishDisabled() {
        this.finishButton.setEnabled( false );
    }

    /**
     * Enable the "finish" button, indicating the user can get out of the wizard at this stage.
     */
    public void setFinishEnabled() {
        this.finishButton.setEnabled( true );
    }
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

class Wizard_cancelButton_actionAdapter implements java.awt.event.ActionListener {
    Wizard adaptee;

    Wizard_cancelButton_actionAdapter( Wizard adaptee ) {
        this.adaptee = adaptee;
    }

    public void actionPerformed( ActionEvent e ) {
        adaptee.cancelButton_actionPerformed( e );
    }
}

class Wizard_finishButton_actionAdapter implements java.awt.event.ActionListener {
    Wizard adaptee;

    Wizard_finishButton_actionAdapter( Wizard adaptee ) {
        this.adaptee = adaptee;
    }

    public void actionPerformed( ActionEvent e ) {
        adaptee.finishButton_actionPerformed( e );
    }
}
