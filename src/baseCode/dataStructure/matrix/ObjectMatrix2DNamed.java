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

import cern.colt.matrix.ObjectMatrix1D;
import cern.colt.matrix.impl.DenseObjectMatrix2D;

/**
 * Very closely related to the StringMatrix2DNamed, but holds ay objects.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class ObjectMatrix2DNamed extends AbstractNamedMatrix {

    private DenseObjectMatrix2D matrix;

    public ObjectMatrix2DNamed( int x, int y ) {
        super();
        matrix = new DenseObjectMatrix2D( x, y );
    }

    /**
     * @return java.lang.Object
     */
    public String toObject() {
        StringBuffer buf = new StringBuffer();
        buf.append( "label" );
        for ( int i = 0; i < columns(); i++ ) {
            buf.append( "\t" + getColName( i ) );
        }
        buf.append( "\n" );

        for ( int i = 0; i < rows(); i++ ) {
            buf.append( getRowName( i ) );
            for ( int j = 0; j < columns(); j++ ) {
                buf.append( "\t" + get( i, j ) );
            }
            buf.append( "\n" );
        }
        return buf.toString();
    }

    public Object[] getRow( int row ) {
        return viewRow( row ).toArray();
    }

    public Object[] getCol( int col ) {
        Object[] result = new Object[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    public Object[] getRowObj( int row ) {
        Object[] result = new Object[columns()];
        for ( int i = 0; i < columns(); i++ ) {
            result[i] = get( row, i );
        }
        return result;
    }

    public Object[] getColObj( int col ) {
        Object[] result = new Object[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    public boolean isMissing( int i, int j ) {
        return get( i, j ) == "";
    }

    /**
     * @return
     */
    public int columns() {
        return matrix.columns();
    }

    /**
     * @param row
     * @param column
     * @return
     */
    public Object get( int row, int column ) {
        return matrix.get( row, column );
    }

    /**
     * @param row
     * @param column
     * @return
     */
    public Object getQuick( int row, int column ) {
        return matrix.getQuick( row, column );
    }

    /**
     * @return
     */
    public int rows() {
        return matrix.rows();
    }

    /**
     * @return
     */
    public int size() {
        return matrix.size();
    }

    /**
     * @param column
     * @return
     */
    public ObjectMatrix1D viewColumn( int column ) {
        return matrix.viewColumn( column );
    }

    /**
     * @param row
     * @return
     */
    public ObjectMatrix1D viewRow( int row ) {
        return matrix.viewRow( row );
    }

    /**
     * @param row
     * @param column
     * @param value
     */
    public void set( int row, int column, Object value ) {
        matrix.set( row, column, value );
    }

    /**
     * @param row
     * @param column
     * @param value
     */
    public void setQuick( int row, int column, Object value ) {
        matrix.setQuick( row, column, value );
    }

}
