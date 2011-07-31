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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.util.FileTools;
import cern.colt.list.DoubleArrayList;

/**
 * Reader for {@link basecode.dataStructure.matrix.DoubleMatrix}. Lines beginning with "#" or "!" will be ignored.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DoubleMatrixReader extends AbstractMatrixReader<DoubleMatrix<String, String>, Double> {

    private int numHeadings;
    private List<String> colNames;
    private static NumberFormat nf = NumberFormat.getInstance();
    static {
        if ( nf instanceof DecimalFormat ) {
            // ( ( DecimalFormat ) nf ).setDecimalSeparatorAlwaysShown( true );
        }
    }

    /**
     * @param stream InputStream stream to read from
     * @return NamedMatrix object constructed from the data file
     * @throws IOException
     */
    @Override
    public DoubleMatrix<String, String> read( InputStream stream ) throws IOException {
        return read( stream, null, 0 );
    }

    /**
     * @param stream InputStream
     * @param wantedRowNames Set
     * @return <code>read( stream, wantedRowNames, createEmptyRows )</code> with <code>createEmptyRows</code> set to
     *         true.
     * @throws IOException
     */
    public DoubleMatrix<String, String> read( InputStream stream, Collection<String> wantedRowNames )
            throws IOException {
        return read( stream, wantedRowNames, true, 0, -1 );
    }

    /**
     * @param stream
     * @param wantedRowNames
     * @param skipColumns
     * @return <code>read( stream, wantedRowNames, createEmptyRows )</code> with <code>createEmptyRows</code> set to
     *         true.
     * @throws IOException
     */
    public DoubleMatrix<String, String> read( InputStream stream, Collection<String> wantedRowNames, int skipColumns )
            throws IOException {
        return read( stream, wantedRowNames, true, skipColumns, -1 );
    }

    /**
     * @param fileName
     * @param wantedRowNames
     * @param skipColumns how many columns to skip -- not counting the first column. So if you set this to 4, the first
     *        four data columns will be skipped.
     * @return
     * @throws IOException
     */
    public DoubleMatrix<String, String> read( String fileName, Collection<String> wantedRowNames, int skipColumns )
            throws IOException {
        File infile = new File( fileName );
        if ( !infile.exists() || !infile.canRead() ) {
            throw new IOException( "Could not read from file " + fileName );
        }
        InputStream stream = FileTools.getInputStreamFromPlainOrCompressedFile( fileName );
        return read( stream, wantedRowNames, true, skipColumns, -1 );
    }

    /**
     * @param stream InputStream
     * @param wantedRowNames Set
     * @param createEmptyRows if a row contained in <code>wantedRowNames</code> is not found in the file, create an
     *        empty row filled with Double.NaN iff this param is true.
     * @param maxRows
     * @return matrix
     * @throws IOException
     */
    public DoubleMatrix<String, String> read( InputStream stream, Collection<String> wantedRowNames,
            boolean createEmptyRows, int skipColumns, int maxRows ) throws IOException {

        BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );

        List<DoubleArrayList> MTemp = new Vector<DoubleArrayList>();

        List<String> rowNames = new Vector<String>();
        String row;

        //
        // We need to keep track of which row names we actually found in the file
        // because will want to add empty rows for each row name we didn't find
        // (if createEmptyRows == true).
        //
        Collection<String> wantedRowsFound = new HashSet<String>();

        colNames = readHeader( dis, skipColumns );

        numHeadings = colNames.size();

        int rowNumber = 0;

        while ( ( row = dis.readLine() ) != null ) {

            if ( StringUtils.isBlank( row ) ) {
                continue;
            }

            String rowName = parseRow( row, rowNames, MTemp, wantedRowNames, skipColumns );

            if ( rowName == null ) {
                // signals a blank or skipped row.
                continue;
            }

            if ( wantedRowNames != null ) {

                // if we already have all the rows we want, then bail out
                if ( wantedRowsFound.size() >= wantedRowNames.size() ) {
                    assert wantedRowsFound.containsAll( wantedRowNames );
                    log.info( "Found all rows needed" );
                    return createMatrix( MTemp, rowNames, colNames );
                }

                if ( wantedRowNames.contains( rowName ) ) {
                    wantedRowsFound.add( rowName );
                }
            }

            if ( maxRows > 0 && ++rowNumber == maxRows ) break;

        }
        stream.close();

        //
        // Add empty rows for each row name we didn't find in the file
        //
        if ( wantedRowNames != null && wantedRowNames.size() != wantedRowsFound.size() && createEmptyRows ) {
            Iterator<String> iterator = wantedRowNames.iterator();
            while ( iterator.hasNext() ) {
                String s = iterator.next();
                if ( !wantedRowsFound.contains( s ) ) {
                    log.info( s + " was not found, adding empty row" );
                    DoubleArrayList emptyRow = createEmptyRow( numHeadings );
                    rowNames.add( s );
                    MTemp.add( emptyRow );
                }
            }
        }
        assert rowNames.size() == MTemp.size();
        return createMatrix( MTemp, rowNames, colNames );

    }

    /**
     * @param filename data file to read from (can be compressed)
     * @return NamedMatrix object constructed from the data file
     * @throws IOException
     */
    @Override
    public DoubleMatrix<String, String> read( String filename ) throws IOException {
        return read( filename, null, -1 );
    }

    @Override
    public DoubleMatrix<String, String> read( String filename, int maxRows ) throws IOException {
        return read( filename, null, maxRows );
    }

    /**
     * Read a matrix from a file, subject to filtering criteria.
     * 
     * @param filename data file to read from (can be compressed)
     * @param wantedRowNames contains names of rows we want to get
     * @return NamedMatrix object constructed from the data file
     * @throws IOException
     */
    public DoubleMatrix<String, String> read( String filename, Collection<String> wantedRowNames ) throws IOException {
        File infile = new File( filename );
        if ( !infile.exists() || !infile.canRead() ) {
            throw new IOException( "Could not read from file " + filename );
        }
        InputStream stream = FileTools.getInputStreamFromPlainOrCompressedFile( filename );
        return read( stream, wantedRowNames, -1 );
    } // end read

    protected DoubleArrayList createEmptyRow( int numColumns ) {

        DoubleArrayList row = new DoubleArrayList();
        for ( int i = 0; i < numColumns; i++ ) {
            row.add( Double.NaN );
        }
        return row;
    }

    // -----------------------------------------------------------------
    // protected methods
    // -----------------------------------------------------------------

    protected DoubleMatrix<String, String> createMatrix( List<DoubleArrayList> MTemp, List<String> rowNames,
            List<String> colNames1 ) {

        if ( MTemp.isEmpty() ) {
            throw new IllegalArgumentException( "Must provide vectors" );
        }
        DoubleMatrix<String, String> matrix = DoubleMatrixFactory.fastrow( MTemp.size(), MTemp.get( 0 ).size() );

        for ( int i = 0; i < matrix.rows(); i++ ) {
            for ( int j = 0; j < matrix.columns(); j++ ) {
                if ( MTemp.get( i ).size() < j + 1 ) {
                    matrix.set( i, j, Double.NaN );
                    // this allows the input file to have ragged ends.
                    // todo I'm not sure allowing ragged inputs is a good idea -PP
                } else {
                    matrix.set( i, j, MTemp.get( i ).elements()[j] );
                }
            }
        }

        assert matrix.rows() == MTemp.size();
        assert matrix.rows() == rowNames.size();
        assert matrix.columns() == colNames.size() : "Got " + matrix.columns() + " != " + colNames.size();

        matrix.setRowNames( rowNames );
        matrix.setColumnNames( colNames1 );
        return matrix;

    } // end createMatrix

    /**
     * @param row
     * @param rowNames
     * @param MTemp
     * @param wantedRowNames
     * @param skipColumns the number of columns after the first to ignore (for example, Gemma output that includes gene
     *        information as well as numeric data)
     * @return
     * @throws IOException
     */
    private String parseRow( String row, Collection<String> rowNames, List<DoubleArrayList> MTemp,
            Collection<String> wantedRowNames, int skipColumns ) throws IOException {

        if ( row.startsWith( "#" ) || row.startsWith( "!" ) ) {
            return null;
        }

        String[] tokens = StringUtils.splitPreserveAllTokens( row, "\t" );

        DoubleArrayList rowTemp = new DoubleArrayList();
        int columnNumber = 0;
        String previousToken = "";
        String currentRowName = null;
        for ( int i = 0; i < tokens.length; i++ ) {
            String tok = tokens[i];
            boolean missing = false;

            if ( tok.compareTo( "\t" ) == 0 ) {
                /* two tabs in a row */
                if ( previousToken.compareTo( "\t" ) == 0 ) {
                    missing = true;
                } else if ( i == tokens.length - 1 ) { // at end of line.
                    missing = true;
                } else {
                    previousToken = tok;
                    continue;
                }
            } else if ( tok.compareTo( " " ) == 0 || tok.compareTo( "" ) == 0 ) {
                missing = true;
            } else if ( tok.compareTo( "NaN" ) == 0 || tok.compareTo( "NA" ) == 0 ) {
                missing = true;
            }

            if ( columnNumber > 0 ) {

                if ( skipColumns > 0 && columnNumber <= skipColumns ) {
                    // skip.
                } else if ( missing ) {
                    rowTemp.add( Double.NaN );
                } else {
                    try {
                        /*
                         * NumberFormat.parse thinks things like 9101001_at are okay. Try to catch such cases. Note that
                         * we can't use Double.parseDouble because that doesn't seem to handle locale-specific number
                         * formats like european decimals (0,001 etc.)
                         */
                        // if ( tok.matches( ".*[a-zA-Z_=].*" ) ) {
                        // throw new NumberFormatException( "Unexpected non-numeric value found in column "
                        // + columnNumber + ": " + tok );
                        // }
                        rowTemp.add( nf.parse( tok.toUpperCase() ).doubleValue() );
                    } catch ( ParseException e ) {
                        throw new RuntimeException( e );
                    }
                }
            } else {
                // First field is the row label.

                if ( missing ) {
                    throw new IOException( "Missing values not allowed for row labels" );
                }

                currentRowName = tok;

                // Skip rows. Return the row name anyway.
                if ( wantedRowNames != null && !wantedRowNames.contains( currentRowName ) ) {
                    return currentRowName;
                }

                rowNames.add( currentRowName );
            }

            columnNumber++;
            previousToken = tok;
        } // end while (st.hasMoreTokens())
        // done parsing one row -- no more tokens

        if ( rowTemp.size() > numHeadings ) {
            throw new IOException( "Too many values (" + rowTemp.size() + ") in row  (based on headings count of "
                    + numHeadings + ")" );
        }

        MTemp.add( rowTemp );
        return currentRowName;

    }

} // end class DoubleMatrixReader
