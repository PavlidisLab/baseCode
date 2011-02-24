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

import cern.colt.matrix.impl.DenseObjectMatrix3D;

/**
 * @author ?
 * @version $Id$
 */
public class DenseObject3DMatrix<R, C, S> extends AbstractMatrix3D<R, C, S, Object> {
    private DenseObjectMatrix3D matrix;

    public DenseObject3DMatrix( int slices, int rows, int columns ) {
        super();
        matrix = new DenseObjectMatrix3D( slices, rows, columns );
    }

    public DenseObject3DMatrix( List<S> sliceNames, List<R> rowNames, List<C> colNames ) {
        super();
        setRowNames( rowNames );
        setColumnNames( colNames );
        setSliceNames( sliceNames );
        matrix = new DenseObjectMatrix3D( sliceNames.size(), rowNames.size(), colNames.size() );
    }

    public DenseObject3DMatrix( Object[][][] data ) {
        super();
        matrix = new DenseObjectMatrix3D( data );
    }

    public DenseObject3DMatrix( Object[][][] data, List<S> sliceNames, List<R> rowNames, List<C> colNames ) {
        super();
        matrix = new DenseObjectMatrix3D( data );
        setRowNames( rowNames );
        setColumnNames( colNames );
        setSliceNames( sliceNames );
    }

    @Override
    public int columns() {
        return matrix.columns();
    }

    public Object get( int slice, int row, int col ) {
        return matrix.get( slice, row, col );
    }

    public Object[] getCol( int slice, int col ) {
        Object[] colObjs = new Object[rows()];
        for ( int i = 0; i < rows(); i++ )
            colObjs[i] = matrix.get( slice, i, col );
        return colObjs;
    }

    public Object getObject( int slice, int row, int col ) {
        return this.get( slice, row, col );
    }

    public Object[] getRow( int slice, int row ) {
        Object[] rowObjs = new Object[columns()];
        for ( int i = 0; i < columns(); i++ )
            rowObjs[i] = matrix.get( slice, row, i );
        return rowObjs;
    }

    public Object[][] getSlice( int slice ) {
        Object[][] sliceObjs = new Object[rows()][columns()];
        for ( int i = 0; i < rows(); i++ )
            for ( int j = 0; j < columns(); j++ )
                sliceObjs[i][j] = matrix.get( slice, i, j );
        return sliceObjs;
    }

    @Override
    public boolean isMissing( int slice, int row, int col ) {
        return slice < slices() || row < rows() || col < columns() || matrix.get( slice, row, col ) == null;
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

    public void set( int slice, int row, int col, Object val ) {
        matrix.set( slice, row, col, val );

    }

    @Override
    public int slices() {
        return matrix.slices();
    }

}
