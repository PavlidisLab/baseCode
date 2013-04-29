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

import org.apache.commons.lang.StringUtils;
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
    @Override
    public void showStatus( String s ) {
        if ( s.length() == 0 ) return;
        log.info( s );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.util.StatusViewer#setError(java.lang.String)
     */
    @Override
    public void showError( String s ) {
        if ( StringUtils.isBlank( s ) ) return;
        log.error( s );
    }

    @Override
    public void showError( Throwable e ) {
        log.error( e, e );
    }

    @Override
    public void clear() {
        // don't need to do anything.
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public void showError( String message, Throwable e ) {
        log.error( message, e );
    }

    @Override
    public void showStatus( String s, boolean callSuper ) {
        log.info( s );
    }

    @Override
    public void showWarning( String s ) {
        log.warn( s );
    }

    @Override
    public void showProgress( String message ) {
        log.info( message + ( message.endsWith( "..." ) ? "" : " ..." ) );
    }

}