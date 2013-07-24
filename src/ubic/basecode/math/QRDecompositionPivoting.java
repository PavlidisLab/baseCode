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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix1D;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;

/**
 * QR with pivoting. See http://www.netlib.org/lapack/lug/node42.html and http://www.netlib.org/lapack/lug/node27.html,
 * and Golub and VanLoan, section 5.5.6+
 * 
 * @author paul
 * @version $Id$
 */
public class QRDecompositionPivoting {

    private static Log log = LogFactory.getLog( QRDecompositionPivoting.class );

    DoubleMatrix1D originalNorms;

    private int[] jpvt;

    /**
     * Rank
     */
    private int k = 0;

    /**
     * Rows in input
     */
    private int n;

    /**
     * Columns in input
     */
    private int p;

    private boolean pivoting = true;

    private DoubleMatrix2D QR;
    private DoubleMatrix1D qraux;
    /**
     * Diagonal of R
     */
    private DoubleMatrix1D Rdiag;

    /**
     * Used to decide when to pivot
     */
    private double tolerance = 1e-7;

    /**
     * @param A the matrix to decompose, pivoting will be used.
     */
    public QRDecompositionPivoting( final DoubleMatrix2D A ) {
        this( A, true );
    }

    /**
     * @param A the matrix to decompose
     * @param pivoting set to false to obtain standard QR behaviour.
     */
    public QRDecompositionPivoting( final DoubleMatrix2D A, boolean pivoting ) {
        // Initialize.
        this.QR = A.copy();
        this.n = A.rows();
        this.p = A.columns();
        this.Rdiag = A.like1D( p );
        this.pivoting = pivoting;

        // initialization
        jpvt = new int[p];
        for ( int i = 0; i < p; i++ ) {
            jpvt[i] = i;
        }

        // initialization. We always compute qraux here.
        originalNorms = new DenseDoubleMatrix1D( p ); // "work" in linpack
        qraux = new DenseDoubleMatrix1D( p );
        for ( int i = 0; i < p; i++ ) {
            DoubleMatrix1D col = QR.viewColumn( i );
            double norm2 = 0;
            for ( int r = 0; r < n; r++ ) {
                norm2 = hypot( norm2, col.getQuick( r ) );
            }
            qraux.set( i, norm2 );
            originalNorms.set( i, norm2 == 0.0 ? 1.0 : norm2 );
        }

        // precompute and cache some views to avoid regenerating them time and again
        DoubleMatrix1D[] QRcolumns = new DoubleMatrix1D[p];
        DoubleMatrix1D[] QRcolumnsPart = new DoubleMatrix1D[p];
        for ( int v = 0; v < p; v++ ) {
            QRcolumns[v] = QR.viewColumn( v );
            QRcolumnsPart[v] = QR.viewColumn( v ).viewPart( v, n - v ); // upper triangle.
        }

        k = p;

        // Main loop.
        for ( int v = 0; v < p; v++ ) {

            /*
             * Rotate columns until we find one with a non-negligible norm.
             */
            while ( pivoting && v < k && qraux.get( v ) < originalNorms.get( v ) * tolerance ) {
                log.debug( "Rotating " + v );
                rotate( QR, originalNorms, v );
            }

            DoubleMatrix1D colv = QRcolumns[v];
            double nrm = 0;
            for ( int i = v; i < n; i++ ) {
                nrm = hypot( nrm, colv.getQuick( i ) );
            }

            /*
             * "Householder reflections can be used to calculate QR decompositions by reflecting first one column of a
             * matrix onto a multiple of a standard basis vector, calculating the transformation matrix, multiplying it
             * with the original matrix and then recursing down the (i, i) minors of that product."
             */
            if ( nrm != 0.0 ) {
                // Form k-th Householder vector: scale and flip
                if ( QR.getQuick( v, v ) < 0 ) nrm = -nrm; // dsign
                QRcolumnsPart[v].assign( Functions.div( nrm ) ); // dscal

                QR.setQuick( v, v, QR.getQuick( v, v ) + 1.0 ); // update diagonal

                // Apply transformation to remaining columns.
                for ( int j = v + 1; j < p; j++ ) {
                    DoubleMatrix1D QRcolj = QR.viewColumn( j ).viewPart( v, n - v );
                    double s = QRcolumnsPart[v].zDotProduct( QRcolj );

                    s = -s / QR.getQuick( v, v );
                    for ( int i = v; i < n; i++ ) {
                        QR.setQuick( i, j, QR.getQuick( i, j ) + s * QR.getQuick( i, v ) );
                    }

                    if ( qraux.getQuick( j ) == 0 ) {
                        continue;
                    }

                    /*
                     * Update the norm of this column. Used even if we are not pivoting.
                     */
                    double tt = QR.getQuick( v, j ) / qraux.getQuick( j );
                    double t = Math.max( 1.0 - Math.pow( tt, 2 ), 0.0 );
                    if ( t < 1e-6 ) {
                        DoubleMatrix1D col = QR.viewColumn( j );
                        double nrmv = 0.0;
                        for ( int r = v + 1; r < n; r++ ) {
                            nrmv = hypot( nrmv, col.getQuick( r ) );
                        }
                        qraux.set( j, nrmv );
                    } else {
                        qraux.set( j, qraux.getQuick( j ) * Math.sqrt( t ) );
                    }
                }
                if ( log.isDebugEnabled() ) log.debug( qraux );

            }

            // save transformation parts we are done with.
            qraux.set( v, QR.getQuick( v, v ) );
            QR.setQuick( v, v, -nrm );
            Rdiag.setQuick( v, -nrm );
        }
        k = Math.min( k, n );
        if ( log.isInfoEnabled() ) {
            // log.info( diagnose( originalNorms ) );
        }
    }

    /**
     * @return
     */
    public IntArrayList getPivotOrder() {
        return new IntArrayList( jpvt );
    }

    /**
     * Generates and returns the (economy-sized) orthogonal factor <tt>Q</tt>.
     * 
     * @return <tt>Q</tt>
     */
    public DoubleMatrix2D getQ() {

        DoubleMatrix2D Q = QR.like();

        for ( int i = 0; i < Q.columns(); i++ ) {
            Q.set( i, i, 1 );
        }

        for ( int jy = 0; jy < p; jy++ ) {

            DoubleMatrix1D y = Q.viewColumn( jy );

            for ( int jj = 1; jj <= p; jj++ ) {
                int j = p - jj;

                if ( qraux.get( j ) == 0.0 ) {
                    continue;
                }

                double temp = QR.get( j, j );
                QR.set( j, j, qraux.get( j ) );
                DoubleMatrix1D QRcolv = QR.viewColumn( j ).viewPart( j, n - j );
                DoubleMatrix1D Qcolj = y.viewPart( j, n - j );
                double s = QRcolv.zDotProduct( Qcolj );
                s = -s / QR.getQuick( j, j );

                Qcolj.assign( QRcolv, Functions.plusMult( s ) );

                QR.set( j, j, temp );
            }
        }

        return Q;

    }

    /**
     * @return
     */
    public DoubleMatrix1D getQraux() {
        return qraux;
    }

    /**
     * Returns the upper triangular factor, <tt>R</tt>.
     * 
     * @return <tt>R</tt>
     */
    public DoubleMatrix2D getR() {
        DoubleMatrix2D R = QR.like( p, p );
        for ( int i = 0; i < p; i++ ) {
            for ( int j = 0; j < p; j++ ) {
                if ( i < j )
                    R.setQuick( i, j, QR.getQuick( i, j ) );
                else if ( i == j )
                    R.setQuick( i, j, Rdiag.getQuick( i ) );
                else
                    R.setQuick( i, j, 0 );
            }
        }
        return R;
    }

    /**
     * @return rank
     */
    public int getRank() {
        return k;
    }

    public double getTolerance() {
        return tolerance;
    }

    /**
     * Returns whether the matrix <tt>A</tt> has full rank.
     * 
     * @return true if <tt>R</tt>, and hence <tt>A</tt>, has full rank.
     */
    public boolean hasFullRank() {
        return k == p;
    }

    /**
     * @return true if pivoting was used
     */
    public boolean isPivoting() {
        return pivoting;
    }

    /**
     * Least squares solution of <tt>A*X = B</tt>; <tt>returns X</tt>.
     * 
     * @param B A matrix with as many rows as <tt>A</tt> and any number of columns.
     * @return <tt>X</tt> that minimizes the two norm of <tt>Q*R*X - B</tt>.
     * @exception IllegalArgumentException if <tt>B.rows() != A.rows()</tt>.
     * @exception IllegalArgumentException if <tt>!this.hasFullRank()</tt> (<tt>A</tt> is rank deficient). However,
     *            rank-deficient cases are handled by pivoting, so if you are using pivoting you should not see this
     *            happening.
     */
    public DoubleMatrix2D solve( DoubleMatrix2D B ) {
        if ( B.rows() != n ) {
            throw new IllegalArgumentException( "Matrix row dimensions must agree." );
        }

        if ( !pivoting && !this.hasFullRank() ) {
            throw new IllegalArgumentException( "Matrix is rank deficient; try using pivoting" );
        }

        Algebra solver = new Algebra();
        /*
         * FIXME direct computation of of Q can be avoided by manipulation of the compact QR instead. But this is so
         * much easier to understand ...
         */
        DoubleMatrix2D r1 = solver.mult( solver.transpose( this.getQ() ), B ).viewPart( 0, 0, k, B.columns() );

        // Solve R*X = Y; backsubstitution
        for ( int k1 = k - 1; k1 >= 0; k1-- ) {

            for ( int j = 0; j < B.columns(); j++ ) {
                r1.setQuick( k1, j, r1.getQuick( k1, j ) / Rdiag.getQuick( k1 ) );
            }
            for ( int i = 0; i < k1; i++ ) {
                // sum up to the parameter we've done.
                for ( int j = 0; j < B.columns(); j++ ) {
                    r1.setQuick( i, j, r1.getQuick( i, j ) - r1.getQuick( k1, j ) * QR.getQuick( i, k1 ) );
                }
            }
        }

        /*
         * Pad r1 back out to the full length p, and in the right original order
         */
        DoubleMatrix2D r = r1.like( p, B.columns() );
        r.assign( Double.NaN );
        for ( int i = 0; i < r1.rows(); i++ ) {
            int piv = jpvt[i];
            for ( int j = 0; j < r1.columns(); j++ ) {
                r.setQuick( piv, j, r1.getQuick( i, j ) );
            }
        }
        return r;

        /*
         * To make this work by manipulating QR directly: I had trouble getting it to work, though the Fortran dqrsl is
         * pretty simple (!).
         */

        // int nx = B.columns();
        // DoubleMatrix2D X = B.copy(); // gets modified.

        //
        // for ( int i = 0; i < nx; i++ ) {
        // DoubleMatrix1D y = X.viewColumn( i );
        //
        // int ju = Math.min( k, n - 1 );
        //
        // log.info( "START: " + y );
        //
        // // inner loop: dqrsl on each column.
        // for ( int jj = 1; jj <= ju; jj++ ) {
        //
        // int jm1 = k - jj;
        //
        // if ( QR.get( jm1, jm1 ) == 0.0 ) {
        // throw new IllegalStateException( "Something wrong with QR, non-pivotal column found at index < k: "
        // + jm1 );
        // }
        //
        // y.set( jm1, y.get( jm1 ) / QR.get( jm1, jm1 ) );
        //
        // if ( jm1 != 0 ) {
        // double t = -y.get( jm1 );
        // Blas_j.colvaxpy_j( jm1, t, QR, y, 0, jm1 );
        //
        // // DoubleMatrix1D colQR = QR.viewColumn( jm1 ).viewPart( 0, jm1 + 1 );
        // // y.viewPart( 0, jm1 + 1 ).assign( colQR, Functions.plusMult( t ) );
        // }
        // log.info( jm1 + ": " + y );
        // }
        //
        // // for ( int jj = 0; jj < ju; jj++ ) {
        // // int jm1 = k - jj - 1;
        // // if ( QR.get( jm1, jm1 ) == 0.0 ) {
        // // //
        // // throw new IllegalStateException();
        // // }
        // //
        // // y.set( jm1, y.get( jm1 ) / QR.get( jm1, jm1 ) );
        // //
        // // // if ( jm1 != 0 ) {
        // // double t = -y.get( jm1 );
        // // DoubleMatrix1D colQR = QR.viewColumn( jm1 ).viewPart( 0, jm1 );
        // // y.viewPart( 0, jm1 ).assign( colQR, Functions.plusMult( t ) );
        // // // }
        // //
        // // log.info( jm1 + " ---> " + y );
        // // }
        //
        // }
        //
        // DoubleMatrix2D result = X.viewPart( 0, 0, p, nx );
        //
        // // zero out unused components.
        // if ( k < p ) {
        // for ( int i = k; k < p; k++ ) {
        // for ( int j = 0; j < nx; j++ ) {
        // result.setQuick( i, j, 0.0 );
        // }
        // }
        // }
        //
        // /*
        // * Return to the original order
        // */
        // X = X.viewSelection( this.jpvt, null );
        //
        // return result;
    }

    /**
     * Returns a String with (propertyName, propertyValue) pairs. Useful for debugging or to quickly get the rough
     * picture.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        String unknown = "Illegal operation or error: ";

        buf.append( "-----------------------------------------------------------------\n" );
        buf.append( "QRDecomposition(A) \n" );
        buf.append( "-----------------------------------------------------------------\n" );

        buf.append( "rank = " + k );

        buf.append( "\n\nQ = " );
        try {
            buf.append( String.valueOf( this.getQ() ) );
        } catch ( IllegalArgumentException exc ) {
            buf.append( unknown + exc.getMessage() );
        }

        buf.append( "\n\nR = " );
        try {
            buf.append( String.valueOf( this.getR() ) );
        } catch ( IllegalArgumentException exc ) {
            buf.append( unknown + exc.getMessage() );
        }

        buf.append( "\n\nQRaux = " + this.qraux );

        return buf.toString();
    }

    protected String diagnose() {
        StringBuilder buf = new StringBuilder();
        buf.append( "Rank = " + k + "\n" );
        buf.append( "Work: " + originalNorms + "\n" );
        buf.append( "Qraux: " + qraux + "\n" );
        buf.append( "Pivot indices: " + StringUtils.join( ArrayUtils.toObject( jpvt ), "  " ) + "\n" );
        return buf.toString();
    }

    /**
     * For testing.
     * 
     * @return
     */
    protected DoubleMatrix2D getQR() {
        return QR;
    }

    /**
     * Returns sqrt(a^2 + b^2) without under/overflow (from Colt)
     */
    private double hypot( double a, double b ) {
        double r;
        if ( Math.abs( a ) > Math.abs( b ) ) {
            r = b / a;
            r = Math.abs( a ) * Math.sqrt( 1 + r * r );
        } else if ( b != 0 ) {
            r = a / b;
            r = Math.abs( b ) * Math.sqrt( 1 + r * r );
        } else {
            r = 0.0;
        }
        return r;
    }

    /**
     * @param x
     * @param work
     * @param v
     */
    private void rotate( DoubleMatrix2D x, DoubleMatrix1D work, int v ) {
        for ( int i = 0; i < n; i++ ) {
            double t = x.get( i, v );
            for ( int j = v; j < p - 1; j++ ) {
                x.set( i, j, x.get( i, j + 1 ) );
            }
            x.set( i, p - 1, t );
        }

        // do the same rotation to our helpers
        int i = jpvt[v];
        double t = qraux.get( v );
        double w0 = work.get( v );

        for ( int j = v; j < p - 1; j++ ) {
            jpvt[j] = jpvt[j + 1];
            qraux.set( j, qraux.get( j + 1 ) );
            work.set( j, work.get( j + 1 ) );
        }
        jpvt[p - 1] = i;
        qraux.set( p - 1, t );
        work.set( p - 1, w0 );
        k = k - 1;
    }
}
