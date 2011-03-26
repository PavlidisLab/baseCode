/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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

package ubic.basecode.dataStructure.matrix;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * @author Paul
 * @version $Id$
 */
public class MatrixUtil {

    /**
     * Extract the diagonal from a matrix.
     * 
     * @param matrix
     * @return
     */
    public static DoubleMatrix1D diagonal( DoubleMatrix2D matrix ) {

        int mindim = Math.min( matrix.rows(), matrix.columns() );
        DoubleMatrix1D result = new DenseDoubleMatrix1D( mindim );
        for ( int i = mindim; --i >= 0; ) {
            result.set( i, matrix.getQuick( i, i ) );
        }
        return result;
    }

    /**
     * @param <R>
     * @param <C>
     * @param <V>
     * @param matrix
     * @param rowIndex
     * @param colIndex
     * @return
     */
    public static <R, C, V> V getObject( Matrix2D<R, C, V> matrix, int rowIndex, int colIndex ) {
        if ( ObjectMatrix.class.isAssignableFrom( matrix.getClass() ) ) {
            return ( ( ObjectMatrix<R, C, V> ) matrix ).get( rowIndex, colIndex );
        } else if ( matrix instanceof PrimitiveMatrix<?, ?, ?> ) {
            return ( ( PrimitiveMatrix<R, C, V> ) matrix ).getObject( rowIndex, colIndex );
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @param <R>
     * @param <C>
     * @param <V>
     * @param matrix
     * @param rowIndex
     * @return
     */
    public static <R, C, V> V[] getRow( Matrix2D<R, C, V> matrix, int rowIndex ) {
        if ( ObjectMatrix.class.isAssignableFrom( matrix.getClass() ) ) {
            return ( ( ObjectMatrix<R, C, V> ) matrix ).getRow( rowIndex );
        } else if ( matrix instanceof PrimitiveMatrix<?, ?, ?> ) {
            return ( ( PrimitiveMatrix<R, C, V> ) matrix ).getRowObj( rowIndex );
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @param source the source of information about missing values
     * @param target the target where we want to convert values to missing
     */
    public static void maskMissing( DoubleMatrix2D source, DoubleMatrix2D target ) {
        source.checkShape( target );

        for ( int i = 0; i < source.rows(); i++ ) {
            for ( int j = 0; j < source.columns(); j++ ) {
                if ( Double.isNaN( source.get( i, j ) ) ) {
                    target.set( i, j, Double.NaN );
                }
            }
        }

    }

    /**
     * Multiple two matrices, tolerate missing values.
     * 
     * @param a
     * @param b
     * @return
     */
    public static DoubleMatrix2D multWithMissing( DoubleMatrix2D a, DoubleMatrix2D b ) {
        int m = a.rows();
        int n = a.columns();
        int p = b.columns();

        if ( b.rows() != a.columns() ) {
            throw new IllegalArgumentException();
        }

        DoubleMatrix2D C = new DenseDoubleMatrix2D( m, p );
        C.assign( 0.0 );
        for ( int i = 0; i < p; i++ ) {
            for ( int j = 0; j < m; j++ ) {
                double s = 0.0;
                for ( int k = 0; k < n; k++ ) {
                    double aval = a.getQuick( j, k );
                    double bval = b.getQuick( k, i );
                    if ( Double.isNaN( aval ) || Double.isNaN( bval ) ) {
                        continue;
                    }
                    s += aval * bval;
                }
                C.setQuick( j, i, s + C.getQuick( j, i ) );
            }
        }
        return C;
    }

    /**
     * @param data
     * @return
     */
    public static DoubleMatrix1D removeMissing( DoubleMatrix1D data ) {
        int sizeWithoutMissingValues = sizeWithoutMissingValues( data );
        if ( sizeWithoutMissingValues == data.size() ) return data;
        DoubleMatrix1D r = new DenseDoubleMatrix1D( sizeWithoutMissingValues );
        double[] elements = data.toArray();
        int size = data.size();
        int j = 0;
        for ( int i = 0; i < size; i++ ) {
            if ( Double.isNaN( elements[i] ) ) {
                continue;
            }
            r.set( j++, elements[i] );
        }
        return r;
    }

    public static int sizeWithoutMissingValues( DoubleMatrix1D list ) {

        int size = 0;
        for ( int i = 0; i < list.size(); i++ ) {
            if ( !Double.isNaN( list.get( i ) ) ) {
                size++;
            }
        }
        return size;
    }
}
