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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Paul
 * @version $Id$
 */
public class ParameterKeeperTest {

    @Test
    public void test() throws Exception {
        ParamKeeper k = new ParamKeeper();
        Map<String, String> params = new HashMap<String, String>();
        params.put( "foo", "f" );
        params.put( "bar", "b" );
        k.addParamInstance( params );
        String a = k.toCSVString();
        assertEquals( "bar=b, foo=f\n", a );
        File tmp = File.createTempFile( "paramkeepertest.", ".xls" );
        k.writeExcel( tmp.getAbsolutePath() );
        tmp.delete();
    }
}
