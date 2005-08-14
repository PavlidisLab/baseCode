package baseCode.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Throw this exception when an InterruptedException or isInterrupted() is true, to notify the thread to quit.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class CancellationException extends RuntimeException {

    private static Log log = LogFactory.getLog( CancellationException.class.getName() );

    /**
     * 
     */
    public CancellationException() {
        super();
    }

    /**
     * @param message
     */
    public CancellationException( String message ) {
        super( message );
    }

    /**
     * @param cause
     */
    public CancellationException( Throwable cause ) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public CancellationException( String message, Throwable cause ) {
        super( message, cause );
        log.debug( "CancellationException thrown" );
    }

}
