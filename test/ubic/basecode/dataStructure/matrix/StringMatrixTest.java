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
package ubic.basecode.dataStructure.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.io.reader.StringMatrixReader;

/**
 * @author Paul
 * 
 */
public class StringMatrixTest {

    StringMatrix<String, String> sm;

    @Before
    public void setup() throws Exception {
        StringMatrixReader sr = new StringMatrixReader();
        sm = sr.read( this.getClass().getResourceAsStream( "/data/testdata.txt" ) );
    }

    @Test
    public void testGetColObj() {
        String[] colObj = sm.getColObj( 3 );
        assertEquals( "213.6", colObj[3] );
        assertEquals( 30, colObj.length );
    }

    @Test
    public void testSubset() {
        ObjectMatrix<String, String, String> subset = sm.subset( 6, 4, 4, 5 );
        assertEquals( 4, subset.rows() );
        assertEquals( 5, subset.columns() );
        assertEquals( "40689.1", subset.get( 3, 1 ) );
    }

    @Test
    public void testSubsetColumns() {
        List<String> cols = Arrays.asList( new String[] { "sample3", "sample6", "sample7" } );
        ObjectMatrix<String, String, String> subset = sm.subsetColumns( cols );
        assertEquals( 30, subset.rows() );
        assertEquals( 3, subset.columns() );
        assertEquals( "878.4", subset.get( 3, 1 ) );
    }

    @Test
    public void testToString() {
        String a = sm.toString();
        assertTrue( a
                .startsWith( "label\tsample1\tsample2\tsample3\tsample4\tsample5\tsample6\tsample7\tsample8\tsample9\tsample10\tsample11\tsample12\n"
                        + "gene1_at\t94.2\t227.7\t308.3\t48.8\t170.1\t154.3\t160.4\t106.2\t40.7\t22.5\t184.4\t98.9\n" ) );
    }

}
