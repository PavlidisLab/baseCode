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

import java.util.List;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix3D;

/**
 * @author ?
 * @version $Id$
 */
public class DenseDoubleMatrix3DNamed<R, C, S> extends DoubleMatrixNamed3D<R, C, S> {

    private DenseDoubleMatrix3D matrix;

    public DenseDoubleMatrix3DNamed( int slices, int rows, int columns ) {
        super();
        matrix = new DenseDoubleMatrix3D( slices, rows, columns );
    }

    public DenseDoubleMatrix3DNamed( List<S> sliceNames, List<R> rowNames, List<C> colNames ) {
        super();
        setRowNames( rowNames );
        setColumnNames( colNames );
        setSliceNames( sliceNames );
        matrix = new DenseDoubleMatrix3D( sliceNames.size(), rowNames.size(), colNames.size() );
    }

    public DenseDoubleMatrix3DNamed( double[][][] data, List<S> sliceNames, List<R> rowNames, List<C> colNames ) {
        super();
        matrix = new DenseDoubleMatrix3D( data );
        setRowNames( rowNames );
        setColumnNames( colNames );
        setSliceNames( sliceNames );
    }

    public DenseDoubleMatrix3DNamed( double[][][] data ) {
        super();
        matrix = new DenseDoubleMatrix3D( data );
    }

    public int columns() {
        return matrix.columns();
    }

    public double[][] getColumn( int col ) {
        double[][] columnArray = new double[slices()][rows()];
        for ( int i = 0; i < slices(); i++ )
            for ( int j = 0; j < rows(); j++ )
                columnArray[i][j] = matrix.get( i, j, col );
        return columnArray;
    }

    public double[][] getRow( int row ) {
        double[][] rowArray = new double[slices()][columns()];
        for ( int i = 0; i < slices(); i++ )
            for ( int j = 0; j < columns(); j++ )
                rowArray[i][j] = matrix.get( i, row, j );
        return rowArray;
    }

    public double[][] getSlice( int slice ) {
        double[][] sliceArray = new double[rows()][columns()];
        for ( int i = 0; i < rows(); i++ )
            for ( int j = 0; j < columns(); j++ )
                sliceArray[i][j] = matrix.get( slice, i, j );
        return sliceArray;
    }

    public Object[][] getColObj( int col ) {
        Object[][] colObj = new Object[slices()][rows()];
        for ( int i = 0; i < slices(); i++ ) {
            for ( int j = 0; j < rows(); i++ ) {
                colObj[i][j] = new Double( matrix.get( i, j, col ) );
            }
        }
        return colObj;
    }

    public double getQuick( int i, int j, int k ) {
        return matrix.getQuick( i, j, k );
    }

    public Object[][] getRowObj( int row ) {
        Object[][] rowObj = new Object[slices()][columns()];
        for ( int i = 0; i < slices(); i++ ) {
            for ( int j = 0; j < columns(); i++ ) {
                rowObj[i][j] = new Double( matrix.get( i, row, j ) );
            }
        }
        return rowObj;
    }

    public Object[][] getSliceObj( int slice ) {
        Object[][] sliceObj = new Object[slices()][columns()];
        for ( int i = 0; i < rows(); i++ ) {
            for ( int j = 0; j < columns(); j++ ) {
                sliceObj[i][j] = new Double( matrix.get( slice, i, j ) );
            }
        }
        return sliceObj;
    }

    public void setQuick( int slice, int row, int column, double c ) {
        matrix.setQuick( slice, row, column, c );
    }

    public DoubleMatrix2D viewRow( int row ) {
        return matrix.viewRow( row );
    }

    public DoubleMatrix2D viewColumn( int column ) {
        return matrix.viewColumn( column );
    }

    public DoubleMatrix2D viewSlice( int slice ) {
        return matrix.viewSlice( slice );
    }

    public boolean isMissing( int slice, int row, int col ) {
        return slice < slices() || row < rows() || col < columns();
    }

    public int numMissing() {
        int num = 0;
        for ( int i = 0; i < slices(); i++ )
            for ( int j = 0; j < rows(); j++ )
                for ( int k = 0; k < columns(); k++ )
                    if ( isMissing( i, j, k ) ) num++;
        return num;
    }

    public int rows() {
        return matrix.rows();
    }

    public void set( int slice, int row, int col, double val ) {
        matrix.set( slice, row, col, val );
    }

    public int slices() {
        return matrix.slices();
    }

    public double get( int slice, int row, int col ) {
        return matrix.get( slice, row, col );
    }

    public Object getObj( int slice, int row, int col ) {
        return new Double( get( slice, row, col ) );
    }

}
