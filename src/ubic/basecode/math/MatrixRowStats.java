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
package ubic.basecode.math;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import cern.colt.list.DoubleArrayList;

/**
 * Convenience functions for getting row statistics from matrices.
 * 
 * @author Paul Pavlidis
 * 
 * @todo Have min() and max() throw an EmptyMatrixException -- this exception class does not yet exist and needs to be
 *       defined somewhere.
 */
public class MatrixRowStats {

    /**
     * Calculates the means of a matrix's rows.
     * 
     * @param M DoubleMatrixNamed
     * @return DoubleArrayList
     */
    public static <R, C> DoubleArrayList means( DoubleMatrix<R, C> M ) {
        DoubleArrayList r = new DoubleArrayList();
        for ( int i = 0; i < M.rows(); i++ ) {
            r.add( DescriptiveWithMissing.mean( new DoubleArrayList( M.getRow( i ) ) ) );
        }
        return r;
    }

    /**
     * Calculates the sample standard deviation of each row of a matrix
     * 
     * @param M DoubleMatrixNamed
     * @return DoubleArrayList
     */
    public static <R, C> DoubleArrayList sampleStandardDeviations( DoubleMatrix<R, C> M ) {
        DoubleArrayList r = new DoubleArrayList();
        for ( int i = 0; i < M.rows(); i++ ) {
            DoubleArrayList row = new DoubleArrayList( M.getRow( i ) );
            double mean = DescriptiveWithMissing.mean( row );
            r.add( Math.sqrt( DescriptiveWithMissing.sampleVariance( row, mean ) ) );
        }
        return r;
    }

    /**
     * Calculates the sum of squares for each row of a matrix
     * 
     * @param M DoubleMatrixNamed
     * @return DoubleArrayList
     */
    public static <R, C> DoubleArrayList sumOfSquares( DoubleMatrix<R, C> M ) {
        DoubleArrayList r = new DoubleArrayList();

        for ( int i = 0; i < M.rows(); i++ ) {
            DoubleArrayList row = new DoubleArrayList( M.getRow( i ) );
            r.add( DescriptiveWithMissing.sumOfSquares( row ) );
        }

        return r;
    }

    /**
     * Calculate the sums of a matrix's rows.
     * 
     * @param M DoubleMatrixNamed
     * @return DoubleArrayList
     */
    public static <R, C> DoubleArrayList sums( DoubleMatrix<R, C> M ) {
        DoubleArrayList r = new DoubleArrayList();
        for ( int i = 0; i < M.rows(); i++ ) {
            r.add( DescriptiveWithMissing.sum( new DoubleArrayList( M.getRow( i ) ) ) );
        }
        return r;
    }

    private MatrixRowStats() { /* keep us from instantiating this */
    }

}