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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.RCDoubleMatrix1D;
import ubic.basecode.dataStructure.matrix.SparseRaggedDoubleMatrix;
import ubic.basecode.util.FileTools;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * Best data structure for reading really big, really sparse matrices when a matrix represetation is needed. This uses a
 * completely different file format than what we use for dense matrices.
 * 
 * @author pavlidis
 * @version $Id$
 * @see DoubleMatrixReader
 */
public class SparseRaggedMatrixReader extends DoubleMatrixReader {

    /**
     * Read an entire sparse matrix from a stream (JW format).
     * 
     * @param stream
     * @param offset A value indicating the lowest value for the indexes listed. This is here in case the indexes in the
     *        stream are numbered starting from 1 instead of zero.
     * @return
     * @throws IOException
     */
    public DoubleMatrix<String, String> read( InputStream stream, int offset ) throws IOException {
        BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );
        SparseRaggedDoubleMatrix<String, String> returnVal = new SparseRaggedDoubleMatrix<String, String>();

        String row;
        int k = 1;

        while ( ( row = dis.readLine() ) != null ) {

            if ( row.equals( "" ) ) { // in case there is a blank line at the top.
                continue;
            }

            StringTokenizer tok = new StringTokenizer( row, " \t" );
            if ( tok.countTokens() != 2 ) { // in case the row count is there.
                continue;
            }

            int index = Integer.parseInt( tok.nextToken() ) - offset;
            int amount = Integer.parseInt( tok.nextToken() );

            if ( index > 0 && index % 5000 == 0 ) {
                log.info( "loading  " + index + "th entry" );
            }

            returnVal.addRow( Integer.valueOf( k ).toString(), readOneRow( dis, amount, offset ) );

            k++;
        }

        dis.close();
        return returnVal;
    }

    /**
     * @throws IOException
     * @throws NumberFormatException Read a sparse symmetric square matrix that is expressed as an adjacency list in a
     *         tab-delimited file:
     * 
     *         <pre>
     * item1 item2 weight
     * item1 item5 weight
     * </pre>
     *         <p>
     *         IMPORTANT: By definition the resulting matrix is square and symmetric, even if the symmetric edges are
     *         not explicitly listed.
     *         </p>
     * @param stream
     * @return
     */
    public DoubleMatrix<String, String> readFromAdjList( InputStream stream ) throws NumberFormatException, IOException {
        Set<String> itemNames = new HashSet<String>();
        Map<String, OpenIntDoubleHashMap> rows = new HashMap<String, OpenIntDoubleHashMap>();

        BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );

        OpenIntObjectHashMap indexNameMap = new OpenIntObjectHashMap(); // eventual row index --> name
        Map<String, Integer> nameIndexMap = new HashMap<String, Integer>(); // name --> eventual row index

        /*
         * Store the information about the matrix in a temporary set of data structures, the most important of which is
         * a map of nodes to edge information. Each edge information object contains the index and the weight of the
         * edge.
         */
        String row;
        int index = 0;
        while ( ( row = dis.readLine() ) != null ) {
            StringTokenizer st = new StringTokenizer( row, " \t", false );

            String itemA = null;
            if ( st.hasMoreTokens() ) {
                itemA = st.nextToken();
                if ( !itemNames.contains( itemA ) ) {
                    rows.put( itemA, new OpenIntDoubleHashMap() );
                    itemNames.add( itemA );
                    indexNameMap.put( index, itemA );
                    nameIndexMap.put( itemA, index );
                    rows.get( itemA ).put( index, 0 ); // to itself. - in case it isn't
                    // there.
                    index++;
                }
            } else
                continue;

            String itemB = null;
            if ( st.hasMoreTokens() ) {
                itemB = st.nextToken();
                if ( !itemNames.contains( itemB ) ) {
                    rows.put( itemB, new OpenIntDoubleHashMap() );
                    itemNames.add( itemB );
                    indexNameMap.put( index, itemB );
                    nameIndexMap.put( itemB, index );
                    rows.get( itemB ).put( index, 0 ); // to itself. - in case it isn't
                    // there.
                    index++;
                }
            } else
                continue;

            double weight;
            if ( st.hasMoreTokens() ) {
                weight = Double.parseDouble( st.nextToken() );
            } else {
                weight = 1.0; // just make it a binary matrix.
            }

            int aind = nameIndexMap.get( itemA ).intValue();
            int bind = nameIndexMap.get( itemB ).intValue();

            // if (itemA.equals("CYP4A11") || itemB.equals("CYP4A11"))
            // System.err.println( itemA + " " + itemB + " " + aind + " " + bind );

            rows.get( itemA ).put( bind, weight ); // link a to b.
            rows.get( itemB ).put( aind, weight ); // link b to a.

            if ( rows.size() % 500 == 0 ) {
                log.info( "loading  " + index + "th pair" );
            }
        }
        dis.close();

        SparseRaggedDoubleMatrix<String, String> matrix = new SparseRaggedDoubleMatrix<String, String>();

        for ( int i = 0; i < indexNameMap.size(); i++ ) {
            Object itemName = indexNameMap.get( i );

            OpenIntDoubleHashMap arow = rows.get( itemName );

            DoubleArrayList finalValues = new DoubleArrayList( arow.size() );

            // System.err.println( itemName + " has " + arow.size() + " links" );
            IntArrayList inB = arow.keys();
            inB.sort();
            int[] rowMemberIndexes = inB.elements();
            // System.err.println( itemName + " " + i + " " + inB );

            for ( int itemNumber : rowMemberIndexes ) {
                double weight = arow.get( itemNumber );
                finalValues.add( weight );
            }

            DoubleMatrix1D rowMatrix = new RCDoubleMatrix1D( inB, finalValues );
            matrix.addRow( ( String ) itemName, rowMatrix );

            if ( i > 0 && i % 500 == 0 ) {
                log.info( "Adding  " + i + "th row" );
            }
        }
        return matrix;
    }

    /**
     * Read a sparse symmetric square matrix that is expressed as an adjacency list in a tab-delimited file:
     * 
     * <pre>
     * 
     *                                       item1 item2 weight
     *                                       item1 item5 weight
     * 
     * </pre>
     * <p>
     * IMPORTANT: By definition the resulting matrix is square and symmetric, even if the symmetric edges are not
     * explicitly listed.
     * 
     * @param name of file
     * @return
     */
    @SuppressWarnings("resource")
    public DoubleMatrix<String, String> readFromAdjList( String fileName ) throws IOException {
        if ( !FileTools.testFile( fileName ) ) {
            throw new IOException( "Could not read from file " + fileName );
        }
        InputStream stream = FileTools.getInputStreamFromPlainOrCompressedFile( fileName );
        return readFromAdjList( stream );
    }

    /**
     * Use this to read one row from a matrix (JW format). It does not close the reader. (this actually has to read
     * several lines to get the data for one matrix row)
     * 
     * @param stream
     * @param offset A value indicating the lowest value for the indexes listed. This is here in case the indexes in the
     *        stream are numbered starting from 1 instead of zero.
     * @return
     * @throws IOException
     */
    public DoubleMatrix<String, String> readOneRow( BufferedReader dis, int offset ) throws IOException {
        SparseRaggedDoubleMatrix<String, String> returnVal = new SparseRaggedDoubleMatrix<String, String>();

        String row = dis.readLine(); // line containing the id and the number of edges.
        if ( row == null ) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer( row, " \t" );

        int index = Integer.parseInt( tok.nextToken() );
        int amount = Integer.parseInt( tok.nextToken() );
        String rowName = Integer.valueOf( index ).toString();
        returnVal.addRow( rowName, readOneRow( dis, amount, offset ) );
        return returnVal;
    }

    private DoubleMatrix1D readOneRow( BufferedReader dis, int amount, int offset ) throws IOException {

        /*
         * we have to be careful to skip any lines that invalid. Each line should have at least two characters. In the
         * files JW provided there are some lines that are just " ".
         */
        String rowInd = "";
        String rowWei = "";

        // while ( rowInd.length() < 2 ) {
        rowInd = dis.readLine(); // row with indices.
        // }

        // while ( rowWei.length() < 2 ) {
        rowWei = dis.readLine(); // row with weights.
        // }

        if ( rowInd == null || rowWei == null ) {
            return null;
        }

        StringTokenizer tokw = new StringTokenizer( rowWei, " \t" );
        StringTokenizer toki = new StringTokenizer( rowInd, " \t" );

        OpenIntIntHashMap map = new OpenIntIntHashMap( amount, 0.4, 0.8 );
        DoubleArrayList values = new DoubleArrayList( amount );
        DoubleArrayList finalValues = new DoubleArrayList( amount );

        int i = 0;
        while ( toki.hasMoreTokens() ) {

            double weight = Double.parseDouble( tokw.nextToken() );
            int ind = Integer.parseInt( toki.nextToken() ) - offset;

            if ( ind < 0 ) {
                throw new IllegalStateException( "Can't have negative index - check offset." );
            }

            map.put( ind, i );
            values.add( weight );
            i++;
        }

        IntArrayList indexes = map.keys();
        indexes.sort();
        int[] ix = indexes.elements();
        int size = ix.length;
        for ( int j = 0; j < size; j++ ) {
            finalValues.add( values.get( map.get( ix[j] ) ) );
        }

        return new RCDoubleMatrix1D( indexes, finalValues );
    }

}