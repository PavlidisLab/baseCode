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

import org.junit.After;
import org.junit.Before;

import ubic.basecode.io.reader.DoubleMatrixReader;

/**
 * @author pavlidis
 * 
 */
public class JRIClientTest extends AbstractRClientTest {

    @Before
    public void setUp() throws Exception {

        connected = JRIClient.ready();

        if ( !connected ) {
            return;
        }
        try {
            log.debug( "java.library.path=" + System.getProperty( "java.library.path" ) );
            rc = new JRIClient();
            if ( rc == null || !rc.isConnected() ) {
                connected = false;
                return;
            }
        } catch ( UnsatisfiedLinkError e ) {
            log.error( e.getMessage(), e );
            connected = false;
            return;
        }

        DoubleMatrixReader reader = new DoubleMatrixReader();

        tester = reader.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
        assert rc.isConnected();
    }

    @After
    public void tearDown() {
        tester = null;
        rc = null;
    }

}