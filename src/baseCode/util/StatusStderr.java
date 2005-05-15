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

    public void setStatus( String s ) {
        if ( s.equals( "" ) ) return;
        System.err.println( s );
    }

    public void setError( String s ) {
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
    public void setError( Throwable e ) {
        e.printStackTrace();
    }

}