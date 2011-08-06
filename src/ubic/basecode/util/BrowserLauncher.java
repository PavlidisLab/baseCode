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

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;

/**
 * @author paul
 * @version $Id$
 */
public class BrowserLauncher {

    /**
     * Attempts to open the default web browser to the given URL.
     * 
     * @param url The URL to open
     * @throws IOException If the web browser could not be located or does not run
     */
    public static void openURL( String url ) throws Exception {

        if ( Desktop.isDesktopSupported() ) {
            Desktop desktop = Desktop.getDesktop();
            if ( desktop.isSupported( Action.BROWSE ) ) {
                desktop.browse( new URI( url.trim() ) );
            } else {
                throw new RuntimeException( "Sorry, you can't open browser windows for some reason" );
            }
        } else {
            throw new RuntimeException( "Sorry, you can't open browser windows for some reason" );
        }
    }

}