package ubic.basecode.dataStructure.matrix;

/**
 * Use this factory to create matrices of type selected at runtime.
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */

public class DoubleMatrix2DNamedFactory {

    public static SparseDoubleMatrix2DNamed sparse( double T[][] ) {
        return new SparseDoubleMatrix2DNamed( T );
    }

    public static SparseDoubleMatrix2DNamed sparse( int rows, int cols ) {
        return new SparseDoubleMatrix2DNamed( rows, cols );
    }

    public static DenseDoubleMatrix2DNamed dense( double T[][] ) {
        return new DenseDoubleMatrix2DNamed( T );
    }

    public static DenseDoubleMatrix2DNamed dense( int rows, int cols ) {
        return new DenseDoubleMatrix2DNamed( rows, cols );
    }

    public static FastRowAccessDoubleMatrix2DNamed fastrow( double T[][] ) {
        return new FastRowAccessDoubleMatrix2DNamed( T );
    }

    public static FastRowAccessDoubleMatrix2DNamed fastrow( int rows, int cols ) {
        return new FastRowAccessDoubleMatrix2DNamed( rows, cols );
    }

}