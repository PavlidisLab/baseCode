package baseCode.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author Homin Lee
 * @author pavlidis
 * @version $Id$
 */
public abstract class WizardStep extends JPanel {
    protected static Log log = LogFactory.getLog( WizardStep.class.getName() );
    Wizard owner;

    /**
     * @param wiz
     */
    public WizardStep( Wizard wiz ) {
        super();
        owner = wiz;
        try {
            this.setLayout( new BorderLayout() );
            // jbInit();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    // Component initialization
    protected abstract void jbInit() throws Exception;

    abstract public boolean isReady();

    protected void addMain( JPanel panel ) {
        this.add( panel, BorderLayout.CENTER );
    }

    /**
     * @param text
     */
    protected void addHelp( String text ) {
        JLabel label = new JLabel( text );
        JLabel jLabel1 = new JLabel( "      " );
        JLabel jLabel2 = new JLabel( " " );
        JLabel jLabel3 = new JLabel( " " );
        JLabel jLabel4 = new JLabel( "      " );
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground( Color.WHITE );
        labelPanel.setLayout( new BorderLayout() );
        labelPanel.add( label, BorderLayout.CENTER );
        labelPanel.add( jLabel1, BorderLayout.WEST );
        labelPanel.add( jLabel2, BorderLayout.NORTH );
        labelPanel.add( jLabel3, BorderLayout.SOUTH );
        labelPanel.add( jLabel4, BorderLayout.EAST );
        this.add( labelPanel, BorderLayout.NORTH );
    }

    /**
     * Print a message to the status bar.
     * 
     * @param a message to show.
     */
    public void showStatus( String a ) {
        owner.showStatus( a );
    }

    /**
     * Print an error message to the status bar.
     * 
     * @param a error message to show.
     */
    public void showError( String a ) {
        owner.showError( a );
    }

}