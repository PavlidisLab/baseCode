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

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.TestDoubleMatrixReader;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

/**
 * @author Paul
 * @version $Id$
 */
public class MatrixUtilTest {

    DoubleMatrix2D testData;

    @Before
    public void setUp() throws Exception {

        DoubleMatrixReader reader = new DoubleMatrixReader();

        InputStream is = TestDoubleMatrixReader.class.getResourceAsStream( "/data/testdatamissing.txt" );
        testData = new DenseDoubleMatrix2D( reader.read( is ).asDoubles() );
    }

    @Test
    public void testDropColumn() {
        assertEquals( 12, testData.columns() );
        assertEquals( 11, MatrixUtil.dropColumn( testData, 1 ).columns() );
    }

    @Test
    public void testSelectColumns() {
        assertEquals( 12, testData.columns() );
        assertEquals( 3, MatrixUtil.selectColumns( testData, Arrays.asList( new Integer[] { 2, 3, 8 } ) ).columns() );
    }

    @Test
    public void testSelectColumnsAndRows() {
        Algebra A = new Algebra();
        DoubleMatrix2D square = A.mult( testData, A.transpose( testData ) );
        assertEquals( square.rows(), square.columns() );
        assertEquals( 3, MatrixUtil.selectColumnsAndRows( square, Arrays.asList( new Integer[] { 2, 3, 4 } ) ).rows() );
        assertEquals( 3, MatrixUtil.selectColumnsAndRows( square, Arrays.asList( new Integer[] { 2, 3, 4 } ) )
                .columns() );

    }

    @Test
    public void testSelectColumnsAndRowsBad() {
        try {
            MatrixUtil.selectColumnsAndRows( testData, Arrays.asList( new Integer[] { 2, 3, 4 } ) );
            fail( "Should have gotten illegalarg ex" );
        } catch ( IllegalArgumentException e ) {
            // expected
        }
    }

    @Test
    public void testSelectRows() {
        assertEquals( 3, MatrixUtil.selectRows( testData, Arrays.asList( new Integer[] { 2, 3, 4 } ) ).rows() );
    }

}
