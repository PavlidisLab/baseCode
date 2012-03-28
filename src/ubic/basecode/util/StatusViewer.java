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
 * Intended use is to display 'status' information or other messages to users in a non-disruptive fashion (though the
 * actual use is up to the implementer). Copyright (c) 2004 University of British Columbia
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public interface StatusViewer {

    /**
     * Print the status to the location appropriate for this application.
     * 
     * @param s
     */
    public abstract void showStatus( String s );

    /**
     * @param s
     * @param callSuper set to false to suppress calling super.showStatus() (default is true)
     */
    public abstract void showStatus( String s, boolean callSuper );

    /**
     * @param s
     * @param sleepSeconds how long more than usual to display the message (if gui-based)
     */
    public abstract void showStatus( String s, int sleepSeconds );

    /**
     * Print an error status messge.
     * 
     * @param s
     */
    public abstract void showError( String s );

    /**
     * @param s
     */
    public abstract void showWarning( String s );

    /**
     * @param e
     */
    public abstract void showError( Throwable e );

    /**
     * @param e
     */
    public abstract void showError( String message, Throwable e );

    /**
     * Clear the status dislay. Implementers that do not write to GUI elements probably don't need to do anything.
     */
    public abstract void clear();
}