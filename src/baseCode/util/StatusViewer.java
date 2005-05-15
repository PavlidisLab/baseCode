package baseCode.util;

/**
 * Intended use is to display 'status' information or other messages to users in a non-disruptive fashion (though the
 * actual use is up to the implementer). Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public interface StatusViewer {

    /**
     * Print the status to the location appropriate for this application.
     * 
     * @param s
     */
    public abstract void setStatus( String s );

    /**
     * Print an error status messge.
     * 
     * @param s
     */
    public abstract void setError( String s );

    /**
     * @param e
     */
    public abstract void setError( Throwable e );

    /**
     * Clear the status dislay. Implementers that do not write to GUI elements probably don't need to do anything.
     */
    public abstract void clear();
}