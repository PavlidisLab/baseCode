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
import java.util.*;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.SparseDoubleMatrix;

/**
 * @author pavlidis
 * 
 */
public class SparseDoubleMatrixReader extends DoubleMatrixReader {

    /**
     * Read a sparse matrix that is expressed as an adjacency list in a tab-delimited file:
     * 
     * <pre>
     *  item1 item2 weight
     *  item1 item5 weight
     * </pre>
     * <p>
     * By definition the resulting matrix is square and symmetric.
     * </p>
     * <p>
     * Note that the ordering of the items will be as they are encountered in the file.
     * 
     * @param stream InputStream
     * @return NamedMatrix
     * @throws IOException
     */
    @Override
    public DoubleMatrix<String, String> read( InputStream stream ) throws IOException {

        Set<String> itemNames = new HashSet<String>();
        Map<String, Collection<IndexScoreDyad>> rows = new HashMap<String, Collection<IndexScoreDyad>>();

        BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );

        String row;
        int index = 0;
        Map<String, Integer> nameIndexMap = new HashMap<String, Integer>(); // name --> eventual row index
        while ( ( row = dis.readLine() ) != null ) {
            StringTokenizer st = new StringTokenizer( row, " \t", false );

            String itemA = "";

            if ( st.hasMoreTokens() ) {
                itemA = st.nextToken();

                if ( !itemNames.contains( itemA ) ) {
                    rows.put( itemA, new HashSet<IndexScoreDyad>() );
                    itemNames.add( itemA );
                    nameIndexMap.put( itemA, index );
                    index++;
                }
            } else {
                // continue;
            }

            String itemB = "";
            if ( st.hasMoreTokens() ) {
                itemB = st.nextToken();
                if ( !itemNames.contains( itemB ) ) {
                    rows.put( itemB, new HashSet<IndexScoreDyad>() );
                    itemNames.add( itemB );
                    nameIndexMap.put( itemB, index );
                    index++;
                }
            } else {
                // continue;
            }

            double weight;
            if ( st.hasMoreTokens() ) {
                weight = Double.parseDouble( st.nextToken() );
            } else {
                weight = 1.0; // just make it a binary matrix.
            }

            rows.get( itemA ).add( new IndexScoreDyad( nameIndexMap.get( itemB ).intValue(), weight ) );
            rows.get( itemB ).add( new IndexScoreDyad( nameIndexMap.get( itemA ).intValue(), weight ) );
        }

        SparseDoubleMatrix<String, String> matrix = new SparseDoubleMatrix<String, String>( itemNames.size(),
                itemNames.size() );

        List<String> itemVec = new Vector<String>( itemNames );
        Collections.sort( itemVec );

        matrix.setColumnNames( itemVec );
        matrix.setRowNames( itemVec );
        for ( Object element2 : itemNames ) {
            String itemA = ( String ) element2;
            int rowIndex = matrix.getRowIndexByName( itemA );
            Collection<IndexScoreDyad> arow = rows.get( itemA );
            for ( Iterator<IndexScoreDyad> iterator = arow.iterator(); iterator.hasNext(); ) {
                IndexScoreDyad element = iterator.next();
                int ind = element.getKey();
                double weight = element.getValue();

                matrix.set( rowIndex, ind, weight );
                matrix.set( ind, rowIndex, weight );
            }

        }

        dis.close();
        return matrix;
    }

    @Override
    public DoubleMatrix<String, String> read( InputStream stream, Collection<String> wantedRowNames ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DoubleMatrix<String, String> read( InputStream stream, Collection<String> wantedRowNames,
            boolean createEmptyRows, int skipColumns, int maxRows ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Read a sparse matrix in "JW" (Jason Weston) format. The format is like this:
     * 
     * <pre>
     * 2          &lt;--- number of items - the first line of the file only. NOTE - this line is often blank or not present.
     * 1 2        &lt;--- items 1 has 2 edges
     * 1 2        &lt;--- edge indices are to items 1 &amp; 2
     * 0.1 100    &lt;--- with the following weights
     * 2 2        &lt;--- items 2 also has 2 edges
     * 1 2        &lt;--- edge indices are also to items 1 &amp; 2 (fully connected)
     * 100 0.1    &lt;--- with the following weights
     * </pre>
     * <p>
     * Note that the item numbering starts at 1. This is a requirement.
     * <p>
     * Note that this cannot handle very large matrices - the limit to rows x columns is the number Integer.MAX_VALUE.
     * This is an implementation problem for colt's sparse matrix.
     * 
     * @param stream
     * @param wantedRowNames
     * @return
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public DoubleMatrix<String, String> readJW( InputStream stream ) throws IOException {

        BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );

        Scanner ff = new Scanner( dis ).useLocale( Locale.ENGLISH );

        int index = 0;
        int amount = 0;
        double eval = 0;

        int dim = Integer.parseInt( dis.readLine() );
        SparseDoubleMatrix<String, String> returnVal = new SparseDoubleMatrix<String, String>( dim, dim );

        for ( int k = 1; k <= dim; k++ ) {

            returnVal.setColumnName( new Integer( k ).toString(), k - 1 );
            returnVal.setRowName( new Integer( k ).toString(), k - 1 );

            index = ff.nextInt(); // "item 1 has 2 edges"
            // log.info( index );
            amount = ff.nextInt();

            if ( index % 500 == 0 ) {
                log.debug( String.format( "loading %2.1f%% complete (%dth entry)... \n", 100.0 * index / dim, index ) );
            }

            int[] rowind = new int[amount];
            for ( int i = 0; i < amount; i++ ) { // "edge indices are to 1 and 2"

                index = ff.nextInt();
                int ind = index;

                if ( ind > dim || ind < 1 ) {
                    ff.close();
                    throw new IllegalStateException( "Illegal value " + ind + " found in index list for item " + k );
                }
                rowind[i] = ind;
            }

            for ( int i = 0; i < amount; i++ ) { // "with the following weights"
                eval = ff.nextDouble();
                returnVal.set( k - 1, rowind[i] - 1, eval );
            }

        }
        ff.close();
        return returnVal;
    }

}
