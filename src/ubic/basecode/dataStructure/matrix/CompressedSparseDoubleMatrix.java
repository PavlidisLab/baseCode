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

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * Supports sparse matrices (where sparse means most values are zero, not that they are missing).
 * 
 * @author xwan
 * @see no.uib.cipr.matrix.sparse.FlexCompRowMatrix for how this is implemented under the hood.
 * @version $Id$
 */
public class CompressedSparseDoubleMatrix<R, C> extends DoubleMatrix<R, C> {
    /**
     * 
     */
    private static final long serialVersionUID = 5771918031750038719L;
    private FlexCompRowMatrix matrix;

    /**
     * @param rows int
     * @param cols int
     */
    public CompressedSparseDoubleMatrix( int rows, int cols ) {
        super();
        matrix = new FlexCompRowMatrix( rows, cols );
    }

    /**
     * @param mat
     */
    public CompressedSparseDoubleMatrix( double[][] mat ) {
        super();
        matrix = new FlexCompRowMatrix( new DenseMatrix( mat ) );
    }

    /**
     * @return
     */
    public int cardinality() {
        int total = 0;
        for ( int i = 0; i < matrix.numRows(); i++ ) {
            total = total + matrix.getRow( i ).getUsed();
        }

        return total;
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
     * @return
     */
    public int columns() {
        return matrix.numColumns();
    }

    /**
     * @param minNonZeros
     */
    public void ensureCapacity( int minNonZeros ) {

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

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.NamedPrimitiveMatrix#getColObj(int)
     */
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
        SparseVector vector = this.matrix.getRow( row );
        double[] data = vector.getData();
        int[] indices = vector.getIndex();
        double[] values = new double[columns()];
        for ( int j = 0; j < data.length; j++ ) {
            if ( indices[j] == 0 && j > 0 ) break;
            values[indices[j]] = data[j];
        }
        return values;
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
        double[] values = getRow( row );
        for ( int i = 0; i < columns(); i++ ) {
            result[i] = new Double( values[i] );
        }
        return result;
    }

    public boolean isMissing( int i, int j ) {
        return Double.isNaN( get( i, j ) );
    }

    public int rows() {
        return matrix.numRows();
    }

    /**
     * @param row
     * @param column
     * @param value
     */
    public void set( int row, int column, Double value ) {
        matrix.set( row, column, value );
    }

    /**
     * @return
     */
    public int size() {
        return matrix.numColumns() * matrix.numRows();
    }

    /**
     * 
     */
    public void trimToSize() {

    }

    /**
     * @param column
     * @return
     */
    public DoubleMatrix1D viewColumn( int column ) {
        double[] oneColumn = new double[this.rows()];
        for ( int i = 0; i < matrix.numRows(); i++ )
            oneColumn[i] = this.get( i, column );
        return new DenseDoubleMatrix1D( oneColumn );
    }

    /**
     * @param row
     * @return
     */
    @Override
    public DoubleMatrix1D viewRow( int row ) {
        return new DenseDoubleMatrix1D( getRow( row ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.DoubleMatrixNamed#copy()
     */
    @Override
    public DoubleMatrix<R, C> copy() {
        DoubleMatrix<R, C> returnval = new CompressedSparseDoubleMatrix<R, C>( this.rows(), this.columns() );

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

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.Matrix2D#getRowRange(int, int)
     */
    @Override
    public DoubleMatrix<R, C> getRowRange( int startRow, int endRow ) {
        super.checkRowRange( startRow, endRow );

        DoubleMatrix<R, C> returnval = new CompressedSparseDoubleMatrix<R, C>( endRow - startRow, this.columns() );
        for ( int i = startRow; i <= endRow; i++ ) {
            R rowName = this.getRowName( i );
            if ( rowName != null ) {
                returnval.addRowName( rowName, i );
            }
            for ( int j = 0, m = this.columns(); j < m; j++ ) {
                if ( i == 0 ) {
                    C colName = this.getColName( j );
                    returnval.addColumnName( colName, j );
                }
                returnval.set( i, j, this.get( i, j ) );
            }
        }
        return returnval;
    }

}
