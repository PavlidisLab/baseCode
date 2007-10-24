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

import junit.framework.TestCase;

/**
 * @author paul
 * @version $Id$
 */
public class CompressedNamedBitMatrixTest extends TestCase {

    public void testGetRowBitCount() throws Exception {
        CompressedNamedBitMatrix mat = new CompressedNamedBitMatrix( 2, 2, 2 );
        mat.set( 0, 0, 0 );
        mat.set( 0, 1, 1 );
        mat.set( 1, 1, 0 );
        mat.set( 1, 1, 1 );

        int[] rowBitCount = mat.getRowBitCount( 1 );

        assertEquals( 0, rowBitCount[0] );
        assertEquals( 2, rowBitCount[1] );
        assertEquals( 1, mat.getRowBitCount( 0 )[0] );
        assertEquals( 1, mat.getRowBitCount( 0 )[1] );
    }

    public final void testTotalBitCount() throws Exception {
        CompressedNamedBitMatrix mat = new CompressedNamedBitMatrix( 2, 2, 2 );
        mat.set( 0, 0, 0 );
        mat.set( 0, 1, 1 );
        mat.set( 1, 1, 0 );
        mat.set( 1, 1, 1 );

        long actualValue = mat.totalBitCount();
        long expectedValue = 4; // number of 'sets' we called on different locations.

        assertEquals( expectedValue, actualValue );

    }
}
