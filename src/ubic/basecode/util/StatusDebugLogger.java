/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
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

    /* (non-Javadoc)
     * @see ubic.basecode.util.StatusViewer#showStatus(java.lang.String, int)
     */
    @Override
    public void showStatus( String s, int sleepSeconds ) {
        showStatus( s );
    }

}