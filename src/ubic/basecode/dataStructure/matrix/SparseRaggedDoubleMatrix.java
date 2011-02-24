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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

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

    private boolean isDirty = true;

    private Vector<List<Double>> matrix;
    int columns = 0;

    public SparseRaggedDoubleMatrix() {
        matrix = new Vector<List<Double>>();
    }

    /**
     * @param matrix1D
     */
    public void addRow( R name, DoubleMatrix1D matrix1D ) {
        List<Double> row = new ArrayList<Double>();
        CollectionUtils.addAll( row, ArrayUtils.toObject( matrix1D.toArray() ) );
        matrix.add( row );
        this.addRowName( name, matrix.size() - 1 );
        isDirty = true;
    }

    /**
     * @param name
     * @param indexes
     * @param values
     */
    public void addRow( R name, IntArrayList indexes, DoubleArrayList values ) {
        List<Double> row = new ArrayList<Double>();
        CollectionUtils.addAll( row, ArrayUtils.toObject( values.elements() ) );
        matrix.add( row );
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
        for ( List<Double> element2 : matrix ) {
            int value = element2.size();
            if ( value > max ) {
                max = value;
            }

        }

        columns = max;
        isDirty = false;
        return columns;
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

    /**
     * @param row
     * @param column
     * @return
     */
    @Override
    public double get( int i, int j ) {
        return matrix.get( i ).get( j );
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

    @Override
    public DoubleMatrix<R, C> getColRange( int startCol, int endCol ) {
        super.checkColRange( startCol, endCol );

        DoubleMatrix<R, C> returnval = new SparseRaggedDoubleMatrix<R, C>();

        for ( int j = 0, m = this.rows(); j < m; j++ ) {
            if ( j == 0 ) {
                R rowName = this.getRowName( j );
                returnval.addRowName( rowName, j );
            }
            int k = 0;
            List<Double> row = this.matrix.get( j );

            for ( int i = startCol; i <= endCol && i < row.size(); i++ ) {
                C colName = this.getColName( i );
                if ( colName != null ) {
                    returnval.addColumnName( colName, k );
                }
                returnval.set( j, k, this.get( j, i ) );
                k++;
            }

        }
        return returnval;
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
        return ArrayUtils.toPrimitive( matrix.get( i ).toArray( new Double[] {} ) );
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

        for ( Double d : matrix.get( row ) ) {
            if ( d != 0.0 ) {
                returnVal.add( d );
            }
        }

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

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.Matrix2D#getRowRange(int, int)
     */
    @Override
    public DoubleMatrix<R, C> getRowRange( int startRow, int endRow ) {
        super.checkRowRange( startRow, endRow );

        DoubleMatrix<R, C> returnval = new SparseRaggedDoubleMatrix<R, C>();
        int k = 0;
        for ( int i = startRow; i <= endRow; i++ ) {
            R rowName = this.getRowName( i );
            if ( rowName != null ) {
                returnval.addRowName( rowName, i );
            }
            List<Double> row = this.matrix.get( i );
            for ( int j = 0, m = row.size(); j < m; j++ ) {
                if ( i == 0 ) {
                    C colName = this.getColName( j );
                    returnval.addColumnName( colName, j );
                }
                double value = this.get( i, j );
                returnval.set( k, j, value );
            }
            k++;
        }
        return returnval;
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

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.Matrix2D#set(int, int, java.lang.Object)
     */
    public void set( int i, int j, Double d ) {
        if ( matrix.size() <= i ) {
            for ( int m = matrix.size() - 1; m < i; m++ ) {
                matrix.add( new ArrayList<Double>() );
            }
        }
        /*
         * Fill out rows with zeros.
         */
        List<Double> row = matrix.get( i );

        if ( row.size() <= j ) {
            for ( int m = row.size() - 1; m < j; m++ ) {
                row.add( 0.0 );
            }
        }
        row.set( j, d );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#viewRow(int)
     */
    @Override
    public DoubleMatrix1D viewRow( int i ) {
        return new RCDoubleMatrix1D( this.getRow( i ) );
    }

}