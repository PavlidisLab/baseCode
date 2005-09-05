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

/**
 * Dense 2D matrix implementation designed for very fast access of entire rows.
 * <p>
 * Implementation note: The key difference between this and the DenseDouble2DMatrixNamed is that this delegates to a
 * DoubleArrayList[], while DenseDouble2DMatrixNamed delegates to a DenseDoubleMatrix2D.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class FastRowAccessDoubleMatrix2DNamed extends DoubleMatrixNamed {

    private DoubleArrayList[] data;

    /**
     * Implementation note: The input matrix is NOT COPIED (due to the way colt DoubleArrayList(double[]) is
     * implemented).F
     * 
     * @param t
     */
    public FastRowAccessDoubleMatrix2DNamed( double[][] t ) {
        super();
        data = new DoubleArrayList[t.length];
        for ( int i = 0; i < t.length; i++ ) {
            data[i] = new DoubleArrayList( t[i] );
        }
    }

    /**
     * @param rows
     * @param cols
     */
    public FastRowAccessDoubleMatrix2DNamed( int rows, int cols ) {
        super();
        data = new DoubleArrayList[rows];
        for ( int i = 0; i < rows; i++ ) {
            data[i] = new DoubleArrayList( new double[cols] );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#rows()
     */
    public int rows() {
        return data.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#columns()
     */
    public int columns() {
        assert data[0] != null;
        return data[0].size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#set(int, int, java.lang.Object)
     */
    public void set( int i, int j, Object val ) {
        assert val instanceof Double;
        data[i].set( j, ( ( Double ) val ).doubleValue() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#getRowObj(int)
     */
    public Object[] getRowObj( int row ) {
        Double[] result = new Double[columns()];
        for ( int i = 0; i < columns(); i++ ) {
            result[i] = new Double( get( row, i ) );
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#getColObj(int)
     */
    public Object[] getColObj( int col ) {
        Double[] result = new Double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = new Double( get( i, col ) );
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#isMissing(int, int)
     */
    public boolean isMissing( int i, int j ) {
        return Double.isNaN( get( i, j ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#getRow(int)
     */
    public double[] getRow( int i ) {
        return data[i].elements();
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#getRowArrayList(int)
     */
    public DoubleArrayList getRowArrayList( int i ) {
        return data[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#get(int, int)
     */
    public double get( int x, int y ) {
        assert ( data[x] != null );
        return data[x].get( y );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#getQuick(int, int)
     */
    public double getQuick( int i, int j ) {
        return data[i].getQuick( j );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#set(int, int, double)
     */
    public void set( int x, int y, double value ) {
        assert ( data[x] != null );
        assert ( y >= 0 && y < data[x].size() );
        data[x].set( y, value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#viewRow(int)
     */
    public DoubleMatrix1D viewRow( int j ) {
        return new DenseDoubleMatrix1D( data[j].elements() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.DoubleMatrixNamed#setQuick(int, int, double)
     */
    public void setQuick( int j, int i, double c ) {
        data[j].set( i, c );
    }

}
