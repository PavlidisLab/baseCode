/*
 * The basecode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package ubic.basecode.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Throw this exception when an InterruptedException or isInterrupted() is true, to notify the thread to quit.
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
