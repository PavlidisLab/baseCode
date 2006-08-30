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
     * Converts to a String that can be read by read.table in R, using default parameters
     * @return java.lang.String
     */
  /*  public String toRReadTableString() {
        Format nf = new Format( "%.4g" );
        StringBuffer result = new StringBuffer( this.rows() * this.columns() );

        if ( this.hasColNames() ) {
            for ( int i = 0; i < this.columns(); i++ ) {
                result.append( "\"" + this.getColName( i ) +"\" ");
                System.out.println("\"" + this.getColName( i ) +"\" ");
            }
            result.append( "\n" );
        }

        for ( int i = 0; i < this.rows(); i++ ) {
            if ( this.hasRowNames() ) {
                result.append("\"" + this.getRowName( i ) + "\"" );
            }
            for ( int j = 0; j < this.columns(); j++ ) {
                if ( Double.isNaN( this.get( i, j ) ) ) {
                    result.append( " NA" );
                } else {
                    result.append( " " + nf.format( this.get( i, j ) ) );
                }
            }
            result.append( "\n" );
        }
        return result.toString();
    }*/
    
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
     * @return basecode.dataStructure.DenseDoubleMatrix2DNamed
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
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#getRowArrayList(int)
     */
    public DoubleArrayList getRowArrayList( int i ) {
        return new DoubleArrayList( getRow( i ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.DoubleMatrixNamed#getColumn(int)
     */
    public double[] getColumn( int col ) {
        double[] result = new double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

}