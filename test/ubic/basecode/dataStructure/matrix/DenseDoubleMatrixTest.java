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

import org.junit.Before;
import org.junit.Test;

/**
 * @author Paul
 * @version $Id$
 */
public class DenseDoubleMatrixTest extends AbstractDoubleMatrixTest {

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        DoubleMatrix<String, String> tmp = f.read( FastRowAccessDoubleMatrixTest.class
                .getResourceAsStream( "/data/testdata.txt" ) );
        testdata = new DenseDoubleMatrix<String, String>( tmp.asArray() );
        testdata.setRowNames( tmp.getRowNames() );
        testdata.setColumnNames( tmp.getColNames() );

        testM = DoubleMatrixFactory.dense( testArray );
        testM.setRowNames( java.util.Arrays.asList( new String[] { "a", "b", "c" } ) );
        testM.setColumnNames( java.util.Arrays.asList( new String[] { "w", "x", "y", "z" } ) );
    }

    @Test
    public void testConstructFromArray() {
        DenseDoubleMatrix<String, String> actual = new DenseDoubleMatrix<String, String>( testdata.asArray() );
        double[][] tt = testdata.asArray();

        for ( int i = 0; i < tt.length; i++ ) {
            int len = tt[i].length;
            for ( int j = 0; j < len; j++ ) {
                assertEquals( tt[i][j], actual.get( i, j ), 0.00001 );
            }
        }
    }

}
