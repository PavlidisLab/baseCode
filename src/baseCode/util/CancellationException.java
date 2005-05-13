package baseCode.util;

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
    }

}
