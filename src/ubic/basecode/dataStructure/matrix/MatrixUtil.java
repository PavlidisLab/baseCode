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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ubic.basecode.math.Constants;
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

    public static DoubleMatrix1D multWithMissing( DoubleMatrix1D a, DoubleMatrix2D b ) {
        return multWithMissing( a.like2D( 1, a.size() ).assign( new double[][] { a.toArray() } ), b ).viewRow( 0 );
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static DoubleMatrix1D multWithMissing( DoubleMatrix2D a, DoubleMatrix1D b ) {
        int m = a.rows();
        int n = a.columns();

        if ( b.size() != a.columns() ) {
            throw new IllegalArgumentException();
        }

        DoubleMatrix1D C = new DenseDoubleMatrix1D( m );
        C.assign( 0.0 );

        for ( int j = 0; j < m; j++ ) {
            double s = 0.0;
            for ( int k = 0; k < n; k++ ) {
                double aval = a.getQuick( j, k );
                double bval = b.getQuick( k );
                if ( Double.isNaN( aval ) || Double.isNaN( bval ) ) {
                    continue;
                }
                s += aval * bval;
            }
            C.setQuick( j, s + C.getQuick( j ) );
        }

        return C;
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
            throw new IllegalArgumentException( "Nonconformant matrices: " + b.rows() + " != " + a.columns() );
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
     * @return a copy of the data with missing values removed (might be empty!)
     */
    public static DoubleMatrix1D removeMissing( DoubleMatrix1D data ) {
        int sizeWithoutMissingValues = sizeWithoutMissingValues( data );
        if ( sizeWithoutMissingValues == data.size() ) return data;
        DoubleMatrix1D r = new DenseDoubleMatrix1D( sizeWithoutMissingValues );
        double[] elements = data.toArray();
        int size = data.size();
        int j = 0;
        for ( int i = 0; i < size; i++ ) {
            if ( Double.isNaN( elements[i] ) || Double.isInfinite( elements[i] ) ) {
                continue;
            }
            r.set( j++, elements[i] );
        }
        return r;
    }

    public static int sizeWithoutMissingValues( DoubleMatrix1D list ) {

        int size = 0;
        for ( int i = 0; i < list.size(); i++ ) {
            double v = list.get( i );
            if ( !Double.isNaN( v ) && !Double.isInfinite( v ) ) {
                size++;
            }
        }
        return size;
    }

    /**
     * @param d
     * @return true if any of the values are very close to zero.
     */
    public static boolean containsNearlyZeros( DoubleMatrix1D d ) {
        for ( int i = 0; i < d.size(); i++ ) {
            if ( Math.abs( d.get( i ) ) < Constants.SMALL ) return true;
        }
        return false;
    }

    /**
     * @param n
     * @param droppedColumns
     */
    public static DoubleMatrix2D dropColumns( DoubleMatrix2D n, Collection<Integer> droppedColumns ) {
        int columns = n.columns() - droppedColumns.size();
        if ( columns < 0 ) throw new IllegalArgumentException( "Must leave some columns" );
        DoubleMatrix2D res = new DenseDoubleMatrix2D( n.rows(), columns );
        int k = 0;
        for ( int j = 0; j < n.columns(); j++ ) {
            if ( droppedColumns.contains( j ) ) {
                continue;
            }
            for ( int i = 0; i < n.rows(); i++ ) {
                res.set( i, k, n.get( i, j ) );
            }
            k++;
        }
        return res;
    }

    /**
     * @param n
     * @param indexToDrop
     * @return
     */
    public static DoubleMatrix2D dropColumn( DoubleMatrix2D n, int indexToDrop ) {
        int columns = n.columns() - 1;
        if ( columns < 0 ) throw new IllegalArgumentException( "Must leave some columns" );
        DoubleMatrix2D res = new DenseDoubleMatrix2D( n.rows(), columns );
        int k = 0;
        for ( int j = 0; j < n.columns(); j++ ) {
            if ( indexToDrop == j ) {
                continue;
            }
            for ( int i = 0; i < n.rows(); i++ ) {
                res.set( i, k, n.get( i, j ) );
            }
            k++;
        }
        return res;
    }

    public static DoubleMatrix2D selectRows( DoubleMatrix2D n, Collection<Integer> selected ) {
        int nrows = selected.size();
        DoubleMatrix2D res = new DenseDoubleMatrix2D( nrows, n.columns() );
        for ( int j = 0; j < n.columns(); j++ ) {
            int m = 0;
            for ( int i = 0; i < n.rows(); i++ ) {
                if ( !selected.contains( i ) ) {
                    continue;
                }
                res.set( m, j, n.get( i, j ) );
                m++;
            }
        }
        return res;
    }

    public static DoubleMatrix2D selectColumns( DoubleMatrix2D n, Collection<Integer> selected ) {
        int ncols = selected.size();
        DoubleMatrix2D res = new DenseDoubleMatrix2D( n.rows(), ncols );
        int k = 0;
        for ( int j = 0; j < n.columns(); j++ ) {
            if ( !selected.contains( j ) ) {
                continue;
            }
            for ( int i = 0; i < n.rows(); i++ ) {
                res.set( i, k, n.get( i, j ) );
                i++;
            }
        }
        return res;
    }

    /**
     * @param n square matrix
     * @param selected
     * @return
     */
    public static DoubleMatrix2D selectColumnsAndRows( DoubleMatrix2D n, Collection<Integer> selected ) {
        if ( n.rows() != n.columns() ) {
            throw new IllegalArgumentException( "must be a square matrix" );
        }

        if ( selected.isEmpty() ) {
            throw new IllegalArgumentException( "must select more than one" );
        }

        int columns = selected.size();

        if ( columns == n.columns() ) {
            return n;
        }

        if ( columns < 0 ) throw new IllegalArgumentException( "Must leave some columns" );
        DoubleMatrix2D res = new DenseDoubleMatrix2D( columns, columns );
        int k = 0;
        for ( int j = 0; j < n.columns(); j++ ) {
            if ( !selected.contains( j ) ) {
                continue;
            }
            int m = 0;
            for ( int i = 0; i < n.rows(); i++ ) {
                if ( !selected.contains( i ) ) {
                    continue;
                }
                res.set( m, k, n.get( i, j ) );
                m++;
            }
            k++;
        }
        return res;
    }

    public static List<Integer> notNearlyZeroIndices( DoubleMatrix1D d ) {
        List<Integer> result = new ArrayList<Integer>();
        for ( int i = 0; i < d.size(); i++ ) {
            if ( Math.abs( d.get( i ) ) > Constants.SMALL ) result.add( i );
        }
        return result;
    }

    public static DoubleMatrix1D select( DoubleMatrix1D v, Collection<Integer> selected ) {
        DoubleMatrix1D result = new DenseDoubleMatrix1D( selected.size() );
        int k = 0;
        for ( int i = 0; i < v.size(); i++ ) {
            if ( selected.contains( i ) ) {
                result.set( k, v.get( i ) );
                k++;
            }
        }
        return result;
    }

}
