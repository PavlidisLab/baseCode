/*
 * The baseCode project
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
package ubic.basecode.dataStructure.params;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

/**
 * @author Paul
 * @version $Id$
 */
public class ParamGrabberTest {

    @Test
    public void test() {
        Foo fo = new Foo();
        Map<String, String> params = ParameterGrabber.getParams( Foo.class, fo );
        assertEquals( "k", params.get( "k" ) );
        assertNull( params.get( "dkdk" ) );
        assertEquals( "1.01", params.get( "v" ) );
        assertTrue( Boolean.parseBoolean( params.get( "m" ) ) );
    }

    @SuppressWarnings("unused")
    class Foo {
        private String k = "k";
        private String u = "u";
        private boolean m = true;
        private double v = 1.01;
    }
}
