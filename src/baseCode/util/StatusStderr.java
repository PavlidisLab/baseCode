package baseCode.util;

/**
 * Prints status info to stderr
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StatusStderr implements StatusViewer {

    public StatusStderr() {
    }

    public void showStatus( String s ) {
        if ( s.equals( "" ) ) return;
        System.err.println( s );
    }

    public void showError( String s ) {
        if ( s.equals( "" ) ) return;
        System.err.println( "Error:" + s );
    }

    public void clear() {
        // don't need to do anything.
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.util.StatusViewer#setError(java.lang.Throwable)
     */
    public void showError( Throwable e ) {
        e.printStackTrace();
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.util.StatusViewer#setError(java.lang.String, java.lang.Throwable)
     */
    public void showError( String message, Throwable e ) {
        this.showError( message );
        e.printStackTrace();
    }

}