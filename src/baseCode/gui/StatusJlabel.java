package baseCode.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import baseCode.util.StatusDebugLogger;

/**
 * <hr>
 * <p>
 * Copyright (c) 2003-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class StatusJlabel extends StatusDebugLogger {
    protected JLabel jlabel;

    Icon errorIcon = null;

    public StatusJlabel( JLabel l ) {
        errorIcon = new ImageIcon( StatusJlabel.class.getResource( "resources/alert.gif" ) );
        this.jlabel = l;
    }

    public void setStatus( String s ) {
        final String m = s;

        if ( SwingUtilities.isEventDispatchThread() ) {
            setLabel( m, null );
        } else {

            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        setLabel( m, null );
                    }
                } );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
        super.setStatus( s );

    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.util.StatusViewer#setError(java.lang.String)
     */

    public void setError( String s ) {

        final String m = s;

        if ( SwingUtilities.isEventDispatchThread() ) {
            setLabel( m, errorIcon );
        } else {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        setLabel( m, errorIcon );
                    }
                } );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
        super.setError( s );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.util.StatusViewer#clear()
     */
    public void clear() {
        if ( SwingUtilities.isEventDispatchThread() ) {
            setLabel( "", null );
        } else {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        setLabel( "", null );
                    }
                } );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param m
     */
    protected void setLabel( final String m, final Icon icon ) {
        jlabel.setText( m );
        jlabel.setIcon( icon );
        jlabel.repaint();
    }
}