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

import java.text.NumberFormat;
import java.util.Iterator;
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
public class SparseRaggedDoubleMatrix2DNamed extends DoubleMatrixNamed {

    private Vector matrix; // a vector of DoubleArrayList containing the values of the matrix

    int columns = 0;
    private boolean isDirty = true;

    public SparseRaggedDoubleMatrix2DNamed() {
        matrix = new Vector();
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
     * (non-Javadoc) Unfortunately this has to iterate over the entire array.
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#columns()
     */
    public int columns() {

        if ( !isDirty ) {
            return columns;
        }

        int max = 0;
        for ( Iterator iter = matrix.iterator(); iter.hasNext(); ) {
            DoubleMatrix1D element = ( DoubleMatrix1D ) iter.next();

            int value = element.size();
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
     * @see basecode.dataStructure.matrix.NamedMatrix#set(int, int, java.lang.Object)
     */
    public void set( int i, int j, Object val ) {
        set( i, j, ( ( Double ) val ).doubleValue() );
    }

    /**
     * @param i row
     * @param j column
     * @param d value
     */
    public void set( int i, int j, double d ) {

        ( ( DoubleMatrix1D ) matrix.get( i ) ).set( j, d );

    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#getRowObj(int)
     */
    public Object[] getRowObj( int i ) {
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
     * @see basecode.dataStructure.matrix.NamedMatrix#getColObj(int)
     */
    public Object[] getColObj( int i ) {
        throw new UnsupportedOperationException();
    }
    
    public Object getObj(int row, int col) {
    	return new Double(get(row, col));
    }

    /**
     * (non-Javadoc) Note that in a sparse matrix, zero values are considered "missing"!
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#isMissing(int, int)
     */
    public boolean isMissing( int i, int j ) {
        return get( i, j ) == 0.0;
    }

    /**
     * @return java.lang.String
     */
    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();

        StringBuffer buf = new StringBuffer();

        if ( this.hasColNames() || this.hasRowNames() ) {
            buf.append( "label" );
        }

        if ( this.hasColNames() ) {
            for ( int i = 0; i < columns(); i++ ) {
                buf.append( "\t" + getColName( i ) );
            }
            buf.append( "\n" );
        }

        for ( int i = 0; i < rows(); i++ ) {
            if ( this.hasRowNames() ) {
                buf.append( getRowName( i ) );
            }
            for ( int j = 0; j < columns(); j++ ) {

                double value = get( i, j );

                if ( value == 0.0 ) {
                    buf.append( "\t" );
                } else {
                    buf.append( "\t" + nf.format( value ) );
                }
            }

            buf.append( "\n" );
        }
        return buf.toString();
    }

    /**
     * @param row
     * @param column
     * @return
     */
    public double get( int i, int j ) {
        return ( ( DoubleMatrix1D ) matrix.get( i ) ).getQuick( j );
    }

    /**
     * This gives just the list of values in the row - make sure this is what you want. It does not include the zero
     * values.
     * 
     * @param row
     * @return
     */
    public DoubleArrayList getRowArrayList( int row ) {
        DoubleArrayList returnVal = new DoubleArrayList();
        ( ( DoubleMatrix1D ) matrix.get( row ) ).getNonZeros( new IntArrayList(), returnVal );
        return returnVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#viewRow(int)
     */
    public DoubleMatrix1D viewRow( int i ) {
        return ( DoubleMatrix1D ) matrix.get( i );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#getRow(int)
     */
    public double[] getRow( int i ) {
        // return getRowMatrix1D( i ).toArray();
        return ( ( DoubleMatrix1D ) matrix.get( i ) ).toArray();
    }

    /**
     * @param name
     * @param indexes
     * @param values
     */
    public void addRow( Object name, IntArrayList indexes, DoubleArrayList values ) {
        DoubleMatrix1D rowToAdd = new RCDoubleMatrix1D( indexes, values );

        matrix.add( rowToAdd );
        this.addColumnName( name, matrix.size() - 1 );
        this.addRowName( name, matrix.size() - 1 );
        isDirty = true;
    }

    /**
     * @param matrix1D
     */
    public void addRow( Object name, DoubleMatrix1D matrix1D ) {
        matrix.add( matrix1D );
        this.addColumnName( name, matrix.size() - 1 );
        this.addRowName( name, matrix.size() - 1 );
        isDirty = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#getQuick(int, int)
     */
    public double getQuick( int i, int j ) {
        return get( i, j );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.DoubleMatrixNamed#setQuick(int, int, double)
     */
    public void setQuick( int j, int i, double c ) {
        ( ( DoubleMatrix1D ) matrix.get( i ) ).set( j, c );
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