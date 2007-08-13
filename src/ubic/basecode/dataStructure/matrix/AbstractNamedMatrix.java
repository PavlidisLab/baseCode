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
public abstract class AbstractNamedMatrix implements NamedMatrix, java.io.Serializable {

    protected static final int MAX_ROWS_TO_PRINT = 20;
    private List rowNames;
    private List colNames;
    private Map rowMap; // contains a map of each row and elements in the row
    private Map colMap;

    private int lastColumnIndex = 0;
    private int lastRowIndex = 0;

    /**
     * 
     *  
     */
    public AbstractNamedMatrix() {
        rowMap = new LinkedHashMap(); // contains a map of each row name to index
        // of the row.
        colMap = new LinkedHashMap();
        rowNames = new ArrayList();
        colNames = new ArrayList();
    }

    /**
     * Add a column name when we don't care what the index will be. The index will be set by the method. This is useful
     * for when we need to set up a matrix before we know how many column or rows there are.
     * 
     * @param s
     */
    public final void addColumnName( Object s ) {

        if ( colMap.containsKey( s ) ) {
            throw new IllegalArgumentException( "Duplicate column name " + s );
        }

        this.colNames.add( s );
        this.colMap.put( s, new Integer( lastColumnIndex ) );
        lastColumnIndex++;

    }

    public final void addColumnName( Object s, int i ) {

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
    public final void addRowName( Object s ) {

        if ( rowMap.containsKey( s ) ) {
            // throw new IllegalArgumentException("Duplicate row name " + s);
            return;
        }

        this.rowNames.add( s );
        this.rowMap.put( s, new Integer( lastRowIndex ) );
        lastRowIndex++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.NamedMatrix#addRowName(java.lang.String, int)
     */
    public final void addRowName( Object s, int i ) {

        if ( rowMap.containsKey( s ) ) {
            // throw new IllegalArgumentException("Duplicate row name " + s);
            return;
        }

        this.rowNames.add( s );
        this.rowMap.put( s, new Integer( i ) );
    }

    /**
     * @param s String
     * @return int
     */
    public final int getRowIndexByName( Object s ) {
        Integer r = ( ( Integer ) rowMap.get( s ) );
        if ( r == null ) throw new IllegalArgumentException( s + " not found" );
        return r.intValue();
    }

    /**
     * @param r String
     * @return int
     */
    public final int getColIndexByName( Object r ) {
        Integer c = ( ( Integer ) colMap.get( r ) );
        if ( c == null ) throw new IllegalArgumentException( r + " not found" );
        return c.intValue();
    }

    /**
     * @param i int
     * @return java.lang.String
     */
    public final Object getRowName( int i ) {
        return rowNames.get( i );
    }

    /**
     * @param i int
     * @return java.lang.String
     */
    public final Object getColName( int i ) {
        return colNames.get( i );
    }

    public final boolean hasRowNames() {
        return rowNames.size() == rows();
    }

    public final boolean hasColNames() {
        return colNames.size() == columns();
    }

    public final void setRowNames( List v ) {
        for ( int i = 0; i < v.size(); i++ ) {
            addRowName( v.get( i ), i );
        }
    }

    public void setColumnNames( List v ) {
        for ( int i = 0; i < v.size(); i++ ) {
            addColumnName( v.get( i ), i );
        }
    }

    public final List getColNames() {
        return colNames;
    }

    public final List getRowNames() {
        return rowNames;
    }

    public final boolean hasRow( Object r ) {
        return this.rowMap.containsKey( r );
    }

    public final Iterator getRowNameMapIterator() {
        return this.rowMap.keySet().iterator();
    }

    public abstract int rows();

    public abstract int columns();

    public abstract void set( int i, int j, Object val );

    public abstract Object[] getRowObj( int i );

    public abstract Object[] getColObj( int i );
    
    public abstract Object getObj(int i , int j);

    public abstract boolean isMissing( int i, int j );

    public final boolean containsRowName( Object rowName ) {
        return hasRow( rowName );
    }

    public final boolean containsColumnName( Object columnName ) {
        return colMap.containsKey( columnName );
    }

    /*
     * (non-Javadoc)
     * 
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

}