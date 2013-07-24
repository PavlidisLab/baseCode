/*
 * The baseCode project
 * 
 * Copyright (c) 2007 University of British Columbia
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
package ubic.basecode.dataStructure.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author paul
 * @version $Id$
 */
public class CompressedNamedBitMatrixTest {

    @Test
    public void testGetRowBitCount() {
        int[] rowBitCount = mat.getRowBitCount( 1 );
        assertEquals( 0, rowBitCount[0] );
        assertEquals( 2, rowBitCount[1] );
        assertEquals( 1, mat.getRowBitCount( 0 )[0] );
        assertEquals( 1, mat.getRowBitCount( 0 )[1] );
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void testOutOfBounds() {
        mat.set( 1929393, 1, 1 );
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void testOutOfBounds2() {
        mat.set( 0, 1, 100 );
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void testGetOutOfBounds() {
        mat.get( 0, 1, 100 );
    }

    CompressedBitMatrix<String, String> mat;

    @Before
    public void setup() {
        mat = new CompressedBitMatrix<String, String>( 2, 2, 2 );
        mat.set( 0, 0, 0 );
        mat.set( 0, 1, 1 );
        mat.set( 1, 1, 0 );
        mat.set( 1, 1, 1 );
    }

    @Test
    public final void testGet() {
        assertTrue( mat.get( 1, 1, 1 ) );
        assertTrue( mat.get( 1, 1, 0 ) );
        assertTrue( !mat.get( 1, 0, 0 ) );
        assertTrue( !mat.get( 1, 0, 1 ) );

        mat.set( 0, 0, 1 );
        assertTrue( mat.get( 0, 0, 1 ) );
        mat.unset( 0, 0, 1 );
        assertTrue( !mat.get( 0, 0, 1 ) );

        mat.unset( 0, 0, 1 );
        assertTrue( !mat.get( 0, 0, 1 ) );

        mat.set( 0, 0, 1 );
        assertTrue( mat.get( 0, 0, 1 ) );
    }

    @Test
    public final void testBitCount() {

        int v = mat.bitCount( 0, 0 );
        assertEquals( 1, v );
        v = mat.bitCount( 1, 1 );
        assertEquals( 2, v );
    }

    @Test
    public final void testOverlap() {
        assertEquals( 1, mat.overlap( 0, 0, 1, 1 ) );
    }

    @Test
    public final void testTotalBitCount() {
        long actualValue = mat.totalBitCount();
        long expectedValue = 4; // number of 'sets' we called on different locations.
        assertEquals( expectedValue, actualValue );
    }

    @Test
    public void testBitMatrix() throws Exception {
        CompressedBitMatrix<Long, Long> matrix = new CompressedBitMatrix<Long, Long>( 21, 11, 125 );
        for ( int i = 0; i < 21; i++ )
            matrix.addRowName( new Long( i ) );
        for ( int i = 0; i < 11; i++ )
            matrix.addColumnName( new Long( i ) );
        matrix.set( 0, 0, 0 );
        matrix.set( 0, 0, 12 );
        matrix.set( 0, 0, 24 );
        matrix.set( 20, 0, 0 );
        matrix.set( 20, 0, 12 );
        matrix.set( 20, 0, 24 );
        matrix.set( 0, 10, 0 );
        matrix.set( 0, 10, 12 );
        matrix.set( 0, 10, 24 );
        matrix.set( 20, 10, 0 );
        matrix.set( 20, 10, 12 );
        matrix.set( 20, 10, 24 );

        File f = File.createTempFile( "bittest.", ".foo" );
        matrix.toFile( f.getAbsolutePath() );
        assertTrue( f.length() > 0 );
        f.delete();

    }

}
