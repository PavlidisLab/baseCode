/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.basecode.math.distribution;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.CholeskyDecomposition;
import cern.jet.random.Gamma;
import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;

/**
 * Wishart distribution, used to simulate covariance matrices.
 * <p>
 * Based on method in Odell and Feiveson JASA 1966 p.199-203
 * <p>
 * The interface is modeled after ContinuousDistribution from colt, which unfortunately is designed only for univariate
 * distributions.
 * 
 * @author pavlidis
 * 
 */
public class Wishart {

    DoubleMatrix2D chol; // cholesky decomposition of the covariance matrix.
    DoubleMatrix2D cov; // input covariance matrix
    double df; // degrees of freedom
    int s; // dimension of matrix
    private Algebra a = new Algebra();
    private DoubleMatrix2D mat;
    private RandomEngine r;
    private Gamma rgamma;
    private Normal rnorm;

    /**
     * @param s
     * @param df
     * @param covariance
     * @param randomGenerator
     */
    public Wishart( double df, DoubleMatrix2D covariance, RandomEngine randomGenerator ) {
        this.s = covariance.columns();
        if ( s != covariance.rows() ) throw new IllegalArgumentException( "Covariance matrix must be square" );
        if ( df <= s - 1 ) throw new IllegalArgumentException( "df must be greater than s - 1" );
        if ( randomGenerator == null ) throw new IllegalArgumentException( "Null random number generator" );

        this.r = randomGenerator;
        this.df = df;
        this.cov = covariance;

        rgamma = new Gamma( 1, 1, r );
        rnorm = new Normal( s * ( s - 1.0 ) / 2.0, 1.0, r );

        CholeskyDecomposition c = new CholeskyDecomposition( cov );
        chol = a.transpose( c.getL() ); // returns lower triangle so we transpose to make upper triangular.
        mat = new DenseDoubleMatrix2D( this.s, this.s );
    }

    /**
     * Based on R code from Francesca Dominici, <a
     * href="http://www.biostat.jhsph.edu/~fdominic/teaching/BM/bm.html">http
     * ://www.biostat.jhsph.edu/~fdominic/teaching/BM/bm.html </a>
     * <p>
     * Returns
     * 
     * <pre>
     * w=(RU)'RU
     * </pre>
     * 
     * where
     * 
     * <pre>
     * Cov=U'U (U is upper triang)
     * </pre>
     * 
     * and where upper-tri R is
     * 
     * <pre>
     * R_ij&tilde;N(0,1), i&lt;j ; (R_ii)&circ;2&tilde;Chisq(nu-s+i)
     * </pre>
     * 
     * @param s
     * @param nu
     * @param covariance
     * @return
     */
    public DoubleMatrix2D nextDoubleMatrix() {
        mat.assign( 0.0 );

        // fill in diagonal with random gamma deviates, upper triangle with random normal deviates.
        for ( int i = 0; i < s; i++ ) {
            mat.setQuick( i, i, Math.sqrt( 2 * rgamma.nextDouble( s, ( df + 1.0 - i ) / 2.0 ) ) );
            for ( int j = i + 1; j < s; j++ ) {
                mat.setQuick( i, j, rnorm.nextDouble() );
            }
        }

        mat = a.mult( mat, chol );
        return a.mult( a.transpose( mat ).copy(), mat );

    }

    // #GENERATE WISHART------------------------------------------------------------
    // "rwish" <- function(s,nu,Cov)
    // {
    // #sxs Wishart matrix, nu degree of freedom, var/covar Cov based on
    // #P.L.Odell & A.H. Feiveson(JASA 1966 p.199-203). Returns w=(RU)'RU
    // #where Cov=U'U (U is upper triang) and where upper-tri R is
    // # R_ij~N(0,1), i<j ; (R_ii)^2~Chisq(nu-s+i)
    // if (nu<=s-1) stop ("Wishart algorithm requires nu>s-1")
    // R<- diag(sqrt(2*rgamma(s,(nu+1 - 1:s)/2)))
    // R[outer(1:s, 1:s, "<")] <- rnorm (s*(s-1)/2)
    // R <- R%*% chol(Cov)
    // return(t(R)%*%R)
    // }
    // #GENERATE INVERSE WISHART----------------------------------------------------
    // "riwish" <- function(s,df,Prec)
    // {
    // #sxs Inverse Wishart matrix, df degree of freedom, precision matrix
    // #Prec. Distribution of W^{-1} for Wishart W with nu=df+s-1 degree of
    // # freedoom, covar martix Prec^{-1}.
    // # NOTE mean of riwish is proportional to Prec
    // if (df<=0) stop ("Inverse Wishart algorithm requires df>0")
    // R <- diag(sqrt(2*rgamma(s,(df + s - 1:s)/2)))
    // R[outer(1:s, 1:s, "<")] <- rnorm (s*(s-1)/2)
    // S <- t(solve(R))%*% chol(Prec)
    // return(t(S)%*%S)

}