/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import ubic.basecode.util.BrowserLauncher;

/**
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
    protected JButton helpButton = new JButton();

    protected Container callingframe;

    public AppDialog() {

    }

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
        cancelButton.addActionListener( new AppDialog_cancelButton_actionAdapter( this ) );
        actionButton.addActionListener( new AppDialog_actionButton_actionAdapter( this ) );

        helpButton.addActionListener( new AppDialog_helpButton_actionAdapter( this ) );
        helpButton.setText( "Help" );

        bottomPanel.add( helpButton, null );
        bottomPanel.add( cancelButton, null );
        bottomPanel.add( actionButton, null );
        mainPanel.add( contentPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );
    }

    public void showDialog() {
        this.setResizable( true );
        Point center = GuiUtil.chooseChildLocation( this, callingframe );
        setLocation( center );
        pack();
        actionButton.requestFocusInWindow();
        this.setVisible( true );
    }

    // helper to respond to links.
    class LinkFollower implements HyperlinkListener {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
         */
        public void hyperlinkUpdate( HyperlinkEvent e ) {
            if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                try {
                    BrowserLauncher.openURL( e.getURL().toExternalForm() );
                } catch ( IOException e1 ) {
                    GuiUtil.error( "Could not open link" );
                }
            }
        }
    }

    // Slightly specialized editor pane.
    class HelpEditorPane extends JEditorPane {
        HelpEditorPane( String text ) {
            super();
            this.setEditable( false );
            this.setFont( new Font( "SansSerif", Font.PLAIN, 11 ) );
            this.setContentType( "text/html" );
            this.setText( text );
            this.addHyperlinkListener( new LinkFollower() );
        }
    }

    protected void addHelp( String text ) {

        HelpEditorPane helpArea = null;

        helpArea = new HelpEditorPane( text );
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

        helpArea.addMouseListener( new AppDialog_mouselistener_actionAdapter( this ) );

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

    protected void setHelpButtonText( String val ) {
        helpButton.setText( val );
    }

    protected abstract void cancelButton_actionPerformed( ActionEvent e );

    protected abstract void actionButton_actionPerformed( ActionEvent e );

    protected abstract void helpButton_actionPerformed( ActionEvent e );

    /**
     * @param e
     */
    public void mouseButton_actionPerformed( MouseEvent e ) {
        // TODO Auto-generated method stub
    }

}

class AppDialog_helpButton_actionAdapter implements java.awt.event.ActionListener {
    AppDialog adaptee;

    AppDialog_helpButton_actionAdapter( AppDialog adaptee ) {
        this.adaptee = adaptee;
    }

    public void actionPerformed( ActionEvent e ) {
        adaptee.helpButton_actionPerformed( e );
    }
}

class AppDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
    AppDialog adaptee;

    AppDialog_cancelButton_actionAdapter( AppDialog adaptee ) {
        this.adaptee = adaptee;
    }

    public void actionPerformed( ActionEvent e ) {
        adaptee.cancelButton_actionPerformed( e );
    }
}

class AppDialog_actionButton_actionAdapter implements java.awt.event.ActionListener {
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