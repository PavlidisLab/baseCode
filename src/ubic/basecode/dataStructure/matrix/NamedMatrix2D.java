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

import java.util.Iterator;
import java.util.List;

/**
 * Represents a matrix with named columns and rows.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public interface NamedMatrix2D {

    /**
     * Add a column name associated with an index.
     * 
     * @param s Object a column name
     * @param index int the column index associated with this name
     */
    public void addColumnName( Object s, int index );

    /**
     * Add a row name associated with a row index.
     * 
     * @param s Object
     * @param index int
     */
    public void addRowName( Object s, int index );

    /**
     * Get the index of a row by name..
     * 
     * @param s Object
     * @return int
     */
    public int getRowIndexByName( Object s );

    /**
     * Get the index of a column by name.
     * 
     * @param s Object
     * @return int
     */
    public int getColIndexByName( Object s );

    /**
     * Get the row name for an index
     * 
     * @param i int
     * @return java.lang.Object
     */
    public Object getRowName( int i );

    /**
     * Gte the column name for an index.
     * 
     * @param i int
     * @return java.lang.Object
     */
    public Object getColName( int i );

    /**
     * @return boolean
     */
    public boolean hasRowNames();

    /**
     * Check if this matrix has a valid set of column names.
     * 
     * @return boolean
     */
    public boolean hasColNames();

    /**
     * @param v List a vector of Strings.
     */
    public void setRowNames( List v );

    /**
     * @param v List a vector of Strings.
     */
    public void setColumnNames( List v );

    /**
     * @return List of Object
     */
    public List getColNames();

    /**
     * @return List of Object
     */
    public List getRowNames();

    /**
     * @param r Object
     * @return boolean
     */
    public boolean hasRow( Object r );

    /**
     * @return java.util.Iterator
     */
    public Iterator getRowNameMapIterator();

    /**
     * Get the number of rows the matrix has
     * 
     * @return int
     */
    public int rows();

    /**
     * Get the number of columns the matrix has.
     * 
     * @return int
     */
    public int columns();

    /**
     * Set a value in the matrix.
     * 
     * @param i int
     * @param j int
     * @param val Object
     */
    public void set( int i, int j, Object val );

    /**
     * Get a row in the matrix as a generic Object[]. This exists so NamedMatrices can be used more generically.
     * 
     * @param i int row
     * @return Object[]
     */
    public Object[] getRowObj( int i );

    /**
     * @param i int column
     * @return Object[]
     */
    public Object[] getColObj( int i );

    /**
     * Check if the value at a given index is missing.
     * 
     * @param i row
     * @param j column
     * @return true if the value is missing, false otherwise.
     */
    public boolean isMissing( int i, int j );

    /**
     * Return the number of missing values in the matrix.
     * 
     * @return
     */
    public int numMissing();

    /**
     * @param rowName
     * @return
     */
    public boolean containsRowName( Object rowName );

    /**
     * @param columnName
     * @return
     */
    public boolean containsColumnName( Object columnName );

}