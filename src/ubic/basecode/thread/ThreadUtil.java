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
package ubic.basecode.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author keshav
 * @version $Id$
 */
public class ThreadUtil {

    private static Log log = LogFactory.getLog( ThreadUtil.class );

    public static void visitAllRunningThreads() {
        // Find the root thread group
        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
        while ( root.getParent() != null ) {
            root = root.getParent();
        }

        // Visit each thread group
        visit( root, 0 );

    }

    // This method recursively visits all thread groups under `group'.
    private static void visit( ThreadGroup group, int level ) {
        // Get threads in `group'
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads * 2];
        numThreads = group.enumerate( threads, false );

        // Enumerate each thread in `group'
        for ( int i = 0; i < numThreads; i++ ) {
            // Get thread
            Thread thread = threads[i];
            log.info( "Name: " + thread.getName() );
        }

        // Get thread subgroups of `group'
        int numGroups = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
        numGroups = group.enumerate( groups, false );

        // Recursively visit each subgroup
        for ( int i = 0; i < numGroups; i++ ) {
            visit( groups[i], level + 1 );
        }
    }
}
