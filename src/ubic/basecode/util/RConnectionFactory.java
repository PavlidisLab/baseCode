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
     * @return
     */
    public static RClient getRConnection() {
        RClient rc = null;
        try {
            rc = new RServeClient();
            log.info( "here" );
            if ( rc.isConnected() ) {
                return rc;
            }
            log.info( "here" );
            return getJRIClient();
        } catch ( IOException e ) {
            return getJRIClient();
        }
    }

    private static RClient getJRIClient() {
        log.info( "Trying to get JRI connection instead" );
        try {
            return new JRIClient();
        } catch ( IOException e ) {
            log.warn( "Was unable to get an R connection", e );
            return null;
        }

    }

}
