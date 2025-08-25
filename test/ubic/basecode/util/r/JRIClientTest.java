/*
 * The baseCode project
 *
 * Copyright (c) 2006-2010 University of British Columbia
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
package ubic.basecode.util.r;

import org.junit.Assume;
import org.junit.BeforeClass;

/**
 * For this test to work, you need to have R installed, the R_HOME environment variable set and rJava installed with
 * the JRI library.
 * @author pavlidis
 */
public class JRIClientTest extends AbstractRClientTest {

    @BeforeClass
    public static void checkRHome() {
        Assume.assumeNotNull( "R_HOME environment variable is not set.", System.getenv( "R_HOME" ) );
        Assume.assumeTrue( JRIClient.ready() );
        log.debug( "java.library.path={}", System.getProperty( "java.library.path" ) );
    }

    @Override
    protected AbstractRClient createRClient() {
        return new JRIClient();
    }
}