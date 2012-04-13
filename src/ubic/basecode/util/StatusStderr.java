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

/**
 * Prints status info to stderr
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StatusStderr implements StatusViewer {

    public StatusStderr() {
    }

    @Override
    public void showStatus( String s ) {
        if ( s.equals( "" ) ) return;
        System.err.println( s );
    }

    @Override
    public void showError( String s ) {
        if ( s.equals( "" ) ) return;
        System.err.println( "Error:" + s );
    }

    @Override
    public void clear() {
        // don't need to do anything.
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public void showError( Throwable e ) {
        e.printStackTrace();
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public void showError( String message, Throwable e ) {
        this.showError( message );
        e.printStackTrace();
    }

    @Override
    public void showStatus( String s, boolean callSuper ) {
        showStatus( s );
    }

    @Override
    public void showWarning( String s ) {
        showStatus( s );
    }

    @Override
    public void showProgress( String message ) {
        showStatus( message + " ... " );
    }

}