/*
 * The basecode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class Dirichlet {

    // private Algebra a = new Algebra();
    private RandomEngine r;

    private DoubleArrayList p;
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
     * @return
     */
    public double nextDouble() {
        return 0;
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
