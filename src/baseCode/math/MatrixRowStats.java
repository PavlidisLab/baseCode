package baseCode.math;

import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import cern.colt.list.DoubleArrayList;

/**
 * Convenience functions for getting row statistics from matrices.
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution:: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 * @todo Have min() and max() throw an EmptyMatrixException -- this exception class does not yet exist and needs to be
 *       defined somewhere.
 */
public class MatrixRowStats {

    private MatrixRowStats() { /* keep us from instantiating this */
    }

    /**
     * Calculates the sum of squares for each row of a matrix
     * 
     * @param M DoubleMatrixNamed
     * @return DoubleArrayList
     */
    public static DoubleArrayList sumOfSquares( DoubleMatrixNamed M ) {
        DoubleArrayList r = new DoubleArrayList();

        for ( int i = 0; i < M.rows(); i++ ) {
            DoubleArrayList row = new DoubleArrayList( M.getRow( i ) );
            r.add( DescriptiveWithMissing.sumOfSquares( row ) );
        }

        return r;
    }

    /**
     * Calculates the means of a matrix's rows.
     * 
     * @param M DoubleMatrixNamed
     * @return DoubleArrayList
     */
    public static DoubleArrayList means( DoubleMatrixNamed M ) {
        DoubleArrayList r = new DoubleArrayList();
        for ( int i = 0; i < M.rows(); i++ ) {
            r.add( DescriptiveWithMissing.mean( new DoubleArrayList( M.getRow( i ) ) ) );
        }
        return r;
    }

    /**
     * Calculate the sums of a matrix's rows.
     * 
     * @param M DoubleMatrixNamed
     * @return DoubleArrayList
     */
    public static DoubleArrayList sums( DoubleMatrixNamed M ) {
        DoubleArrayList r = new DoubleArrayList();
        for ( int i = 0; i < M.rows(); i++ ) {
            r.add( DescriptiveWithMissing.sum( new DoubleArrayList( M.getRow( i ) ) ) );
        }
        return r;
    }

    /**
     * Calculates the sample standard deviation of each row of a matrix
     * 
     * @param M DoubleMatrixNamed
     * @return DoubleArrayList
     */
    public static DoubleArrayList sampleStandardDeviations( DoubleMatrixNamed M ) {
        DoubleArrayList r = new DoubleArrayList();
        for ( int i = 0; i < M.rows(); i++ ) {
            DoubleArrayList row = new DoubleArrayList( M.getRow( i ) );
            double mean = DescriptiveWithMissing.mean( row );
            r.add( Math.sqrt( DescriptiveWithMissing.sampleVariance( row, mean ) ) );
        }
        return r;
    }

}