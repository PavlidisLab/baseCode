/*
 * The baseCode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.dataStructure.matrix;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import corejava.Format;

/**
 * A matrix of doubles that knows about row and column names.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DenseDoubleMatrix2DNamed extends DoubleMatrixNamed {

    private DenseDoubleMatrix2D matrix;

    /**
     * @param T double[][]
     */
    public DenseDoubleMatrix2DNamed( double T[][] ) {
        super();
        matrix = new DenseDoubleMatrix2D( T );
    }

    /**
     * @param rows int
     * @param cols int
     */
    public DenseDoubleMatrix2DNamed( int rows, int cols ) {
        super();
        matrix = new DenseDoubleMatrix2D( rows, cols );
    }

    /**
     * Return a reference to a specific row.
     * 
     * @param row int
     * @return double[]
     */
    public double[] getRow( int row ) {
        return viewRow( row ).toArray();
    }

    /**
     * Return a reference to a specific row.
     * 
     * @param s String
     * @return double[]
     */
    public double[] getRowByName( String s ) {
        return getRow( getRowIndexByName( s ) );
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

    /**
     * Return a copy of a given column.
     * 
     * @param col int
     * @return double[]
     */
    public double[] getColByName( String s ) {
        int col = getColIndexByName( s );
        double[] result = new double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    public Object[] getRowObj( int row ) {
        Double[] result = new Double[columns()];
        for ( int i = 0; i < columns(); i++ ) {
            result[i] = new Double( get( row, i ) );
        }
        return result;
    }

    public Object[] getColObj( int col ) {
        Double[] result = new Double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = new Double( get( i, col ) );
        }
        return result;
    }

    /**
     * @return java.lang.String
     */
    public String toString() {
        Format nf = new Format( "%.4g" );
        StringBuffer result = new StringBuffer( this.rows() * this.columns() );
        if ( this.hasColNames() || this.hasRowNames() ) {
            result.append( "label" );
        }

        if ( this.hasColNames() ) {
            for ( int i = 0; i < columns(); i++ ) {
                result.append( "\t" + getColName( i ) );
            }
            result.append( "\n" );
        }

        for ( int i = 0; i < rows(); i++ ) {
            if ( this.hasRowNames() ) {
                result.append( getRowName( i ) );
            }
            for ( int j = 0; j < columns(); j++ ) {
                if ( Double.isNaN( get( i, j ) ) ) {
                    result.append( "\t" );
                } else {
                    result.append( "\t" + nf.format( get( i, j ) ) );
                }
            }
            result.append( "\n" );
        }
        return result.toString();
    }

    public void set( int row, int col, Object value ) {
        set( row, col, ( ( Double ) value ).doubleValue() );
    }

    /**
     * Make a copy of a matrix. FIXME move to superclass.
     * 
     * @return baseCode.dataStructure.DenseDoubleMatrix2DNamed
     */
    public DenseDoubleMatrix2DNamed copy() {
        DenseDoubleMatrix2DNamed returnval = new DenseDoubleMatrix2DNamed( this.rows(), this.columns() );
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

    public boolean isMissing( int i, int j ) {
        return Double.isNaN( get( i, j ) );
    }

    /**
     * @param row int
     * @param column int
     * @return
     * @see DoubleMatrix2D#get(int, int)
     */
    public double get( int row, int column ) {
        return matrix.get( row, column );
    }

    /**
     * @param row int
     * @param column int
     * @return double
     * @see DenseDoubleMatrix2D#getQuick(int, int)
     */
    public double getQuick( int row, int column ) {
        return matrix.getQuick( row, column );
    }

    /**
     * @param row int
     * @param column int
     * @param value double
     */
    public void set( int row, int column, double value ) {
        matrix.set( row, column, value );
    }

    /**
     * @param row int
     * @param column int
     * @param value double
     * @see DenseDoubleMatrix2D#setQuick(int, int, double)
     */
    public void setQuick( int row, int column, double value ) {
        matrix.setQuick( row, column, value );
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
     * @see DenseDoubleMatrix2D#viewRow(int)
     */
    public DoubleMatrix1D viewRow( int row ) {
        return matrix.viewRow( row );
    }

    /**
     * @return the number of columns in the matrix
     * @see cern.colt.matrix.impl.AbstractMatrix2D#columns()
     */
    public int columns() {
        return matrix.columns();
    }

    /**
     * @return the number of rows in the matrix
     * @see AbstractMatrix2D#rows()
     */
    public int rows() {
        return matrix.rows();
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
    public double[][] toArray() {
        return matrix.toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix#getRowArrayList(int)
     */
    public DoubleArrayList getRowArrayList( int i ) {
        return new DoubleArrayList( getRow( i ) );
    }

}