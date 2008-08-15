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
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.AbstractMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * A dense matrix of doubles that knows about row and column names.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DenseDoubleMatrix<R, C> extends DoubleMatrix<R, C> {

    /**
     * 
     */
    private static final long serialVersionUID = -239226762166912931L;
    private DoubleMatrix2D matrix;

    /**
     * @param T double[][]
     */
    public DenseDoubleMatrix( double T[][] ) {
        super();
        matrix = new DenseDoubleMatrix2D( T );
    }

    /**
     * @param rows int
     * @param cols int
     */
    public DenseDoubleMatrix( int rows, int cols ) {
        super();
        matrix = new DenseDoubleMatrix2D( rows, cols );
    }

    public int columns() {
        return matrix.columns();
    }

    /**
     * @return basecode.dataStructure.DenseDoubleMatrix2DNamed
     */
    @Override
    public DoubleMatrix<R, C> copy() {
        DoubleMatrix<R, C> returnval = new DenseDoubleMatrix<R, C>( this.rows(), this.columns() );
        for ( int i = 0, n = this.rows(); i < n; i++ ) {
            returnval.addRowName( this.getRowName( i ), i );
            for ( int j = 0, m = this.columns(); j < m; j++ ) {
                if ( i == 0 ) {
                    returnval.addColumnName( this.getColName( j ), j );
                }
                returnval.set( i, j, this.get( i, j ) );
            }
        }
        return returnval;
    }

    /**
     * @param row int
     * @param column int
     * @return
     * @see DoubleMatrix2D#get(int, int)
     */
    @Override
    public double get( int row, int column ) {
        return matrix.getQuick( row, column );
    }

    /**
     * Return a copy of a given column.
     * 
     * @param col int
     * @return double[]
     */
    public double[] getColByName( C s ) {
        int col = getColIndexByName( s );
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

    /**
     * Converts to a String that can be read by read.table in R, using default parameters
     * 
     * @return java.lang.String
     */
    /*
     * public String toRReadTableString() { Format nf = new Format( "%.4g" ); StringBuffer result = new StringBuffer(
     * this.rows() * this.columns() ); if ( this.hasColNames() ) { for ( int i = 0; i < this.columns(); i++ ) {
     * result.append( "\"" + this.getColName( i ) +"\" "); System.out.println("\"" + this.getColName( i ) +"\" "); }
     * result.append( "\n" ); } for ( int i = 0; i < this.rows(); i++ ) { if ( this.hasRowNames() ) { result.append("\"" +
     * this.getRowName( i ) + "\"" ); } for ( int j = 0; j < this.columns(); j++ ) { if ( Double.isNaN( this.get( i, j ) ) ) {
     * result.append( " NA" ); } else { result.append( " " + nf.format( this.get( i, j ) ) ); } } result.append( "\n" ); }
     * return result.toString(); }
     */

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
     * Return a reference to a specific row.
     * 
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

    public int rows() {
        return matrix.rows();
    }

    public void set( int row, int column, Double value ) {
        matrix.set( row, column, value );
    }

    /**
     * @return int
     * @see AbstractMatrix2D#size()
     */
    public int size() {
        return matrix.size();
    }

    /**
     * @return double[][]
     */
    @Override
    public double[][] asArray() {
        return matrix.toArray();
    }

    /**
     * @param column int
     * @return cern.colt.matrix.DoubleMatrix1D
     */
    public DoubleMatrix1D viewColumn( int column ) {
        return matrix.viewColumn( column );
    }

    /**
     * @param row int
     * @return DoubleMatrix1D
     * @see DenseDoubleMatrix#viewRow(int)
     */
    @Override
    public DoubleMatrix1D viewRow( int row ) {
        return matrix.viewRow( row );
    }

}