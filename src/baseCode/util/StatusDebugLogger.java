package baseCode.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StatusDebugLogger implements StatusViewer {

    protected static final Log log = LogFactory.getLog( StatusDebugLogger.class );

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.util.StatusViewer#setStatus(java.lang.String)
     */
    public void setStatus( String s ) {
        if ( s.length() == 0 ) return;
        log.info( s );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.util.StatusViewer#setError(java.lang.String)
     */
    public void setError( String s ) {
        if ( s.length() == 0 ) return;
        log.error( s );
    }

    public void clear() {
        // don't need to do anything.
    }

}