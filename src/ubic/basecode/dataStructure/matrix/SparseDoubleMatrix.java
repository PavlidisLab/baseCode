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
package ubic.basecode.dataStructure.matrix;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * A sparse matrix that knows about row and column names.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class SparseDoubleMatrix<R, C> extends DoubleMatrix<R, C> {

    /**
     * 
     */
    private static final long serialVersionUID = -1651885517252689369L;
    private SparseDoubleMatrix2D matrix;

    /**
     * @param T double[][]
     */
    public SparseDoubleMatrix( double T[][] ) {
        super();
        matrix = new SparseDoubleMatrix2D( T );
    }

    /**
     * @param rows int
     * @param cols int
     */
    public SparseDoubleMatrix( int rows, int cols ) {
        super();
        matrix = new SparseDoubleMatrix2D( rows, cols );
    }

    /**
     * @return double[][]
     */
    @Override
    public double[][] asArray() {
        double[][] result = new double[rows()][];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = getRow( i );
        }
        return result;
    }

    /**
     * @param rows int
     * @param cols int
     * @param initlalCapacity int
     * @param minLoadFactor double
     * @param maxLoadFactor double
     */
    public SparseDoubleMatrix( int rows, int cols, int initialCapacity, double minLoadFactor,
            double maxLoadFactor ) {
        super();
        matrix = new SparseDoubleMatrix2D( rows, cols, initialCapacity, minLoadFactor, maxLoadFactor );
    }

    /**
     * @return
     */
    public int cardinality() {
        return matrix.cardinality();
    }

    /**
     * @return
     */

    public int columns() {
        return matrix.columns();
    }

    /**
     * @param minNonZeros
     */
    public void ensureCapacity( int minNonZeros ) {
        matrix.ensureCapacity( minNonZeros );
    }

    /**
     * @param row
     * @param column
     * @return
     */
    @Override
    public double get( int row, int column ) {
        return matrix.get( row, column );
    }

    /**
     * Return a copy of a given column.
     * 
     * @param col int
     * @return double[]
     */
    public double[] getCol( int col ) {
        double[] result = new double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    public Double[] getColObj( int col ) {
        Double[] result = new Double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = new Double( get( i, col ) );
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.DoubleMatrixNamed#getColumn(int)
     */
    @Override
    public double[] getColumn( int col ) {
        double[] result = new double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    public Double getObject( int row, int col ) {
        return new Double( get( row, col ) );
    }

    /**
     * Return a reference to a specific row.
     * 
     * @param row int
     * @return double[]
     */
    @Override
    public double[] getRow( int row ) {
        return viewRow( row ).toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#getRowArrayList(int)
     */
    @Override
    public DoubleArrayList getRowArrayList( int i ) {
        return new DoubleArrayList( getRow( i ) );
    }

    /**
     * @param s String
     * @return double[]
     */
    @Override
    public double[] getRowByName( R s ) {
        return getRow( getRowIndexByName( s ) );
    }

    public Double[] getRowObj( int row ) {
        Double[] result = new Double[columns()];
        for ( int i = 0; i < columns(); i++ ) {
            result[i] = new Double( get( row, i ) );
        }
        return result;
    }

    public boolean isMissing( int i, int j ) {
        return Double.isNaN( get( i, j ) );
    }

    /**
     * @return
     */
    public int rows() {
        return matrix.rows();
    }

    public void set( int row, int column, Double value ) {
        matrix.set( row, column, value );
    }

    /**
     * @return
     */
    public int size() {
        return matrix.size();
    }

    /**
     * 
     */
    public void trimToSize() {
        matrix.trimToSize();
    }

    /**
     * @param column
     * @return
     */
    public DoubleMatrix1D viewColumn( int column ) {
        return matrix.viewColumn( column );
    }

    /**
     * @param row
     * @return
     */
    @Override
    public DoubleMatrix1D viewRow( int row ) {
        return matrix.viewRow( row );
    }

    @Override
    public DoubleMatrix<R, C> copy() {
        DoubleMatrix<R, C> returnval = new SparseDoubleMatrix<R, C>( this.rows(), this.columns() );

        for ( int i = 0; i < this.rows(); i++ ) {
            returnval.addRowName( this.getRowName( i ), i );
            for ( int j = 0; j < this.columns(); j++ ) {
                if ( i == 0 ) {
                    returnval.addColumnName( this.getColName( j ), j );
                }
                returnval.set( i, j, this.get( i, j ) );
            }
        }
        return returnval;

    }

}