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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * Abstract base class for 2D matrices of double values with named columns and rows.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public abstract class DoubleMatrixNamed extends AbstractNamedMatrix {

    protected static Log log = LogFactory.getLog( DoubleMatrixNamed.class.getName() );

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.NamedMatrix#rows()
     */
    public abstract int rows();

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.NamedMatrix#columns()
     */
    public abstract int columns();

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.NamedMatrix#set(int, int, java.lang.Object)
     */
    public abstract void set( int i, int j, Object val );

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.NamedMatrix#getRowObj(int)
     */
    public abstract Object[] getRowObj( int i );

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.NamedMatrix#getColObj(int)
     */
    public abstract Object[] getColObj( int i );

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.dataStructure.matrix.NamedMatrix#isMissing(int, int)
     */
    public abstract boolean isMissing( int i, int j );

    public abstract double[] getRow( int i );

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
    public double[] getRowByName( String s ) {
        return getRow( getRowIndexByName( s ) );
    }

    /**
     * @param j
     * @param i
     * @param c
     */
    public abstract void setQuick( int j, int i, double c );

}
