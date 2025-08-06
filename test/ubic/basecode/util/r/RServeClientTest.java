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
package ubic.basecode.util.r;

import org.junit.Assume;
import org.junit.BeforeClass;

import java.io.IOException;

/**
 * For this test to work, you need to have Rserve running on the default port (6311).
 * @author pavlidis
 */
public class RServeClientTest extends AbstractRClientTest {

    @BeforeClass
    public static void checkRserve() {
        try {
            RServeClient rc = new RServeClient();
            Assume.assumeTrue( rc.isConnected() );
        } catch ( IOException e ) {
            Assume.assumeNoException( e );
        }
    }

    @Override
    protected AbstractRClient createRClient() {
        try {
            RServeClient rc = new RServeClient();
            Assume.assumeTrue( rc.isConnected() );
            return rc;
        } catch ( IOException e ) {
            Assume.assumeNoException( e );
            return null;
        }
    }
}