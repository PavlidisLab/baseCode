/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.jet.stat.Probability;

/**
 * Perform a Kruskal-Wallis test.
 * 
 * @author paul
 * @version $Id$
 */
public class KruskalWallis {
    /**
     * Perform a Kruskal-Wallis one-way ANOVA.
     * <p>
     * Implementation note: Does not make special corrections for ties, though ties are given the averaged ranks.
     * Completely bare-bones. Missing values are not tolerated well.
     * 
     * @param scores
     * @param groupings integer indicators of which values are in which groups. The actual values don't matter.
     * @return p-values based on the chi-squared statistic.
     */
    public static double test( DoubleArrayList scores, IntArrayList groupings ) {

        assert scores != null && groupings != null && scores.size() == groupings.size();

        assert scores.size() > 2;

        DoubleArrayList ranks = Rank.rankTransform( scores );

        int n = scores.size();

        /*
         * See for example http://en.wikipedia.org/wiki/Kruskal%E2%80%93Wallis_one-way_analysis_of_variance
         */

        double meanRank = ( n + 1.0 ) * 0.5;
        double meanTotalDev = 0.0; // denominator

        double scale = 12.0 / ( n * ( n + 1.0 ) );

        Map<Integer, Collection<Integer>> groupedRanks = new HashMap<Integer, Collection<Integer>>();
        for ( int i = 0; i < groupings.size(); i++ ) {
            Integer group = ( int ) groupings.get( i );

            if ( !groupedRanks.containsKey( group ) ) {
                groupedRanks.put( group, new ArrayList<Integer>() );
            }
            groupedRanks.get( group ).add( ( int ) ranks.get( i ) );

            meanTotalDev += Math.pow( ranks.get( i ) - meanRank, 2 );

        }

        int numGroups = groupedRanks.size();

        if ( numGroups < 2 ) {
            throw new IllegalArgumentException( "Must have at least two group" );
        }

        double sum = 0.0;
        for ( Integer g : groupedRanks.keySet() ) {

            Collection<Integer> gr = groupedRanks.get( g );

            double m = gr.size();

            double groupMeanRank = 0.0;
            for ( Integer r : gr ) {
                groupMeanRank += r;
            }
            groupMeanRank /= m;

            double term = m * Math.pow( groupMeanRank - ( ( n + 1.0 ) / 2.0 ), 2 );

            sum += term;
        }

        double k = scale * sum;

        return Probability.chiSquareComplemented( numGroups - 1, k );
    }
}
