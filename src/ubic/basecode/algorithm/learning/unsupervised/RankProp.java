/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package ubic.basecode.algorithm.learning.unsupervised;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix1D;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import cern.colt.matrix.DoubleMatrix1D;

import com.braju.beta.format.Format;
import com.braju.beta.format.Parameters;

/**
 * Implementation of RankProp, as described in Weston et al. PNAS
 * 
 * @author Paul Pavlidis (port from Jason's code)
 * @version $Id$
 */
public class RankProp {
    protected static final Log log = LogFactory.getLog( RankProp.class );

    double alpha = 0.95; // alpha parameter, controls amount of "clustering"
    int maxIter = 20;// number of iterations of algorithm

    /**
     * @param matrix
     * @param query
     * @param k
     * @return
     */
    public DoubleMatrix1D computeRanking( DoubleMatrixNamed matrix, DoubleMatrixNamed query, int k ) {

        DoubleMatrix1D yorig = new DenseDoubleMatrix1D( query.viewRow( 0 ).toArray() );

        return this.computeRanking( matrix, yorig, k );

    }

    /**
     * @param matrix
     * @param matrix1D
     * @param k
     * @return
     */
    public DoubleMatrix1D computeRanking( DoubleMatrixNamed matrix, DoubleMatrix1D query, int indexOfQuery ) {
        int dim = query.size();
        DoubleMatrix1D y = new DenseDoubleMatrix1D( dim ); // we use own implementation for performance.s
        DoubleMatrix1D yold = new DenseDoubleMatrix1D( dim );

        if ( query.size() <= 1 ) {
            return null;
        }

        y.assign( 0.0 ); // set all to zero.
        y.setQuick( indexOfQuery, 1.0 );

        if ( alpha == 0.0 ) {
            return query;
        }

        for ( int loops = 0; loops < maxIter; loops++ ) { // iterations of propagation

            yold.assign( y ); // initially all zero except for 1 at the query point.

            int lim = Math.min( query.size(), matrix.rows() );

            for ( int j = 0; j < lim; j++ ) {
                if ( j == indexOfQuery ) continue; // don't update query

                double dotProduct = matrix.viewRow( j ).zDotProduct( yold );

                // new y is old y +
                // new weighted linear combination of neighbors
                y.set( j, ( alpha * dotProduct ) + query.getQuick( j ) );
            }

            if ( loops % 5 == 0 ) {
                log.info( " iteration " + loops + " y[0]=" + Format.sprintf( "%g", new Parameters( y.getQuick( 0 ) ) ) );
            }
        }
        return y;
    }

    /**
     * @return Returns the alpha.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * controls amount of "clustering"
     * 
     * @param alpha The alpha to set.
     */
    public void setAlpha( double alpha ) {
        this.alpha = alpha;
    }

    /**
     * Maximum iterations before stopping.
     * 
     * @return Returns the max_loops.
     */
    public int getMaxIter() {
        return maxIter;
    }

    /**
     * Maximum iterations before stopping.
     * 
     * @param max_loops The max_loops to set.
     */
    public void setMaxIter( int maxIter ) {
        this.maxIter = maxIter;
    }

}
