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

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.random.Beta;
import cern.jet.random.engine.RandomEngine;
import cern.jet.stat.Descriptive;

/**
 * @author pavlidis
 * 
 */
public class Dirichlet {

    private DoubleArrayList p;

    // private Algebra a = new Algebra();
    private RandomEngine r;
    private Beta rbeta;

    /**
     * @param p
     * @param randomGenerator
     */
    public Dirichlet( DoubleArrayList p, RandomEngine randomGenerator ) {
        if ( randomGenerator == null ) throw new IllegalArgumentException( "Null random number generator" );
        this.r = randomGenerator;
        this.p = p;
        rbeta = new Beta( 1, 1, r );
    }

    /**
     * @param n
     * @return
     */
    public DoubleArrayList draws( int n ) {

        DoubleMatrix2D mat = new DenseDoubleMatrix2D( n, p.size() );
        double psum = Descriptive.sum( ( DoubleArrayList ) p.partFromTo( 1, p.size() - 1 ) );

        for ( int i = 0; i < mat.rows(); i++ ) {
            mat.setQuick( 0, i, rbeta.nextDouble( p.getQuick( 0 ), psum ) );
        }

        for ( int i = 1; i < p.size(); i++ ) {
            for ( int j = 0; j < mat.rows(); j++ ) {
                mat.setQuick( i, j, rbeta.nextDouble( p.getQuick( 0 ), psum ) );
            }
        }

        return null;
    }

    /**
     * @return
     */
    public double nextDouble() {
        return 0;
    }
}
// rdirichlet <- function ( n, p ) {
// # return n random samples from a Dirichlet distribution with parameter p
// if ( !is.vector(n, "numeric")
// | length(n) != 1
// | !is.vector(p, "numeric")
// ) { stop("error in call to rdirichlet") }
// mat <- matrix ( NA, n, length(p) )
// mat[,1] <- rbeta ( n, p[1], sum(p[-1]) )
//
// for ( i in 2:(length(p)-1) ) {
//
// mat[,i] <- ( rbeta ( n, p[i], sum(p[(i+1):length(p)]) )
// * ( 1 - apply ((mat[,1:(i-1),drop=F]), 1, sum) )
// )
// }
// mat[,length(p)] <- 1 - apply ( (mat[,-length(p),drop=F]), 1, sum )
//
// return ( mat )
// }
