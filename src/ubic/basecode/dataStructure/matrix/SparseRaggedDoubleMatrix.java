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

import java.util.Vector;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * A sparse matrix class where the rows are ragged and compressed.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class SparseRaggedDoubleMatrix<R, C> extends DoubleMatrix<R, C> {

    /**
     * 
     */
    private static final long serialVersionUID = -8911689395488681312L;

    int columns = 0;

    private Vector<DoubleMatrix1D> matrix; // a vector of DoubleMatrix1D containing the values of the matrix
    private boolean isDirty = true;

    public SparseRaggedDoubleMatrix() {
        matrix = new Vector<DoubleMatrix1D>();
    }

    /**
     * @param matrix1D
     */
    public void addRow( R name, DoubleMatrix1D matrix1D ) {
        matrix.add( matrix1D );
        this.addRowName( name, matrix.size() - 1 );
        isDirty = true;
    }

    /**
     * @param name
     * @param indexes
     * @param values
     */
    public void addRow( R name, IntArrayList indexes, DoubleArrayList values ) {
        DoubleMatrix1D rowToAdd = new RCDoubleMatrix1D( indexes, values );

        matrix.add( rowToAdd );
        this.addRowName( name, matrix.size() - 1 );
        isDirty = true;
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
     * Returns the size of the widest row.
     * 
     * @see basecode.dataStructure.matrix.Matrix2D#columns()
     */
    public int columns() {

        if ( !isDirty ) {
            return columns;
        }

        int max = 0;
        for ( Object element2 : matrix ) {
            DoubleMatrix1D element = ( DoubleMatrix1D ) element2;

            int value = element.size();
            if ( value > max ) {
                max = value;
            }

        }

        columns = max;
        isDirty = false;
        return columns;
    }

    /**
     * @param row
     * @param column
     * @return
     */
    @Override
    public double get( int i, int j ) {
        return matrix.get( i ).getQuick( j );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#getColObj(int)
     */
    public Double[] getColObj( int col ) {
        Double[] result = new Double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
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

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#getRow(int)
     */
    @Override
    public double[] getRow( int i ) {
        return matrix.get( i ).toArray();
    }

    /**
     * This gives just the list of values in the row - make sure this is what you want. It does not include the zero
     * values.
     * 
     * @param row
     * @return
     */
    @Override
    public DoubleArrayList getRowArrayList( int row ) {
        DoubleArrayList returnVal = new DoubleArrayList();
        matrix.get( row ).getNonZeros( new IntArrayList(), returnVal );
        return returnVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#getRowObj(int)
     */
    public Double[] getRowObj( int i ) {
        Double[] result = new Double[columns()];

        double[] row = getRow( i );

        for ( int j = 0; j < columns(); j++ ) {
            result[i] = new Double( row[j] );
        }
        return result;
    }

    /**
     * (non-Javadoc) Note that in a sparse matrix, zero values are considered "missing"!
     * 
     * @see basecode.dataStructure.matrix.Matrix2D#isMissing(int, int)
     */
    public boolean isMissing( int i, int j ) {
        return get( i, j ) == 0.0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#rows()
     */
    public int rows() {
        return matrix.size();
    }

    public void set( int i, int j, Double d ) {
        matrix.get( i ).set( j, d );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#viewRow(int)
     */
    @Override
    public DoubleMatrix1D viewRow( int i ) {
        return matrix.get( i );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.DoubleMatrixNamed#copy()
     */
    @Override
    public DoubleMatrix<R, C> copy() {
        DoubleMatrix<R, C> returnval = new SparseRaggedDoubleMatrix<R, C>();

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