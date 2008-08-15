/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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

import java.util.Iterator;
import java.util.List;

/**
 * @author ?
 * @version $Id$
 */
public interface Matrix3D<R, C, S, V> {
    /**
     * Add a column name associated with an index.
     * 
     * @param s a column name
     * @param index int the column index associated with this name
     */
    public void addColumnName( C s, int index );

    /**
     * Add a row name associated with a row index.
     * 
     * @param s row name
     * @param index
     */
    public void addRowName( R s, int index );

    /**
     * Add a slice name
     * 
     * @param s name of the slice
     * @param index
     */
    public void addSliceName( S s, int index );

    /**
     * Get the number of columns the matrix has.
     * 
     * @return int
     */
    public int columns();

    /**
     * Check if the matrix contains a column name
     * 
     * @param colName
     * @return true if the matrix contains the column name
     */
    public boolean containsColumnName( C columnName );

    /**
     * Check if the matrix contains a row name
     * 
     * @param rowName
     * @return true if the matrix contains the row name
     */
    public boolean containsRowName( R rowName );

    /**
     * Check if the matrix contains a slice name
     * 
     * @param stripeName
     * @return true if the matrix contains the slice name
     */
    public boolean containsSliceName( S sliceName );

    /**
     * Get the index of a column by name.
     * 
     * @param s name
     * @return column index
     */
    public int getColIndexByName( C s );

    /**
     * Get the column name for an index.
     * 
     * @param i column index
     * @return column name
     */
    public C getColName( int i );

    public Iterator<C> getColNameIterator();

    /**
     * @return List of Object
     */
    public List<C> getColNames();

    /**
     * Get the index of a row by name.
     * 
     * @param s name
     * @return row index
     */
    public int getRowIndexByName( R s );

    /**
     * Get the row name for an index
     * 
     * @param i row index
     * @return name of the row
     */
    public R getRowName( int i );

    /**
     * @return java.util.Iterator
     */
    public Iterator<R> getRowNameIterator();

    /**
     * @return List of Object
     */
    public List<R> getRowNames();

    /**
     * Get a slice index
     * 
     * @param s name
     * @return slice index
     */
    public int getSliceIndexByName( S s );

    /**
     * Get a slice name
     * 
     * @param i index
     * @return slice name
     */
    public S getSliceName( int i );

    public Iterator<S> getSliceNameIterator();

    public List<S> getSliceNames();

    /**
     * Check if this matrix has a valid set of column names.
     * 
     * @return boolean
     */
    public boolean hasColNames();

    /**
     * @param r row name
     * @return whether the row exists
     */
    public boolean hasRow( R r );

    /**
     * @return boolean
     */
    public boolean hasRowNames();

    public boolean hasSliceNames();

    /**
     * Check if the value at a given index is missing.
     * 
     * @param slice
     * @param row
     * @param column
     * @return true if the value is missing, false otherwise.
     */
    public boolean isMissing( int slice, int row, int column );

    /**
     * Return the number of missing values in the matrix.
     * 
     * @return number missing
     */
    public int numMissing();

    /**
     * Get the number of rows the matrix has
     * 
     * @return int
     */
    public int rows();

    /**
     * @param v List a vector of Strings.
     */
    public void setColumnNames( List<C> v );

    /**
     * @param v List a vector of Strings.
     */
    public void setRowNames( List<R> v );

    public void setSliceNames( List<S> v );

    public int slices();

    public V getObject( int slice, int row, int column );

}
