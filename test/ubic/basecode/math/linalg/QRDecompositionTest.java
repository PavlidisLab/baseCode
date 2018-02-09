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
package ubic.basecode.math.linalg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import ubic.basecode.math.linalg.QRDecomposition;
import ubic.basecode.math.linearmodels.DesignMatrix;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;

public class QRDecompositionTest {
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger( QRDecompositionTest.class );
    Algebra solver = new Algebra();

    /**
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        DoubleMatrixReader f = new DoubleMatrixReader();

        DoubleMatrix<String, String> testMatrix = f
                .read( this.getClass().getResourceAsStream( "/data/lmtest2.dat.txt" ) );
        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass()
                .getResourceAsStream( "/data/lmtest2.des.txt" ) );
        DesignMatrix d = new DesignMatrix( sampleInfo, true );

        /*
         * Note that R, when reading the same file, ends up with a different model.matrix that we do (as of this writing
         * April 2011). It does make a difference in these singular cases.
         */
        DoubleMatrix<String, String> matrix = d.getMatrix();
        assertEquals( 9, matrix.columns() );

        QRDecomposition qrlrs = new QRDecomposition( new DenseDoubleMatrix2D( matrix.asArray() ) );

        /*
         * qr(matrix(c(1,0,0,0,0,0,0,0,0,
         * 1,0,0,0,0,0,0,0,0,
         * 1,0,0,0,1,0,0,0,0,
         * 1,0,0,0,1,0,0,0,0,
         * 1,1,0,0,0,1,0,0,0,
         * 1,1,0,0,0,1,0,0,0,
         * 1,1,0,0,0,1,0,0,0,
         * 1,0,1,0,0,0,1,0,0,
         * 1,0,1,0,0,0,0,1,0,
         * 1,0,1,0,1,0,0,0,0,
         * 1,0,1,0,1,0,0,0,0,
         * 1,0,0,1,1,0,0,0,0,
         * 1,0,0,1,1,0,0,0,0,
         * 1,0,0,1,0,0,0,0,1), byrow=T, 14,9))
         */

        DoubleMatrix2D r = qrlrs.getR();
        // log.info( r );
        assertEquals( -3.741657, r.get( 0, 0 ), 0.001 );
        assertEquals( 0.0, r.get( 8, 8 ), 0.001 );
        assertEquals( 1.6329932, r.get( 4, 4 ), 0.001 );
        assertEquals( 8.100926e-01, r.get( 5, 5 ), 0.001 );
        assertEquals( 6.900656e-01, r.get( 6, 6 ), 0.001 );
        assertEquals( -0.6324555, r.get( 7, 7 ), 0.001 );

        DoubleMatrix2D qr = qrlrs.getQR();
        // log.info( qr );
        assertEquals( -3.741657, qr.get( 0, 0 ), 0.001 );
        assertEquals( 0.0, qr.get( 8, 8 ), 0.001 );
        assertEquals( 1.6329932, qr.get( 4, 4 ), 0.001 );
        assertEquals( 8.100926e-01, qr.get( 5, 5 ), 0.001 );
        assertEquals( 6.900656e-01, qr.get( 6, 6 ), 0.001 );
        assertEquals( -0.6324555, qr.get( 7, 7 ), 0.001 );
        assertEquals( 0.2672612, qr.get( 12, 0 ), 0.001 );
        assertEquals( -0.001596204, qr.get( 10, 3 ), 0.001 );
        assertEquals( -0.563532716, qr.get( 11, 3 ), 0.001 );
        assertEquals( -0.55306383, qr.get( 10, 4 ), 0.001 );
        assertEquals( 1.151469e-01, qr.get( 10, 5 ), 0.001 );
        assertEquals( 2.003380e-01, qr.get( 9, 6 ), 0.001 );
        assertEquals( 1.078415e-01, qr.get( 9, 8 ), 0.001 );

        DoubleMatrix2D q = qrlrs.getQ();
        // log.info( q );
        assertEquals( -0.2672612, q.get( 0, 0 ), 0.001 );
        assertEquals( 0.0, q.get( 8, 8 ), 0.001 );
        assertEquals( 0, q.get( 4, 4 ), 0.001 );
        assertEquals( 8.100926e-01, q.get( 7, 5 ), 0.001 );
        assertEquals( 0, q.get( 5, 5 ), 0.001 );
        assertEquals( 6.900656e-01, q.get( 8, 6 ), 0.001 );
        assertEquals( -0.6324555, q.get( 13, 7 ), 0.001 );
        assertEquals( -0.1395726, q.get( 0, 1 ), 0.001 );
        assertEquals( -2.279212e-01, q.get( 0, 2 ), 0.001 );
        assertEquals( -3.273268e-01, q.get( 0, 3 ), 0.001 );
        assertEquals( -3.061862e-01, q.get( 0, 4 ), 0.001 );
        assertEquals( -1.157275e-01, q.get( 0, 5 ), 0.001 );
        assertEquals( -2.070197e-01, q.get( 0, 6 ), 0.001 );
        assertEquals( 3.162278e-01, q.get( 0, 7 ), 0.001 );
        // / assertEquals( -1.462024e-01, q.get( 0, 8 ), 0.001 );

        DoubleMatrix2D leastSq = qrlrs.solve( solver.transpose( new DenseDoubleMatrix2D( testMatrix.getRowRange( 0, 0 )
                .asArray() ) ) );

        double[] expectedCoeffs = new double[] { 7.3740000, 0.1147667, -0.7489000, -0.6931000, 0.8567000, Double.NaN,
                -0.5873000, -1.3434000, -0.0385000 };

        DoubleMatrix1D result = leastSq.viewColumn( 0 );
        for ( int i = 0; i < expectedCoeffs.length; i++ ) {
            assertEquals( "At : " + i, expectedCoeffs[i], result.get( i ), 0.0001 );
        }

    }

    /**
     * Test ability to reproduce standard QR
     * 
     * @throws Exception
     */
    @Test
    public void test2NoPivoting() throws Exception {

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass()
                .getResourceAsStream( "/data/lmtest2.des.txt" ) );
        DesignMatrix d = new DesignMatrix( sampleInfo, true );

        DoubleMatrix<String, String> matrix = d.getMatrix();
        assertEquals( 9, matrix.columns() );

        QRDecomposition qrlrs = new QRDecomposition( new DenseDoubleMatrix2D( matrix.asArray() ), false );
        //   DoubleMatrix2D q = qrlrs.getQ();
        DoubleMatrix2D r = qrlrs.getR();

        assertEquals( -3.741657, r.get( 0, 0 ), 0.001 );
        assertEquals( -0.618566, r.get( 8, 8 ), 0.001 );
        assertEquals( 1.6329932, r.get( 4, 4 ), 0.001 );
        assertEquals( 0.0, r.get( 5, 5 ), 0.001 );
        assertEquals( -0.807789, r.get( 6, 6 ), 0.001 );
        assertEquals( -0.68373, r.get( 7, 7 ), 0.001 );

    }

    /**
     * Case where the Design matrix values include values other than 1's and 0's.
     * 
     * @throws Exception
     */
    @Test
    public void testScaledDesign() throws Exception {

        DoubleMatrixReader of = new DoubleMatrixReader();
        DoubleMatrix<String, String> d = of.read( this.getClass().getResourceAsStream( "/data/lmtest3.des.txt" ) );
        QRDecomposition qrlrs = null;
        DoubleMatrix2D qr = null;

        // this is ok if we only have 1's and 0's
        assertEquals( 9, d.columns() );
        qrlrs = new QRDecomposition( new DenseDoubleMatrix2D( d.asArray() ) );
        qr = qrlrs.getQR();
        assertEquals( -3.741657, qr.get( 0, 0 ), 0.001 );
        assertEquals( 0.0, qr.get( 8, 8 ), 0.001 );
        assertEquals( -8.660254e-01, qr.get( 4, 4 ), 0.001 );
        assertEquals( -1, qr.get( 5, 5 ), 0.001 );

        // but now we multiply by 2
        assertEquals( 9, d.columns() );
        DoubleMatrix2D d2 = new DenseDoubleMatrix2D( d.asArray() );
        d2.assign( Functions.mult( 2 ) );
        qrlrs = new QRDecomposition( d2 );
        qr = qrlrs.getQR();
        assertEquals( -7.483315, qr.get( 0, 0 ), 0.001 );
        assertEquals( 0.0, qr.get( 8, 8 ), 0.001 );
        assertEquals( 3.070598, qr.get( 1, 1 ), 0.001 );
        assertEquals( -1.11658105, qr.get( 1, 2 ), 0.001 );
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSingular2() throws Exception {

        /*
         * This matrix represents what happens when R reads in the sampleinfo lmtest2.des.txt. Using the ensuing
         * model.matrix, we end up with slightly different QR but the LSQ results at the end are the same.
         */
        DoubleMatrixReader of = new DoubleMatrixReader();
        DoubleMatrix<String, String> d = of.read( this.getClass().getResourceAsStream( "/data/lmtest3.des.txt" ) );

        assertEquals( 9, d.columns() );
        QRDecomposition qrlrs = new QRDecomposition( new DenseDoubleMatrix2D( d.asArray() ) );
        qrlrs.getQ();
        DoubleMatrix2D r = qrlrs.getR();

        assertEquals( -3.741657, r.get( 0, 0 ), 0.001 );
        assertEquals( 0.0, r.get( 8, 8 ), 0.001 );
        assertEquals( -8.660254e-01, r.get( 4, 4 ), 0.001 ); // R gets +8.66
        assertEquals( -1, r.get( 5, 5 ), 0.001 );
        assertEquals( 1.1547005, r.get( 6, 6 ), 0.001 );
        assertEquals( 5.773503e-01, r.get( 7, 7 ), 0.001 );

        DoubleMatrix2D qr = qrlrs.getQR();
        assertEquals( -3.741657, qr.get( 0, 0 ), 0.001 );
        assertEquals( 0.0, qr.get( 8, 8 ), 0.001 );
        assertEquals( -8.660254e-01, qr.get( 4, 4 ), 0.001 );
        assertEquals( -1, qr.get( 5, 5 ), 0.001 );
        assertEquals( 1.1547005, qr.get( 6, 6 ), 0.001 );
        assertEquals( 5.773503e-01, qr.get( 7, 7 ), 0.001 );
        assertEquals( 0.2672612, qr.get( 12, 0 ), 0.001 );
        assertEquals( -0.001596204, qr.get( 10, 3 ), 0.001 );
        assertEquals( -0.563532716, qr.get( 11, 3 ), 0.001 );
        assertEquals( -0.2886751, qr.get( 10, 4 ), 0.001 ); // R gets +2.8...

        DoubleMatrix2D q = qrlrs.getQ();
        // log.info( q );
        assertEquals( -0.2672612, q.get( 0, 0 ), 0.001 );
        assertEquals( 0.0, q.get( 8, 8 ), 0.001 );
        assertEquals( 0, q.get( 4, 4 ), 0.001 );
        assertEquals( 0, q.get( 7, 5 ), 0.001 );
        assertEquals( 0, q.get( 5, 5 ), 0.001 );
        assertEquals( 0, q.get( 8, 6 ), 0.001 );
        assertEquals( 5.773503e-01, q.get( 13, 7 ), 0.001 );
        assertEquals( -0.1395726, q.get( 0, 1 ), 0.001 );
        assertEquals( -2.279212e-01, q.get( 0, 2 ), 0.001 );
        assertEquals( -3.273268e-01, q.get( 0, 3 ), 0.001 );
        assertEquals( 0, q.get( 0, 4 ), 0.001 );
        assertEquals( -5.000000e-01, q.get( 0, 5 ), 0.001 );
        assertEquals( 0, q.get( 0, 6 ), 0.001 );
        assertEquals( 0, q.get( 0, 7 ), 0.001 );

        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f
                .read( this.getClass().getResourceAsStream( "/data/lmtest2.dat.txt" ) );
        DoubleMatrix2D leastSq = qrlrs.solve( solver.transpose( new DenseDoubleMatrix2D( testMatrix.getRowRange( 0, 0 )
                .asArray() ) ) );

        double[] expectedCoeffs = new double[] { 6.7867, 0.7021, -0.7489, -0.6931, -0.7561, 0.5873, Double.NaN, 1.4440,
                0.5488 };

        DoubleMatrix1D result = leastSq.viewColumn( 0 );
        for ( int i = 0; i < expectedCoeffs.length; i++ ) {
            assertEquals( "At : " + i, expectedCoeffs[i], result.get( i ), 0.0001 );
        }

    }

    @Test
    public void test3() {

        QRDecomposition qr = new QRDecomposition( new DenseDoubleMatrix2D( new double[][] { { 1, 0 }, { 1, 0 }, { 1, 1 }, { 1, 1 } } ) );
        System.err.println( qr );

    }
}
