/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Get a connection to R, somehow (if possible).
 * 
 * @author Paul
 * @version $Id$
 */
public class RConnectionFactory {

    private static Log log = LogFactory.getLog( RConnectionFactory.class.getName() );

    /**
     * Tries JRI connection, then Rserve connection.
     * 
     * @param hostName The host to use for rserve connections. Ignored if JRI is available.
     * @return
     */
    public static RClient getRConnection( String hostName ) {

        RClient rc = null;

        rc = getJRIClient();
        if ( rc != null ) {
            return rc;
        }
        try {
            rc = new RServeClient( hostName );
        } catch ( Exception e ) {
            log.error( e );
        }
        return rc;
    }

    /**
     * Get connection; if Rserve is used, connect to localhost.
     * 
     * @return
     */
    public static RClient getRConnection() {
        return getRConnection( "localhost" );
    }

    /**
     * @return
     */
    private static RClient getJRIClient() {
        try {
            RClient j = new JRIClient();
            log.debug( "Got JRI connection" );
            return j;
        } catch ( IOException e ) {
            log.warn( "Was unable to get a JRI connection", e );
            return null;
        }

    }

}
