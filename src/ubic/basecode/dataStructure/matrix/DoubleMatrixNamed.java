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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * Abstract base class for 2D matrices of double values with named columns and rows.
 * 
 * @author pavlidis
 * @version $Id$
 */
public abstract class DoubleMatrixNamed<R, C> extends AbstractNamedMatrix<R, C> {

    protected static Log log = LogFactory.getLog( DoubleMatrixNamed.class.getName() );

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#rows()
     */
    public abstract int rows();

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#columns()
     */
    public abstract int columns();

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#set(int, int, java.lang.Object)
     */
    public abstract void set( int i, int j, Object val );

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#getRowObj(int)
     */
    public abstract Object[] getRowObj( int i );

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#getColObj(int)
     */
    public abstract Object[] getColObj( int i );

    public abstract Object getObj( int i, int j );

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#isMissing(int, int)
     */
    public abstract boolean isMissing( int i, int j );

    public abstract double[] getRow( int i );

    public abstract double[] getColumn( int i );

    public abstract DoubleArrayList getRowArrayList( int i );

    public abstract double get( int x, int y );

    /**
     * @param i
     * @param j
     * @return
     */
    public abstract double getQuick( int i, int j );

    public abstract void set( int x, int y, double value );

    /**
     * @param j
     * @return
     */
    public abstract DoubleMatrix1D viewRow( int j );

    /**
     * @param s String
     * @return double[]
     */
    public double[] getRowByName( R s ) {
        return getRow( getRowIndexByName( s ) );
    }

    /**
     * @param s String
     * @return double[]
     */
    public double[] getColumnByName( C s ) {
        return getColumn( getColIndexByName( s ) );
    }

    /**
     * @param j
     * @param i
     * @param c
     */
    public abstract void setQuick( int j, int i, double c );

    public String toString() {
        int rows = this.rows();
        int columns = this.columns();
        StringBuffer buf = new StringBuffer();
        int stop = 0;
        buf.append( "Row\\Col" );
        for ( int i = 0; i < columns; i++ ) {
            buf.append( "\t" + this.getColName( i ) );
        }
        buf.append( "\n" );
        for ( int j = 0; j < rows; j++ ) {

            buf.append( this.getRowName( j ) );
            for ( int i = 0; i < columns; i++ ) {
                buf.append( "\t" + this.get( j, i ) );
            }
            buf.append( "\n" );
            if ( stop > MAX_ROWS_TO_PRINT ) {
                buf.append( "...\n" );
                break;
            }
            stop++;
        }
        return buf.toString();
    }
}
