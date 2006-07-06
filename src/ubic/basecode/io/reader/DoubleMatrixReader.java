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
package ubic.basecode.io.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import ubic.basecode.dataStructure.matrix.DoubleMatrix2DNamedFactory;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.dataStructure.matrix.NamedMatrix;
import cern.colt.list.DoubleArrayList;

/**
 * Reader for {@link basecode.dataStructure.matrix.DoubleMatrixNamed}.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DoubleMatrixReader extends AbstractNamedMatrixReader {

    private int numHeadings;
    private List colNames;

    /**
     * @param filename data file to read from
     * @return NamedMatrix object constructed from the data file
     * @throws IOException
     */
    public NamedMatrix read( String filename ) throws IOException {
        return read( filename, null );
    }

    /**
     * @param stream InputStream stream to read from
     * @return NamedMatrix object constructed from the data file
     * @throws IOException
     */
    public NamedMatrix read( InputStream stream ) throws IOException {
        return read( stream, null );
    }

    /**
     * @param stream InputStream
     * @param wantedRowNames Set
     * @return <code>read( stream, wantedRowNames, createEmptyRows )</code> with <code>createEmptyRows</code> set to
     *         true.
     * @throws IOException
     */
    public NamedMatrix read( InputStream stream, Collection wantedRowNames ) throws IOException {
        return read( stream, wantedRowNames, true );
    }

    /**
     * @param stream InputStream
     * @param wantedRowNames Set
     * @param createEmptyRows if a row contained in <code>wantedRowNames</code> is not found in the file, create an
     *        empty row filled with Double.NaN iff this param is true.
     * @return matrix
     * @throws IOException
     */
    public NamedMatrix read( InputStream stream, Collection wantedRowNames, boolean createEmptyRows )
            throws IOException {

        BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );

        List MTemp = new Vector();

        List rowNames = new Vector();

        // BufferedReader dis = new BufferedReader( new FileReader( filename ) );
        // int columnNumber = 0;
        int rowNumber = 0;
        String row;

        //
        // We need to keep track of which row names we actually found in the file
        // because will want to add empty rows for each row name we didn't find
        // (if createEmptyRows == true).
        //
        Collection wantedRowsFound = null;
        if ( wantedRowNames != null && createEmptyRows ) {
            wantedRowsFound = new HashSet();
        }

        colNames = readHeader( dis );

        numHeadings = colNames.size();

        while ( ( row = dis.readLine() ) != null ) {

            String rowName = parseRow( row, rowNames, MTemp, wantedRowNames );

            if ( wantedRowNames != null ) {

                // if we already have all the rows we want, then bail out
                if ( rowNumber >= wantedRowNames.size() ) {
                    return createMatrix( MTemp, rowNumber, numHeadings, rowNames, colNames );
                }
                // skip this row if it's not in wantedRowNames
                else if ( !wantedRowNames.contains( rowName ) ) {
                    continue;
                } else if ( createEmptyRows ) {
                    // we found the row we want in the file
                    wantedRowsFound.add( rowName );
                }
            }
            rowNumber++;
        }
        stream.close();

        //
        // Add empty rows for each row name we didn't find in the file
        //
        if ( wantedRowNames != null && createEmptyRows ) {
            Iterator iterator = wantedRowNames.iterator();
            while ( iterator.hasNext() ) {
                String s = ( String ) iterator.next();
                if ( !wantedRowsFound.contains( s ) ) {
                    // add an empty row
                    DoubleArrayList emptyRow = createEmptyRow( numHeadings );
                    rowNames.add( s );
                    MTemp.add( emptyRow );
                    rowNumber++;
                }
            }
        }

        return createMatrix( MTemp, rowNumber, numHeadings, rowNames, colNames );

    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.io.reader.AbstractNamedMatrixReader#readOneRow(java.io.BufferedReader)
     */
    public NamedMatrix readOneRow( BufferedReader dis ) throws IOException {
        String row = dis.readLine();
        List MTemp = new Vector();

        List rowNames = new Vector();
        parseRow( row, rowNames, MTemp, null );
        return createMatrix( MTemp, 1, numHeadings, rowNames, colNames );
    }

    /**
     * @param row
     * @param rowNames
     * @param MTemp
     * @param wantedRowNames
     * @return
     * @throws IOException
     */
    private String parseRow( String row, Collection rowNames, List MTemp, Collection wantedRowNames )
            throws IOException {

        String[] tokens = StringUtils.split( row, "\t" );

        DoubleArrayList rowTemp = new DoubleArrayList();
        int columnNumber = 0;
        String previousToken = "";
        String s = null;

        for ( int i = 0; i < tokens.length; i++ ) {
            s = tokens[i];
            boolean missing = false;

            if ( s.compareTo( "\t" ) == 0 ) {
                /* two tabs in a row */
                if ( previousToken.compareTo( "\t" ) == 0 ) {
                    missing = true;
                } else if ( i == tokens.length - 1 ) { // at end of line.
                    missing = true;
                } else {
                    previousToken = s;
                    continue;
                }
            } else if ( s.compareTo( " " ) == 0 ) {
                if ( previousToken.compareTo( "\t" ) == 0 ) {
                    missing = true;
                } else {
                    throw new IOException( "Spaces not allowed after values" );
                    // bad, not allowed.
                }
            } else if ( s.compareTo( "NaN" ) == 0 || s.compareTo( "NA" ) == 0 ) {
                if ( previousToken.compareTo( "\t" ) == 0 ) {
                    missing = true;
                } else {
                    throw new IOException( "NaN found where it isn't supposed to be" );
                    // bad, not allowed - missing a tab?
                }
            }

            if ( columnNumber > 0 ) {
                if ( missing ) {
                    rowTemp.add( Double.NaN );
                } else {
                    NumberFormat nf = NumberFormat.getInstance( Locale.getDefault() );
                    try {
                        log.debug( "" + nf.parse( s ).doubleValue() );
                        rowTemp.add( ( nf.parse( s ) ).doubleValue() );
                        // rowTemp.add( Double.parseDouble( s ) );
                    } catch ( ParseException e ) {
                        e.printStackTrace();
                    }
                }
            } else {
                if ( missing ) {
                    throw new IOException( "Missing values not allowed for row labels" );
                }
                if ( wantedRowNames != null && !wantedRowNames.contains( s ) ) {
                    return s;
                }
                rowNames.add( s.intern() );
            }

            columnNumber++;
            previousToken = s;
        } // end while (st.hasMoreTokens())
        // done parsing one row -- no more tokens

        if ( rowTemp.size() > numHeadings ) {
            throw new IOException( "Too many values (" + rowTemp.size() + ") in row  (based on headings count of "
                    + numHeadings + ")" );
        }

        MTemp.add( rowTemp );
        return s;

    }

    /**
     * Read a matrix from a file, subject to filtering criteria.
     * 
     * @param filename data file to read from
     * @param wantedRowNames contains names of rows we want to get
     * @return NamedMatrix object constructed from the data file
     * @throws IOException
     */
    public NamedMatrix read( String filename, Collection wantedRowNames ) throws IOException {
        File infile = new File( filename );
        if ( !infile.exists() || !infile.canRead() ) {
            throw new IOException( "Could not read from file " + filename );
        }
        FileInputStream stream = new FileInputStream( infile );
        return read( stream, wantedRowNames );
    } // end read

    // -----------------------------------------------------------------
    // protected methods
    // -----------------------------------------------------------------

    protected DoubleMatrixNamed createMatrix( List MTemp, int rowCount, int colCount, List rowNames, List colNames1 ) {

        DoubleMatrixNamed matrix = DoubleMatrix2DNamedFactory.fastrow( rowCount, colCount );

        for ( int i = 0; i < matrix.rows(); i++ ) {
            for ( int j = 0; j < matrix.columns(); j++ ) {
                if ( ( ( DoubleArrayList ) MTemp.get( i ) ).size() < j + 1 ) {
                    matrix.setQuick( i, j, Double.NaN );
                    // this allows the input file to have ragged ends.
                    // todo I'm not sure allowing ragged inputs is a good idea -PP
                } else {
                    matrix.setQuick( i, j, ( ( DoubleArrayList ) MTemp.get( i ) ).elements()[j] );
                }
            }
        }
        matrix.setRowNames( rowNames );
        matrix.setColumnNames( colNames1 );
        return matrix;

    } // end createMatrix

    protected DoubleArrayList createEmptyRow( int numColumns ) {

        DoubleArrayList row = new DoubleArrayList();
        for ( int i = 0; i < numColumns; i++ ) {
            row.add( Double.NaN );
        }
        return row;
    }

} // end class DoubleMatrixReader
