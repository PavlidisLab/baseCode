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
import java.util.Vector;

import ubic.basecode.dataStructure.matrix.NamedMatrix;
import cern.colt.list.IntArrayList;

/**
 * Remove rows from a matrix that are missing too many points.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class RowMissingFilter<R, C> extends AbstractFilter<R, C> implements Filter<R, C> {

    private int minPresentCount = 5;
    private static final int ABSOLUTEMINPRESENT = 1;
    private double maxFractionRemoved = 0.0;
    private double minPresentFraction = 1.0;
    private boolean minPresentFractionIsSet = false;
    private boolean minPresentIsSet = false;

    /**
     * Set the minimum number of values that must be present in each row. The default value is 5. This is always
     * overridden by a hard-coded value (currently 2) that must be present for a row to be kept; but this value is in
     * turn overridden by the maxfractionRemoved.
     * 
     * @param m int
     */
    public void setMinPresentCount( int m ) {
        if ( m < 0 ) {
            throw new IllegalArgumentException( "Minimum present count must be > 0." );
        }
        minPresentIsSet = true;
        minPresentCount = m;
    }

    /**
     * @param k double the fraction of values to be removed.
     */
    public void setMinPresentFraction( double k ) {
        if ( k < 0.0 || k > 1.0 )
            throw new IllegalArgumentException( "Min present fraction must be between 0 and 1, got " + k );
        minPresentFractionIsSet = true;
        minPresentFraction = k;
    }

    /**
     * Set the maximum fraction of rows which will be removed from the data set. The default value is 0.3 Set it to 1.0
     * to remove this restriction.
     * 
     * @param f double
     */
    public void setMaxFractionRemoved( double f ) {
        if ( f < 0.0 || f > 1.0 )
            throw new IllegalArgumentException( "Max fraction removed must be between 0 and 1, got " + f );
        maxFractionRemoved = f;
    }

    public NamedMatrix<R, C> filter( NamedMatrix<R, C> data ) {
        List<Object[]> MTemp = new Vector<Object[]>();
        List<R> rowNames = new Vector<R>();
        int numRows = data.rows();
        int numCols = data.columns();
        IntArrayList present = new IntArrayList( numRows );

        int kept = 0;

        if ( minPresentFractionIsSet ) {
            setMinPresentCount( ( int ) Math.ceil( minPresentFraction * numCols ) );
        }

        if ( minPresentCount > numCols ) {
            throw new IllegalStateException( "Minimum present count is set to " + minPresentCount
                    + " but there are only " + numCols + " columns in the matrix." );
        }

        if ( !minPresentIsSet ) {
            log.info( "No filtering was requested" );
            return data;
        }

        /* first pass - determine how many missing values there are per row */
        for ( int i = 0; i < numRows; i++ ) {
            int missingCount = 0;
            for ( int j = 0; j < numCols; j++ ) {
                if ( !data.isMissing( i, j ) ) {
                    missingCount++;
                }
            }
            present.add( missingCount );
            if ( missingCount >= ABSOLUTEMINPRESENT && missingCount >= minPresentCount ) {
                kept++;
                MTemp.add( data.getRowObj( i ) );
                rowNames.add( data.getRowName( i ) );
            }
        }

        /* decide whether we need to invoke the 'too many removed' clause */
        if ( kept < numRows * ( 1.0 - maxFractionRemoved ) && maxFractionRemoved != 0.0 ) {
            IntArrayList sortedPresent = new IntArrayList( numRows );
            sortedPresent = present.copy();
            sortedPresent.sort();
            sortedPresent.reverse();

            log.info( "There are " + kept + " rows that meet criterion of at least " + minPresentCount
                    + " non-missing values, but that's too many given the max fraction of " + maxFractionRemoved
                    + "; minpresent adjusted to " + sortedPresent.get( ( int ) ( numRows * ( maxFractionRemoved ) ) ) );
            minPresentCount = sortedPresent.get( ( int ) ( numRows * ( maxFractionRemoved ) ) );

            // Do another pass to add rows we missed before.
            kept = 0;
            MTemp.clear();
            for ( int i = 0; i < numRows; i++ ) {
                if ( present.get( i ) >= minPresentCount && present.get( i ) >= ABSOLUTEMINPRESENT ) {
                    kept++;
                    MTemp.add( data.getRowObj( i ) );
                    if ( !rowNames.contains( data.getRowName( i ) ) ) {
                        rowNames.add( data.getRowName( i ) );
                    }
                }
            }
        }

        NamedMatrix<R, C> returnval = getOutputMatrix( data, MTemp.size(), numCols );

        // Finally fill in the return value.
        for ( int i = 0; i < MTemp.size(); i++ ) {
            for ( int j = 0; j < numCols; j++ ) {
                returnval.set( i, j, MTemp.get( i )[j] );
            }
        }
        returnval.setColumnNames( data.getColNames() );
        returnval.setRowNames( rowNames );

        log.info( "There are " + kept + " rows after removing rows which have fewer than " + minPresentCount
                + " values (or fewer than " + ABSOLUTEMINPRESENT + ")" );

        return ( returnval );

    }
}