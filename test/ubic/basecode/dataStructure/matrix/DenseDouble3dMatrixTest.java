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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DenseDouble3dMatrixTest {
    double[][][] data3d = { { { 1, 2 }, { 3, 4 } }, { { 5, 6 }, { 7, 8 } } };
    DoubleMatrix3D<String, String, String> m3;

    @Before
    public void setUp() throws Exception {
        m3 = new DenseDouble3dMatrix<String, String, String>( data3d );
        List<String> sln = new ArrayList<String>();
        sln.add( "Slice1" );
        sln.add( "Slice2" );
        List<String> rn = new ArrayList<String>();
        rn.add( "row1" );
        rn.add( "row2" );
        List<String> cn = new ArrayList<String>();
        cn.add( "col1" );
        cn.add( "col2" );

        m3.setColumnNames( cn );
        m3.setRowNames( rn );
        m3.setSliceNames( sln );
    }

    @Test
    public void testget() {
        assertEquals( 7, m3.get( 1, 1, 0 ), 0.001 );
    }

    @Test
    public void testgetcolobj() {
        Double[][] r = m3.getColObj( 1 );
        assertEquals( 2, r.length );
        assertEquals( 4, r[0][1], 0.001 );
        assertEquals( 8, r[1][1], 0.001 );
    }

    @Test
    public void testgetrowobj() {
        Double[][] r = m3.getRowObj( 1 );
        assertEquals( 2, r.length );
        assertEquals( 4, r[0][1], 0.001 );
        assertEquals( 8, r[1][1], 0.001 );
        assertEquals( 7, r[1][0], 0.001 );
        assertEquals( 3, r[0][0], 0.001 );
        r = m3.getRowObj( 0 );
        assertEquals( 2, r.length );
        assertEquals( 2, r[0][1], 0.001 );
        assertEquals( 6, r[1][1], 0.001 );
        assertEquals( 5, r[1][0], 0.001 );
        assertEquals( 1, r[0][0], 0.001 );
    }

    @Test
    public void testtostring() {
        String result = m3.toString();
        assertEquals( "Slice\tRow\tcol1\tcol2\n" + "Slice1\trow1\t1.0\t2.0\n" + "Slice1\trow2\t3.0\t4.0\n"
                + "Slice2\trow1\t5.0\t6.0\n" + "Slice2\trow2\t7.0\t8.0\n", result );
        System.err.println( result );
    }

}
