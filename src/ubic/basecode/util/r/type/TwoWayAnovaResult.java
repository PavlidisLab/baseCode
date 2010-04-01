/*
 * The baseCode project
 * 
 * Copyright (c) 2010 University of British Columbia
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
package ubic.basecode.util.r.type;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

/**
 * A simple (flat) structure to hold Two Way Anova results. The user must know how they are storing the results for two
 * way anova with and without interactions. For example, for two way anova with interactions, the user may wish to store
 * the pvalue for main effect 1, main effect 2, and interaction effect like:
 * <p>
 * pvalue[0] -> p value for main effect 1
 * <p>
 * pvalue[1] -> p value for main effect 2
 * <p>
 * pvalue[2] -> p value for interaction effect
 * 
 * @author keshav
 * @version $Id$
 */
public class TwoWayAnovaResult {

    private double[] pvalues = null;
    private double[] statistics = null;

    private boolean withInteractions;

    /**
     * @param pvalues
     * @param statistics
     */
    public TwoWayAnovaResult( LinkedHashMap<String, double[]> pvalues, LinkedHashMap<String, double[]> statistics,
            boolean withInteractions ) {

        if ( pvalues.size() != statistics.size() ) {
            throw new RuntimeException( "Number of pvalues and statistics must match." );
        }

        this.withInteractions = withInteractions;

        Set<String> keys = pvalues.keySet();
        List<Double> pvalsAsList = new ArrayList<Double>();
        List<Double> statisticsAsList = new ArrayList<Double>();

        /*
         * Unrolling the results.
         */
        for ( String key : keys ) {
            double[] pvs = pvalues.get( key );
            double[] sts = statistics.get( key );

            assert pvs.length == sts.length;

            assert withInteractions ? pvs.length == 3 : pvs.length == 2;

            for ( int j = 0; j < pvs.length; j++ ) {
                pvalsAsList.add( pvs[j] );
                statisticsAsList.add( sts[j] );
            }
        }

        this.pvalues = ArrayUtils.toPrimitive( pvalsAsList.toArray( new Double[] {} ) );
        this.statistics = ArrayUtils.toPrimitive( statisticsAsList.toArray( new Double[] {} ) );

        assert this.pvalues.length == this.statistics.length;

        assert this.pvalues.length == pvalues.size() * ( withInteractions ? 3 : 2 );
        assert this.statistics.length == statistics.size() * ( withInteractions ? 3 : 2 );
    }

    public boolean isWithInteractions() {
        return this.withInteractions;
    }

    /**
     * @return
     */
    public double[] getPvalues() {
        return this.pvalues;
    }

    /**
     * @return
     */
    public double[] getStatistics() {
        return this.statistics;
    }
}
