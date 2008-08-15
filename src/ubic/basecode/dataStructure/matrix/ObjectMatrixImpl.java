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

import cern.colt.matrix.ObjectMatrix1D;
import cern.colt.matrix.impl.DenseObjectMatrix2D;

/**
 * @author pavlidis
 * @version $Id$
 */
public class ObjectMatrixImpl<R, C, V> extends AbstractMatrix<R, C, V> implements ObjectMatrix<R, C, V> {

    private static final long serialVersionUID = -902358802107186038L;
    private DenseObjectMatrix2D matrix;

    public ObjectMatrixImpl( int x, int y ) {
        super();
        matrix = new DenseObjectMatrix2D( x, y );
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
    @SuppressWarnings("unchecked")
    public V get( int row, int column ) {
        return ( V ) matrix.getQuick( row, column );
    }

    @SuppressWarnings("unchecked")
    public V[] getColumn( int col ) {
        V[] result = ( V[] ) new Object[rows()]; // this is how they do it in ArrayList
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public V[] getRow( int row ) {
        Object[] ro = viewRow( row ).toArray();
        V[] result = ( V[] ) new Object[columns()]; // this is how they do it in ArrayList
        for ( int i = 0; i < columns(); i++ ) {
            result[i] = ( V ) ro[i];
        }
        return result;
    }

    public boolean isMissing( int i, int j ) {
        return get( i, j ) == "";
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

    @Override
    public String toString() {
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

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.NamedObjectMatrix#get(java.lang.Object, java.lang.Object)
     */
    public Object get( R row, C column ) {
        return get( getRowIndexByName( row ), getColIndexByName( column ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.NamedMatrix#set(int, int, java.lang.Object)
     */
    public void set( int row, int column, V value ) {
        matrix.setQuick( row, column, value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.NamedMatrix#set(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public void setByKeys( R r, C c, V v ) {
        this.set( getRowIndexByName( r ), getColIndexByName( c ), v );
    }

    public V getByKeys( R r, C c ) {
        return this.get( getRowIndexByName( r ), getColIndexByName( c ) );
    }
}
