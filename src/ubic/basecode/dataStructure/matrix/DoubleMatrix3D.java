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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * TODO Document Me
 * 
 * @author Xwan
 * @version $Id$
 * @param <R> Row label type
 * @param <C> Column label type
 * @param <S> Slice label type
 */
public abstract class DoubleMatrix3D<R, C, S> extends AbstractMatrix3D<R, C, S, Double> {
    protected static Log log = LogFactory.getLog( DoubleMatrix.class.getName() );
    protected static final int MAX_ROWS_TO_PRINT = 20;
    protected static final int MAX_SLICES_TO_PRINT = 10;

    public abstract double get( int slice, int row, int column );

    public abstract Double[][] getColObj( int col );

    public abstract double[][] getColumn( int column );

    /**
     * @param s String
     * @return double[]
     */
    public double[][] getColumnByName( C col ) {
        return getColumn( getColIndexByName( col ) );
    }

    public abstract double[][] getRow( int row );

    /**
     * @param s String
     * @return double[]
     */
    public double[][] getRowByName( R row ) {
        return getRow( getRowIndexByName( row ) );
    }

    public abstract Double[][] getRowObj( int row );

    public abstract double[][] getSlice( int slice );

    public double[][] getSliceByName( S slice ) {
        return getSlice( getSliceIndexByName( slice ) );
    }

    public abstract Double[][] getSliceObj( int slice );

    @Override
    public abstract boolean isMissing( int slice, int row, int col );

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#rows()
     */
    @Override
    public abstract int rows();

    public abstract void set( int x, int y, int z, double value );

    /**
     * @param j
     * @param i
     * @param c
     */
    public abstract void setQuick( int slice, int row, int column, double c );

    @Override
    public abstract int slices();

    @Override
    public String toString() {
        int slices = this.slices();
        int rows = this.rows();
        int columns = this.columns();
        StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < slices; i++ ) {
            buf.append( "Slice\t" + getSliceName( i ) );
            buf.append( "Row\\Col" );
            for ( int j = 0; j < columns; j++ ) {
                buf.append( "\t" + this.getColName( j ) );
            }
            buf.append( "\n" );
            for ( int k = 0; k < rows; k++ ) {

                buf.append( this.getRowName( k ) );
                for ( int j = 0; j < columns; j++ ) {
                    buf.append( "\t" + this.get( i, k, j ) );
                }
                buf.append( "\n" );
                if ( k > MAX_ROWS_TO_PRINT ) {
                    buf.append( "...\n" );
                    break;
                }
            }
            if ( i > MAX_SLICES_TO_PRINT ) {
                buf.append( "...\n" );
                break;
            }
        }
        return buf.toString();
    }

    public abstract DoubleMatrix2D viewColumn( int column );

    /**
     * @param j
     * @return
     */
    public abstract DoubleMatrix2D viewRow( int slice );

    public abstract DoubleMatrix2D viewSlice( int slice );
}