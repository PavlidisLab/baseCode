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
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import ubic.basecode.dataStructure.matrix.StringMatrix2DNamed;

/**
 * Reader for {@link basecode.dataStructure.matrix.StringMatrix2DNamed}
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StringMatrixReader extends AbstractNamedMatrixReader<StringMatrix2DNamed<String, String>, String> {

    /**
     * Missing values are entered as an empty string.
     * 
     * @param stream InputStream
     * @return NamedMatrix
     * @throws IOException
     */
    @Override
    public StringMatrix2DNamed<String, String> read( InputStream stream ) throws IOException {
        StringMatrix2DNamed<String, String> matrix = null;
        List<List<String>> MTemp = new Vector<List<String>>();
        List<String> rowNames = new Vector<String>();
        List<String> columnNames;
        BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );
        // BufferedReader dis = new BufferedReader( new FileReader( filename ) );
        int columnNumber = 0;
        int rowNumber = 0;
        String row;

        columnNames = readHeader( dis );
        int numHeadings = columnNames.size();

        while ( ( row = dis.readLine() ) != null ) {
            StringTokenizer st = new StringTokenizer( row, "\t", true );
            List<String> rowTemp = new Vector<String>();
            columnNumber = 0;
            String previousToken = "";

            while ( st.hasMoreTokens() ) {
                String s = st.nextToken();

                boolean missing = false;

                if ( s.compareTo( "\t" ) == 0 ) {
                    /* two tabs in a row */
                    if ( previousToken.compareTo( "\t" ) == 0 ) {
                        missing = true;
                    } else if ( !st.hasMoreTokens() ) { // at end of line.
                        missing = true;
                    } else {
                        previousToken = s;
                        continue;
                    }
                }

                if ( columnNumber > 0 ) {
                    if ( missing ) {
                        // rowTemp.add(Double.toString(Double.NaN));
                        rowTemp.add( "" );
                    } else {
                        rowTemp.add( s );
                    }
                } else {
                    if ( missing ) {
                        throw new IOException( "Missing values not allowed for row labels" );
                    }
                    rowNames.add( s );
                }

                columnNumber++;
                previousToken = s;
            }
            MTemp.add( rowTemp );
            if ( rowTemp.size() > numHeadings ) {
                throw new IOException( "Warning: too many values (" + rowTemp.size() + ") in row " + rowNumber
                        + " (based on headings count of " + numHeadings + ")" );
            }
            rowNumber++;
        }

        matrix = new StringMatrix2DNamed<String, String>( rowNumber, numHeadings );
        matrix.setColumnNames( columnNames );
        matrix.setRowNames( rowNames );

        for ( int i = 0; i < matrix.rows(); i++ ) {
            for ( int j = 0; j < matrix.columns(); j++ ) {
                if ( MTemp.get( i ).size() < j + 1 ) {
                    matrix.set( i, j, "" );
                    // this allows the input file to have ragged ends.
                } else {
                    matrix.set( i, j, MTemp.get( i ).get( j ) );
                }
            }
        }
        stream.close();
        return matrix;

    }

    @Override
    public StringMatrix2DNamed<String, String> read( String filename ) throws IOException {
        File infile = new File( filename );
        if ( !infile.exists() || !infile.canRead() ) {
            throw new IllegalArgumentException( "Could not read from " + filename );
        }
        FileInputStream stream = new FileInputStream( infile );
        return read( stream );
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.io.reader.AbstractNamedMatrixReader#readOneRow(java.io.BufferedReader)
     */
    @Override
    @SuppressWarnings("unused")
    public StringMatrix2DNamed<String, String> readOneRow( BufferedReader dis ) throws IOException {
        throw new UnsupportedOperationException();
    }
}