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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pavlidis
 * @version $Id$
 */
public abstract class AbstractMatrix<R, C, V> implements Matrix2D<R, C, V>, java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected static final int MAX_ROWS_TO_PRINT = 20;
    private Map<C, Integer> colMap;
    private List<C> colNames;
    private int lastColumnIndex = 0;

    private int lastRowIndex = 0;
    private Map<R, Integer> rowMap; // contains a map of each row and elements in the row

    private List<R> rowNames;

    /**
     * 
     *  
     */
    public AbstractMatrix() {
        rowMap = new LinkedHashMap<R, Integer>(); // contains a map of each row name to index
        // of the row.
        colMap = new LinkedHashMap<C, Integer>();
        rowNames = new ArrayList<R>();
        colNames = new ArrayList<C>();
    }

    /**
     * Add a column name when we don't care what the index will be. The index will be set by the method. This is useful
     * for when we need to set up a matrix before we know how many column or rows there are.
     * 
     * @param s
     */
    public final void addColumnName( C s ) {

        if ( s == null ) {
            throw new IllegalArgumentException( "Column name cannot be null" );
        }

        if ( colMap.containsKey( s ) ) {
            throw new IllegalArgumentException( "Duplicate column name " + s );
        }

        this.colNames.add( s );
        this.colMap.put( s, new Integer( lastColumnIndex ) );
        lastColumnIndex++;

    }

    public final void addColumnName( C s, int i ) {

        if ( s == null ) {
            throw new IllegalArgumentException( "Column name cannot be null" );
        }

        if ( colMap.containsKey( s ) ) {
            throw new IllegalArgumentException( "Duplicate column name " + s );
        }

        this.colNames.add( s );
        this.colMap.put( s, new Integer( i ) );
    }

    /**
     * Add a row name when we don't care what the index will be. The index will be set by the method. This is useful for
     * when we need to set up a matrix before we know how many column or rows there are.
     * 
     * @param s
     */
    public final void addRowName( R s ) {

        if ( s == null ) {
            throw new IllegalArgumentException( "Row name cannot be null" );
        }
        if ( rowMap.containsKey( s ) ) {
            throw new IllegalArgumentException( "Duplicate row name " + s );
        }

        this.rowNames.add( s );
        this.rowMap.put( s, new Integer( lastRowIndex ) );
        lastRowIndex++;
    }

    /*
     * (non-Javadoc)
     * @see basecode.dataStructure.NamedMatrix#addRowName(java.lang.String, int)
     */
    public final void addRowName( R s, int i ) {
        if ( s == null ) {
            throw new IllegalArgumentException( "Row name cannot be null" );
        }
        if ( this.hasRow( s ) ) {
            throw new IllegalArgumentException( "Duplicate row name " + s );
        }

        this.rowNames.add( s );
        this.rowMap.put( s, new Integer( i ) );
    }

    public final boolean containsColumnName( C columnName ) {
        return colMap.containsKey( columnName );
    }

    public final boolean containsRowName( R rowName ) {
        return hasRow( rowName );
    }

    /**
     * @param columnKey String
     * @return int
     */
    public final int getColIndexByName( C columnKey ) {
        Integer c = colMap.get( columnKey );
        if ( c == null ) throw new IllegalArgumentException( columnKey + " not found" );
        return c.intValue();
    }

    /**
     * @param i int
     * @return java.lang.String
     */
    public final C getColName( int i ) {
        if ( !this.hasColNames() ) return null;
        return colNames.get( i );
    }

    public final List<C> getColNames() {
        return colNames;
    }

    /**
     * @param s String
     * @return int
     */
    public final int getRowIndexByName( R s ) {
        Integer r = rowMap.get( s );
        if ( r == null ) throw new IllegalArgumentException( s + " not found" );
        return r.intValue();
    }

    /**
     * @param i int
     * @return java.lang.String
     */
    public final R getRowName( int i ) {
        if ( !this.hasRowNames() ) return null;
        return rowNames.get( i );
    }

    public final Iterator<R> getRowNameMapIterator() {
        return this.rowMap.keySet().iterator();
    }

    public final List<R> getRowNames() {
        return rowNames;
    }

    public final boolean hasColNames() {
        return colNames.size() == columns();
    }

    public final boolean hasRow( R r ) {
        return this.rowMap.containsKey( r );
    }

    public final boolean hasRowNames() {
        return rowNames.size() == rows();
    }

    /*
     * (non-Javadoc)
     * @see basecode.dataStructure.NamedMatrix#numMissing()
     */
    public int numMissing() {
        int count = 0;
        int n = this.rows();
        int m = this.columns();
        for ( int i = 0; i < n; i++ ) {
            for ( int j = 0; j < m; j++ ) {
                if ( isMissing( i, j ) ) {
                    count++;
                }
            }
        }
        return count;
    }

    public void setColumnNames( List<C> v ) {
        this.colNames.clear();
        for ( int i = 0; i < v.size(); i++ ) {
            addColumnName( v.get( i ), i );
        }
    }

    public final void setRowNames( List<R> v ) {
        this.rowNames.clear();

        if ( v.size() != this.rows() ) {
            throw new IllegalArgumentException( "Cannot add " + v.size() + " row names to a matrix with " + this.rows()
                    + " rows" );
        }

        for ( int i = 0; i < v.size(); i++ ) {
            R rowName = v.get( i );
            this.addRowName( rowName, i );
        }
    }
}