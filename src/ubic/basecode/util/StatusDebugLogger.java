package ubic.basecode.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Copyright (c) 2006 University of British Columbia
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StatusDebugLogger implements StatusViewer {

    protected static final Log log = LogFactory.getLog( StatusDebugLogger.class );

    /*
     * (non-Javadoc)
     * 
     * @see basecode.util.StatusViewer#setStatus(java.lang.String)
     */
    public void showStatus( String s ) {
        if ( s.length() == 0 ) return;
        log.info( s );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.util.StatusViewer#setError(java.lang.String)
     */
    public void showError( String s ) {
        if ( s.length() == 0 ) return;
        log.error( s );
    }

    public void showError( Throwable e ) {
        log.error( e, e );
    }

    public void clear() {
        // don't need to do anything.
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.util.StatusViewer#setError(java.lang.String, java.lang.Throwable)
     */
    public void showError( String message, Throwable e ) {
        log.error( message, e );
    }

}