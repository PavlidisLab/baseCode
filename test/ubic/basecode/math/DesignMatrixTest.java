/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
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
package ubic.basecode.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrixImpl;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.StringMatrixReader;

/**
 * @author paul
 * @version $Id$
 */
public class DesignMatrixTest {

    /**
     * This matrix is singular.
     * 
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass()
                .getResourceAsStream( "/data/lmtest2.des.txt" ) );
        DesignMatrix d = new DesignMatrix( sampleInfo, true );
        // log.info( d );

        DoubleMatrix<String, String> m = d.getMatrix();

        assertEquals( 9, d.getMatrix().columns() );

        assertEquals( 1.0, m.get( 11, 0 ), 0.0001 );
        assertEquals( 0.0, m.get( 12, 8 ), 0.0001 );
    }

    /**
     * @throws Exception
     */
    @Test
    public void testA() throws Exception {
        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass().getResourceAsStream(
                "/data/example.metadata.small.txt" ) );

        DoubleMatrix<String, String> X = new DesignMatrix( sampleInfo, true ).getMatrix();
        assertEquals( 3, X.columns() );
        assertEquals( 1, X.get( 0, 0 ), 0.001 );
        assertEquals( 1, X.get( 1, 0 ), 0.001 );
        assertEquals( 1, X.get( 2, 0 ), 0.001 );
        assertEquals( 1, X.get( 3, 0 ), 0.001 );
        assertEquals( 0, X.get( 0, 1 ), 0.001 );
        assertEquals( 1, X.get( 4, 1 ), 0.001 );
        assertEquals( 0, X.get( 4, 2 ), 0.001 );
        assertEquals( 1, X.get( 8, 2 ), 0.001 );
    }

    /**
     * @throws Exception
     */
    @Test
    public void testB() throws Exception {
        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 8, 2 );

        design.set( 0, 0, "A" );
        design.set( 1, 0, "A" );
        design.set( 2, 0, "A" );
        design.set( 3, 0, "A" );
        design.set( 4, 0, "B" );
        design.set( 5, 0, "B" );
        design.set( 6, 0, "B" );
        design.set( 7, 0, "B" );
        design.set( 0, 1, 0.12 );
        design.set( 1, 1, 0.24 );
        design.set( 2, 1, 0.48 );
        design.set( 3, 1, 0.96 );
        design.set( 4, 1, 0.12 );
        design.set( 5, 1, 0.24 );
        design.set( 6, 1, 0.48 );
        design.set( 7, 1, 0.96 );
        design.addColumnName( "Factor" );
        design.addColumnName( "Value" );
        DesignMatrix designMatrix = new DesignMatrix( design, true );
        DoubleMatrix<String, String> X = designMatrix.getMatrix();
        assertEquals( 3, X.columns() );
        assertEquals( 1, X.get( 0, 0 ), 0.001 );
        assertEquals( 1, X.get( 4, 0 ), 0.001 );
        assertEquals( 0, X.get( 0, 1 ), 0.001 );
        assertEquals( 1, X.get( 4, 1 ), 0.001 );
        assertEquals( 0.12, X.get( 4, 2 ), 0.001 );
        assertEquals( 0.96, X.get( 7, 2 ), 0.001 );

        String beforeRebuild = designMatrix.toString();
        designMatrix.rebuild();
        designMatrix.rebuild();
        designMatrix.rebuild();
        designMatrix.rebuild();
        assertEquals( beforeRebuild, designMatrix.toString() );
    }

    /**
     * @throws Exception
     */
    @Test
    public void testInteractionAndRelevel() throws Exception {
        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 8, 2 );

        design.set( 0, 0, "A" );
        design.set( 1, 0, "A" );
        design.set( 2, 0, "A" );
        design.set( 3, 0, "A" );
        design.set( 4, 0, "B" );
        design.set( 5, 0, "B" );
        design.set( 6, 0, "B" );
        design.set( 7, 0, "B" );
        design.set( 0, 1, "C" );
        design.set( 1, 1, "D" );
        design.set( 2, 1, "E" );
        design.set( 3, 1, "C" );
        design.set( 4, 1, "D" );
        design.set( 5, 1, "E" );
        design.set( 6, 1, "C" );
        design.set( 7, 1, "D" );
        design.addColumnName( "Treat" );
        design.addColumnName( "Geno" );

        DesignMatrix designMatrix = new DesignMatrix( design, true );
        designMatrix.addInteraction( "Treat", "Geno" );

        String beforeRebuild = designMatrix.toString();

        DoubleMatrix<String, String> matrix = designMatrix.getMatrix();

        assertEquals( 6, matrix.columns() );
        assertEquals( 8, matrix.rows() );
        assertEquals( 1, matrix.get( 0, 0 ), 0.001 );
        assertEquals( 1, matrix.get( 4, 0 ), 0.001 );
        assertEquals( 0, matrix.get( 0, 1 ), 0.001 );
        assertEquals( 1, matrix.get( 4, 1 ), 0.001 );
        assertEquals( 1, matrix.get( 4, 4 ), 0.001 );
        assertEquals( 1, matrix.get( 7, 4 ), 0.001 );
        assertEquals( 1, matrix.get( 5, 5 ), 0.001 );
        assertEquals( "TreatB", matrix.getColName( 1 ) );
        assertEquals( "GenoD", matrix.getColName( 2 ) );
        assertEquals( "GenoE", matrix.getColName( 3 ) );
        assertEquals( "TreatB:GenoD", matrix.getColName( 4 ) );
        assertEquals( "TreatB:GenoE", matrix.getColName( 5 ) );

        /*
         * Test rebuilding.
         */
        designMatrix.rebuild();
        designMatrix.rebuild();
        designMatrix.rebuild();
        designMatrix.rebuild();

        assertEquals( beforeRebuild, designMatrix.toString() );

        /*
         * Relevel
         */
        designMatrix.setBaseline( "Treat", "B" );
        assertTrue( !beforeRebuild.equals( designMatrix.toString() ) );
        matrix = designMatrix.getMatrix();
        assertEquals( 8, matrix.rows() );
        assertEquals( 1, matrix.get( 0, 0 ), 0.001 );
        assertEquals( 1, matrix.get( 4, 0 ), 0.001 );
        assertEquals( 1, matrix.get( 0, 1 ), 0.001 );
        assertEquals( 0, matrix.get( 4, 1 ), 0.001 );
        assertEquals( 0, matrix.get( 4, 4 ), 0.001 );
        assertEquals( 1, matrix.get( 1, 4 ), 0.001 );
        assertEquals( 0, matrix.get( 7, 4 ), 0.001 );
        assertEquals( 0, matrix.get( 5, 5 ), 0.001 );
        assertEquals( 1, matrix.get( 2, 5 ), 0.001 );

        assertEquals( "TreatA", matrix.getColName( 1 ) );
        assertEquals( "GenoD", matrix.getColName( 2 ) );
        assertEquals( "GenoE", matrix.getColName( 3 ) );
        assertEquals( "TreatA:GenoD", matrix.getColName( 4 ) );
        assertEquals( "TreatA:GenoE", matrix.getColName( 5 ) );

    }

    /**
     * @throws Exception
     */
    @Test
    public void testInteractionC() throws Exception {
        ObjectMatrix<String, String, Object> design = new ObjectMatrixImpl<String, String, Object>( 9, 3 );

        design.set( 0, 0, "A" );
        design.set( 1, 0, "A" );
        design.set( 2, 0, "A" );
        design.set( 3, 0, "A" );
        design.set( 4, 0, "B" );
        design.set( 5, 0, "B" );
        design.set( 6, 0, "B" );
        design.set( 7, 0, "B" );
        design.set( 8, 0, "B" );
        design.set( 0, 1, 0.12 );
        design.set( 1, 1, 0.24 );
        design.set( 2, 1, 0.48 );
        design.set( 3, 1, 0.96 );
        design.set( 4, 1, 0.12 );
        design.set( 5, 1, 0.24 );
        design.set( 6, 1, 0.48 );
        design.set( 7, 1, 0.96 );
        design.set( 8, 1, 0.96 );
        design.set( 0, 2, "C" );
        design.set( 1, 2, "C" );
        design.set( 2, 2, "D" );
        design.set( 3, 2, "D" );
        design.set( 4, 2, "C" );
        design.set( 5, 2, "C" );
        design.set( 6, 2, "D" );
        design.set( 7, 2, "D" );
        design.set( 8, 2, "D" );
        design.addColumnName( "Treat" );
        design.addColumnName( "Value" );
        design.addColumnName( "Geno" );

        DesignMatrix designMatrix = new DesignMatrix( design, true );
        designMatrix.addInteraction( "Treat", "Geno" );

        DoubleMatrix<String, String> matrix = designMatrix.getMatrix();
        assertEquals( 5, matrix.columns() );
        assertEquals( 1, matrix.get( 0, 0 ), 0.001 );
        assertEquals( 1, matrix.get( 4, 0 ), 0.001 );
        assertEquals( 0, matrix.get( 0, 1 ), 0.001 );
        assertEquals( 1, matrix.get( 4, 1 ), 0.001 );
        assertEquals( 0.12, matrix.get( 4, 2 ), 0.001 );
        assertEquals( 0.96, matrix.get( 7, 2 ), 0.001 );

        assertEquals( 0, matrix.get( 4, 4 ), 0.001 );
        assertEquals( 0, matrix.get( 4, 4 ), 0.001 );
        assertEquals( 1, matrix.get( 8, 4 ), 0.001 );
        assertEquals( "TreatB", matrix.getColName( 1 ) );
        assertEquals( "Value", matrix.getColName( 2 ) );
        assertEquals( "GenoD", matrix.getColName( 3 ) );
        assertEquals( "TreatB:GenoD", matrix.getColName( 4 ) );

        String beforeRebuild = designMatrix.toString();
        designMatrix.rebuild();
        designMatrix.rebuild();
        designMatrix.rebuild();
        designMatrix.rebuild();
        assertEquals( beforeRebuild, designMatrix.toString() );

    }
}
