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
package ubic.basecode.datafilter;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import ubic.basecode.dataStructure.matrix.Matrix2D;
import ubic.basecode.dataStructure.matrix.MatrixUtil;

/**
 * Remove or retain rows that are on a list.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class RowNameFilter<M extends Matrix2D<R, C, V>, R, C, V> extends AbstractFilter<M, R, C, V> {

    private boolean exclude = false;
    private Set<R> filterNames;

    public RowNameFilter() {
        filterNames = null;
    }

    /**
     * @param namesToFilter
     */
    public RowNameFilter( Set<R> namesToFilter ) {
        filterNames = namesToFilter;
    }

    /**
     * @param namesToFilter
     * @param exclude Set to true if you want the list to indicate items to be skipped, rather than selected.
     */
    public RowNameFilter( Set<R> namesToFilter, boolean exclude ) {
        this( namesToFilter );
        this.exclude = exclude;
    }

    /**
     * Filter according to row names.
     * 
     * @param data
     * @return
     */
    @Override
    public M filter( M data ) {
        List<V[]> MTemp = new Vector<V[]>();
        List<R> rowNames = new Vector<R>();
        int numRows = data.rows();
        int numCols = data.columns();
        int numNeeded = filterNames.size();
        int kept = 0;
        for ( int i = 0; i < numRows; i++ ) {
            R name = data.getRowName( i );

            // apply the rules.
            if ( filterNames.contains( name ) ) {
                if ( exclude ) {
                    continue;
                }
                MTemp.add( MatrixUtil.getRow( data, i ) );
                rowNames.add( name );
                kept++;
                if ( kept >= numNeeded ) {
                    break; // no use in continuing.
                }
            }

            if ( exclude ) {
                MTemp.add( MatrixUtil.getRow( data, i ) );
                rowNames.add( name );
                kept++;
            }
        }

        M returnval = getOutputMatrix( data, MTemp.size(), numCols );

        for ( int i = 0; i < MTemp.size(); i++ ) {
            for ( int j = 0; j < numCols; j++ ) {
                returnval.set( i, j, MTemp.get( i )[j] );
            }
        }
        returnval.setColumnNames( data.getColNames() );
        returnval.setRowNames( rowNames );

        log.info( "There are " + kept + " rows left after filtering." );

        return returnval;
    }

    public void setFilterNames( Set<R> namesToFilter, boolean exclude ) {
        this.filterNames = namesToFilter;
        this.exclude = exclude;
    }

}