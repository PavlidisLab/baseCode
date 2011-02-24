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

/**
 * @author Xwan
 * @version $Id$
 */
public class DenseDouble3dMatrix<R, C, S> extends DoubleMatrix3D<R, C, S> {

    private cern.colt.matrix.impl.DenseDoubleMatrix3D matrix;

    public DenseDouble3dMatrix( double[][][] data ) {
        super();
        matrix = new cern.colt.matrix.impl.DenseDoubleMatrix3D( data );
    }

    public DenseDouble3dMatrix( double[][][] data, List<S> sliceNames, List<R> rowNames, List<C> colNames ) {
        super();
        matrix = new cern.colt.matrix.impl.DenseDoubleMatrix3D( data );
        setRowNames( rowNames );
        setColumnNames( colNames );
        setSliceNames( sliceNames );
    }

    public DenseDouble3dMatrix( int slices, int rows, int columns ) {
        super();
        matrix = new cern.colt.matrix.impl.DenseDoubleMatrix3D( slices, rows, columns );
    }

    public DenseDouble3dMatrix( List<S> sliceNames, List<R> rowNames, List<C> colNames ) {
        super();
        setRowNames( rowNames );
        setColumnNames( colNames );
        setSliceNames( sliceNames );
        matrix = new cern.colt.matrix.impl.DenseDoubleMatrix3D( sliceNames.size(), rowNames.size(), colNames.size() );
    }

    @Override
    public int columns() {
        return matrix.columns();
    }

    @Override
    public double get( int i, int j, int k ) {
        return matrix.get( i, j, k );
    }

    @Override
    public Double[][] getColObj( int col ) {
        Double[][] colObj = new Double[slices()][rows()];
        for ( int i = 0; i < slices(); i++ ) {
            for ( int j = 0; j < rows(); i++ ) {
                colObj[i][j] = new Double( matrix.get( i, j, col ) );
            }
        }
        return colObj;
    }

    @Override
    public double[][] getColumn( int col ) {
        double[][] columnArray = new double[slices()][rows()];
        for ( int i = 0; i < slices(); i++ )
            for ( int j = 0; j < rows(); j++ )
                columnArray[i][j] = matrix.get( i, j, col );
        return columnArray;
    }

    public Double getObject( int slice, int row, int col ) {
        return new Double( get( slice, row, col ) );
    }

    @Override
    public double[][] getRow( int row ) {
        double[][] rowArray = new double[slices()][columns()];
        for ( int i = 0; i < slices(); i++ )
            for ( int j = 0; j < columns(); j++ )
                rowArray[i][j] = matrix.get( i, row, j );
        return rowArray;
    }

    @Override
    public Double[][] getRowObj( int row ) {
        Double[][] rowObj = new Double[slices()][columns()];
        for ( int i = 0; i < slices(); i++ ) {
            for ( int j = 0; j < columns(); i++ ) {
                rowObj[i][j] = new Double( matrix.get( i, row, j ) );
            }
        }
        return rowObj;
    }

    @Override
    public double[][] getSlice( int slice ) {
        double[][] sliceArray = new double[rows()][columns()];
        for ( int i = 0; i < rows(); i++ )
            for ( int j = 0; j < columns(); j++ )
                sliceArray[i][j] = matrix.get( slice, i, j );
        return sliceArray;
    }

    @Override
    public Double[][] getSliceObj( int slice ) {
        Double[][] sliceObj = new Double[slices()][columns()];
        for ( int i = 0; i < rows(); i++ ) {
            for ( int j = 0; j < columns(); j++ ) {
                sliceObj[i][j] = new Double( matrix.get( slice, i, j ) );
            }
        }
        return sliceObj;
    }

    @Override
    public boolean isMissing( int slice, int row, int col ) {
        return slice < slices() || row < rows() || col < columns();
    }

    @Override
    public int numMissing() {
        int num = 0;
        for ( int i = 0; i < slices(); i++ )
            for ( int j = 0; j < rows(); j++ )
                for ( int k = 0; k < columns(); k++ )
                    if ( isMissing( i, j, k ) ) num++;
        return num;
    }

    @Override
    public int rows() {
        return matrix.rows();
    }

    @Override
    public void set( int slice, int row, int col, double val ) {
        matrix.set( slice, row, col, val );
    }

    @Override
    public void setQuick( int slice, int row, int column, double c ) {
        matrix.setQuick( slice, row, column, c );
    }

    @Override
    public int slices() {
        return matrix.slices();
    }

    @Override
    public DoubleMatrix2D viewColumn( int column ) {
        return matrix.viewColumn( column );
    }

    @Override
    public DoubleMatrix2D viewRow( int row ) {
        return matrix.viewRow( row );
    }

    @Override
    public DoubleMatrix2D viewSlice( int slice ) {
        return matrix.viewSlice( slice );
    }

}
