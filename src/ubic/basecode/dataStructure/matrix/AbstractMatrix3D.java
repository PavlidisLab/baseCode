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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * TODO Document Me
 * 
 * @author Raymond
 * @version $Id$
 */
public abstract class AbstractMatrix3D<R, C, S, V> implements Matrix3D<R, C, S, V> {
    public LinkedHashMap<C, Integer> colMap;
    public List<C> colNames;
    public LinkedHashMap<R, Integer> rowMap;
    public List<R> rowNames;
    public LinkedHashMap<S, Integer> sliceMap;
    public List<S> sliceNames;

    public AbstractMatrix3D() {
        colMap = new LinkedHashMap<C, Integer>();
        rowMap = new LinkedHashMap<R, Integer>();
        sliceMap = new LinkedHashMap<S, Integer>();
        colNames = new ArrayList<C>();
        rowNames = new ArrayList<R>();
        sliceNames = new ArrayList<S>();
    }

    @Override
    public final void addColumnName( C s, int index ) {
        if ( colNames.contains( s ) ) return;
        colMap.put( s, index );
        colNames.add( s );
    }

    @Override
    public final void addRowName( R s, int index ) {
        if ( rowNames.contains( s ) ) return;
        rowMap.put( s, index );
        rowNames.add( s );

    }

    @Override
    public final void addSliceName( S s, int index ) {
        if ( sliceNames.contains( s ) ) return;
        sliceMap.put( s, index );
        sliceNames.add( s );
    }

    @Override
    public abstract int columns();

    @Override
    public final boolean containsColumnName( Object columnName ) {
        return colMap.containsKey( columnName );
    }

    @Override
    public final boolean containsRowName( Object rowName ) {
        return rowMap.containsKey( rowName );
    }

    @Override
    public final boolean containsSliceName( Object sliceName ) {
        return sliceMap.containsKey( sliceName );
    }

    @Override
    public final int getColIndexByName( Object s ) {
        Integer index = colMap.get( s );
        if ( index == null ) throw new IllegalArgumentException( s + " not found" );
        return index.intValue();
    }

    @Override
    public C getColName( int i ) {
        return colNames.get( i );
    }

    @Override
    public Iterator<C> getColNameIterator() {
        return colNames.iterator();
    }

    @Override
    public List<C> getColNames() {
        return colNames;
    }

    @Override
    public int getRowIndexByName( R s ) {
        Integer index = rowMap.get( s );
        if ( index == null ) throw new IllegalArgumentException( s + " not found" );
        return index.intValue();
    }

    @Override
    public R getRowName( int i ) {
        return rowNames.get( i );
    }

    @Override
    public Iterator<R> getRowNameIterator() {
        return rowMap.keySet().iterator();
    }

    @Override
    public List<R> getRowNames() {
        return rowNames;
    }

    @Override
    public int getSliceIndexByName( S s ) {
        Integer index = sliceMap.get( s );
        if ( index == null ) throw new IllegalArgumentException( s + " not found" );
        return index.intValue();
    }

    @Override
    public S getSliceName( int i ) {
        return sliceNames.get( i );
    }

    @Override
    public Iterator<S> getSliceNameIterator() {
        return sliceNames.iterator();
    }

    @Override
    public List<S> getSliceNames() {
        return sliceNames;
    }

    @Override
    public boolean hasColNames() {
        return columns() == colNames.size();
    }

    @Override
    public boolean hasRow( Object r ) {
        return rowMap.containsKey( r );
    }

    @Override
    public boolean hasRowNames() {
        return rows() == rowNames.size();
    }

    @Override
    public boolean hasSliceNames() {
        return slices() == sliceNames.size();
    }

    @Override
    public abstract boolean isMissing( int slice, int row, int col );

    @Override
    public abstract int numMissing();

    @Override
    public abstract int rows();

    @Override
    public void setColumnNames( List<C> v ) {
        colNames = v;
        for ( int i = 0; i < v.size(); i++ )
            colMap.put( v.get( i ), i );
    }

    @Override
    public void setRowNames( List<R> v ) {
        rowNames = v;
        for ( int i = 0; i < v.size(); i++ )
            rowMap.put( v.get( i ), i );
    }

    @Override
    public void setSliceNames( List<S> v ) {
        sliceNames = v;
        for ( int i = 0; i < v.size(); i++ )
            sliceMap.put( v.get( i ), i );
    }

    @Override
    public abstract int slices();

}
