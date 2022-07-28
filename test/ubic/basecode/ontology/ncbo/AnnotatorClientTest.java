/*
 * The basecode project
 * 
 * Copyright (c) 2013 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.ontology.ncbo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Paul
 */
public class AnnotatorClientTest {

    @Before
    public void setUp() {
        Assume.assumeTrue( "This test require -Dncbi.api.key to be set.", System.getProperty( "ncbo.api.key" ) != null );
    }

    @Test
    public void test() throws Exception {
        Collection<AnnotatorResponse> results = AnnotatorClient.findTerm( "cancer" );
        assertTrue( results.size() > 0 );
    }

    @Test
    public void testB() throws Exception {
        String result = AnnotatorClient.findLabelForIdentifier( "MESH", "D010689" );
        assertNotNull( result );
        assertEquals( "phlebitis", result.toLowerCase() );
    }

    @Test
    public void testC() throws Exception {
        String result = AnnotatorClient.findLabelForIdentifier( "DOID", "DOID_8986" );
        assertNotNull( result );
        assertEquals( "narcolepsy", result.toLowerCase() );
    }
    //
    //    @Test
    //    public void testD() throws Exception {
    //        String result = AnnotatorClient.findLabelForIdentifier( "MESH", "D000077192" );
    //        assertNotNull( result );
    //        assertEquals( "phlebitis", result.toLowerCase() );
    //    }

}
