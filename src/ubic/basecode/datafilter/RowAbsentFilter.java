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

import ubic.basecode.dataStructure.matrix.Matrix2D;
import ubic.basecode.dataStructure.matrix.MatrixUtil;
import ubic.basecode.dataStructure.matrix.StringMatrix;

/**
 * Filter a data matrix according to flags given in a separate matrix.
 * <p>
 * The flags can be 'A', 'P' or 'M', for absent, present and marginal, following the Affymetrix convention. By default,
 * Marginal flags are counted as "absent", but this can be changed by the user.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class RowAbsentFilter<M extends Matrix2D<R, C, V>, R, C, V> extends AbstractFilter<M, R, C, V> {

    private StringMatrix<R, C> flags = null;

    private double minPresentFraction = 0.0;
    private int minPresentCount = 0;
    private boolean keepMarginal = false;
    private boolean fractionIsSet = false;
    private boolean countIsSet = false;
    private boolean flagsSet = false;

    /**
     * The data is going to be filtered in accordance to strings in 'flags'. These are either 'A', 'P' or 'M' for
     * absent, present and marginal.
     * 
     * @param data The input matrix
     * @return Matrix after filtering.
     */
    public M filter( M data ) {

        int numRows = data.rows();
        int numCols = data.columns();

        if ( minPresentCount > numCols ) {
            throw new IllegalStateException( "Minimum present count is set to " + minPresentCount
                    + " but there are only " + numCols + " columns in the matrix." );
        }

        if ( flags == null ) {
            throw new IllegalStateException( "Flag matrix is null" );
        }

        // no filtering requested.
        if ( !fractionIsSet && !countIsSet ) {
            log.info( "No filtering was requested" );
            return data;
        }

        if ( !flagsSet ) {
            log.info( "No flag matrix was provided." );
            return data;
        }

        validateFlags( data );

        // nothing will happen.
        if ( minPresentFraction == 0.0 && minPresentCount == 0 ) {
            log.info( "Criteria are set too low to result in any changes to the input." );
            return data;
        }

        List<V[]> MTemp = new Vector<V[]>();
        List<R> rowNames = new Vector<R>();

        int kept = 0;
        for ( int i = 0; i < numRows; i++ ) {
            R rowName = data.getRowName( i );

            if ( !flags.containsRowName( rowName ) ) {
                log.debug( "Row " + rowName + " not found in flags, skipping." );
                continue;
            }

            int numPresent = 0;
            for ( int j = 0; j < numCols; j++ ) {
                C colName = data.getColName( j );

                if ( !flags.containsColumnName( colName ) ) {
                    log.debug( "Column " + colName + " not found in flags, skipping." );
                    continue;
                }

                // count missing values in the data as "absent", whatever the
                // flag really is.
                if ( data.isMissing( i, j ) ) {
                    // log.debug( "Found missing data, counting as absent." );
                    continue;
                }

                String flag = flags.get( flags.getRowIndexByName( rowName ), flags.getColIndexByName( colName ) );

                if ( flags.isMissing( flags.getRowIndexByName( rowName ), flags.getColIndexByName( colName ) ) ) {
                    log.warn( "Flags had no value for an item, counting as present." );
                } else if ( flag.equals( "A" ) ) {
                    continue;
                } else if ( flag.equals( "M" ) && !keepMarginal ) {
                    continue;
                } else if ( !flag.equals( "P" ) && !flag.equals( "M" ) ) {
                    log.warn( "Found a flag I don't know about, ignoring " + flag + " and counting as present." );
                }

                numPresent++;
            }

            /* decide whether this row is a keeper */
            if ( countIsSet && numPresent >= minPresentCount || fractionIsSet
                    && ( double ) numPresent / numCols >= minPresentFraction ) {
                MTemp.add( MatrixUtil.getRow( data, i ) );
                rowNames.add( rowName );
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

    /**
     * @param f the matrix containing the flags.
     */
    public void setFlagMatrix( StringMatrix<R, C> f ) {
        if ( f == null ) {
            throw new IllegalArgumentException( "Flag matrix is null" );
        }
        flags = f;
        flagsSet = true;
    }

    /**
     * @param k whether to count 'marginal' as 'present'. Default is false.
     */
    public void setKeepMarginal( boolean k ) {
        keepMarginal = k;
    }

    /**
     * @param k the minimum number of present values there must be in order to keep the row.
     */
    public void setMinPresentCount( int k ) {
        if ( k < 0 ) {
            throw new IllegalArgumentException( "Minimum present count must be > 0." );
        }
        minPresentCount = k;
        countIsSet = true;
    }

    /**
     * @param k the minimum fraction of present values that there must be, in order to keep the row.
     */
    public void setMinPresentFraction( double k ) {
        if ( k < 0.0 || k > 1.0 )
            throw new IllegalArgumentException( "Min present fraction must be between 0 and 1, got " + k );
        minPresentFraction = k;
        fractionIsSet = true;
    }

    /**
     * @param data NamedMatrix
     * @todo this should check more carefully - actually test that the rows are all the same.
     */
    private void validateFlags( Matrix2D<?, ?, ?> data ) {
        if ( flags == null || flags.rows() < data.rows() || flags.columns() < data.columns() ) {
            throw new IllegalStateException( "Flags do not match data." );
        }
    }

}