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

import java.util.List;

/**
 * Functions for calculating Receiver operator characteristics.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class ROC {

    /**
     * Calculate area under ROC, up to a given number of False positives. The input is the total number of items in the
     * data, and the ranks of the positives in the current ranking. LOW ranks are considered better. (e.g., rank 1 is
     * the 'best')
     * 
     * @param totalSize
     * @param ranks LOW ranks are considered better. (e.g., rank 1 is the 'best')
     * @return AROC
     */
    public static double aroc( int totalSize, List<Double> ranks ) {

        double sumOfRanks = 0.0;
        for ( Double r : ranks ) {
            if ( r == 0.0 ) throw new IllegalArgumentException( "Ranks must be one-based" );
            sumOfRanks += r; // ranks are 1-based.
        }

        Long inGroup = new Long( ranks.size() );
        Long outGroup = totalSize - inGroup;

        Double t1 = inGroup * ( inGroup + 1.0 ) / 2.0;

        Long t2 = inGroup * outGroup;

        assert t2 > 0;

        Double t3 = sumOfRanks - t1;

        Double auc = Math.max( 0.0, 1.0 - t3 / t2 );

        assert auc >= 0.0 && auc <= 1.0 : "AUC was " + auc + " t1=" + t1 + "t2=" + t2 + " t3=" + t3;

        return auc;

    }

    /**
     * For an AROC value, calculates a p value. Uses fact that ROC is equivalent to the Wilcoxon rank sum test.
     * 
     * @param numpos How many positives are in the data.
     * @param Ranks of objects in the class, where low ranks are considered better. (one-based)
     * @return The p value.
     */
    public static double rocpval( int totalSize, List<Double> ranks ) {
        if ( totalSize == 0 && ( ranks == null || ranks.size() == 0 ) ) return 1.0;
        return Wilcoxon.wilcoxonP( totalSize, ranks );
    }

}