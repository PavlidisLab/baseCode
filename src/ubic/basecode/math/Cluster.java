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
package ubic.basecode.math;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;

/**
 * Ported directly from Michel de Hoon's Cluster 3.0 (in C).
 * 
 * @author paul
 * @version $Id$
 */
public class Cluster {

    class Node {
        int left;
        int right;
        double distance;
    }

    /**
     * The palcluster routine performs clustering using pairwise average linking on the given distance matrix.
     * 
     * @param nelements (input) int The number of elements to be clustered.
     * @param distmatrix (input) double The distance matrix, with nelements rows, each row being filled up to the
     *        diagonal. The elements on the diagonal are not used, as they are assumed to be zero. The distance matrix
     *        will be modified by this routine.
     * @return newly allocated array of Node structs, describing the hierarchical clustering solution consisting of
     *         nelements-1 nodes. Depending on whether genes (rows) or microarrays (columns) were clustered, nelements
     *         is equal to nrows or ncolumns. See src/cluster.h for a description of the Node structure.
     */
    public Node[] averageLinkage( DoubleMatrix distanceMatrix ) {
        int j;
        int n;
        int[] clusterid;
        int[] number;
        Node[] result;
        int nelements = distanceMatrix.rows();

        double[][] distmatrix = distanceMatrix.getRawMatrix();

        clusterid = new int[nelements];
        number = new int[nelements];

        result = new Node[nelements - 1];

        /*
         * Setup a list specifying to which cluster a gene belongs, and keep track of the number of elements in each
         * cluster (needed to calculate the average).
         */
        for ( j = 0; j < nelements; j++ ) {
            number[j] = 1;
            clusterid[j] = j;
            result[j] = new Node();
        }

        for ( n = nelements; n > 1; n-- ) {
            int sum;
            Integer is = 1;
            Integer js = 0;
            result[nelements - n].distance = find_closest_pair( distmatrix, is, js );

            /* Save result */
            result[nelements - n].left = clusterid[is];
            result[nelements - n].right = clusterid[js];

            /* Fix the distances */
            sum = number[is] + number[js];
            for ( j = 0; j < js; j++ ) {
                distmatrix[js][j] = distmatrix[is][j] * number[is] + distmatrix[js][j] * number[js];
                distmatrix[js][j] /= sum;
            }
            for ( j = js + 1; j < is; j++ ) {
                distmatrix[j][js] = distmatrix[is][j] * number[is] + distmatrix[j][js] * number[js];
                distmatrix[j][js] /= sum;
            }
            for ( j = is + 1; j < n; j++ ) {
                distmatrix[j][js] = distmatrix[j][is] * number[is] + distmatrix[j][js] * number[js];
                distmatrix[j][js] /= sum;
            }

            for ( j = 0; j < is; j++ )
                distmatrix[is][j] = distmatrix[n - 1][j];
            for ( j = is + 1; j < n - 1; j++ )
                distmatrix[j][is] = distmatrix[n - 1][j];

            /* Update number of elements in the clusters */
            number[js] = sum;
            number[is] = number[n - 1];

            /* Update clusterids */
            clusterid[js] = n - nelements - 1;
            clusterid[is] = clusterid[n - 1];
        }

        return result;
    }

    /**
     * This function searches the distance matrix to find the pair with the shortest distance between them. The indices
     * of the pair are returned in ip and jp; the distance itself is returned by the function.
     * 
     * @param distmatrix (input) distance matrix
     * @param ip (output) int A pointer to the integer that is to receive the first index of the pair with the shortest
     *        distance.
     * @param jp (output) int A pointer to the integer that is to receive the second index of the pair with the shortest
     *        distance.
     */
    private double find_closest_pair( double[][] distmatrix, Integer ip, Integer jp ) {
        int i, j;
        double temp;
        double distance = distmatrix[1][0];
        int n = distmatrix.length;
        ip = 1;
        jp = 0;
        for ( i = 1; i < n; i++ ) {
            for ( j = 0; j < i; j++ ) {
                temp = distmatrix[i][j];
                if ( temp < distance ) {
                    distance = temp;
                    ip = i;
                    jp = j;
                }
            }
        }
        return distance;
    }

}
