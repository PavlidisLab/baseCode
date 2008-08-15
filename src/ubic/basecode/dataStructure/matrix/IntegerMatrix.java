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

/**
 * @author pavlidis
 * @version $Id$
 */
public class IntegerMatrix<R, C> extends AbstractMatrix<R, C, Integer> implements
        PrimitiveMatrix<R, C, Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = -8413796057024940237L;
    private ObjectMatrixImpl<R, C, Integer> matrix;

    public IntegerMatrix( int x, int y ) {
        super();
        matrix = new ObjectMatrixImpl<R, C, Integer>( x, y );
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
    public Integer get( int row, int column ) {
        return matrix.get( row, column );
    }

    public void set( int row, int column, Integer value ) {
        matrix.set( row, column, value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.NamedMatrix#set(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public void setByKeys( R r, C c, Integer v ) {
        this.set( getRowIndexByName( r ), getColIndexByName( c ), v );
    }

    public Integer getByKeys( R r, C c) {
        return this.get( getRowIndexByName( r ), getColIndexByName( c ));
    }

    
    
    public Integer[] getColObj( int col ) {
        Integer[] result = new Integer[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    public Integer[] getColumn( int col ) {
        Integer[] result = new Integer[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    public Integer getObject( int row, int col ) {
        return get( row, col );
    }

    public Integer[] getRow( int row ) {
        return ( Integer[] ) viewRow( row ).toArray();
    }

    public Integer[] getRowObj( int row ) {
        Integer[] result = new Integer[columns()];
        for ( int i = 0; i < columns(); i++ ) {
            result[i] = get( row, i );
        }
        return result;
    }

    public boolean isMissing( int i, int j ) {
        return get( i, j ) == null;
    }

    /**
     * @return
     */
    public int rows() {
        return matrix.rows();
    }

    /**
     * @param row
     * @param column
     * @param value
     */
    public void setObj( int row, int column, Integer value ) {
        matrix.set( row, column, value );
    }

    /**
     * @return
     */
    public int size() {
        return matrix.size();
    }

    /**
     * @return java.lang.Integer
     */
    public String toInteger() {
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

}
